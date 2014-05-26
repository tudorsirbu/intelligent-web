package model;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * This is a local model where user details can be stored. The class
 * stores the user's id, name, username, location, description, profile picture url, 
 * the venues where the user checked in, any other users they came in contact with and 
 * a list of keyword they used.
 * @author tudorsirbu
 *
 */
public class User {
	
	// details stored for each user when a query has been performed
	private String id;
	private String name;
	private String username;
	private String location;
	private String description;
	private String profilePicURL;
	private ArrayList<Venue> visited;
	private ArrayList<User> inContact;
	private ArrayList<Keyword> keywords;
	private ArrayList<Tweet> tweets;
	
	/**
	 * The method returns the venues visited by the user
	 * @return a list ov Venues where the user has been
	 */
	public ArrayList<Venue> getVisited() {
		return visited;
	}
	/**
	 * The method sets visited venues by this user
	 * @param visited a list of Venues where the use has checked in.
	 */
	public void setVisited(ArrayList<Venue> visited) {
		this.visited = visited;
	}
	
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
			String profilePicURL, ArrayList<User> inContact) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.location = location;
		this.description = description;
		this.profilePicURL = profilePicURL;
		this.inContact = inContact;
		this.keywords = new ArrayList<Keyword>();
	}
	/**
	 * 
	 * @param id user's twitter id
	 * @param name user's name
	 * @param location user's location (if provided)
	 * @param description user's description (if provided)
	 * @param profilePicURL user's profile picture URL
	 * @param keywords a list of keywords associated to this user
	 */
	public User(String id, String name, String username, String location, String description,
			String profilePicURL, ArrayList<User> inContact, ArrayList<Tweet> tweets) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.location = location;
		this.description = description;
		this.profilePicURL = profilePicURL;
		this.inContact = inContact;
		this.tweets = tweets;
		this.keywords = new ArrayList<Keyword>();
	}
	
	/**
	 * 
	 * @param id user's twitter id
	 * @param name user's name
	 * @param location user's location (if provided)
	 * @param description user's description (if provided)
	 * @param profilePicURL user's profile picture URL
	 */
	public User(String id, String name, String username, String location, String description, String profilePicURL) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.location = location;
		this.description = description;
		this.profilePicURL = profilePicURL;
		this.keywords = new ArrayList<Keyword>();
	}
	
	/**
	 * Constructor
	 * @param twitterUser Twitter4j user can be passed
	 */
	public User(twitter4j.User twitterUser) {
		this(String.valueOf(twitterUser.getId()),
			twitterUser.getName(), 
			twitterUser.getScreenName(), 
			twitterUser.getLocation(), 
			twitterUser.getDescription(), 
			twitterUser.getProfileImageURL(), 
			null);
		this.keywords = new ArrayList<Keyword>();
	}
	
	/* 
	 * Getters and setters for each detail stored for the user
	 */
	/**
	 * The method adds a keyword used by the user to 
	 * their list of keywords
	 * @param k a keyword used by this user
	 */
	public void addKeyword(Keyword k){
		keywords.add(k);
	}
	/**
	 * 
	 * @return returns the user's id as string
	 */
	public String getId() {
		return id;
	}
	/**
	 * Sets the user's id	
	 * @param id the user's id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the user's name
	 * @return user's name
	 */
	public String getName() {
		return name;
	}
	/** 
	 * Sets the user's name
	 * @param name the user's name (eg. Christian)
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * The method returns the user's screen name
	 * @return the user's scree name
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * The method changes the user's screen name
	 * @param username the user's screen name
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/** 
	 * The method returns the user's location
	 * @return the user's location which was posted on Twitter (if available)
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * The method sets the user's location, if available
	 * @param location
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * The method returns any description the user might
	 * have added about themselves.
	 * @return the user's description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * The method sets the user's description 
	 * @param description the user's description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * The method returns the URL to the user's profile picture
	 * @return the URL to the user's profile picture
	 */
	public String getProfilePicURL() {
		return profilePicURL;
	}
	/**
	 * The method sets the user's profile picture URL
	 * @param profilePicURL
	 */
	public void setProfilePicURL(String profilePicURL) {
		this.profilePicURL = profilePicURL;
	}

	public ArrayList<User> getInContact() {
		return inContact;
	}

	public void setInContact(ArrayList<User> inContact) {
		this.inContact = inContact;
	}
	/**
	 * Returns the keywords used by the user
	 * @return the list of keywords used by the user of an empty list
	 */
	public ArrayList<Keyword> getKeywords() {
		if(this.keywords != null)
			return keywords;
		else
			return new ArrayList<Keyword>();
	}
	/**
	 * Sets the list of keywords
	 * @param a list of keywords used by the user
	 */
	public void setKeywords(ArrayList<Keyword> keywords) {
		this.keywords = keywords;
	}
	/**
	 * Returns the tweets posted by the user
	 * @return a list of tweets posted by this user
	 */
	public ArrayList<Tweet> getTweets() {
		return tweets;
	}
	/**
	 * The method changes the list of tweets posted by this user
	 * @param tweets a list of tweets that were psoted by this user
	 */
	public void setTweets(ArrayList<Tweet> tweets) {
		this.tweets = tweets;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/**
	 * The method allows this type of object to be 
	 * checked if it is the same as another object like this.
	 */
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
				+ ", profilePicURL=" + profilePicURL + ", friends=" + inContact
				+ ", keywords=" + keywords + ", tweets=" + tweets + "]";
	}	
	
}
