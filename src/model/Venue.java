package model;
/**
 * The class represents a venue object
 * @author tudorsirbu
 *
 */
public class Venue {
	// Venue details
	private String id;
	private String venueName;
	
	/**
	 * Constructor for the venue object
	 * @param id the id of the venue
	 * @param venueName the venue name (eg. Bar One)
	 */
	public Venue(String venueName) {
		this.id = null;
		this.venueName = venueName;
	}
	
	/* Getters and setters for the venue's details */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVenueName() {
		return venueName;
	}

	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}
	
	
	
	
	
}
