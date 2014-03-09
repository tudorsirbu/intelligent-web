package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;

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

	public void createIndex(String word, String userID, Date tweetDate){
		// get the word id
		WordService wordService = new WordService(connection);
		int wordID = wordService.getWordId(word);
		
		// parse date to string
		String date = tweetDate.toString();
		
		try {
			//  statement for query execution
			statement = connection.createStatement();
			// query 
			String query = "INSERT INTO inverted_index (`user_id`, `word_id`) values('"+ userID +"','" + wordID
					+ "','" + date + "')";
			// Updating Table
			statement.executeUpdate(query);          
		}
		catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
