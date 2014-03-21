package api;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.User;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.Checkin;
import fi.foyt.foursquare.api.entities.CompactUser;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteTip;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.Location;
import fi.foyt.foursquare.api.entities.VenueGroup;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class FoursquareManager {

	private FoursquareApi init() {

		String clientID = "JRIYO5DDJ43NBLDNNU3PH1URI0RQ4ZA3TTHEDPFDPYOC2C5L";
		String clientSecret = "QUIFUD44KU22UDMZUD0FQFRPWH21I2H4Y1J3CSWISUWOMZ1R";
		String redirectUrl = "http://www.sheffield.ac.uk";
		String accessToken = "5PJGTZWVP2QR1BK3LJHVLDYPQVFURSTUA1GGN0V3ZI2NBXIT";

		FoursquareApi fsAPI = new FoursquareApi(clientID, clientSecret, redirectUrl);
		fsAPI.setoAuthToken(accessToken);

		return fsAPI;
	}

	public void getLocationInformation(String shortURLs) {

		FoursquareApi fsAPI = this.init();
		String url = expandUrl(shortURLs);
		
		//if it is not a 4square login url then we return!
		if (url.startsWith("https://foursquare.com/") && url.contains("checkin") && url.contains("s=")) {
			Pattern pId = Pattern.compile(".+?checkin/(.+?)\\?s=.+", Pattern.DOTALL);
			Matcher matcher = pId.matcher(url);
			String checkInId = (matcher.matches()) ? matcher.group(1) : "";

			Pattern pSig = Pattern.compile(".+?\\?s=(.*)\\&.+", Pattern.DOTALL);
			matcher = pSig.matcher(url);
			String sig = (matcher.matches()) ? matcher.group(1) : "";
			Result<Checkin> checkin = null;

			try {
				checkin = fsAPI.checkin(checkInId, sig);
			} catch (FoursquareApiException e) {
				e.printStackTrace(); 
			}

			Checkin cc = checkin.getResult();
			CompactUser user= cc.getUser();
			CompactVenue venue= cc.getVenue();
			Location loc= venue.getLocation();

			System.out.print("CHECK IN: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getGender());
			System.out.print(") Just checked in at: ");
			System.out.print(" " + venue.getName());
			System.out.print(" " + loc.getAddress() + " " + loc.getCity());
			System.out.println(" " + loc.getLat() + ", " + loc.getLng());
		}
		else if (url.startsWith("https://foursquare.com/item/")) {

			/* Getting the id of the tip. */
			String tipId = null;
			tipId = url.replaceFirst("https://foursquare.com/item/", "");
			int index = tipId.indexOf("?");
			if (index != -1)
				tipId = tipId.substring(0, index);

			/* Attempting to get the tip. */
			Result<CompleteTip> tip = null;
			try {
				tip = fsAPI.tip(tipId);
			} catch (FoursquareApiException e) {
				e.printStackTrace(); 
			}

			/*
			 * https://developer.foursquare.com/docs/responses/tip 
			 * might be a good idea to only get a CompactTip 
			 */
			System.out.println(tip.getResult().getText());

		}
	}
	
	public CompactVenue[] queryByLocation(String locationLat, String locationLong) {
		/* 
		 * 
		 * https://developer.foursquare.com/docs/checkins/recent
		 * 
		 */

		FoursquareApi fs = this.init();
		CompactVenue[] venues = null;
		
		try {
			
			Map<String, String> searchParameters = new HashMap<String, String>();
			searchParameters.put("ll", ""+ locationLat + ","+ locationLong +"");
			searchParameters.put("intent", "checkin");
			searchParameters.put("limit", "10");
			
			Result<VenuesSearchResult> result = fs.venuesSearch(searchParameters);
			if (result.getMeta().getCode() == 200) {
				VenuesSearchResult venuesResult = result.getResult();
				System.out.println("INTRU AICI!");
				for (CompactVenue venue : venuesResult.getVenues()) {
					System.out.println(venue.getName());
				}
				venues = venuesResult.getVenues();
			}
		} catch (FoursquareApiException e) {
			e.printStackTrace();
		}
		
		return venues;
	}

	public CompactVenue[] getVenues(String location) {
		/* 
		 * 
		 * https://developer.foursquare.com/docs/venues/search 
		 * 
		 */
		FoursquareApi fs = this.init();

		try {
			String[] splittedLocation = location.split(",");

			Map<String, String> searchParams = new HashMap<String, String>();
			searchParams.put("query", splittedLocation[0].trim());
			searchParams.put("near", splittedLocation[1].trim());	

			Result<VenuesSearchResult> result = fs.venuesSearch(searchParams);
			System.out.println(result.getMeta().getCode());
			if (result.getMeta().getCode() == 200) {
				return result.getResult().getVenues();
			}

		} catch (FoursquareApiException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * The method searches for a venues given some search parameters
	 * @param searchParams https://developer.foursquare.com/docs/venues/search
	 * @return List of venues that match the query
	 */
	public CompactVenue[] getVenuesList(Map<String, String> searchParams) {
		/* 
		 * 
		 * https://developer.foursquare.com/docs/venues/search 
		 * 
		 */
		FoursquareApi fs = this.init();

		try {
			Result<VenuesSearchResult> result = fs.venuesSearch(searchParams);
			if (result.getMeta().getCode() == 200) {
				return result.getResult().getVenues();
			}

		} catch (FoursquareApiException e) {
			e.printStackTrace();
		}

		return null;
	}
	

	public CompleteVenue getVenueName(String shortURLs){

		FoursquareApi fsAPI = this.init();
		String url = expandUrl(shortURLs);
		CompleteVenue venue = null;

		//if it is not a 4square login url then we return!
		if (url.startsWith("https://foursquare.com/") && url.contains("checkin") && url.contains("s=")) {
			Pattern pId = Pattern.compile(".+?checkin/(.+?)\\?s=.+", Pattern.DOTALL);
			Matcher matcher = pId.matcher(url);
			String checkInId = (matcher.matches()) ? matcher.group(1) : "";

			Pattern pSig = Pattern.compile(".+?\\?s=(.*)\\&.+", Pattern.DOTALL);
			matcher = pSig.matcher(url);
			String sig = (matcher.matches()) ? matcher.group(1) : "";
			Result<Checkin> checkin = null;

			try {
				checkin = fsAPI.checkin(checkInId, sig);
				Checkin cc = checkin.getResult();
				venue = fsAPI.venue(cc.getVenue().getId()).getResult();
			} catch (FoursquareApiException e) {
				e.printStackTrace(); 
			}

			Checkin cc = checkin.getResult();
			venue = (CompleteVenue) cc.getVenue();

			try {
				venue =fsAPI.venue(cc.getVenue().getId()).getResult();
			} catch (FoursquareApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if (url.startsWith("https://foursquare.com/item/")) {

			/* Getting the id of the tip. */
			String tipId = null;
			tipId = url.replaceFirst("https://foursquare.com/item/", "");
			int index = tipId.indexOf("?");
			if (index != -1)
				tipId = tipId.substring(0, index);

			/* Attempting to get the tip. */
			Result<CompleteTip> tip = null;
			try {
				tip = fsAPI.tip(tipId);
			} catch (FoursquareApiException e) {
				e.printStackTrace(); 
			}

			/*
			 * https://developer.foursquare.com/docs/responses/tip 
			 * might be a good idea to only get a CompactTip 
			 */
			System.out.println(tip.getResult().getText());

		}
		return venue;

	}

	private String expandUrl(String shortURLs) {
		String url = shortURLs;
		String initialUrl = shortURLs;
		while (url!=null){
			try {
				url = getFullURL(shortURLs);
				if (url!=null) 
					shortURLs= url;
				else {
					url= shortURLs;
					break;
				}
			} catch (IOException e) {
				// this is not a tiny URL as it is not redirected!
				break;
			}
		}

		return shortURLs;
	}

	private String getFullURL (String shortURLs) throws IOException {
		URL shortUrl= new URL(shortURLs);
		final HttpURLConnection httpURLConnection = (HttpURLConnection)shortUrl.openConnection();
		httpURLConnection.setInstanceFollowRedirects(false);
		httpURLConnection.connect();
		final int responseCode = httpURLConnection.getResponseCode();
		final String header = httpURLConnection.getHeaderField("Location");

		return header;
	}

}
