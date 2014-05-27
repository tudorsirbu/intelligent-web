package servlets.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;
/**
 * The class stores only some of the details provided
 * by a Twitter 4j status. This allows statuses to be 
 * easily returned from the storage. 
 * @author tudorsirbu
 *
 */
public class MiniStatus {
	private String profileImageUrl;
	private String name;
	private String screenName;
	private String text;
	private String id;
	private long user_id;
	private User user;
	private String retweetCount;
	private List<String> extendedUrls;
	private Date createdAt; 
	
	/**
	 * Returns the user who posted this tweet
	 * @return
	 */
	public User getUser() {
		return user;
	}
	/** 
	 * Sets the user who posted this tweet
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Constructor
	 * @param status takes a Twitter4j Status
	 */
	public MiniStatus(Status status){
		this.id = String.valueOf((status.getId()));
		
		// get the user
		User u = status.getUser();
		
		this.profileImageUrl = u.getProfileImageURL();
		this.name = u.getName();
		this.screenName = u.getScreenName();
		this.user_id = u.getId();
		this.user = status.getUser();
		this.extendedUrls = new ArrayList<String>();
		if(status.isRetweet()) {
			this.text = "RT " + status.getRetweetedStatus().getText();
		}
		else
			this.text = status.getText();
		if(!status.isRetweet())
			this.retweetCount = String.valueOf(status.getRetweetCount());
		else 
			this.retweetCount = "0";
		
		this.createdAt = status.getCreatedAt();
	}
	/**
	 * Constructor	
	 * @param status the Twitter4j Status
	 * @param extendedUrls the urls found in this status
	 */
	public MiniStatus(Status status, List<String> extendedUrls) {
		this(status);
		this.extendedUrls = extendedUrls;
	}
	/**
	 * The method returns the user id of the person who posted
	 * this status
	 * @return
	 */
	public long getUser_id() {
		return user_id;
	}
	/** 
	 * The method sets the user id of the person who posted this
	 * tweet
	 * @param user_id long
	 */
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	/**
	 * Returns the profile image URL of the user who 
	 * posted this status
	 * @return
	 */
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	/**
	 * Changes the profile pic URL of the person who posted
	 * this status
	 * @param profileImageUrl
	 */
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	/**
	 * Returns the name of the person who posted this status
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of the person who posted this status
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the screen name of the person who 
	 * posted this tweet
	 * @return
	 */
	public String getScreenName() {
		return screenName;
	}
	/**
	 * Sets the screen name of tweet's poster
	 * @param screenName
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	/**
	 * Returns the text of the tweet
	 * @return
	 */
	public String getText() {
		return text;
	}
	/**
	 * Sets the text of the tweet
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Returns the id of the tweet
	 * @return
	 */
	public String getId() {
		return id;
	}
	/**
	 * Sets the id of the status 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Returns how many times the status
	 * has been retweeted
	 * @return
	 */
	public String getRetweetCount() {
		return retweetCount;
	}
	/**
	 * Sets how many times the status has been retweeted
	 * @param retweetCount
	 */
	public void setRetweetCount(String retweetCount) {
		this.retweetCount = retweetCount;
	}

	@Override
	public String toString() {
		return "MiniStatus [profileImageUrl=" + profileImageUrl + ", name="
				+ name + ", screenName=" + screenName + ", text=" + text
				+ ", id=" + id + ", retweetCount=" + retweetCount + "]";
	}

	/**
	 * Retuns a list of URL which have been extended from short 
	 * URL to normal URLs
	 * @return
	 */
	public List<String> getExtendedUrls() {
		return extendedUrls;
	}
	/**
	 * Sets the list of URLs that are in this tweet after they've been
	 * extended from short url to normal urls
	 * @param extendedUrls
	 */
	public void setExtendedUrls(List<String> extendedUrls) {
		this.extendedUrls = extendedUrls;
	}


	
}
