package api;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import util.Config;
import util.RDFBuilder;
import api.listeners.TwitterStatusListener;
import api.listeners.TwitterUserListener;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;

/**
 * This class consists of methods that use the Twitter API in order to get data. 
 * Can only be instantiated as a Singleton.
 * 
 * This class is tightly coupled with FoursquareManager and is not expected to
 * work without it.
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class TwitterManager {	

	/*
	 * The access credentials for the Twitter API.
	 */
	Config config = new Config();	
	
	/**
	 * A list of venues which will be used for the Twitter streaming API.
	 */
	private ArrayList<CompleteVenue> venues = new ArrayList<CompleteVenue>();
	
	/**
	 * A list of users which will be used for the Twitter streaming API.
	 */
	private ArrayList<User> users = new ArrayList<User>();
	
	/**
	 * The connection to the Streaming Twitter API.
	 */
	private TwitterStream twitterStream;
	
	/**
	 * The instance of TwitterManager, which will only be one as the class is a Singleton.
	 */
	private static TwitterManager instance = null;
	
	/*
	 * Values for how much a kilometre means in geographic coordinates.
	 */
	public final Double LONGITUDE_IN_KM = 0.008983;
	public final Double LATITUDE_IN_KM = 0.015060;

	protected TwitterManager() {
		/* Exists only to prevent instantiation. */
	}
	
	/**
	 * Creates the instance of TwitterManager.
	 * 
	 * @return the instance of TwitterManager.
	 */
	public static TwitterManager getInstance() {
		if(instance == null) {
			instance = new TwitterManager();
		}
		return instance;
	}
	
	/**
	 * The method initialises the connection to the Twitter Stream API.
	 * 
	 * @param userId the id of the user to listen for
	 */
	@Deprecated
	public void initConfiguration(long[] userId){
		// load the configuration class which stored the necessary keys
		Config config = new Config();
		
		if (this.twitterStream == null) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			.setJSONStoreEnabled(true)
			.setOAuthConsumerKey(config.CONSUMER_KEY)
			.setOAuthConsumerSecret(config.CONSUMER_SECRET);
			
			TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
			this.twitterStream = twitterStreamFactory.getInstance(new AccessToken("1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra", "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS"));
			this.twitterStream.addListener(new TwitterUserListener());
			
			FilterQuery fq = new FilterQuery();
			
			fq.follow(userId);
			
			twitterStream.filter(fq);
		}
	}
	
	/**
	 * The method initialises the connection to the Twitter Stream API.
	 * 
	 * @return the twitter stream connection
	 */
	public TwitterStream initStream() {
		// load the configuration class which stored the necessary keys
		Config config = new Config();
		
		if (this.twitterStream == null) {
			System.out.println("this is wrong");
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			.setJSONStoreEnabled(true)
			.setOAuthConsumerKey(config.CONSUMER_KEY)
			.setOAuthConsumerSecret(config.CONSUMER_SECRET);
			
			TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
			this.twitterStream = twitterStreamFactory.getInstance(new AccessToken("1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra", "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS"));
		}
		return this.twitterStream;
	}
	
	/**
	 * Initializes a connection to the Twitter API.
	 * 
	 * @return A twitter connection
	 * @throws Exception if the authentication keys are not correct
	 */
	public Twitter init() throws Exception{	
		Twitter twitterConnection = initTwitter(config.CONSUMER_KEY, config.CONSUMER_SECRET, config.ACCESS_TOKEN, config.ACCESS_TOKEN_SECRET);	
		return twitterConnection;	
	}

	/**
	 * Initializes a connection to the Twitter API.
	 * 
	 * @param consumerKey the consumer key available from Twitter
	 * @param consumerSecret the consumer secret available from Twitter
	 * @param accessToken the access token available from Twitter
	 * @param accessTokenSecret the access token secret available from Twitter
	 * @return a twitter connection
	 * @throws Exception if the authentication keys are not correct
	 */
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
	
	
	/**
	 * Checks if a users exists on Twitter.
	 * 
	 * @param userId the id of the user
	 * @return true if the user exists, false otherwise
	 */
	public boolean userExists(long userId){
		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}
		
		// list of statuses
		ResponseList<Status> statuses = null;

		// get the user's timeline
		try {
			statuses = twitterConnection.getUserTimeline(userId, new Paging(1,100));
		} catch (TwitterException e) {
			System.out.println("Could not retrieve user's (" + userId + ") timeline.");
			return false;
		}
		return true;
	}

	
	/**
	 * Queries the Twitter API based on the combination of parameters it gets and returns 100 results for the 
	 * keywords/hashtags provided.
	 * 
	 * @param keywords keywords for the query
	 * @param latitude latitude for the area where the search should be done
	 * @param longitude longitude for the area where the search should be done
	 * @param radius radius in kilometres for the area where the search should be done with the centre of the longitude and latitude provided
	 * @return
	 */
	public List<Status> query(String keywords, String latitude, String longitude, String radius) {	

		Integer radiusNumber;
		Double latitudeNumber, longitudeNumber;
		QueryResult result = null;
		try {
			longitudeNumber = Double.parseDouble(longitude);
			latitudeNumber = Double.parseDouble(latitude);
			radiusNumber = Integer.parseInt(radius);
		} catch (NumberFormatException e) {
			radiusNumber = null;
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

		List<MiniStatus> tweets = new ArrayList<MiniStatus>();

		try {
			Query query = new Query(keywords.trim());	
			query.setCount(100);
			
			/* If the coordinates are set as well, restrict the area to the one provided. */
			if(longitudeNumber != null && latitudeNumber != null) {
				if(radiusNumber == null)
					radiusNumber = 3;

				query.setGeoCode(new GeoLocation(latitudeNumber, longitudeNumber), radiusNumber, Query.KILOMETERS);	
			}

			/* Get the results. */	
			result = twitterConnection.search(query);	
			
			
		} 
		catch (Exception e) {	
			e.printStackTrace(); 	
			System.out.println("Failed to search tweets:" + e.getMessage());	
			System.exit(-1);	
		}	

		return result.getTweets();	
	}
	
	/**
	 * Queries the Twitter API based on the combination of parameters it gets and returns 100 results if
	 * location name is provided. If both location name and coordinates are provided or only the coordinates are
	 * provided, it will return 10 results for each venue that it finds near the specified coordinates.
	 * 
	 * @param locationName name of the location to be searched
	 * @param locationLat latitude of the location
	 * @param locationLong longitude of the location
	 * @param days the number of days for the results to be provided
	 * @return a set of unique users that have checked in on Foursquare in the provided location
	 */
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
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
	
	/**
	 * Initializes the Twitter Streaming API for the search by location. If both location name and coordinates
	 * (latitude and longitude) are provided, the location name will be the only one take into consideration.
	 * If just one of the two is provided, then the results will be provided for it.
	 * 
	 * @param locationName name of the location
	 * @param locationLat latitude of the location
	 * @param locationLong longitude of the location
	 */
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

	/**
	 * Method that searches for the tags "foursquare" and "#foursquare" and then returns a list of users that
	 * checked in on Foursquare from the results.
	 * 
	 * @return a set of unique users that checked in on foursquare.
	 */
	@Deprecated
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
	
	/**
	 * Returns a list of statuses for a certain status, provided its id is given.
	 * 
	 * @param statusId the id of the status for which retweets are wanted
	 * @return a list of statuses
	 */
	public List<Status> retweetsForStatus(String statusId) {

		long statusIdNumber = Long.parseLong(statusId);
		List<Status> retweets = null;

		/* Connect to twitter. */
		Twitter twitterConnection = null;	
		try {	
			twitterConnection = this.init();		 	 	 	

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
	 * The method gets the venues that the user has visited in the last given days.
	 * 
	 * @param userId the Twitter id of the user
	 * @param days how many days in the past to look for visited venues
	 * @return a list of CompleteVenues and CompactVenues
	 */
	public ArrayList<CompleteVenue> getVenuesSince(Long userId, Integer days) {
		ArrayList<CompleteVenue> venues = new ArrayList<CompleteVenue>();

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
			statuses = twitterC.getUserTimeline(userId, new Paging(1,100));
		} catch (TwitterException e) {
			System.out.println("Could not retrieve user's (" + userId + ") timeline.");
			return venues;
		}
		if(statuses!=null){
			RDFBuilder rdf = new RDFBuilder();
			rdf.addUser(statuses.get(0).getUser());
			rdf.save();
			rdf.close();
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
	 * The method extracts all URLs from a Status.
	 * 
	 * @param status the status for which to get the URLs.
	 * @return a list of extracted URLs.
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
	
	/**
	 * Extracts the URLs in a status and then checks if there is a Foursquare in one of them, then adds it 
	 * to the venues instance variable.
	 * 
	 * @param status a status for which to check for Foursquare checkins.
	 */
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
	
	/**
	 * Used for the streaming functionality. Extracts the URLs in a status got from the Twitter Stream
	 * and checks for Foursquare checkins. If there are any present, it adds the user to the users instance
	 * variable.
	 * 
	 * @param status a status for which to check for Foursquare checkins.
	 */
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
