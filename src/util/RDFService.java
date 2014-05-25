package util;

import java.util.ArrayList;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import model.User;
import model.Venue;
import api.TwitterManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class RDFService extends RDFBase {
	
	public void getUser(long id) {
		System.out.println(id);
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			      "select ?id " +
			      "where { " +
			      	"?user a sweb:User. " +
			      	"?user sweb:id " + id +
			      "} \n ";
	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.statementsModel);
	    ResultSet results =  qe.execSelect();
	    
	    // Output query results    
	    ResultSetFormatter.out(System.out, results, query);
	}
	
	public void getUsers(long[] ids) {
		for(long id:ids)
			this.getUser(id);
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
