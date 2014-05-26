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
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Deals with retrieving triples from the RDF triple store and returns them as objects.
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class RDFService extends RDFBase {
	
	/**
	 * Retrieves an user given its id on Twitter.
	 * 
	 * @param id the twitter user id
	 * @return a User object
	 */
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

	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("user");

	        user = this.buildUserFromResource(currentResource);
	        
	        user.setTweets(this.getTweetsByUserId(id));
	        user.setVisited(this.getVenuesVisitedByUserId(id));
	        user.setInContact(this.getInContactForUserId(id));
	    }
	    
	    return user;
	}

	private ArrayList<User> getInContactForUserId(long id) {
		ArrayList<User> users = new ArrayList<User>();
		
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "select ?contact " +
			      "where { " +
			      	"?user sweb:inContactWith ?contact." +
			      	"?user sweb:id "+ id +"." +
			      "} \n ";

	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("contact");

	        users.add(this.buildUserFromResource(currentResource));
	    }
	    
	    return users;
	}

	/**
	 * Retrieves a list of users given an array of user ids.
	 * 
	 * @param ids an array of twitter user ids.
	 * @return a list of User objects
	 */
	public List<User> getUsers(long[] ids) {
		
		List<User> users = new ArrayList<User>();
		
		for(long id:ids)
			users.add(this.getUser(id));
		
		return users;
	}
		
	/**
	 * Fetches all the tweets stored in the RDF store for a twitter user id.
	 * 
	 * @param id a twitter user id
	 * @return a list of Tweet objects
	 */
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
	
	/**
	 * The method queries the RDF for a venue using the provided venue name.
	 * 
	 * @param name The name of the venue to be looked up.
	 * @return Venue the venue with that name or null
	 */
	public Venue getVenue(String name){
		Venue venue = null;
		
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
			      "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
			      "select * " +
			      "where { " +
			      	"?venue sweb:name <" + name + ">." +
			      "} \n ";

	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    ResultSetFormatter.out(results);
	    
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("FoursquareVenue");

	        venue = new Venue(
	        		currentResource.getProperty(this.venueId).getString(),
	        		currentResource.getProperty(this.name).getString(),
	        		currentResource.getProperty(this.venueDescription).getString(),
	        		currentResource.getProperty(this.address).getString(),
	        		currentResource.getProperty(this.hasPhoto).getString(),
	        		currentResource.getProperty(this.category).getString(),
	        		currentResource.getProperty(this.URL).getString()
	        		);	
	    }
	    
	    return venue;
	}
	
	/**
	 * Retrieves a list of users that visited a certain venue from the RDF store given the Foursquare venue id.
	 * 
	 * @param venueId a Foursquare Venue id
	 * @return the list of users that visited the venue
	 */
	public ArrayList<User> getUsersVisitingVenueByName(String venueName){
		ArrayList<User> users = new ArrayList<User>();
		
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "select ?user " +
			      "where { " +
			      	"?user sweb:visited ?venue." +
			      	"?venue sweb:name ?name." +
			      	"FILTER regex(?name, \""+ venueName +"\", \"i\" )" +
			      "} \n ";

	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("user");

	        users.add(this.buildUserFromResource(currentResource));
	    }
	    
	    return users;
	}
	

	public ArrayList<Venue> getVenuesVisitedByUserId(long id){

		ArrayList<Venue> venues = new ArrayList<Venue>();
		
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "select ?venue " +
			      "where { " +
			      	"?user sweb:visited ?venue." +
			      	"?user sweb:id "+ id +"." +
			      "} \n ";

	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.model);
	    ResultSet results =  qe.execSelect();
	    
	    ResultSetFormatter.out(results);
	    
	    while(results.hasNext()) {
	    	QuerySolution solution = results.nextSolution() ;
	        Resource currentResource = solution.getResource("venue");

	        venues.add(this.buildVenueFromResource(currentResource));
	    }
	    
	    return venues;
	}
	
	
	private Venue buildVenueFromResource(Resource resource) {
		return new Venue(
				resource.getProperty(this.venueId).getString(),
				resource.getProperty(this.name).getString(),
				resource.getProperty(this.venueDescription).getString(),
				resource.getProperty(this.address).getString(),
				resource.getProperty(this.hasPhoto).getString(),
				resource.getProperty(this.category).getString(),
				resource.getProperty(this.URL).getString()
        		);
	}

	private User buildUserFromResource(Resource resource) {
		return new User(
				resource.getProperty(this.id).getString(),
				resource.getProperty(this.foaf_name).getString(),
				resource.getProperty(this.screenName).getString(),
				resource.getProperty(this.locationName).getString(),
				resource.getProperty(this.description).getString(),
				resource.getProperty(this.depiction).getString()
        );	
	}
}
