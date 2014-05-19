package util;

import java.io.FileInputStream;

import twitter4j.User;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class RDFBuilder {

	private final String NS = "file:///home/cristi/workspace/intelligent-web/#";
	private final String ontologyPath = "src/model/ontology.owl";
	
	public OntModel model;
	
	public RDFBuilder() {
		org.apache.log4j.BasicConfigurator.configure();
		this.model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		
		/* Reading and parsing the ontology file */
		try { 
			this.model.read(new FileInputStream(ontologyPath), ""); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
	
	public void addUser(User user) {
		Resource resource = ResourceFactory.createResource(this.NS + user.getId());
		System.out.println(this.model);
		Property hasName = this.model.getOntProperty(this.NS + "hasName");
		Property hasId = this.model.getOntProperty(this.NS + "hasId");
		Property hasLocation = this.model.getOntProperty(this.NS + "hasLocation");
		Property hasProfilePicture = this.model.getOntProperty(this.NS + "hasProfilePicture");
		Property hasDescription = this.model.getOntProperty(this.NS + "hasDescription");
		
		Statement[] statements = {
				this.model.createStatement(resource, hasName, user.getName()),
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
