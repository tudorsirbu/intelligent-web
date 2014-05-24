package util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fi.foyt.foursquare.api.entities.Category;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.Photo;
import fi.foyt.foursquare.api.entities.PhotoGroup;

public class RDFBuilder {
	
	public OntModel model;
	public Model statementsModel;
	
	public RDFBuilder() {
		org.apache.log4j.BasicConfigurator.configure();
		this.model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		this.statementsModel = ModelFactory.createDefaultModel();
		
		this.model.addSubModel(this.statementsModel);
		
		/* Outputs the relative path to the files. No longer useful */
		/*URL resource = getClass().getResource("/");
		  System.out.println(resource.getPath()); */
		
		/* Reading and parsing the ontology file */
		try { 
			this.model.read(new FileInputStream(Config.ONTOLOGY_PATH), "");
			this.statementsModel.read(new FileInputStream(Config.TRIPLE_STORE_PATH), "");
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		/* Setting the namespace so that the statments will look like <domain:hasId>131321</domain:hasId>
		 * Useful info on this here: http://nuin.blogspot.co.uk/2005/04/jena-tip-namespaces-and-j0-problem.html */
		this.model.setNsPrefix("domain", Config.NS);
		this.model.setNsPrefix("foaf", Config.FOAF_NS);
		this.model.setNsPrefix("geo", Config.GEO_NS);
		
		this.statementsModel.setNsPrefix("domain", Config.NS);
		this.statementsModel.setNsPrefix("foaf", Config.FOAF_NS);
		this.statementsModel.setNsPrefix("geo", Config.GEO_NS);
		
		/* Lists all classes */
		ExtendedIterator<OntClass> classIterator = model.listClasses(); 
		while (classIterator.hasNext()) { 
			OntClass ontClass = classIterator.next(); 
			System.out.println(ontClass.toString()); 
		}
		
		
		Property hasName = this.model.getOntProperty(Config.NS + "name");
		ExtendedIterator<Resource> iter = this.model.listResourcesWithProperty(hasName);
		while (iter.hasNext()) { 
			Resource ontClass = iter.next();
			System.out.println(ontClass.toString()); 
		}
	}
	
	public void addUser(User user) {
		Resource resource = ResourceFactory.createResource(Config.NS + "https://twitter.com/" + user.getScreenName());

		Property name = this.model.createProperty(Config.FOAF_NS + "name");
		Property screenName = this.model.getOntProperty(Config.NS + "screenName");
		Property id = this.model.getOntProperty(Config.NS + "id");
		Property locationName = this.model.getOntProperty(Config.NS + "locationName");
		Property depiction = this.model.createProperty(Config.FOAF_NS + "depiction");
		Property description = this.model.getOntProperty(Config.NS + "description");
		
		List<Statement> statements = new ArrayList<Statement>();
		
		System.out.println(screenName);
		
		statements.add(this.statementsModel.createStatement(resource, name, user.getName()));
		statements.add(this.statementsModel.createStatement(resource, screenName, user.getScreenName()));
		statements.add(this.statementsModel.createLiteralStatement(resource, id, user.getId()));
		statements.add(this.statementsModel.createStatement(resource, locationName, user.getLocation()));
		statements.add(this.statementsModel.createStatement(resource, depiction, user.getProfileBackgroundImageURL()));
		statements.add(this.statementsModel.createStatement(resource, description, user.getDescription()));
		
		this.addStatementsToModel(statements);
	}
	
	public void addVenue(CompleteVenue venue){
		Resource venueResource = ResourceFactory.createResource(venue.getUrl());
		
		Property name = this.model.getOntProperty(Config.NS + "name");
		Property hasPhoto = this.model.getOntProperty(Config.NS + "hasPhoto");
		Property category = this.model.getOntProperty(Config.NS + "category");
		Property location = this.model.getOntProperty(Config.NS + "location");
		Property latitude  = this.model.createProperty(Config.GEO_NS + "lat");
		Property longitude = this.model.createProperty(Config.GEO_NS + "long");
		Property hasBeenVisitedBy =this.model.getOntProperty(Config.NS + "hasBeenVisitedBy");
		
		List<Statement> statements = new ArrayList<Statement>();
		
		statements.add(this.statementsModel.createStatement(venueResource, name, venue.getName()));
		
		
		for(String photoURL:this.venuePhotosURL(venue)) {
			statements.add(this.statementsModel.createStatement(venueResource, hasPhoto, photoURL));
		}
		
		for(String categoryName:this.venueCategories(venue)) {
			statements.add(this.statementsModel.createStatement(venueResource, category, categoryName));
		}
		
		Resource locationResource = ResourceFactory.createResource(Config.GEO_NS + "Point");
		locationResource.addLiteral(latitude, venue.getLocation().getLat());
		locationResource.addLiteral(longitude, venue.getLocation().getLng());
		statements.add(this.statementsModel.createStatement(venueResource, location, locationResource));

		this.addStatementsToModel(statements);
	}
	
	public void addTweets(ResponseList<Status> tweets) {
		for(Status tweet:tweets) {
			this.addTweet(tweet);
		}
	}

	public void addTweet(Status tweet) {
		Resource tweetResource = ResourceFactory.createResource("https://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId());
		
		Property user = this.model.getOntProperty(Config.NS + "user");
		Property text = this.model.getOntProperty(Config.NS + "text");
		Property createdAt = this.model.getOntProperty(Config.NS + "createdAt");
		Property retweetedBy = this.model.getOntProperty(Config.NS + "retweetedBy");
		
		List<Statement> statements = new ArrayList<Statement>();
		
		Resource userResource = ResourceFactory.createResource("https://twitter.com/" + tweet.getUser().getScreenName());
		statements.add(this.statementsModel.createStatement(tweetResource, user, userResource));
		statements.add(this.statementsModel.createStatement(tweetResource, text, tweet.getText()));
		statements.add(this.statementsModel.createLiteralStatement(tweetResource, createdAt, tweet.getCreatedAt()));
		
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
			this.statementsModel.write(System.out, "RDF/XML");
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
