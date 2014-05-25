package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import servlets.util.TrackingForm;
import servlets.util.UserVenuesForm;
import servlets.util.Util;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import com.google.gson.Gson;

import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import api.TwitterManager;

/**
 * Servlet implementation class UserVenuesServlet
 */
@WebServlet("/UserVenuesServlet")
public class UserVenuesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserVenuesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		UserVenuesForm uvf = gson.fromJson(sb.toString(), UserVenuesForm.class);
		
		// try to convert user ids into screen names
		boolean isScreenName = false;
		long id = 0;
		try{
			id = Long.valueOf(uvf.getUserId());
		} catch (NumberFormatException e){
			isScreenName = true;
		}
		
		/* Parse the JSON object it got from the request */
		try {
			long[] idList = new long[1];
			Long userID = (long) 0;
			
			/* Get tweets according to the query parameters */
			TwitterManager tm = TwitterManager.getInstance();
			
			// if the parameter is a screen name
			if(isScreenName){
				
				idList = new long[1];
				userID = (long) 0;
			
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
				try {
					userID =twitterConnection.getUserTimeline(uvf.getUserId()).get(0).getUser().getId();
					idList[0] = userID;
					
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				idList = new long[1];
				userID = id;
			}
			
			int days = uvf.getDays();
			
			
			
			ArrayList<CompleteVenue> venuesStreamed=null;
			System.out.println(days);
			if(days!=0){
				ArrayList<Object> venues= tm.getVenuesSince(userID, days);
				String json = gson.toJson(venues);
				tm.clearVenues();
				response.getWriter().write(json);
			} 
			else{
				if(tm.userExists(idList[0])==true){
					tm.initConfiguration(idList);
					 venuesStreamed = tm.getVenues();
					 String json = gson.toJson(venuesStreamed);
					 tm.clearVenues();
					 response.getWriter().write(json);
				}
				else{
					 String json = gson.toJson("");
					 tm.clearVenues();
					 response.getWriter().write(json);
				}
				
			}
				
			/* Create the response JSON */
			
			
		} catch (Exception e) {
			System.out.println("No input yet.");
			e.printStackTrace();
		}
	}
}


