package servlets.util;
/**
 * The class is used when transforming JSON data received through
 * a post request into Java recognizable data.
 * @author Tudor Sirbu
 * @author Claudiu Tarta
 * @author Cristi Gavrila
 *
 */
public class UserVenuesForm {
	/* Form fields */
	
	// the id of the user
	private String userId;
	
	// the days 
	private int days;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String user_id) {
		this.userId = user_id;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "UserVenuesForm [screenName=" + userId + ", days=" + days + "]";
	}
	

	
	
}
