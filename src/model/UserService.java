package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	public User getUser(String id){
		try {
			String query = "SELECT * FROM users  WHERE `id`='?'";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, id);
			ResultSet results = preparedStatement.executeQuery(query);
			while(results.next()){
				String name = results.getString("name");
				String username = results.getString("username");
				String location = results.getString("location");
				String description = results.getString("description");
				String profilePicURL = results.getString("profilePictureURL");
				return new User(id, name, username, location, description, profilePicURL, null);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
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
			this.statement = this.connection.createStatement();
			// query 
			String query = "INSERT INTO users (`id`, `name`, `username`, "+
					"`location`, `description`, `profilePictureURL`) values('"+id+"',"
					+ "'"+name+"','"+username+"','"+ location+"','"+ description+"','"+ profilePictureURL+"')";
			
			System.out.println(query);
			// Updating Table
			this.statement.executeUpdate(query);          
		}
		catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			try {
				this.statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The method inserts a user in the users table in the database
	 * @param user the details for the user to be inserted in the database
	 */
	public void insertUser(twitter4j.User user){
		// user details
		String id = String.valueOf(user.getId());
		String name = user.getName();
		String username= user.getScreenName();
		String location = user.getLocation();
		String description = user.getDescription();
		String profilePictureURL = user.getProfileImageURL();


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
