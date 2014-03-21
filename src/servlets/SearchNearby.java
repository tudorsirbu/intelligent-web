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

import fi.foyt.foursquare.api.entities.CompactVenue;

/**
 * Servlet implementation class SearchNearby
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
		CompactVenue[] venues = foursquare.getVenuesList(params); 
			
		/* Create the response JSON */
		String json = gson.toJson(venues);
		response.getWriter().write(json.toString());
	}

}
