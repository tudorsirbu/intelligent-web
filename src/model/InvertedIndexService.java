package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

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

	public void createIndex(String word, String userID, Date tweetDate, int count){
		// get the word id
		WordService wordService = new WordService(connection);
		int wordID = wordService.getWordId(word);
		// if the wordID = -1 then the word is not found in the database
		if(wordID == -1){
			// insert the word in the db
			wordService.insertWord(word);
			// get the word's id
			wordID = wordService.getWordId(word);
		}
		
		// convert date to sql format
//		java.sql.Date date = new java.sql.Date(tweetDate.getTime());
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
