package api;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {	
	
	public String query(String keywords, Integer latitude, Integer longitude, Integer radius) {	
		
		String[] keywordsArray = keywords.split(",");
		
		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}
		
		String resultString = "";	
		FoursquareManager fs = new FoursquareManager();
		Pattern urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		
		try {
			for(String keyword:keywordsArray) {
				Query query = new Query(keyword.trim());	
				query.setCount(10);
				if(longitude != null && latitude != null) {
					if(radius == null)
						radius = 3;
					
					query.setGeoCode(new GeoLocation(latitude, longitude), radius, Query.KILOMETERS);	
				}

				//it fires the query	
				QueryResult result = twitterConnection.search(query);	
				//it cycles on the tweets	
				List<Status> tweets = result.getTweets();

				for (Status tweet:tweets) {
					// Gets the user 		
					User user = tweet.getUser();

					/* Only access foursquare if the user mentions it in his tweet. */
					if (tweet.getText().toLowerCase().contains("foursquare") == true) {
						Matcher m = urlPattern.matcher(tweet.getText());

						while (m.find()) {
							System.out.println(m.group());
							fs.getLocationInformation(m.group());
						}					
					}

					/* Display the tweets. */
					Status status = user.isGeoEnabled() ? user.getStatus() : null;
					resultString+="@" + user.getName() + " - " + tweet.getText();
					if (status==null) {
						resultString += " (" + user.getLocation() + ") \n";						
					} 	
					else {
						String coordinates = status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude();
						resultString += " (" + (status!=null && status.getGeoLocation() != null ? coordinates : user.getLocation()) + ") \n";						
					}
				}
			}	
		} 
		catch (Exception e) {	
			e.printStackTrace(); 	
			System.out.println("Failed to search tweets:" + e.getMessage());	
			System.exit(-1);	
		}	
		
		return resultString;	
	}

	private Twitter init() throws Exception{	
		String consumerkey = "H4VHRaf8ybmPhzzK47uQ";	
		String consumersecret = "y6oxNsvuoauf4sPcGU45Ct5eVfryYlai5TUBU92Uxbk";	
		String accesstoken = "1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra";	
		String accesstokensecret = "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS";	
		
		Twitter twitterConnection = initTwitter(consumerkey, 	
				consumersecret, accesstoken, accesstokensecret);	
		
		return twitterConnection;	
	}

	private Twitter initTwitter(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) 	
					throws Exception {	
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)	
		.setOAuthConsumerKey(consumerKey)	
		.setOAuthConsumerSecret(consumerSecret)	
		.setOAuthAccessToken(accessToken)	
		.setOAuthAccessTokenSecret(accessTokenSecret)	
		.setJSONStoreEnabled(true);	
		return (new TwitterFactory(cb.build()).getInstance());	
	}
	
}