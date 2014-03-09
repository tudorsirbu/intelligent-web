package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class handles relationships between users
 * @author tudorsirbu
 *
 */
public class FriendService {
	// the connection to the database
	Connection connection = null;
	Statement statement = null;

	/**
	 * Constructor which connects to the database using the DatabaseConnection class
	 */
	public FriendService(Connection connection){
		// create a connection to the database
		this.connection = connection;
	}
	/**
	 * The method saves a connection between two users that have interacted. 
	 * Eg. If user 1 retwitted user2's status update then that is considered as 
	 * a connection between the two users.
	 * @param userID1
	 * @param userID2
	 */
	public void createFriend(String userID1, String userID2){

		try {
			//  statement for query execution
			statement = connection.createStatement();
			// query 
			String query = "INSERT INTO friends (`user_1_id`,`user_2_id`) values('"+ userID1 +"', '" + userID2 + "')";
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
