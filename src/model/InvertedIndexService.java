package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import java.util.HashMap;

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

	public HashMap<User,HashMap<String,Integer>> getKeywords(long[] ids, int days, int noKeywords){
		// the returned hashmap containing for each user a hashmap of the top keywords
		HashMap<User,HashMap<String,Integer>> top = new HashMap<User,HashMap<String,Integer>>();

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
					+ "GROUP BY users.id, words.word "
					+ "ORDER BY sumCount DESC";
			ResultSet results = statement.executeQuery(query);
			while(results.next()){
				// create a new user
				String id = results.getString("id");
				String name = results.getString("name");
				String username = results.getString("username");
				String location = results.getString("location");
				String description = results.getString("description");
				String profilePictureURL = results.getString("profilePictureURL");
				User u = new User(id, name, username, location, description, profilePictureURL, null);

				// check if the user already has a map of keywords
				HashMap<String,Integer> keywords = top.get(u);
				if(keywords == null){
					keywords = new HashMap<String,Integer>();
				}
				if(keywords.size() <= noKeywords){
					String word = results.getString("word");
					int count = results.getInt("sumCount");
					keywords.put(word, count);
					top.put(u,keywords);
				}
				
			}


		} catch (SQLException e) {
			System.out.println("The inverted indexing fetching failed. Check your InvertedIndexService code.");
			e.printStackTrace();
		}	

		return top;
	}

}
