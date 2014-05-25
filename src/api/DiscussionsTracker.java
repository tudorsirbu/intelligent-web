package api;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import model.DatabaseConnection;
import model.InvertedIndexService;
import model.UserService;
import model.WordService;
import twitter4j.*;
import util.RDFBuilder;
import util.Remover;

/**
 * DiscussionsTracker is used to query Twitter for the statuses
 * of a specific user. 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
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
	 * 
	 * @param twitter the twitter api connection
	 * @param ids the ids of the users that are being queried 
	 */
	public void usersQuery(long[] ids){
		// for each user id
		for(long id : ids){
			// get the user's statuses 
			ResponseList<Status> statuses = getTweets(id);

			// add the statuses to the RDF
			RDFBuilder rdfBuilder = new RDFBuilder();
			rdfBuilder.addTweets(statuses);
			
			// add the user to the RDF
			rdfBuilder.addUser(statuses.get(0).getUser());
		}
	}

	/**
	 * The method retrieves all the statuses of a user
	 * 
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

}
