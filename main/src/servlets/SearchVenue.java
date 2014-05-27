package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.util.MiniStatus;
import servlets.util.SearchVenueForm;
import servlets.util.Util;
import twitter4j.Status;
import api.FoursquareManager;
import api.TwitterManager;

import com.google.gson.Gson;

import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.CompleteVenue;

/**
 * Servlet implementation class that deals with finding venues that match the suggested name and return a list with venues that
 * match the name.
 * 
 * The class works with Foursquare Manager.
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class SearchVenue extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchVenue() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request,response);
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
		SearchVenueForm form = gson.fromJson(sb.toString(), SearchVenueForm.class);
		
		/* Get tweets according to the query parameters */
		FoursquareManager foursquare = new FoursquareManager();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("query", form.getVenue_name());
		params.put("near", form.getVenue_city());
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
