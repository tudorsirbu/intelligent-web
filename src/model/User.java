package model;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
	
	// details stored for each user when a query has been performed
	private String id;
	private String name;
	private String username;
	private String location;
	private String description;
	private String profilePicURL;
	private ArrayList<User> friends;
	private HashMap<String,Integer> keywords;
	
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
		this.keywords = new HashMap<String,Integer>();
	}
	
	public User(String id, String name, String username, String location, String description,
			String profilePicURL, ArrayList<User> friends, HashMap<String,Integer> keywords) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.location = location;
		this.description = description;
		this.profilePicURL = profilePicURL;
		this.friends = friends;
		this.keywords = keywords;
	}
	
	public User(twitter4j.User twitterUser) {
		this(String.valueOf(twitterUser.getId()),
			twitterUser.getName(), 
			twitterUser.getScreenName(), 
			twitterUser.getLocation(), 
			twitterUser.getDescription(), 
			twitterUser.getProfileImageURL(), 
			null);
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

	public HashMap<String, Integer> getKeywords() {
		return keywords;
	}

	public void setKeywords(HashMap<String, Integer> keywords) {
		this.keywords = keywords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", username=" + username
				+ ", location=" + location + ", description=" + description
				+ ", profilePicURL=" + profilePicURL + ", friends=" + friends
				+ "]";
	}	
	
}
