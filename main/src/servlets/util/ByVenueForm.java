package servlets.util;
/**
 * The class is used when transforming JSON data received through
 * a post request into Java recognizable data.
 * @author Tudor Sirbu
 * @author Claudiu Tarta
 * @author Cristi Gavrila
 *
 */
public class ByVenueForm {
	
	private String locationName;
	private String locationLat;
	private String locationLong;
	private String days;
	
	
	
	public String getLocationName() {
		return locationName;
	}



	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}



	public String getLocationLat() {
		return locationLat;
	}



	public void setLocationLat(String locationLat) {
		this.locationLat = locationLat;
	}



	public String getLocationLong() {
		return locationLong;
	}



	public void setLocationLong(String locationLong) {
		this.locationLong = locationLong;
	}



	public String getDays() {
		return days;
	}



	public void setDays(String days) {
		this.days = days;
	}



	@Override
	public String toString() {
		return "ByVenueForm [location_name=" + locationName
				+ ", location_lat=" + locationLat + ", location_long="
				+ locationLong + ", days=" + days + "]";
	}
	
}
