package model;

import java.util.ArrayList;

/**
 * The class represents a venue object by storing its
 * id, name, description, address, photo, a list of users
 * that visited it, its category, url and lat&long.
 * @author tudorsirbu
 *
 */
public class Venue {	
	// the properties of a venue
	private String id;
	private String name;
	private String description;
	private String address;
	private String photo;
	private ArrayList<User> visitedBy;
	private String category;
	/**
	 * Returns the latitude of this venue's location
	 * @return the lat of this venue
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * Sets the venue's latitude
	 * @param latitude String
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * Returns the longitude of this method
	 * @return String
	 */
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	private String URL;
	private String latitude;
	private String longitude;
	/**
	 * Constructor
	 * @param id the id of the venue
	 * @param name the name of the venue
	 * @param visitedBy a list of users who visited this venue
	 * @param location the location of this venue
	 */
	public Venue(String id, String name, ArrayList<User> visitedBy,	String location) {
		super();
		this.id = id;
		this.name = name;
		this.visitedBy = visitedBy;
	}
	/**
	 * Construct
	 * @param id the id of the venue
	 * @param name venue's name
	 * @param description venue's description
	 * @param address venues' address
	 * @param photo venue's photo
	 * @param URL venue's url
	 * @param category venue's category
	 */
	public Venue(String id, String name, String description, String address,
			String photo, String URL, String category) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.address = address;
		this.photo = photo;
		this.category = category;
		this.URL = URL;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<User> getVisitedBy() {
		return visitedBy;
	}

	public void setVisitedBy(ArrayList<User> visitedBy) {
		this.visitedBy = visitedBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	} 
}
