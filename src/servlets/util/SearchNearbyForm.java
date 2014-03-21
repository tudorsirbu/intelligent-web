package servlets.util;

public class SearchNearbyForm {
	// option selected in the form
	private String venues_list;
	// radius of location
	private String nearby_radius;

	
	
	public String getVenues_list() {
		return venues_list;
	}



	public void setVenues_list(String venues_list) {
		this.venues_list = venues_list;
	}



	public String getNearby_radius() {
		return nearby_radius;
	}



	public void setNearby_radius(String nearby_radius) {
		this.nearby_radius = nearby_radius;
	}



	@Override
	public String toString() {
		return "SearchNearbyForm [venues_list=" + venues_list
				+ ", nearby_radius=" + nearby_radius + "]";
	}
	
	
	
}
