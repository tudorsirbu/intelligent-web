package servlets.util;
/**
 * The class is used when transforming JSON data received through
 * a post request into Java recognizable data.
 * @author Tudor Sirbu
 * @author Claudiu Tarta
 * @author Cristi Gavrila
 *
 */
public class TrackingForm {

	private String keywords;
	private String regionLat;
	private String regionLong;
	private String radius;
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getRegionLat() {
		return regionLat;
	}
	public void setRegionLat(String regionLat) {
		this.regionLat = regionLat;
	}
	public String getRegionLong() {
		return regionLong;
	}
	public void setRegionLong(String regionLong) {
		this.regionLong = regionLong;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	
	@Override
	public String toString() {
		return "TrackingForm [keywords=" + keywords + ", regionLat="
				+ regionLat + ", regionLong=" + regionLong + ", radius="
				+ radius + "]";
	}
	
	
	
	
}
