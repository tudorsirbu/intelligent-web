package servlets.util;
/**
 * The class stores the information collected from a form completion
 * @author tudorsirbu
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
