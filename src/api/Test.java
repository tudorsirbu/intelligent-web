package api;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;

public class Test {
	public static void main(String[] args){
		TwitterManager tm = new TwitterManager();
		try {
			Twitter twitter = tm.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Status> statuses = tm.query("#IntelligentWebCOM3504", "", "", "");
		Status status = null;
		for(Status s:statuses){
			status = s; break; }
		DiscussionsTracker dT = new DiscussionsTracker();
		System.out.println(status.getText());
		dT.createInvertedIndex(status);
		
	}
}
