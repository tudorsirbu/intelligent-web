package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;
import api.TwitterManager;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.Photo;
import fi.foyt.foursquare.api.entities.PhotoGroup;

/**
 * Deals with storing Java objects into an RDF triple store. 	 
 * 
 * @author Florin-Cristian Gavrila, Tudor Sirbu, Claudiu Tarta
 */
public class RDFBuilder extends RDFBase {
	
	/**
	 * Adds a user to the RDF triple store.
	 * 
	 * @param user a User object which will be stored in the store
	 * @return the Resource it created when building the triple
	 */
	public Resource addUser(User user) {
		Resource resource = ResourceFactory.createResource("https://twitter.com/" + user.getScreenName());
		Resource twitterUserType = model.createResource(Config.NS + "TwitterUser");
		
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.statementsModel.createStatement(resource, RDF.type, twitterUserType));
	
		statements.add(this.statementsModel.createStatement(resource, this.foaf_name, user.getName()));
		statements.add(this.statementsModel.createStatement(resource, this.screenName, user.getScreenName()));
		statements.add(this.statementsModel.createLiteralStatement(resource, this.id, user.getId()));
		statements.add(this.statementsModel.createStatement(resource, this.locationName, user.getLocation()));
		statements.add(this.statementsModel.createStatement(resource, this.depiction, user.getProfileImageURL()));
		statements.add(this.statementsModel.createStatement(resource, this.description, user.getDescription()));
		
		this.addStatementsToModel(statements);
		
