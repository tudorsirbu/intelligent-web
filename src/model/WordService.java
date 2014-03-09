package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class handles all the operations on the words table
 * @author tudorsirbu
 *
 */
public class WordService {
	Connection connection = null;
	Statement statement = null;

	/**
	 * Constructor which connects to the database using the DatabaseConnection class
	 */
	public WordService(Connection connection){
		this.connection = connection;
	}
	/**
	 * The method checks whether a given word is unique
	 * @param word the word which is checked for uniqueness
	 * @return true is the word is unique and false otherwise
	 * @throws SQLException
	 */
	public boolean isUnique(String word){
		// we assume the word is unique
		boolean unique = true;

		try {
			// create statement
			statement = connection.createStatement();

			String query = "SELECT * FROM words";

			// query the database
			ResultSet results = statement.executeQuery(query);

			// go through results and whether any words have been returned
			while(results.next())
				unique = false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return unique;
	}
	/**
	 * The method return the id of a given word
	 * @param word the word for which the id is requested
	 * @return the id of the given word or -1 if it isn't found
	 */
	public int getWordId(String word){
		try{
			// create statement
			statement = connection.createStatement();
			// query the database
			String query = "SELECT FROM words WHERE `word` = '" + word + "'";
			ResultSet results = statement.executeQuery(query);
			// get the word 
			while(results.next())
				return results.getInt("id");
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	/**
	 * The method searches for a word by its id
	 * @param id the word's id
	 * @return the word matching the given id
	 */
	public String getWord(int id){
		try{
			// create statement
			statement = connection.createStatement();
			// query the database
			String query = "SELECT FROM words WHERE `id` = '" + id + "'";
			ResultSet results = statement.executeQuery(query);
			// get the word 
			while(results.next())
				return results.getString("word");
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * The method inserts a word in the words table
	 * @param word the word to be inserted
	 * @return true if the insert was successful and false otherwise
	 */
	public boolean insertWord(String word){
		// check is the word is unique 
		if (!isUnique(word))
			return false;
		// if the word is unique add it to the database
		try {
			//  statement for query execution
			statement = connection.createStatement();
			// query 
			String query = "INSERT INTO words (`word`) values('"+ word +"')";
			// Updating Table
			statement.executeUpdate(query); 
			return true;
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
		return false;
	}

}
