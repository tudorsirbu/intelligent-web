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
	
	private void RDFSBuilder() {
		model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		
		/* Reading and parsing the ontology file */
		try { 
			model.read(new FileInputStream(ontologyPath), ""); 
		} catch (Exception e) { 
			e.printStackTrace();
		} 	
	}
	
	public void addUser(User user) {
		Resource resource = ResourceFactory.createResource(NS + "cristigavrila");
		Property hasName = this.model.getOntProperty(NS + "hasName");
		
		Statement s = this.model.createStatement(resource, hasName, "Florin-Cristian Gavrila");
		
		this.model.add(s);
	}
	
}
