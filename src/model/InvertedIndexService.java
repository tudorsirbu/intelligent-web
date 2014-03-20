package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The class handles opertation on the inverted index table.
 * @author tudorsirbu
 *
 */
public class InvertedIndexService {
	Connection connection = null;
	Statement statement = null;

	public InvertedIndexService(Connection connection){
		this.connection = connection;
	}

	public void createIndex(String word, String userID, java.util.Date tweetDate, int count){
		// get the word id
		WordService wordService = new WordService(connection);
		int wordID = wordService.getWordId(word);
		// if the wordID = -1 then the word is not found in the database
		if(wordID == -1){
			// insert the word in the db
			wordService.insertWord(word);
			// get the word's id
			wordID = wordService.getWordId(word);
		} else if(wordService.isIndexable(word)){
			// convert date to sql format
			java.sql.Timestamp date = new java.sql.Timestamp(tweetDate.getTime());

			try {
				//  statement for query execution
				statement = connection.createStatement();
				// query 
				String query = "INSERT IGNORE INTO inverted_index (`user_id`, `word_id`, `count`, `date`) values('"+ userID +"','" + wordID
						+ "','" + count + "','" + date + "')";
				// Updating Table
				statement.executeUpdate(query);          
			}
			catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<User> getKeywords(long[] ids, int days, int noKeywords){
		// the returned hashmap containing for each user a hashmap of the top keywords
		HashSet<User> top = new HashSet<User>();

		// keep track of the top keywords
		ArrayList<String> topKeywords = new ArrayList<String>();

		// get the users
		ArrayList<User> users = new UserService(connection).getUsers(ids); 

		// compute date
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -days);
		java.sql.Date date = new java.sql.Date(c.getTimeInMillis());

		// prepare ids for the sql query
		String user_ids = "";
		for(long id:ids){
			if(!user_ids.equals(""))
				user_ids += ",";
			user_ids += id;
		}

		try {
			this.statement = connection.createStatement();
			String query = "SELECT users.*, words.word, SUM(inverted_index.count) as sumCount "
					+ "FROM inverted_index "
					+ "INNER JOIN users ON inverted_index.user_id = users.id "
					+ "INNER JOIN words ON words.id = inverted_index.word_id "
					+ "WHERE (DATE(inverted_index.date) BETWEEN '" + date + "' AND CURDATE()) "
					+ "AND inverted_index.user_id in (" + user_ids + ") "
					+ "GROUP BY words.word, users.id "
					+ "ORDER BY sumCount DESC";
			ResultSet results = statement.executeQuery(query);
			while(results.next()){
				// get the keyword
				String keyword = results.getString("word");

				// get the user who has used this keyword
				String id = results.getString("id");
				String name = results.getString("name");
				String username = results.getString("username");
				String location = results.getString("location");
				String description = results.getString("description");
				String profilePictureURL = results.getString("profilePictureURL");
				User rowUser = new User(id, name, username, location, description, profilePictureURL, null);
				
				// if the keyword is in the top
				if(topKeywords.contains(keyword)){
					// get the user's current top count
					HashMap<String, Integer> keywords = this.getUser(users, rowUser).getKeywords();
					int keywordCount = keywords.get(keyword) + results.getInt("sumCount");
					keywords.put(keyword, keywordCount);
				} else if(topKeywords.size() < noKeywords){
					// add the keyword to the top
					topKeywords.add(keyword);

					// for each user, make the keyword 
					for(User u:users){
						HashMap<String, Integer> keywords = u.getKeywords();
						// add the new keyword to the map
						if(u.equals(rowUser))
							keywords.put(keyword, results.getInt("sumCount"));
						else
							keywords.put(keyword, 0);
					}
					

					// update this row's user keyword count for this keyword
					HashMap<String, Integer> keywords = rowUser.getKeywords();
					keywords.put(keyword, results.getInt("sumCount"));

				}
			}
		} catch (SQLException e) {
			System.out.println("The inverted indexing fetching failed. Check your InvertedIndexService code.");
			e.printStackTrace();
		}	

		return users;
	}

	private User getUser(ArrayList<User> users, User searchedUser){
		for(User user: users)
			if(user.equals(searchedUser))
				return user;

		return null;
	}

}
