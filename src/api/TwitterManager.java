package api;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import util.ApiUtil;
import api.listeners.TwitterStatusListener;
import api.listeners.TwitterUserListener;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;

public class TwitterManager {	
	private ArrayList<CompleteVenue> venues = new ArrayList<CompleteVenue>();
	private ArrayList<User> users = new ArrayList<User>();
	private TwitterStream twitterStream;
	
	private String consumerkey = "gHCxnRIAapqAfBG5oyt6w";	
	private String consumersecret = "pisw8haLdrOvmPxyOLkT7xJoEqQkxgei2xrOkhdeJjA";	
	private String accesstoken = "18540628-4dkbUfF495u9r35CxEfqm5PDorm7e4nraiPFRQCoD";	
	private String accesstokensecret = "ZgwdueIhOQeZ3ZIt29dKZlWrc4lwcrquQ8JaDCTLeLD1i";	
	
	private static TwitterManager instance = null;
	
	public final Double LONGITUDE_IN_KM = 0.008983;
	public final Double LATITUDE_IN_KM = 0.015060;
	
	protected TwitterManager() {
		// Exists only to defeat instantiation.
	}
	
	public static TwitterManager getInstance() {
		if(instance == null) {
			instance = new TwitterManager();
		}
		return instance;
	}
	
	public List<MiniStatus> query(String keywords, String latitude, String longitude, String radius) {	
		
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

		List<MiniStatus> tweets = new ArrayList<MiniStatus>();

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
				for(Status tweet:result.getTweets()) {
					tweets.add(new MiniStatus(tweet, ApiUtil.expandStatus(tweet)));
				}
						
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

		try { longitudeNumber = Double.parseDouble(locationLong); } catch (NumberFormatException e) { longitudeNumber = null; }
		try { latitudeNumber = Double.parseDouble(locationLat);	} catch (NumberFormatException e) {	latitudeNumber = null; }
		try { daysNumber = Integer.parseInt(days); } catch (NumberFormatException e) { daysNumber = null; }

		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}

		FoursquareManager fm = new FoursquareManager();
	
		HashSet<User> users = new HashSet<User>();
		List<Status> tweets = new ArrayList<Status>(); 
		CompactVenue[] suggestedVenues = null;
		Status currentTweet;
		
		try { 
			/* Get the results for the last daysNumber days, by querying previously posted tweets. */
			Query query = new Query();
			
			/* Choosing whether to use the geographic coordinates or the name of the location.
			 * Coordinates take priority. */
			if (latitudeNumber != null && longitudeNumber != null) {
				query.setCount(10);
				suggestedVenues = fm.queryByLocation(locationLat, locationLong);
			}
			else if (locationName != null) {
				/* Search for exact location, that's what the quotes are for */
				query.setQuery("\"" + locationName + "\"");	
				query.setCount(100);
			}
			
			/* Subtract the number of days so that only the queries in the last X days are taken into consideration. */
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, -daysNumber);
			SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
			query.setSince(format.format(calendar.getTime()));
			
			/* Query twitter for results.
			 * If there are any suggestedVenues, it means that the geographic coordinates were provided
			 * and a list of venues near that location was extracted from foursquare. Otherwise, it
			 * queries twitter normally. */
			if(suggestedVenues != null) {
				for (CompactVenue venue : suggestedVenues) {
					query.setQuery(venue.getName());
					tweets.addAll(twitterConnection.search(query).getTweets());
				}
			}
			else {
				tweets.addAll(twitterConnection.search(query).getTweets());
			}
			
