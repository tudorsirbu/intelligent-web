package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.User;
import model.Venue;
import servlets.util.UserTrackerForm;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import util.RDFService;
import api.TwitterManager;

import com.google.gson.Gson;

/**
 * Servlet implementation class VenuesForUserServlet
 */
public class VenuesForUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VenuesForUserServlet() {
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
		String userIdentifier = gson.fromJson(sb.toString(), String.class);
		System.out.println("######## " + userIdentifier );
		// try to convert a screen name into an id
		boolean isScreenName = false;
		long id = 0;
		try{
			id = Long.valueOf(userIdentifier);
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
				id =twitterConnection.getUserTimeline(userIdentifier).get(0).getUser().getId();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		RDFService rdf = new RDFService();
		User user = rdf.getUser(id);
		
		ArrayList<Venue> venues = user.getVisited();
		
		String venuesJSON = gson.toJson(venues);
		response.getWriter().write(venuesJSON.toString());
	}

}
