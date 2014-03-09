package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class handles all the operations on the users table.
 * @author tudorsirbu
 *
 */
public class UserService {
	// the connection to the database
	Connection connection = null;
	Statement statement = null;

	/**
	 * Constructor which connects to the database using the DatabaseConnection class
	 */
	public UserService(Connection connection){
		// create a connection to the database
		this.connection = connection;
	}
	/**
	 * The method inserts a user in the users table in the database
	 * @param user the details for the user to be inserted in the database
	 */
	public void insertUser(User user){
		// user details
		String id = user.getId();
		String name = user.getName();
		String username= user.getUsername();
		String location = user.getLocation();
		String description = user.getDescription();
		String profilePictureURL = user.getProfilePicURL();


		try {
			//  statement for query execution
			statement = connection.createStatement();
			// query 
			String query = "INSERT INTO users (`id`, `name`, `username`, "+
					"`location`, `description`, `profilePictureURL`) values('"+id+"',"
					+ "'"+name+"','"+username+"','"+ location+"','"+ description+"','"+ profilePictureURL+"')";
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