			int i = 1;
			/* Only get the users from the results. */
			for (Status tweet:tweets) {
				
				currentTweet = (Status) (tweet.isRetweet() ? tweet.getRetweetedStatus() : tweet);
				
				/* Extract all the links in the tweet. */
				for(String link:this.extractURL(currentTweet)) {
					System.out.println("-------------------------");
					System.out.println("Result " + i + " out of " + tweets.size());
					System.out.println("Found link");
					System.out.println(tweet.getText());
					
					/* Check if it has a foursquare checkin in the link. */
					CompleteVenue venue = fm.getVenueName(link);
					if (venue != null) {
						System.out.println("AND IT'S A CHECKIN OMG OMG OMG !!!!!");
						System.out.println(tweet.getUser().getScreenName());
						users.add(tweet.getUser());
					}
				}
				i++;
			}
		} 
		catch (Exception e) {	
			e.printStackTrace(); 	
			System.out.println("Failed to search tweets:" + e.getMessage());	
		}	

		return users;	
	}
	
	public void streamByLocation(String locationName, String locationLat, String locationLong) {
		Double latitudeNumber, longitudeNumber; 

		try { longitudeNumber = Double.parseDouble(locationLong); } catch (NumberFormatException e) { longitudeNumber = null; }
		try { latitudeNumber = Double.parseDouble(locationLat);	} catch (NumberFormatException e) {	latitudeNumber = null; }
		
		/* Use twitter stream to get the info. */
		if (this.twitterStream == null) {
			this.initStream();
			this.twitterStream.addListener(new TwitterStatusListener());
			
			FilterQuery fq = new FilterQuery();
			
			/* Getting data by taking the provided data into consideration. Location takes priority to coordinates. */
			if(!locationName.isEmpty()) {
				String[] locations = {locationName};
				fq.track(locations);
			}
			else if(latitudeNumber != null && longitudeNumber != null) {
				/* https://dev.twitter.com/docs/streaming-apis/parameters#locations */
				
				/* Gets all the tweets which are in a square of 2 km per side with the 
				 * (longitudeNumber, latitudeNumber) as the center of it. */
				double[][] locationBox = {{longitudeNumber - this.LONGITUDE_IN_KM, latitudeNumber - this.LATITUDE_IN_KM}, 
										  {longitudeNumber + this.LONGITUDE_IN_KM, latitudeNumber + this.LATITUDE_IN_KM}};
				fq.locations(locationBox);
			}
			
			this.twitterStream.filter(fq);
		}
	}

	public HashSet<User> findFoursquareUsers() {
		
		String[] keywordsArray = {"#foursquare", "foursquare"};

		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}

		List<Status> tweets = new ArrayList<Status>();
		HashSet<User> users = new HashSet<User>();

		try {
			for(String keyword:keywordsArray) {
				Query query = new Query(keyword.trim());	
				query.setCount(100);

				//it fires the query	
				QueryResult result = twitterConnection.search(query);	
				//it cycles on the tweets	
				for (Status tweet:result.getTweets()) {
					users.add(tweet.getUser());
				}

			}	
		} 
		catch (Exception e) {	
			e.printStackTrace(); 	
			System.out.println("Failed to search tweets:" + e.getMessage());	
		}
		
		return users;
	}
	
	public Twitter init() throws Exception{	
		Twitter twitterConnection = initTwitter(this.consumerkey, this.consumersecret, this.accesstoken, this.accesstokensecret);	
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
	public ArrayList<Object> getVenuesSince(Long userID, Integer days){
		ArrayList<Object> venues = new ArrayList<Object>();
	
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
			venues.add("");
			return venues;
		}
		if(statuses!=null){
			DatabaseConnection connection = new DatabaseConnection();
			UserService userService = new UserService(connection.getConnection());
			userService.insertUser(statuses.get(0).getUser());
			connection.disconnect();
			
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
				CompleteVenue currentVenue = fm.getVenueName(url);
				if(currentVenue != null)
					venues.add(currentVenue);
				
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
		
		if (this.twitterStream == null) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			.setJSONStoreEnabled(true)
			.setOAuthConsumerKey("H4VHRaf8ybmPhzzK47uQ")
			.setOAuthConsumerSecret("y6oxNsvuoauf4sPcGU45Ct5eVfryYlai5TUBU92Uxbk");
			
			TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
			this.twitterStream = twitterStreamFactory.getInstance(new AccessToken("1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra", "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS"));
			this.twitterStream.addListener(userStreamListener);
			
			FilterQuery fq = new FilterQuery();
			
			fq.follow(userID);
			
			twitterStream.filter(fq);
		}
	}
	
	public TwitterStream initStream() {
		if (this.twitterStream == null) {
			System.out.println("this is wrong");
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			.setJSONStoreEnabled(true)
			.setOAuthConsumerKey("H4VHRaf8ybmPhzzK47uQ")
			.setOAuthConsumerSecret("y6oxNsvuoauf4sPcGU45Ct5eVfryYlai5TUBU92Uxbk");
			
			TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
			this.twitterStream = twitterStreamFactory.getInstance(new AccessToken("1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra", "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS"));
			
		}
		return this.twitterStream;
	}
	
	UserStreamListener userStreamListener = new TwitterUserListener();

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

	public void handleStatus(Status status) {
		FoursquareManager fm = new FoursquareManager();

		ArrayList<String> urls = new ArrayList<String>();
		urls = this.extractURL(status);
		for(String url : urls){
			CompleteVenue currentVenue = fm.getVenueName(url);
			if(currentVenue != null)
				this.venues.add(currentVenue);
		}
	}
	
	public void handleNewStatusInStream(Status status) {
		FoursquareManager fm = new FoursquareManager();
		ArrayList<String> urls = this.extractURL(status);
		
		for(String url : urls){
			CompleteVenue currentVenue = fm.getVenueName(url);
			if(currentVenue != null)
				this.users.add(status.getUser());
		}
	}
	
	public ArrayList<CompleteVenue> getVenues() {
		return venues;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public void clearUsers() {
		this.users.clear();
	}
	
	public void clearVenues() {
		this.venues = new ArrayList<CompleteVenue>();
	}

}
