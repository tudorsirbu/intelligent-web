package servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.util.UserVenuesForm;
import servlets.util.Util;
import twitter4j.Twitter;
import twitter4j.User;
import util.RDFBuilder;
import api.TwitterManager;

import com.google.gson.Gson;

import fi.foyt.foursquare.api.entities.CompleteVenue;

/**
 * Servlet implementation class UserVenuesServlet returns as json 
 * either the locations where the user has been in the past X days
 * or it creates a Stream and listens for any user checkins.
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
		
		ArrayList<CompleteVenue> venuesStreamed=null;
		ArrayList<CompleteVenue> venues = null;
		RDFBuilder rdfBuilder = new RDFBuilder();
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
			Long userID = 0l;
			
			/* Get tweets according to the query parameters */
			TwitterManager tm = TwitterManager.getInstance();
			
			// create a connection to twitter
			Twitter twitterConnection = tm.init();
			
			// if the parameter is a screen name
			if(isScreenName){
				
				idList = new long[1];
				userID = 0l;
				
				userID = twitterConnection.getUserTimeline(uvf.getUserId()).get(0).getUser().getId();
				idList[0] = userID;
			
			} else {
				idList = new long[1];
				userID = id;
				idList[0] = id;
			}
			
			User user = twitterConnection.getUserTimeline(userID).get(0).getUser();
			rdfBuilder.addUser(user);
			rdfBuilder.save();
			
			int days = uvf.getDays();
			
			if(tm.userExists(idList[0])){
			if(days != 0) {
				venues = tm.getVenuesSince(userID, days);
				rdfBuilder.addVenues(venues);
				
				String json = gson.toJson(venues);
				tm.clearVenues();
				response.getWriter().write(json);
			} else {
					tm.initConfiguration(idList);
					 venues = tm.getVenues();
					 System.out.println(venues.isEmpty());
					 if(venues.size()!=0){
						 System.out.println("CACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
						 rdfBuilder.addVenues(venues);
						 String json = gson.toJson(venues);
						 tm.clearVenues();
						 response.getWriter().write(json);
				}
				
			}
			rdfBuilder.addVisitsForUser(user, venues);
			rdfBuilder.save();
			}
		} catch (Exception e) {
			System.out.println("No input yet.");
			e.printStackTrace();
		}
	}
}