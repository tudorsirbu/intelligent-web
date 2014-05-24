package servlets.util;
/**
 * The class stores the information collected from a form completion
 * @author tudorsirbu
 *
 */
public class UserVenuesForm {
	/* Form fields */
	
	// the id of the user
	private long screenName;
	
	// the days 
	private int days;

	

	public long getScreenName() {
		return screenName;
	}

	public void setScreenName(long screenName) {
		this.screenName = screenName;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "UserVenuesForm [screenName=" + screenName + ", days=" + days + "]";
	}
	

	
	
}
