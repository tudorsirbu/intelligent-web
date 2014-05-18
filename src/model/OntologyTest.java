package model;

import java.io.FileInputStream;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyTest {

	public static void main (String args[]) {
		org.apache.log4j.BasicConfigurator.configure();
		
		String ns = "file:///home/cristi/workspace/intelligent-web/#";
		
		String filePath = "src/model/ontology.owl"; 
		OntModel m = 
				ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		try { 
			m.read(new FileInputStream(filePath), ""); 
		} catch (Exception e) { 
			e.printStackTrace();
		} 
		
		Resource resource = ResourceFactory.createResource(ns + "cristigavrila");
		Property hasName = m.getOntProperty(ns + "hasName");
		
		Statement s = m.createStatement(resource, hasName, "Florin-Cristian Gavrila");
		
		m.add(s);
		
//		//lists all classes 
//		ExtendedIterator<OntClass> classIterator = m.listClasses(); 
//		while (classIterator.hasNext()) { 
//			OntClass ontClass = classIterator.next(); 
//			//do something with ontClass e.g. 
//			System.out.println(ontClass.toString()); 
//		}
		
		//lists all classes 
		ExtendedIterator<Resource> classIterator = m.listResourcesWithProperty(hasName); 
		while (classIterator.hasNext()) { 
			Resource ontClass = classIterator.next(); 
			//do something with ontClass e.g. 
			System.out.println(ontClass.toString()); 
		}
		
		m.write(System.out, "RDF/XML");
	}
}
