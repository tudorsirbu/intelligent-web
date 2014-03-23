package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	 * The method retrieved the stop list as an array list of strings.
	 * @return ArrayList<String> containing the stop list words
	 */
	public ArrayList<String> getStopList(){
		ArrayList<String> stopList = new ArrayList<String>();
		try {
			statement = this.connection.createStatement();
			String query = "SELECT word FROM words WHERE indexed = 0";
			ResultSet results = statement.executeQuery(query);
			while(results.next())
				stopList.add(results.getString("word"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stopList;
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
			String query = "SELECT * FROM words WHERE `word`='?'";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, word);
			// query the database
			ResultSet results = preparedStatement.executeQuery(query);

			// go through results and whether any words have been returned
			results.last();
			if(results.getRow() == 0)
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
			String query = "SELECT * FROM words WHERE `word`='" + word + "';";
			ResultSet results = statement.executeQuery(query);
			// get the word 
			while(results.next()){
				return results.getInt("id"); }
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		return -1;
	}
	/**
	 * Is the word indexable?
	 * @param word the word to be checked if it can be indexed?
	 * @return true if the word can be indexed and false otherwise
	 */
	public boolean isIndexable(String word){
		try{
			// create statement
			statement = connection.createStatement();
			// query the database
			String query = "SELECT * FROM words WHERE `word`='" + word + "';";
			ResultSet results = statement.executeQuery(query);
			// get the word 
			while(results.next()){
				if(results.getInt("indexed") == 1)
					return true;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		return false;
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
			String query = "SELECT * FROM words WHERE `id` = '" + id + "'";
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
		// if the word is unique add it to the database
		try {
			//  statement for query execution
			statement = connection.createStatement();
			// query 
			String query = "INSERT IGNORE INTO words (`word`, `indexed`) values ('"+ word +"', '1')";
			// Updating Table
			statement.executeUpdate(query); 
			return true;
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
		return false;
	}

}
