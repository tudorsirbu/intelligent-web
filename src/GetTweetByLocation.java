import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class GetTweetByLocation {	
	
	public String getSimpleTimeLine(Twitter twitter){	
		String resultString= "";	
		FourSquareTest fs = new FourSquareTest();
		Pattern p = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		
		try {	
			// it creates a query and sets the geocode	
			//requirement	
			Query query= new Query("#foursquare");	
			query.setCount(100);
//			query.setGeoCode(new GeoLocation(53.383, -1.483), 2, Query.KILOMETERS);	

			//it fires the query	
			QueryResult result = twitter.search(query);	
			//it cycles on the tweets	
			List<Status> tweets = result.getTweets();

			for (Status tweet:tweets) {
				///gets the user		
								
				User user = tweet.getUser();
				
				if (tweet.getText().toLowerCase().contains("foursquare") == true) {
					Matcher m = p.matcher(tweet.getText());
					
					while (m.find()) {
						System.out.println(m.group());
						fs.getLocationInformation(m.group());
					}					
				}
				
				Status status= (user.isGeoEnabled())? user.getStatus() : null;
				if (status==null) 	
					resultString+="@" + tweet.getText() + " (" + user.getLocation() + ") \n";	
				else 
					resultString+="@" + tweet.getText() + " (" + ((status!=null&&status.getGeoLocation()!=null) ? status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude():user.getLocation()) + ") \n";	

			}	
		} 
		catch (Exception te) {	
			te.printStackTrace(); 	
			System.out.println("Failed to search tweets:" +
					te.getMessage());	
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

	private Twitter initTwitter(String consumerKey, String consumerSecret, 	
			String accessToken, String accessTokenSecret) 	
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

	public static void main(String[] args) {	
		GetTweetByLocation tt = new GetTweetByLocation();	
		Twitter twitterConnection = null;	
		try {	
			twitterConnection= tt.init();		 	 	 	
			System.out.print(tt.getSimpleTimeLine(twitterConnection));	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter");	
			e.printStackTrace();	
		}	
	}
}