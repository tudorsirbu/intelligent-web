package model;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;



/**
 * The class handles the necessary operations on the database.
 * @author tudorsirbu
 *
 */

public class DatabaseConnection {
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	// database connection details
	private final String HOST = "localhost";
	private final String USERNAME = "root";
	private final String PASSWORD = "mysql";
	private final String DATABASE ="intelwebdb";
	
	
	/**
	 * Create a connection to the database
	 */
	public DatabaseConnection(){
		String driver = "com.mysql.jdbc.Driver";
		 
		 try {
			 Class.forName(driver).newInstance();
			 connection = DriverManager.getConnection("jdbc:mysql://" + HOST +"/"
					 + DATABASE + "?user=" + USERNAME + "&password="+ PASSWORD);
		 } catch (Exception e) {
			 System.out.println("The system could not connect to the database.");
			 e.printStackTrace();
		 }
	}
	/**
	 * The method returns the connection to the database
	 * @return the connection to the database
	 */
	public Connection getConnection(){
		return connection;
	}
	/**
	 * The method terminates the connection to the database.
	 */
	public void disconnect(){
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("The connection to the database could not be terminated.");
			e.printStackTrace();
		}		
	}
}
