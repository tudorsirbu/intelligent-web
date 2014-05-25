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

public class RDFBase {

	public OntModel model;
	public Model statementsModel;
	
	/* List of properties for the sweb:TwitterUser class */
	Property foaf_name, screenName, id, locationName, depiction, description;
	
	/* List of properties for the swb:FoursquareVenue class */
	Property name, hasPhoto, category, location, latitude, longitude, hasBeenVisitedBy;
	
	/* List of properties for the swb:Tweet class */
	Property user, text, createdAt, retweetedBy;
	
	public RDFBase() {
		org.apache.log4j.BasicConfigurator.configure();
		this.model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM); 
		this.statementsModel = ModelFactory.createDefaultModel();
		
		this.model.addSubModel(this.statementsModel);
		
		/* Outputs the relative path to the files. No longer useful */
		/*URL resource = getClass().getResource("/");
		  System.out.println(resource.getPath()); */
		
		/* Reading and parsing the ontology file */
		try { 
			this.model.read(new FileInputStream(Config.ONTOLOGY_PATH), Config.NS);
			this.statementsModel.read(new FileInputStream(Config.TRIPLE_STORE_PATH), Config.NS);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		/* Setting the namespace so that the statments will look like <domain:hasId>131321</domain:hasId>
		 * Useful info on this here: http://nuin.blogspot.co.uk/2005/04/jena-tip-namespaces-and-j0-problem.html */
		this.model.setNsPrefix("sweb", Config.NS);
		this.model.setNsPrefix("foaf", Config.FOAF_NS);
		this.model.setNsPrefix("geo", Config.GEO_NS);
		
		this.statementsModel.setNsPrefix("sweb", Config.NS);
		this.statementsModel.setNsPrefix("foaf", Config.FOAF_NS);
		this.statementsModel.setNsPrefix("geo", Config.GEO_NS);
		
		/* Initialising the list of properties */
		this.foaf_name = this.model.createProperty(Config.FOAF_NS + "name");
		this.screenName = this.model.getOntProperty(Config.NS + "screenName");
		this.id = this.model.getOntProperty(Config.NS + "id");
		this.locationName = this.model.getOntProperty(Config.NS + "locationName");
		this.depiction = this.model.createProperty(Config.FOAF_NS + "depiction");
		this.description = this.model.getOntProperty(Config.NS + "description");
		this.name = this.model.getOntProperty(Config.NS + "name");
		this.hasPhoto = this.model.getOntProperty(Config.NS + "hasPhoto");
		this.category = this.model.getOntProperty(Config.NS + "category");
		this.location = this.model.getOntProperty(Config.NS + "location");
		this.latitude  = this.model.createProperty(Config.GEO_NS + "lat");
		this.longitude = this.model.createProperty(Config.GEO_NS + "long");
		this.hasBeenVisitedBy =this.model.getOntProperty(Config.NS + "hasBeenVisitedBy");
		this.user = this.model.getOntProperty(Config.NS + "user");
		this.text = this.model.getOntProperty(Config.NS + "text");
		this.createdAt = this.model.getOntProperty(Config.NS + "createdAt");
		this.retweetedBy = this.model.getOntProperty(Config.NS + "retweetedBy");
		
		/* Lists all classes */
		ExtendedIterator<OntClass> classIterator = model.listClasses(); 
		while (classIterator.hasNext()) { 
			OntClass ontClass = classIterator.next(); 
			System.out.println(ontClass.toString()); 
		}
//			
//		Property hasName = this.model.getOntProperty(Config.NS + "name");
//		ExtendedIterator<Resource> iter = this.model.listResourcesWithProperty(hasName);
//		while (iter.hasNext()) { 
//			Resource ontClass = iter.next();
//			System.out.println(ontClass.toString()); 
//		}
	}
	
}
