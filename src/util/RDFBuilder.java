package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.Photo;
import fi.foyt.foursquare.api.entities.PhotoGroup;

public class RDFBuilder extends RDFBase {
	
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
	
	public Resource addVenue(CompleteVenue venue){
		Resource venueResource = ResourceFactory.createResource("https://foursquare.com/v/"+ venue.getId());
		Resource venueType = ResourceFactory.createResource(Config.NS + "FoursquareVenue");
		Resource spatialThingType = ResourceFactory.createResource(Config.GEO_NS + "SpatialThing");
		
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.statementsModel.createStatement(venueResource, RDF.type, venueType));
		statements.add(this.statementsModel.createStatement(venueResource, RDF.type, spatialThingType));
		
		statements.add(this.statementsModel.createStatement(venueResource, this.name, venue.getName()));
		
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
	
	public void addVenues(List<CompleteVenue> venues) {
		for(CompleteVenue venue:venues) 
			this.addVenue(venue);
	}
	
	public void addTweets(List<Status> tweets) {
		for(Status tweet:tweets) {
			this.addTweet(tweet);
		}
	}

	public void addTweet(Status tweet) {
		
		this.addUser(tweet.getUser());
		
		Resource tweetResource = ResourceFactory.createResource("https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId());
		Resource tweetType = model.createResource(Config.NS + "Tweet");
		
		Calendar myCal = new GregorianCalendar();
		myCal.setTime(tweet.getCreatedAt());
		
		List<Statement> statements = new ArrayList<Statement>();
		XSDDateTime dateTimeLiteral = new XSDDateTime(myCal);
		
		Resource userResource = ResourceFactory.createResource("https://twitter.com/" + tweet.getUser().getScreenName());
		statements.add(this.statementsModel.createStatement(tweetResource, RDF.type, tweetType));
		statements.add(this.statementsModel.createStatement(tweetResource, user, userResource));
		statements.add(this.statementsModel.createStatement(tweetResource, text, tweet.getText()));
		statements.add(this.statementsModel.createLiteralStatement(tweetResource, createdAt, dateTimeLiteral));
		
		this.addStatementsToModel(statements);
	}
	
	public Resource addVisit(User user, CompleteVenue venue, Date date) {
		
		Resource userResource = this.addUser(user);
		Resource venueResource = this.addVenue(venue);
		Resource visitResource = ResourceFactory.createResource();
		Resource visitType = model.createResource(Config.NS + "Visit");
		
		Calendar myCal = new GregorianCalendar();
		myCal.setTime(date);
		XSDDateTime dateTimeLiteral = new XSDDateTime(myCal);
		
		List<Statement> statements = new ArrayList<Statement>();

		statements.add(this.statementsModel.createStatement(visitResource, RDF.type, visitType));
		statements.add(this.statementsModel.createStatement(visitResource, this.twitterUser, userResource));
		statements.add(this.statementsModel.createStatement(visitResource, this.venue, venueResource));
		statements.add(this.statementsModel.createLiteralStatement(visitResource, this.date, dateTimeLiteral));
		
		this.addStatementsToModel(statements);
		
		return visitResource;
	}
	
	public List<String> venuePhotosURL (CompleteVenue venue){
		List<String> photoURLs = new ArrayList<String>();
		
		for(PhotoGroup photoGroup:venue.getPhotos().getGroups()) {
			for(Photo photo:photoGroup.getItems()) {
				photoURLs.add(photo.getUrl());
			}
		}
		
		return photoURLs;
	}
	
	public List<String> venueCategories(CompleteVenue venue){
		List<String> categories = new ArrayList<String>();
		
		for(Category category:venue.getCategories()) {
			categories.add(category.getName());
		}
		
		return categories;
	}
	
	private void addStatementsToModel(List<Statement> statements) {
		for(Statement s:statements) {
			this.statementsModel.add(s);			
		}
	}
	
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
