package api;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.Iterator;

import model.DatabaseConnection;
import model.InvertedIndexService;
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
	 * The method gets the tweets for the provided ids and indexes them
	 * @param twitter the twitter api connection
	 * @param ids the ids of the users that are being queried 
	 */
	public void usersQuery(long[] ids){
		// for each user id
		for(long id : ids){
			// get the user's statuses 
			ResponseList<Status> statuses = getTweets(id);

			// index each status
			for(Status status : statuses){
				// create inveted index
				createInvertedIndex(status);
			}
		}
	}

	/**
	 * The method retrieves all the statuses of a user
	 * @param twitter the twitter connection to the api
	 * @param id the id of the user for which the statuses are retrieved
	 * @return the retrieved statuses
	 */
	public ResponseList<Status> getTweets(long id){
		// connect to twitter
		Twitter twitter = null;	
		try {	
			twitter = this.init();		 	 	 	
		} catch (Exception e) {	
			System.out.println("Cannot initialise Twitter.");	
			e.printStackTrace();	
		}

		// list of statuses
		ResponseList<Status> statuses = null;

		// get the user's timeline
		try {
			statuses = twitter.getUserTimeline(id, new Paging(1,100));
		} catch (TwitterException e) {
			System.out.println("Could not retrieve user's (" + id + ") timeline.");
			e.printStackTrace();
		}

		return statuses;
	}
	
	/**
	 * The method performs an inverted index on a provided status
	 * @param status the status to be indexed
	 */
	public void createInvertedIndex(Status status) {
		// get the text of the status
		String statusText = status.getText();

		// get the date of the status
		Date date = status.getCreatedAt();

		// get the user id
		String userID = Long.toString(((status.getUser()).getId()));

		// remove hashtags
		statusText = statusText.replaceAll("#\\w", "");

		// remove all user calls (@username)
		statusText = statusText.replaceAll("@\\w", "");

		// remove URLs
		statusText = statusText.replaceAll("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "");

		// remove the RTs
		statusText= statusText.replaceAll("\\bRT\\b", "");

		// split the string into words
		String[] tweetWords = statusText.split(" ");

		// use HashMap to count how many times a word appears in a tweet
		HashMap<String, Integer> words = new HashMap<String,Integer>();

		// count # of appearances of a word in a tweet
		for(String word:tweetWords){
			if(words.get(word) == null)
				words.put(word, 1);
			else{
				int count = words.get(word) + 1;
				words.remove(word);
				words.put(word, count);
			}		
		}

		// iterator used to go through the HashMap
		java.util.Iterator<Entry<String, Integer>> i =  words.entrySet().iterator();

		while(((QueryResult) i).hasNext()){
			Map.Entry<String, Integer> pairs = (Map.Entry) i.next();

			// get the word and bring it to lowercase
			String word = pairs.getKey().toLowerCase();

			// get the word count
			int count = pairs.getValue();

			// open a connection to the database
			DatabaseConnection db = new DatabaseConnection();

			// create a service for the inverted_index table
			InvertedIndexService index = new InvertedIndexService(db.getConnection());

			// create the inverted index
			index.createIndex(word, userID, date, count);
		}
	}
}