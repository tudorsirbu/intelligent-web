package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Keyword;
import model.Tweet;
import model.User;
import servlets.util.UserTrackerForm;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import util.RDFService;
import api.DiscussionsTracker;
import api.TwitterManager;

import com.google.gson.Gson;

/**
 * Servlet implementation class that deals with finding the most used keywords by a list of users.
 * 
 * The class works with Foursquare Manager.
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
@WebServlet("/UserTrackerServlet")
public class UserTrackerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserTrackerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();

		/* Build the string containing the JSON object so that it can be parsed by gson */
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}

		/* Parse the JSON object it got from the request */
		UserTrackerForm form = gson.fromJson(sb.toString(), UserTrackerForm.class);

		// convert the screen name into ids
		long[] ids = this.toUserIdFromScreenName(form.getUserIds());
		
		/* Get tweets according to the query parameters */
		DiscussionsTracker d = new DiscussionsTracker();
		d.usersQuery(ids);
		
		// get data from RDF
		RDFService rdfService = new RDFService();
		
		// get the tweets for the given user names
		ArrayList<User> users = (ArrayList<User>) rdfService.getUsers(ids);
		rdfService.close();
		
		// create inverted index for the given users' tweets
		HashMap<String,ArrayList<Keyword>> keywords = new HashMap<String,ArrayList<Keyword>>();
		
		for(User user:users){
			// compute the date of the last tweet to be taken into account
			Calendar currentDate = Calendar.getInstance();
			currentDate.add(Calendar.DAY_OF_MONTH, -form.getDaysSince());
			Date lastTweetDate = new Date(currentDate.getTimeInMillis());
			
			// get the current user's tweets
			ArrayList<Tweet> tweets  = user.getTweets();
			
			// loop through the user's tweets and create an inverted index on those that are after the lastTweetDate
			ArrayList<Keyword> userKeywords = new ArrayList<Keyword>();
			
			for(Tweet t:tweets){
				if(t.getDate().after(lastTweetDate)){
					// add the current tweet's keyword to the user's list of keywords
					userKeywords.addAll(t.getInvertedIndex());
				}
			}
			
			// add the user's keywords to the list of all keywords
			for(Keyword k:userKeywords){
				if(keywords.get(k.getKeyword()) != null){
					// get the list containing all occurances of this keyword
					ArrayList<Keyword> similarKeywords = keywords.get(k.getKeyword());
					
					// add the current keyword to the list
					similarKeywords.add(k);
 				} else { 
 					// create new list of keywords like this one
 					ArrayList<Keyword> similarKeywords = new ArrayList<Keyword>();
 					similarKeywords.add(k);
 					keywords.put(k.getKeyword(), similarKeywords);
 				}
			}
		}
		
		// add up the keywords and see which ones are in the top 
		ArrayList<Keyword> top = new ArrayList<Keyword>();
		Iterator it = keywords.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        ArrayList<Keyword> similarKeywords = (ArrayList<Keyword>) pairs.getValue();
	        // the number of times this keyword has shown up for all users
	        int count = 0;
	        for(Keyword k:similarKeywords){
	        	count += k.getCount();
	        }
	        
	        top.add((new Keyword((String) pairs.getKey(), count)));
	        
	    }
	    
	    // sort the top
	    Collections.sort(top);
		int topLength;
		if(top.size() > form.getKeywords()){
			topLength = form.getKeywords();
		} else {
			topLength = top.size();
		}
		// create a list of all keywords
		ArrayList<String> generalKeywords = new ArrayList<String>();
				
	    for(int i=0; i<topLength; i++){
	    	// get the list with how many times the keyword has been used by each user
	    	ArrayList<Keyword> allUserCounts = keywords.get(top.get(i).getKeyword());
	    	
	    	// add the keyword to the general list
	    	generalKeywords.add(top.get(i).getKeyword());
	    	
	    	for(User u:users){
	    		// add the current keyword to the user's hashmap
	    		for(Keyword k:allUserCounts){
	    			if(k.getUserId().equals(u.getId())){
	    				u.addKeyword(new  Keyword(top.get(i).getKeyword(), k.getCount()));
	    				System.out.println(u.getName() + "-" + (top.get(i).getKeyword() + " = " + k.getCount()));
	    			}
	    		}
	    	}
	    }

	    for(User u:users){
	    	// get the user's keywords and add up the same one eg home-1, home-3 => home-4
	    	ArrayList<Keyword> userKeywords = u.getKeywords();
	    	
	    	for(Keyword k:userKeywords){
	    		System.out.println(k.getKeyword()+ " *-> " + k.getCount());
	    	}
	    	
	    	Set<Keyword> keywordsSet = new HashSet<Keyword>(userKeywords);
	    	
	    	while(keywordsSet.size() != userKeywords.size()){
		    	for(int i=0; i<userKeywords.size(); i++){
		    		for(int j=i+1; j<userKeywords.size(); j++){
		    			if(userKeywords.get(i).getKeyword().equals(userKeywords.get(j).getKeyword())){
		    				userKeywords.get(i).setCount(userKeywords.get(i).getCount() + userKeywords.get(j).getCount());
		    				userKeywords.remove(j);
		    			}
		    		}
		    	}
	    	}

	    	u.setKeywords(userKeywords);
	    }
	    
		/* Create the response JSON */
		String usersJson = gson.toJson(users);
		String keywordsJson = gson.toJson(generalKeywords);
		response.getWriter().write("["+usersJson.toString()+","+keywordsJson.toString()+"]");
	}
	
	private long[] toUserIdFromScreenName(String screenNames){
		String[] screenNamesList = screenNames.split(",");
		long[] ids = new long[screenNamesList.length];
		for(int i=0; i<screenNamesList.length; i++){
			// try to convert a screen name into an id
			boolean isScreenName = false;
			long id = 0;
			try{
				id = Long.valueOf(screenNamesList[i]);
			} catch (NumberFormatException e) {
				isScreenName=true;
			}
			
			if(isScreenName){
				TwitterManager twitterManager = TwitterManager.getInstance();
				// create a connection to twitter
				Twitter twitterConnection = null;
				try {
					twitterConnection = twitterManager.init();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// get the user's id
				try {
					ids[i] =twitterConnection.getUserTimeline(screenNamesList[i]).get(0).getUser().getId();
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				ids[i] = id;
			}
		}
		
		return ids;
	}

}
