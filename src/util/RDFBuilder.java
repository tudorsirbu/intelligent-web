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
		this.model.setNsPrefix("foaf", this.FOAF_NS);
		
		this.statementsModel.setNsPrefix("domain", this.NS);
		this.statementsModel.setNsPrefix("foaf", this.FOAF_NS);
		
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

		Property name = this.model.createProperty(this.FOAF_NS + "name");
		Property screenName = this.model.getOntProperty(this.NS + "screenName");
		Property id = this.model.getOntProperty(this.NS + "id");
		Property locationName = this.model.getOntProperty(this.NS + "locationName");
		Property depiction = this.model.createProperty(this.FOAF_NS + "depiction");
		Property description = this.model.getOntProperty(this.NS + "description");
		
		Statement[] statements = {
				this.statementsModel.createStatement(resource, name, user.getName()),
				this.statementsModel.createStatement(resource, screenName, user.getScreenName()),
				this.statementsModel.createLiteralStatement(resource, id, user.getId()),
				this.statementsModel.createStatement(resource, locationName, user.getLocation()),
				this.statementsModel.createStatement(resource, depiction, user.getProfileBackgroundImageURL()),
				this.statementsModel.createStatement(resource, description, user.getDescription())
		};
		
		this.addStatementsToModel(statements);
	}
	
	public void addVenue(Venue venue){
		Resource resourceVenue = ResourceFactory.createResource(this.NS + "https://foursquare.com/" + venue.getVenueName());
		
		Property hasName = this.model.getOntProperty(this.NS + "hasName");
		Property hasPhotos = this.model.getOntProperty(this.NS + "hasPhotos");
		Property hasCategory = this.model.getOntProperty(this.NS + "hasCategory");
		Property hasAddress = this.model.getOntProperty(this.NS + "hasAddress");
		Property hasUrl = this.model.getOntProperty(this.NS + "hasUrl");
		Property hasDescription = this.model.getOntProperty(this.NS + "hasDescription");
	}
	
	private void addStatementsToModel(Statement[] statements) {
		for(Statement s:statements) {
			System.out.println(s);
			this.statementsModel.add(s);			
		}
	}
	
	public void save() {
		this.statementsModel.write(System.out, "RDF/XML");
	}
	
}
