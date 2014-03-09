package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class handles all the operations on the venues table.
 * @author tudorsirbu
 *
 */
public class VenueService {
	// the connection to the database
	Connection connection = null;
	Statement statement = null;
	
	/**
	 * Constructor which connects to the database using the DatabaseConnection class
	 */
	public VenueService(Connection connection){
		// create a connection to the database
		this.connection = connection;
	}
	/**
	 * The method inserts a user in the users table in the database
	 * @param user the details for the user to be inserted in the database
	 */
	public void insertVenue(Venue v){
		// venue details
		String name = v.getVenueName();
	
		try {
            //  statement for query execution
            statement = connection.createStatement();
            // query 
            String query = "INSERT INTO users (``name`) values('"+ name +"')";
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
