package servlets.util;
/**
 * The class stores the information collected from a form completion
 * @author tudorsirbu
 *
 */
public class UserTrackerForm {
	/* Form fields */
	
	// the ids of the users
	private String screenNames;
	
	// number of most frequent keywords
	private int keywords;
	
	// number of days
	private int daysSince;

	public String getScreenNames() {
		return screenNames;
	}

	public void setScreenNames(String screenNames) {
		this.screenNames = screenNames;
	}

	public int getKeywords() {
		return keywords;
	}

	public void setKeywords(int keywords) {
		this.keywords = keywords;
	}

	public int getDaysSince() {
		return daysSince;
	}

	public void setDaysSince(int daysSince) {
		this.daysSince = daysSince;
	}
	
	
	
}
