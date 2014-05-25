package util;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class RDFService extends RDFBase {
	
	public void getUsers(long[] ids) {
		String queryString =        
			      "PREFIX sweb: <" + Config.NS + "> " +
			      "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			      "select ?id " +
			      "where { " +
			      	"?user a sweb:User. " +
			      	"?user sweb:id ?id " +
			      "} \n ";
	    Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qe = QueryExecutionFactory.create(query, this.statementsModel);
	    ResultSet results =  qe.execSelect();
	    
	    // Output query results    
	    ResultSetFormatter.out(System.out, results, query);
	}
	
}
