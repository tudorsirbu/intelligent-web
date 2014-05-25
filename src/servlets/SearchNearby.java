package servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.util.SearchNearbyForm;
import servlets.util.SearchVenueForm;
import servlets.util.Util;
import api.FoursquareManager;

import com.google.gson.Gson;

import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;

/**
 * Servlet implementation class that deals with finding venues that are nearby the inputed venue, in the selected city from the list
 * of returned possible matches.
 * 
 * The class works with Foursquare Manager.
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class SearchNearby extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchNearby() {
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
		StringBuilder sb = Util.jsonRequestToString(request);
		/* Parse the JSON object it got from the request */
		SearchNearbyForm form = gson.fromJson(sb.toString(), SearchNearbyForm.class);
		
		/* Get tweets according to the query parameters */
		FoursquareManager foursquare = new FoursquareManager();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("ll", form.getVenues_list());
		params.put("intent", "browse");
		params.put("radius", form.getNearby_radius());
		CompleteVenue[] venues = null;
		try {
			venues = foursquare.getVenuesList(params);
		} catch (FoursquareApiException e) {
			e.printStackTrace();
		} 
			
		/* Create the response JSON */
		String json = gson.toJson(venues);
		response.getWriter().write(json.toString());
	}

}
