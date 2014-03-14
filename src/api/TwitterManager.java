package api;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.foyt.foursquare.api.entities.CompactVenue;
import model.DatabaseConnection;
import model.InvertedIndexService;
import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterManager {	
	private TwitterStream twitterStream;
	public List<Status> query(String keywords, String latitude, String longitude, String radius) {	
		
		Integer radiusNumber;
		Double latitudeNumber, longitudeNumber;
		try {
			longitudeNumber = Double.parseDouble(longitude);
			latitudeNumber = Double.parseDouble(latitude);
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
				query.setCount(100);
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

	public HashSet<User> queryByLocation(String locationName, String locationLat, String locationLong, String days) {	

		Integer daysNumber;
		Double latitudeNumber, longitudeNumber; 
		try {
			longitudeNumber = Double.parseDouble(locationLong);
			latitudeNumber = Double.parseDouble(locationLat);
			daysNumber = Integer.parseInt(days);
		} catch (NumberFormatException e) {
			daysNumber = null;
			latitudeNumber = null;
			longitudeNumber = null;
		}

		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}

		List<Status> tweets = new ArrayList<Status>();
		HashSet<User> users = new HashSet<>();
		
		try { 
			if (latitudeNumber != null && longitudeNumber != null) {
				Query query = new Query();	
				query.setCount(100);
				query.setGeoCode(new GeoLocation(latitudeNumber, longitudeNumber), 3, Query.KILOMETERS);				
				
				if(daysNumber > 0) {
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.DAY_OF_MONTH, -daysNumber);
					SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
					query.setSince(format.format(calendar.getTime()));
				}
				
				//it fires the query	
				QueryResult result = twitterConnection.search(query);	
				//it cycles on the tweets	
				tweets.addAll(result.getTweets());	
				
				System.out.println(tweets.size());
				
				for (Status tweet:tweets) {
					users.add(tweet.getUser());
				}
			}
		} 
		catch (Exception e) {	
			e.printStackTrace(); 	
			System.out.println("Failed to search tweets:" + e.getMessage());	
			System.exit(-1);	
		}	

		return users;	
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


	public List<Status> retweetsForStatus(String statusId) {

		long statusIdNumber = Long.parseLong(statusId);
		List<Status> retweets = null;

		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	

			System.out.println("################## aici" +  statusIdNumber);
			retweets = twitterConnection.getRetweets(statusIdNumber);

		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();
		} 

		System.out.println(retweets);

		return retweets;
	}
	/**
	 * The method gets the venues the give user has visited in the last given days
	 * @param userID the id of the user to get the venues for
	 * @param days the number of days to go back
	 * @param twitterC the connection to the twitter api
	 * @param statuses the statuses of the user
	 * @venues the string containg all the venues the user has visited
	 */
	public String getVenues(Long userID, Integer days){
		String venues=null;

		/* Connect to twitter. */
		Twitter twitterC = null;	
		try {	
			twitterC = this.init();		 	 	 		
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();
		} 

		// list of statuses
		ResponseList<Status> statuses = null;

		// get the user's timeline
		try {
			statuses = twitterC.getUserTimeline(userID, new Paging(1,100));
		} catch (TwitterException e) {
			System.out.println("Could not retrieve user's (" + userID + ") timeline.");
			e.printStackTrace();
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -days);
		Date daysAgo = new Date(c.getTimeInMillis());
		List<Status> sts = new ArrayList<Status>();
		//Sort the statusses according to how old they need to be #days
		for(Status status : statuses){
			//Create a list with only the statuses from #days ago
			Date statusDate = new Date();
			statusDate = status.getCreatedAt();
			if(statusDate.compareTo(daysAgo)>=0){
				sts.add(status);
			}

		}
		
		FoursquareManager fm = new FoursquareManager();
		for(Status status : sts){
			ArrayList<String> urls = new ArrayList<String>();
			urls = extractURL(status);
			for(String url : urls){
				
				String name = fm.getVenueName(url);
				if(name!=null)
					venues+=name+"<br />";
			}	
		}
		return venues;
	}
	/**
	 * The method initialises the connection to twitter stream api
	 * @param userID the id of the user to listen for
	 * @param cb the configuration builder
	 * @param fq the filter query that will enable us to listen for the given users
	 */
	public void initConfiguration(long[] userID){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setJSONStoreEnabled(true)
		.setOAuthConsumerKey("H4VHRaf8ybmPhzzK47uQ")
		.setOAuthConsumerSecret("y6oxNsvuoauf4sPcGU45Ct5eVfryYlai5TUBU92Uxbk");

		TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
		twitterStream = twitterStreamFactory.getInstance(new AccessToken("1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra", "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS"));
		twitterStream.addListener(userStreamListener);

		FilterQuery fq = new FilterQuery();

		fq.follow(userID);
		twitterStream.filter(fq);
	}
	
	UserStreamListener userStreamListener = new UserStreamListener() {

		@Override
		public void onException(Exception arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTrackLimitationNotice(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatus(Status status) {
			// TODO Auto-generated method stub
			FoursquareManager fm = new FoursquareManager();
			TwitterManager tm = new TwitterManager();
			ArrayList<String> urls = new ArrayList<String>();
			urls = tm.extractURL(status);
			for(String url : urls){
				String name = fm.getVenueName(url);
				if(name!=null)
					System.out.println(name);
			}

			
		}


		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserProfileUpdate(User arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListUpdate(User arg0, UserList arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListDeletion(User arg0, UserList arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListCreation(User arg0, UserList arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUnfavorite(User arg0, User arg1, Status arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUnblock(User arg0, User arg1) {
			// TODO Auto-generated method stub

		}



		@Override
		public void onFriendList(long[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFollow(User arg0, User arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFavorite(User arg0, User arg1, Status arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDirectMessage(DirectMessage message) {
			// TODO Auto-generated method stub
			System.out.println(message.getText());
		}

		@Override
		public void onDeletionNotice(long arg0, long arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBlock(User arg0, User arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO Auto-generated method stub
			
		}

		
	};

	/**
	 * The method extracts all urls from a Status object
	 * @param status the status to extract the urls from
	 * @param links the list all the found links will be put in
	 * @param text the text of the given status
	 * @param p the compiled pattern that identifies a url
	 * @param m the matcher that matches the text agains the pattern
	 * @param urlStr the string that contains the found url to be put in the list
	 */
	public ArrayList<String> extractURL(Status status) {
		ArrayList<String> links = new ArrayList<String>();
		String text = status.getText();
		
		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		while(m.find()) {
			String urlStr = m.group();
			if (urlStr.startsWith("(") && urlStr.endsWith(")"))
			{
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			}
			links.add(urlStr);
		}
		return links;
	}
}