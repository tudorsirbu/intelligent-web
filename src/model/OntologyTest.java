package model;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class OntologyTest {

	public static void main (String args[]) {
		
		String ns = "http://localhost/#";
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		
		Resource cristi = m.createResource("Cristi");
		Resource clau = m.createResource("Clau");
		
		Property hasFlatMate = m.createProperty(ns, "hasFlatMate");
		
		cristi.addProperty(hasFlatMate, clau);
		clau.addProperty(hasFlatMate, cristi);
		
		Statement flatMateStmt = m.createStatement(cristi, hasFlatMate, clau);
		
		m.add(flatMateStmt);
		
		
		
	}
	
	
}
