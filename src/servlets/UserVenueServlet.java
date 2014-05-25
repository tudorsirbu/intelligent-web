package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import model.Venue;
import servlets.util.TrackingForm;
import servlets.util.Util;
import util.RDFService;

/**
 * Servlet implementation class that deals with finding all the users that have visited a specific venue.
 * 
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class UserVenueServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserVenueServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// process it as a post
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Gson gson = new Gson();
		
		/* Build the string containing the JSON object so that it can be parsed by gson */
		StringBuilder sb = Util.jsonRequestToString(request);
		JsonObject jSon = gson.fromJson(sb.toString(), JsonObject.class);
		
		// get the name of the venue
		String venueName = jSon.get("venue_name").getAsString();
		
		// get the venue with that name
		RDFService rdf = new RDFService();
		Venue venue = rdf.getVenue(venueName);
		
		// conver the venue in json 
		String venueAsJson = gson.toJson(venue);
		
		// display the venue as json
		System.out.println(venueAsJson);
		response.getWriter().write(venueAsJson);
	}

}
