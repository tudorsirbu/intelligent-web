package util;

import java.net.URL;

import twitter4j.User;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class RDFBuilder {

	private final String NS = "file:///home/cristi/Desktop/eclipse/#";
	private final String ontologyPath = "ontology.owl";
	
	public OntModel model;
	
	public RDFBuilder() {
		org.apache.log4j.BasicConfigurator.configure();
		this.model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		
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
		
		/* Lists all classes */
		ExtendedIterator<OntClass> classIterator = model.listClasses(); 
		while (classIterator.hasNext()) { 
			OntClass ontClass = classIterator.next(); 
			//do something with ontClass e.g. 
			System.out.println(ontClass.toString()); 
		}
		
	}
	
	public void addUser(User user) {
		Resource resource = ResourceFactory.createResource(this.NS + "https://twitter.com/" + user.getScreenName());

		Property hasFullName = this.model.getOntProperty(this.NS + "hasFullName");
		Property hasScreenName = this.model.getOntProperty(this.NS + "hasScreenName");
		Property hasId = this.model.getOntProperty(this.NS + "hasId");
		Property hasLocation = this.model.getOntProperty(this.NS + "hasLocation");
		Property hasProfilePicture = this.model.getOntProperty(this.NS + "hasProfilePicture");
		Property hasDescription = this.model.getOntProperty(this.NS + "hasDescription");
		
		Statement[] statements = {
				this.model.createStatement(resource, hasFullName, user.getName()),
				this.model.createStatement(resource, hasScreenName, user.getScreenName()),
				this.model.createLiteralStatement(resource, hasId, user.getId()),
				this.model.createStatement(resource, hasLocation, user.getLocation()),
				this.model.createStatement(resource, hasProfilePicture, user.getProfileBackgroundImageURL()),
				this.model.createStatement(resource, hasDescription, user.getDescription())
		};
		
		for(Statement s:statements) {
			System.out.println(s);
			this.model.add(s);			
		}
		
	}
	
	public void save() {
		this.model.write(System.out, "RDF/XML");
	}
	
}
