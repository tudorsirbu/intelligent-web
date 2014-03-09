package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The class handles all the operations on the visits table.
 * @author tudorsirbu
 *
 */
public class VisitedVenueService {
	// the connection to the database
	Connection connection = null;
	Statement statement = null;

	/**
	 * Constructor which connects to the database using the DatabaseConnection class
	 */
	public VisitedVenueService(Connection connection){
		// create a connection to the database
		this.connection = connection;
	}
	/**
	 * The method adds a visit to a venue by a certain user.
	 * @param userID
	 * @param venueID
	 */
	public void addVisit(String userID, String venueID){
		try {
			//  statement for query execution
			statement = connection.createStatement();
			// query 
			String query = "INSERT INTO visits (`user_id`,`venue_id`) values('"+ userID +"','" + venueID + "')";
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
