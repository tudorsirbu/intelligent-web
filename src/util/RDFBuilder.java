package util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import twitter4j.Status;
import twitter4j.User;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.Photo;
import fi.foyt.foursquare.api.entities.PhotoGroup;

public class RDFBuilder extends RDFBase {
	
	public void addUser(User user) {
		Resource resource = ResourceFactory.createResource("https://twitter.com/" + user.getScreenName());
		Resource twitterUserType = model.createResource(Config.NS + "TwitterUser");
		
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.statementsModel.createStatement(resource, RDF.type, twitterUserType));
	
		statements.add(this.statementsModel.createStatement(resource, this.foaf_name, user.getName()));
		statements.add(this.statementsModel.createStatement(resource, this.screenName, user.getScreenName()));
		statements.add(this.statementsModel.createLiteralStatement(resource, this.id, user.getId()));
		statements.add(this.statementsModel.createStatement(resource, this.locationName, user.getLocation()));
		statements.add(this.statementsModel.createStatement(resource, this.depiction, user.getProfileBackgroundImageURL()));
		statements.add(this.statementsModel.createStatement(resource, this.description, user.getDescription()));
		
		this.addStatementsToModel(statements);
	}
	
	public void addVenue(CompleteVenue venue){
		Resource venueResource = ResourceFactory.createResource(venue.getUrl());
		Resource venueType = model.createResource(Config.NS + "FoursquareVenue");
		
		List<Statement> statements = new ArrayList<Statement>();
		statements.add(this.statementsModel.createStatement(venueResource, RDF.type, venueType));
		
		statements.add(this.statementsModel.createStatement(venueResource, this.name, venue.getName()));
		
		
		for(String photoURL:this.venuePhotosURL(venue)) {
			statements.add(this.statementsModel.createStatement(venueResource, this.hasPhoto, photoURL));
		}
		
		for(String categoryName:this.venueCategories(venue)) {
			statements.add(this.statementsModel.createStatement(venueResource, this.category, categoryName));
		}
		
		Resource locationResource = ResourceFactory.createResource(Config.GEO_NS + "Point");
		locationResource.addLiteral(latitude, venue.getLocation().getLat());
		locationResource.addLiteral(longitude, venue.getLocation().getLng());
		statements.add(this.statementsModel.createStatement(venueResource, this.location, locationResource));

		this.addStatementsToModel(statements);
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
			System.out.println(s);
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
