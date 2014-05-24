package util;

import java.io.FileInputStream;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class RDFService {
	
	public OntModel model;
	public Model statementsModel;
	
	public RDFService() {
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
	
	
	
}
