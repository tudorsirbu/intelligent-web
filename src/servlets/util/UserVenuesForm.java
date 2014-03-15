package servlets.util;
/**
 * The class stores the information collected from a form completion
 * @author tudorsirbu
 *
 */
public class UserVenuesForm {
	/* Form fields */
	
	// the id of the user
	private long userId;
	
	// the days 
	private int days;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "UserVenuesForm [userId=" + userId + ", days=" + days + "]";
	}
	

	
	
}
