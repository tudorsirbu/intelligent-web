package servlets;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.util.ByVenueForm;
import servlets.util.Util;
import twitter4j.User;
import api.FoursquareManager;
import api.TwitterManager;

import com.google.gson.Gson;

import fi.foyt.foursquare.api.entities.CompactUser;

/**
 * Servlet implementation class VenueServlet
 */
public class ByVenueServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ByVenueServlet() {
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
		
		TwitterManager tm = TwitterManager.getInstance();
		FoursquareManager fm = new FoursquareManager();
		
		Gson gson = new Gson();
		
		StringBuilder sb = Util.jsonRequestToString(request);
		
		ByVenueForm form = gson.fromJson(sb.toString(), ByVenueForm.class);
		System.out.println(form);
		
//		HashSet<User> twitterUsers = tm.queryByLocation(form.getLocationName(), form.getLocationLat(), form.getLocationLong(), form.getDays());		
		HashSet<CompactUser> fsUsers = fm.queryByLocation(form.getLocationName(), form.getLocationLat(), form.getLocationLong(), form.getDays());
		
		System.out.println(fsUsers.size());
		
		/* Create the response JSON */
		String json = gson.toJson(fsUsers);
		response.getWriter().write(json.toString());
	}

}
