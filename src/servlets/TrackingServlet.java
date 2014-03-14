package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import servlets.util.TrackingForm;
import twitter4j.Status;
import twitter4j.User;

import com.google.gson.Gson;

import api.FoursquareManager;
import api.TwitterManager;
import servlets.util.*;

/**
 * Servlet implementation class TrackingServlet
 */
public class TrackingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrackingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	
		out.println("<h2>Results</h2>");	

		String keywords = request.getParameter("keywords");
		String regionLat = request.getParameter("region_lat");
		String regionLong = request.getParameter("region_long");
		String radius = request.getParameter("radius");
		
		String resultString = "";	
		
		TwitterManager tm = new TwitterManager();
		List<Status> tweets = tm.query(keywords, regionLat, regionLong, radius);

		if(!keywords.trim().isEmpty()){	
			FoursquareManager fs = new FoursquareManager();
			Pattern urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
			
			for (Status tweet:tweets) {
				// Gets the user 		
				User user = tweet.getUser();

				/* Only access foursquare if the user mentions it in his tweet. */
				if (tweet.getText().toLowerCase().contains("foursquare") == true) {
					Matcher m = urlPattern.matcher(tweet.getText());

					while (m.find()) {
						System.out.println(m.group());
						fs.getLocationInformation(m.group());
					}					
				}

				/* Display the tweets. */
				Status status = user.isGeoEnabled() ? user.getStatus() : null;
				resultString+="@" + user.getName() + " - " + tweet.getText();
				if (status==null) {
					resultString += " (" + user.getLocation() + ") \n";						
				} 	
				else {
					String coordinates = status.getGeoLocation().getLatitude() + "," + status.getGeoLocation().getLongitude();
					resultString += " (" + (status!=null && status.getGeoLocation() != null ? coordinates : user.getLocation()) + ") \n";						
				}
			}
				
			out.println(resultString);
		} else {	
			out.println("No keywords entered.");	
		}	
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
		TrackingForm tf = gson.fromJson(sb.toString(), TrackingForm.class);
		
		/* Get tweets according to the query parameters */
		TwitterManager tm = new TwitterManager();
		List<Status> tweets = tm.query(tf.getKeywords(), tf.getRegionLat(), tf.getRegionLong(), tf.getRadius());
		
		for (Status t:tweets) {
			System.out.println(t.getText()+" "+t.getId());
		}
		
		List<MiniStatus> processedTweets = new ArrayList<MiniStatus>();
		for (Status t:tweets) {
			processedTweets.add(new MiniStatus(t));
			
			UserService us = new UserService(new DatabaseConnection().getConnection());
			User user = t.getUser();
			us.insertUser(new model.User(String.valueOf(user.getId()), user.getName(), user.getScreenName(), user.getLocation(), user.getDescription(), user.getProfileImageURL(), null));
		}
		
		/* Create the response JSON */
		String json = gson.toJson(processedTweets);
		response.getWriter().write(json.toString());
	}

}
