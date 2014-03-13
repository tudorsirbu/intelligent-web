package servlets.util;
import twitter4j.*;
public class MiniStatus {
	private String profileImageUrl;
	private String name;
	private String screenName;
	private String text;
	private String id;
	private String retweetCount;
	
	public MiniStatus(Status status){
		this.id = String.valueOf((status.getId()));
		
		// get the user
		User u = status.getUser();
		
		this.profileImageUrl = u.getProfileImageURL();
		this.name = u.getName();
		this.screenName = u.getScreenName();
		if(status.isRetweet()) {
			this.text = "RT " + status.getRetweetedStatus().getText();
		}
		else
			this.text = status.getText();
		if(!status.isRetweet())
			this.retweetCount = String.valueOf(status.getRetweetCount());
		else 
			this.retweetCount = "0";
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


	
}
