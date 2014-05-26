package model;

import java.util.ArrayList;

/**
 * The class represents a venue object
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
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	private String URL;
	private String latitude;
	private String longitude;
	
	public Venue(String id, String name, ArrayList<User> visitedBy,	String location) {
		super();
		this.id = id;
		this.name = name;
		this.visitedBy = visitedBy;
	}

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
