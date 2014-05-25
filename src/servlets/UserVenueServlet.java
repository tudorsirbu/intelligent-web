package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import model.Venue;
import util.RDFService;

/**
 * Servlet implementation class UserVenueServlet
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
		// get the name of the venue
		String venueName = request.getParameter("venue_name");
		
		// get the venue with that name
		RDFService rdf = new RDFService();
		Venue venue = rdf.getVenue(venueName);
		
		// conver the venue in json 
		String venueAsJson = new Gson().toJson(venue);
		
		// display the venue as json
		response.getWriter().write(venueAsJson);
	}

}
