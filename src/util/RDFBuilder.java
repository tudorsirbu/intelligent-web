package util;

import model.Venue;
import twitter4j.User;

import com.hp.hpl.jena.ontology.AllDifferent;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.PhotoGroup;

public class RDFBuilder {

	private final String NS = "file:///home/cristi/Desktop/eclipse/#";
	private final String FOAF_NS = "http://xmlns.com/foaf/0.1/#";
	private final String ontologyPath = "ontology.owl";
	
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
			this.model.read(this.getClass().getClassLoader().getResourceAsStream(ontologyPath), ""); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		/* Setting the namespace so that the statments will look like <domain:hasId>131321</domain:hasId>
		 * Useful info on this here: http://nuin.blogspot.co.uk/2005/04/jena-tip-namespaces-and-j0-problem.html */
		this.model.setNsPrefix("domain", this.NS);
		this.model.setNsPrefix("foaf", this.NS);
		
		this.statementsModel.setNsPrefix("domain", this.NS);
		this.statementsModel.setNsPrefix("foaf", this.NS);
		
		/* Lists all classes */
		ExtendedIterator<OntClass> classIterator = model.listClasses(); 
		while (classIterator.hasNext()) { 
			OntClass ontClass = classIterator.next(); 
			//do something with ontClass e.g. 
			System.out.println(ontClass.toString()); 
		}
		
		
		Property hasName = this.model.getOntProperty(this.NS + "name");
		ExtendedIterator<Resource> iter = this.model.listResourcesWithProperty(hasName);
		while (iter.hasNext()) { 
			Resource ontClass = iter.next(); 
			//do something with ontClass e.g. 
			System.out.println(ontClass.toString()); 
		}
	}
	
	public void addUser(User user) {
		Resource resource = ResourceFactory.createResource(this.NS + "https://twitter.com/" + user.getScreenName());

		Property name = this.model.getOntProperty(this.FOAF_NS + "name");
		Property screenName = this.model.getOntProperty(this.NS + "screenName");
		Property id = this.model.getOntProperty(this.NS + "id");
		Property locationName = this.model.getOntProperty(this.NS + "locationName");
		Property depiction = this.model.getOntProperty(this.FOAF_NS + "depiction");
		Property description = this.model.getOntProperty(this.NS + "description");
		
		System.out.println(name);
		System.out.println("THIS IS IT ->>> " + screenName);
		
		Statement[] statements = {
				this.statementsModel.createStatement(resource, name, user.getName()),
				this.statementsModel.createStatement(resource, screenName, user.getScreenName()),
				this.statementsModel.createLiteralStatement(resource, id, user.getId()),
				this.statementsModel.createStatement(resource, locationName, user.getLocation()),
				this.statementsModel.createStatement(resource, depiction, user.getProfileBackgroundImageURL()),
				this.statementsModel.createStatement(resource, description, user.getDescription())
		};
		
		for(Statement s:statements) {
			System.out.println(s);
			this.statementsModel.add(s);			
		}
		
	}
	
	public void addVenue(CompleteVenue venue){
		Resource resource = ResourceFactory.createResource(this.NS + "https://foursquare.com/" + venue.getName());
		
		Property name = this.model.getOntProperty(this.NS + "name");
		Property hasPhoto = this.model.getOntProperty(this.NS + "hasPhoto");
		Property category = this.model.getOntProperty(this.NS + "category");
		Property address = this.model.getOntProperty(this.NS + "address");
		Property hasBeenVisitedBy =this.model.getOntProperty(this.NS + "hasBeenVisitedBy");
		
		Statement[] statements = {
				this.statementsModel.createStatement(resource, name, venue.getName()),
				this.statementsModel.createStatement(resource, hasPhoto, venue.getPhotos().getGroups().),
				
				
		};
	}
	
	public String venuePhotos (CompleteVenue venue){
		String photos = new String();
		PhotoGroup[] photoGroups = venue.getPhotos().getGroups();
		if(photoGroups.length>1)
			if(photoGroups[1].getItems().length!=0)
				$.each(photoGroups[1].items,function(key,value){
					div += "<img src='"+ value.url +"' />";
				});
			else{
				if(photoGroups[0].length!=0 )
					$.each(photoGroups[0].items,function(key,value){
						div += "<img src='"+ value.url +"' />";
					});
			}
		
		
		return null;
	}
	
	public void save() {
		this.model.write(System.out, "RDF/XML");
	}
	
}
