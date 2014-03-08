import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.foyt.foursquare.api.*;
import fi.foyt.foursquare.api.entities.*;

public class FourSquareTest {

	public void getLocationInformation(String shortURLs) {
		
		String clientID = "JRIYO5DDJ43NBLDNNU3PH1URI0RQ4ZA3TTHEDPFDPYOC2C5L";
		String clientSecret = "QUIFUD44KU22UDMZUD0FQFRPWH21I2H4Y1J3CSWISUWOMZ1R";
		String redirectUrl = "http://www.sheffield.ac.uk";
		String accessToken = "5PJGTZWVP2QR1BK3LJHVLDYPQVFURSTUA1GGN0V3ZI2NBXIT";

		FoursquareApi fsAPI = new FoursquareApi(clientID, clientSecret, redirectUrl);
		fsAPI.setoAuthToken(accessToken);
		String url = expandUrl(shortURLs);

		//if it is not a 4square login url then we return!
		if (!((url.startsWith("https://foursquare.com/"))&&(url.contains("checkin"))&&(url.contains("s=")))) 
			return;
		
		//url now contains the full url!
		Pattern pId = Pattern.compile(".+?checkin/(.+?)\\?s=.+", Pattern.DOTALL);
		Matcher matche = pId.matcher(url);
		String checkInId = (matche.matches()) ? matche.group(1) : "";
		
		Pattern pSig = Pattern.compile(".+?\\?s=(.*)\\&.+", Pattern.DOTALL);
		matche = pSig.matcher(url);
		String sig = (matche.matches()) ? matche.group(1) : "";
		Result<Checkin> chck = null;
//Testing the push with a comment
		try {
			chck = fsAPI.checkin(checkInId, sig);
		} catch (FoursquareApiException e) {
			e.printStackTrace(); 
		}

		Checkin cc = chck.getResult();
		CompactUser user= cc.getUser();
		System.out.print("CHECK IN: " + user.getFirstName() + " " + user.getLastName() + " (" + user.getGender());
		System.out.print(") Just checked in at: ");
		CompactVenue venue= cc.getVenue();
		System.out.print(" " + venue.getName());
		fi.foyt.foursquare.api.entities.Location loc= venue.getLocation();
		System.out.print(" " + loc.getAddress() + " " + loc.getCity());
		System.out.println(" " + loc.getLat() + ", " + loc.getLng());
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
		
		return url;
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