		return resource;
	}

	/**
	 * Adds a venue to the RDF triple store.
	 * 
	 * @param venue a Venue object which will be stored in the store
	 * @return the Resource it created when building the triple
	 */
	public Resource addVenue(CompleteVenue venue){
		Resource venueResource = ResourceFactory.createResource("https://foursquare.com/v/"+ venue.getId());
		Resource venueType = ResourceFactory.createResource(Config.NS + "FoursquareVenue");
		Resource spatialThingType = ResourceFactory.createResource(Config.GEO_NS + "SpatialThing");
		
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.statementsModel.createStatement(venueResource, RDF.type, venueType));
		statements.add(this.statementsModel.createStatement(venueResource, RDF.type, spatialThingType));
		
		statements.add(this.statementsModel.createStatement(venueResource, this.name, venue.getName()));
		statements.add(this.statementsModel.createStatement(venueResource, this.venueId, venue.getId()));
		if(venue.getDescription() != null)
			statements.add(this.statementsModel.createStatement(venueResource, this.venueDescription, venue.getDescription()));
		else
			statements.add(this.statementsModel.createStatement(venueResource, this.venueDescription, "Description not available"));
		if(venue.getLocation().getAddress() != null && venue.getLocation() != null)
			statements.add(this.statementsModel.createStatement(venueResource, this.address, venue.getLocation().getAddress()));
		else
			statements.add(this.statementsModel.createStatement(venueResource, this.address, "Address not available"));
		if(venue.getUrl() != null)
			statements.add(this.statementsModel.createStatement(venueResource, this.URL, venue.getUrl()));
		else
			statements.add(this.statementsModel.createStatement(venueResource, this.URL, "URL not available"));
		for(String photoURL:this.venuePhotosURL(venue)) {
			statements.add(this.statementsModel.createStatement(venueResource, this.hasPhoto, photoURL));
		}
		
		for(String categoryName:this.venueCategories(venue)) {
			statements.add(this.statementsModel.createStatement(venueResource, this.category, categoryName));
		}
		
		statements.add(this.statementsModel.createLiteralStatement(venueResource, this.latitude, venue.getLocation().getLat()));
		statements.add(this.statementsModel.createLiteralStatement(venueResource, this.longitude, venue.getLocation().getLng()));
		
		this.addStatementsToModel(statements);
		
		return venueResource;
	}
	
	/**
	 * Adds a list of Venues in the RDF triple store.
	 * 
	 * @param venues a list of Foursquare's CompleteVenues
	 */
	public void addVenues(List<CompleteVenue> venues) {
		for(CompleteVenue venue:venues) 
			this.addVenue(venue);
	}
	
	/**
	 * Adds a list of Tweet objects in the RDF triple store.
	 * 
	 * @param venues a list of Twitters's Status objects
	 */
	public void addTweets(List<Status> tweets) {
		for(Status tweet:tweets) {
			this.addTweet(tweet);
		}
	}

	/**
	 * Adds a Tweet in the RDF triple store.
	 * 
	 * @param tweet a Twitter Status which will be added to the store
	 */
	public void addTweet(Status tweet) {
		
		User tweetUser = tweet.getUser();
		this.addUser(tweetUser);
		
		List<Object> tweets = new ArrayList<Object>();
		tweets.add(tweet);
		if(tweet.isRetweeted()) {
			TwitterManager tm = TwitterManager.getInstance();
			Twitter twitterConnection = null;
			
			try {
				twitterConnection = tm.init();
				tweets.addAll(twitterConnection.getRetweets(tweet.getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		if(tweet.isRetweet()) {
			tweets.add(tweet.getRetweetedStatus());
		}
		
		for(Object aTweet:tweets) {
			Status currentTweet = (Status) aTweet;
			User currentUser = currentTweet.getUser();
			
			if(currentUser.getId() != tweetUser.getId())
				this.addInContactWith(currentUser, tweetUser);
			
			Resource tweetResource = ResourceFactory.createResource("https://twitter.com/" + currentTweet.getUser().getScreenName() + "/status/" + currentTweet.getId());
			Resource tweetType = model.createResource(Config.NS + "Tweet");
			
			Calendar myCal = new GregorianCalendar();
			myCal.setTime(currentTweet.getCreatedAt());
			
			List<Statement> statements = new ArrayList<Statement>();
			XSDDateTime dateTimeLiteral = new XSDDateTime(myCal);
			
			Resource userResource = ResourceFactory.createResource("https://twitter.com/" + currentTweet.getUser().getScreenName());
			statements.add(this.statementsModel.createStatement(tweetResource, RDF.type, tweetType));
			statements.add(this.statementsModel.createStatement(tweetResource, user, userResource));
			statements.add(this.statementsModel.createStatement(tweetResource, text, currentTweet.getText()));
			statements.add(this.statementsModel.createLiteralStatement(tweetResource, createdAt, dateTimeLiteral));
			
			this.addStatementsToModel(statements);
		}
		
	}
	
	/**
	 * Creates the connection between two separate users which have been in contact on Twitter. 
	 * Typically, the two users will be different.
	 * 
	 * @param firstUser one of the users, type User.
	 * @param secondUser the other user, type User.
	 */
	public void addInContactWith(User firstUser, User secondUser) {
		
		Resource firstUserResource = this.addUser(firstUser);
		Resource secondUserResource = this.addUser(secondUser);
		
		List<Statement> statements = new ArrayList<Statement>();
		
		statements.add(this.statementsModel.createStatement(firstUserResource, this.inContactWith, secondUserResource));
		statements.add(this.statementsModel.createStatement(secondUserResource, this.inContactWith, firstUserResource));

		
		this.addStatementsToModel(statements);
	}

	/**
	 * Adds a list of visits to a user in the triple store.
	 * 
	 * @param user The user for which to add the venues
	 * @param venues The list of venues to add
	 */
	public void addVisitsForUser(User user, List<CompleteVenue> venues) {
		
		Resource userResource = this.addUser(user);
		Resource venueResource;
		List<Statement> statements = new ArrayList<Statement>();

		for(CompleteVenue venue:venues) {
			venueResource = this.addVenue(venue);
			
			statements.add(this.statementsModel.createStatement(userResource, this.visited, venueResource));
		}
		
		this.addStatementsToModel(statements);
		
	}
	
	/**
	 * Extracts all the photos from a complete venue as strings.
	 * 
	 * @param venue a CompleteVenue from Foursquare.
	 * @return a list of Strings
	 */
	public List<String> venuePhotosURL (CompleteVenue venue){
		List<String> photoURLs = new ArrayList<String>();
		
		for(PhotoGroup photoGroup:venue.getPhotos().getGroups()) {
			for(Photo photo:photoGroup.getItems()) {
				photoURLs.add(photo.getUrl());
			}
		}
		
		return photoURLs;
	}
	
	/**
	 * Extracts all the categories from a CompleteVenue as strings.
	 * 
	 * @param venue a CompleteVenue from Foursquare.
	 * @return a list of Strings with the categories
	 */
	public List<String> venueCategories(CompleteVenue venue){
		List<String> categories = new ArrayList<String>();
		
		for(Category category:venue.getCategories()) {
			categories.add(category.getName());
		}
		
		return categories;
	}
	
	/**
	 * Adds all the statements provided into the model.
	 * 
	 * @param statements a list of statements.
	 */
	private void addStatementsToModel(List<Statement> statements) {
		for(Statement s:statements) {
			this.statementsModel.add(s);			
		}
	}
	
	/**
	 * Saves all the statements provided into the RDF statements model.
	 */
	public void save() {
		FileWriter out = null;
		try {
			out = new FileWriter(Config.TRIPLE_STORE_PATH);
			this.statementsModel.write(out, "RDF/XML");
			System.out.println("RDF updated!");
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not write to file.");
		}
		finally {
			try {
				System.out.println("Closing file.");
				out.close();
			}
			catch (IOException closeException) {
				closeException.printStackTrace();
				System.out.println("Could not close file.");
			}
		}
	}
	
}
