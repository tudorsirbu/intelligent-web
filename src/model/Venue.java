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
	private ArrayList<User> visitedBy;
	private String location;
	
	public Venue(String id, String name, ArrayList<User> visitedBy,
			String location) {
		super();
		this.id = id;
		this.name = name;
		this.visitedBy = visitedBy;
		this.location = location;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	} 
}
