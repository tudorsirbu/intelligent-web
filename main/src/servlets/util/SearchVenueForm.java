package servlets.util;
/**
 * The class is used when transforming JSON data received through
 * a post request into Java recognizable data.
 * @author Tudor Sirbu
 * @author Claudiu Tarta
 * @author Cristi Gavrila
 *
 */
public class SearchVenueForm {
	
	// venue name
	private String venue_name;
	
	// venue 
	private String venue_city;

	public String getVenue_name() {
		return venue_name;
	}

	public void setVenue_name(String venue_name) {
		this.venue_name = venue_name;
	}

	public String getVenue_city() {
		return venue_city;
	}

	public void setVenue_city(String venue_city) {
		this.venue_city = venue_city;
	}

	@Override
	public String toString() {
		return "SearchVenueForm [venue_name=" + venue_name + ", venue_city="
				+ venue_city + "]";
	}

	

}
