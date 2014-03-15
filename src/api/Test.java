package api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.DatabaseConnection;
import model.InvertedIndexService;
import model.User;

public class Test {
	public static void main(String[] args){
		DiscussionsTracker dT = new DiscussionsTracker();
		long[] ids = new long[]{18540628, 2379054998L, 1225017144L, 74147350 };
		dT.usersQuery(ids);	    
		
		DatabaseConnection db = new DatabaseConnection();
		InvertedIndexService in = new InvertedIndexService(db.getConnection());
		
		HashMap<User,HashMap<String,Integer>> map = in.getKeywords(ids, 1 , 3);
		Iterator i = map.entrySet().iterator();
		while(i.hasNext()){
			Map.Entry pairs = (Map.Entry)i.next();
			User u = (User) pairs.getKey();
			
			System.out.println("************ " + u.getName() + " ************");
			
			HashMap<String,Integer> keywords = map.get(u);
			System.out.println(keywords);
			
//			Iterator j = keywords.entrySet().iterator();
//			while(j.hasNext()){
//				Map.Entry pair = (Map.Entry) j.next();
//				System.out.println(pair.getKey() + " ---- " + pair.getValue());
//			}
//			
			System.out.println("************************************************"); 
		}
		db.disconnect();
	}
}
