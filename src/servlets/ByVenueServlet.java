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
import util.RDFBuilder;
import api.FoursquareManager;
import api.TwitterManager;

import com.google.gson.Gson;

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
		RDFBuilder rdfBuilder = new RDFBuilder();
		
		StringBuilder sb = Util.jsonRequestToString(request);
		
		ByVenueForm form = gson.fromJson(sb.toString(), ByVenueForm.class);
		HashSet<model.User> users = new HashSet<model.User>();
		
		if(Integer.parseInt(form.getDays()) > 0) {
			for (User twitterUser : tm.queryByLocation(form.getLocationName(), form.getLocationLat(), form.getLocationLong(), form.getDays())) {
				users.add(new model.User(twitterUser));
				rdfBuilder.addUser(twitterUser);
			}
		}
		else {
			tm.streamByLocation(form.getLocationName(), form.getLocationLat(), form.getLocationLong());
			for(User twitterUser:tm.getUsers()) {
				users.add(new model.User(twitterUser));
			}
		}
		
		rdfBuilder.save();
		
		/* Create the response JSON */
		String json = gson.toJson(users);
		tm.clearUsers();
		response.getWriter().write(json.toString());
	}

}


