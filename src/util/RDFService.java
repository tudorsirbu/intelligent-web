package util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Tweet;
import model.User;
import model.Venue;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import api.TwitterManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFService extends RDFBase {
	
	public User getUser(long id) {
		User user = null;
		
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			      "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
			      "select * " +
			      "where { " +
			      	"?user sweb:id " + id + "." +
			      "} \n ";
		System.out.println(queryString);
	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("user");

	        user = new User(
	        		currentResource.getProperty(this.id).getString(),
	        		currentResource.getProperty(this.foaf_name).getString(),
	        		currentResource.getProperty(this.screenName).getString(),
	        		currentResource.getProperty(this.locationName).getString(),
	        		currentResource.getProperty(this.description).getString(),
	        		currentResource.getProperty(this.depiction).getString()
	        );	
	        
	        user.setTweets(this.getTweetsByUserId(id));
	    }
	    
	    return user;
	}
	
	public List<User> getUsers(long[] ids) {
		
		List<User> users = new ArrayList<User>();
		
		for(long id:ids)
			users.add(this.getUser(id));
		
		return users;
	}
		
	public ArrayList<Tweet> getTweetsByUserId(long id) {
		Tweet tweet = null;
		
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			      "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
			      "select * " +
			      "where { " +
			      		"?user sweb:id " + id + "." +
			      		"?tweet sweb:user ?user. " +
			      "} \n ";
		System.out.println(queryString);
	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("tweet");

	        /* Taking care of the date */
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d k:m:s");
			String dateRaw = currentResource.getProperty(this.createdAt).getString();
			dateRaw = dateRaw.replace('T', ' ');
			dateRaw = dateRaw.replace("Z", "");
			
			Date date = null;
			try {
				date = dateFormat.parse(dateRaw);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			
	        tweet = new Tweet(
	        		String.valueOf(id),
	        		currentResource.getProperty(this.text).getString(),
	        		date
	        );	
	        
	        tweets.add(tweet);
	    }
	    
	    return tweets;
	}
	
	// simulator
	/**
	 * The method queries the RDF for a a venue using the provided venue name.
	 * @param name The name of the venue to be looked up.
	 * @return Venue the venue with that name or null
	 */
	public Venue getVenue(String name){
		ArrayList<User> users  = new ArrayList<User>();
		
		TwitterManager twitterManager = TwitterManager.getInstance();
		// create a connection to twitter
		Twitter twitterConnection = null;
		try {
			twitterConnection = twitterManager.init();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get the user's id
		try {
			users.add(new User(twitterConnection.getUserTimeline("studor").get(0).getUser()));
			users.add(new User(twitterConnection.getUserTimeline("cristi_gavrila").get(0).getUser()));
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Venue("1", name, users, "In cur cu satelitul");
	}
	
}
