package servlets;
/**
 * The class stores the information collected from a form completion
 * @author tudorsirbu
 *
 */
public class UserVenuesForm {
	/* Form fields */
	
	// the id of the user
	private long id;
	
	// the days 
	private int days;
	
	public UserVenuesForm(Long userID, int days){
		this.id = userID;
		this.days = days;
		
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}
	
	
	
}
