package api;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {	
	
	public List<Status> query(String keywords, String latitude, String longitude, String radius) {	
		
		Integer latitudeNumber, longitudeNumber, radiusNumber;
		try {
			longitudeNumber = Integer.parseInt(longitude);
			latitudeNumber = Integer.parseInt(latitude);
			radiusNumber = Integer.parseInt(radius);
		} catch (NumberFormatException e) {
			radiusNumber = null;
			latitudeNumber = null;
			longitudeNumber = null;
		}
		
		String[] keywordsArray = keywords.split(",");
		
		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}
		
		List<Status> tweets = new ArrayList<Status>();
		
		try {
			for(String keyword:keywordsArray) {
				Query query = new Query(keyword.trim());	
				query.setCount(10);
				if(longitudeNumber != null && latitudeNumber != null) {
					if(radiusNumber == null)
						radiusNumber = 3;
					
					query.setGeoCode(new GeoLocation(latitudeNumber, longitudeNumber), radiusNumber, Query.KILOMETERS);	
				}

				//it fires the query	
				QueryResult result = twitterConnection.search(query);	
				//it cycles on the tweets	
				tweets.addAll(result.getTweets());	
			}	
		} 
		catch (Exception e) {	
			e.printStackTrace(); 	
			System.out.println("Failed to search tweets:" + e.getMessage());	
			System.exit(-1);	
		}	
		
		return tweets;	
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