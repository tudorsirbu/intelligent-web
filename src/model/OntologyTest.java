package model;

import java.io.FileInputStream;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class OntologyTest {

	public static void main (String args[]) {
		org.apache.log4j.BasicConfigurator.configure();
		String filePath = "src/model/ontology.owl"; 
		OntModel m = 
				ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		try { 
			m.read(new FileInputStream(filePath), ""); 
		} catch (Exception e) { 
			e.printStackTrace();
		} 
		//lists all classes 
		ExtendedIterator<OntClass> classIterator = m.listClasses(); 
		while (classIterator.hasNext()) { 
			OntClass ontClass = classIterator.next(); 
			//do something with ontClass e.g. 
			System.out.println(ontClass.toString()); 
		}
	}
}
