package util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.Keyword;
import model.Tweet;
import model.User;

public class InvertedIndex {
	// top list of keywords
	
	public void createInvertedIndex(ArrayList<Tweet> tweets, int noKeywords, int lastDays){
		// compute the date after which the top needs to be created
		Calendar currentDate = Calendar.getInstance();
		currentDate.add(Calendar.DAY_OF_MONTH, -lastDays);
		Date date = new Date(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DAY_OF_MONTH));
		
		// the list where all the keywords will be stored
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		
		// loop through the list of tweets and select the ones which have been posted in the lastDays days
		for(Tweet t: tweets){
			if(t.getDate().after(date)){
				keywords.addAll(t.getInvertedIndex());
			}
		}
		
		HashMap<String, HashMap<String, Integer>> allKeywords = addUpSameKeywords(keywords);
		
		HashMap<String, HashMap<String, Integer>> top = new HashMap<String, HashMap<String, Integer>>();
		
		Iterator it = allKeywords.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        // get the user id 
	        String userId = (String) pairs.getKey();
	        
	        // get the keywords associated with this user
	        HashMap<String, Integer> userKeywords = (HashMap<String,Integer>) pairs.getValue();
	        HashMap<String, Integer> userTop = new HashMap<String, Integer>();
	        
	        // loop through them and get only the top X keywords
	        int minMax = 0;
	        String minMaxKeyword = "";
	        
	        Iterator i = userKeywords.entrySet().iterator();
	        while(i.hasNext()){
	        	Map.Entry keywordPair = (Map.Entry) i.next();
	        	
	        	if(((Integer) keywordPair.getValue()) > minMax){
	        		if(userTop.size() < noKeywords){
	        			// add the keyword to the user's top
	        			userTop.put((String) keywordPair.getKey(), (Integer) keywordPair.getValue());
	        		} else {
	        			// remove the smallest keyword
	        			userTop.remove(minMaxKeyword);
	        			
	        		}
	        	}
	        }
	        
	        
	    }
	}
	
	private HashMap<String, HashMap<String, Integer>> addUpSameKeywords(ArrayList<Keyword> keywords){
		// the hashmap will store the top keywords
		HashMap<String, HashMap<String, Integer>> top = new HashMap<String, HashMap<String, Integer>>();
		
		for(Keyword keyword: keywords){
			// check if the user has any keywords in the top
			if(top.get(keyword.getUserId()) != null){
				// get the hashmap with all the keyword belonging to this user
				HashMap<String,Integer> currentUserKeywords = top.get(keyword.getUserId());
				
				// check if the current keyword has already been added to the top
				if(currentUserKeywords.get(keyword.getKeyword()) != null){
					// get the count for this keyword
					int currentCount = currentUserKeywords.get(keyword.getKeyword());
					
					// update the count for this keyword
					currentUserKeywords.remove(keyword.getKeyword());
					currentUserKeywords.put(keyword.getKeyword(), currentCount + keyword.getCount());
				} else {
					currentUserKeywords.put(keyword.getKeyword(), keyword.getCount());
				}
			} else {
				// add the current keyword to the top
				HashMap<String, Integer> keywordsCount = new HashMap<String,Integer>();
				keywordsCount.put(keyword.getKeyword(), keyword.getCount());
				
				top.put(keyword.getUserId(), keywordsCount);
			}
		}
		
		return top;
	}

}
