package servlets.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;
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
	
	public User getUser() {
		return user;
	}


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
	
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(String retweetCount) {
		this.retweetCount = retweetCount;
	}

	@Override
	public String toString() {
		return "MiniStatus [profileImageUrl=" + profileImageUrl + ", name="
				+ name + ", screenName=" + screenName + ", text=" + text
				+ ", id=" + id + ", retweetCount=" + retweetCount + "]";
	}


	public List<String> getExtendedUrls() {
		return extendedUrls;
	}


	public void setExtendedUrls(List<String> extendedUrls) {
		this.extendedUrls = extendedUrls;
	}


	
}
