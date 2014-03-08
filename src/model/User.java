package model;

import java.util.ArrayList;

public class User {
	
	// details stored for each user when a query has been performed
	private String id;
	private String name;
	private String username;
	private String location;
	private String description;
	private String profilePicURL;
	private ArrayList<User> friends;
	
	/**
	 * Constructor
	 * @param id user's twitter id
	 * @param name user's name
	 * @param location user's location (if provided)
	 * @param description user's description (if provided)
	 * @param profilePicURL user's profile picture URL
	 * @param friends a list of people the user has been in contact with
	 */
	public User(String id, String name, String username, String location, String description,
			String profilePicURL, ArrayList<User> friends) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.location = location;
		this.description = description;
		this.profilePicURL = profilePicURL;
		this.friends = friends;
	}
	
	/* 
	 * Getters and setters for each detail stored for the user
	 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProfilePicURL() {
		return profilePicURL;
	}

	public void setProfilePicURL(String profilePicURL) {
		this.profilePicURL = profilePicURL;
	}

	public ArrayList<User> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<User> friends) {
		this.friends = friends;
	}	
}
