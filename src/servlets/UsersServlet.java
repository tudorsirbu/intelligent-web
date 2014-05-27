package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.User;
import model.Venue;
import servlets.util.MiniStatus;
import servlets.util.Template;
import servlets.util.Util;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import util.RDFService;
import api.DiscussionsTracker;
import api.TwitterManager;

import com.google.gson.Gson;

/**
 * Servlet implementation class that deals with showing information about a specific user.
 * 
 * The class works with Twitter Manager.
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	

		String userId = request.getParameter("user_id");
		long id = 0;
		boolean isScreenName = false;
		try{
			id = Long.valueOf(userId);
		} catch (NumberFormatException e){
			isScreenName = true;
		}
		User user = null;
		
		if(isScreenName){
		
			// convert the user's screen name to id
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
			long userID = 0;
			try {
				userID =twitterConnection.getUserTimeline(userId).get(0).getUser().getId();
				RDFService rdf = new RDFService();
				user = rdf.getUser(userID);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { 
			RDFService rdf = new RDFService();
			user = rdf.getUser(id);
		}
		
		
		String entry = "";
		if(user != null){	
			entry +="<div id=\"user_results\"xmlns:sweb=\"http://www.smartweb.com/data/#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmls:xs=\"http://www.w3.org/2001/XMLSchema#\">";
				entry += "<div id='user' about=\"https://www.twitter.com/"+ user.getUsername() +"\"" + "typeof=\"foaf:Agent\">";
				entry +="<img property=\"foaf:depiction\" src=\""+user.getProfilePicURL()+"\" />";
				entry +="<a href=\"UsersServlet?user_id="+user.getId()+" class=\"title\"  property=\"foaf:name\">" + user.getName() + "</a><span class=\"screen_name\" property=\"sweb:screenName\">@"+ user.getUsername() +"</span>";
				entry +="<h3 property=\"sweb:id\" datatype=\"xs:integer\">" + user.getId() + "</h3>";
				entry +="<h3 property=\"sweb:locationName\">"+ user.getLocation() + "</h3>";
				entry +="<h3 property=\"sweb:description\">" +user.getDescription()+"</h3>";
				if (user.getVisited() != null) {
					for(Venue venue:user.getVisited()) {
						entry +="<h3 property=\"sweb:visited\">" + venue.getName() +"</h3>";
					}				
				}
				entry += "<div style=\"clear:both;\"></div>";
				entry += "<button data-href=\""+user.getId()+"\" class=\"get_tweets\"> Get tweets </button>";
				entry += "<button data-href=\""+user.getId()+"\" class=\"showMap\"> Show Visited Venues </button>";
				entry +="<div id=\"mapRes\"></div>";
				entry += "</div>";
			entry += "</div>";	
			entry += "<div id=\"results\"></div>";
		} else {	
			entry += "<div id=\"user\">";
			entry += "<h1>The user ID provided isn't in the database.</h1>";
			entry += "</div>";
		}
		
		Template page = new Template(entry, "Users results.");
		out.write(page.getPage());
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		
		/* Build the string containing the JSON object so that it can be parsed by gson */
		StringBuilder sb = Util.jsonRequestToString(request);
	    
	    /* Parse the JSON object it got from the request */
		Long ids = gson.fromJson(sb.toString(), Long.class);
		
		/* Get tweets according to the query parameters */
		DiscussionsTracker dt = new DiscussionsTracker();
		ResponseList<Status> tweets = dt.getTweets(ids);
		
		List<MiniStatus> processedTweets = new ArrayList<MiniStatus>();
		for (Status tweet:tweets) {
			processedTweets.add(new MiniStatus(tweet));
		}
		
		/* Create the response JSON */
		String json = gson.toJson(processedTweets);
		response.getWriter().write(json.toString());

	}

}
