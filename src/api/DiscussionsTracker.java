package api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import model.DatabaseConnection;
import model.InvertedIndexService;
import model.UserService;
import twitter4j.*;
import util.Remover;

/**
 * DiscussionsTracker is used to query Twitter for the statuses
 * of a specific user. 
 * @author tudorsirbu
 *
 */
public class DiscussionsTracker {
	Twitter twitterConnection = null;

	public DiscussionsTracker(){
		// create a twitter connection
		try {
			twitterConnection = (new TwitterManager()).init();
		} catch (Exception e) {
			System.out.println("DiscussionsTracker: A connection to Twitter API could not be established.");
			e.printStackTrace();
		}

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
			
			// add the user to the database
			UserService userService = new UserService(new DatabaseConnection().getConnection());
			// get the user details from a status and insert it in the database
			userService.insertUser(statuses.get(0).getUser());
		}
	}

	/**
	 * The method retrieves all the statuses of a user
	 * @param twitter the twitter connection to the api
	 * @param id the id of the user for which the statuses are retrieved
	 * @return the retrieved statuses
	 */
	public ResponseList<Status> getTweets(long id){

		// list of statuses
		ResponseList<Status> statuses = null;

		// get the user's timeline
		try {
			statuses = twitterConnection.getUserTimeline(id, new Paging(1,100));
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
		// get the date of the status
		Date date = status.getCreatedAt();

		// get the user id
		String userID = Long.toString(((status.getUser()).getId()));

		// remove unnecessary words, tags and characters
		Remover remover = new Remover(status.getText());
		remover.removeAll();

		// split the string into words
		String[] tweetWords = remover.getText().split(" ");

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
