package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.User;
import model.Venue;
import servlets.util.Template;
import servlets.util.Util;
import util.RDFService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class that deals with finding all the users that have visited a specific venue.
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class VisitedByServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VisitedByServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	
		
		String venueName = request.getParameter("visited_venue");
		
		System.out.println(venueName);
		
		// get the venue with that name
		RDFService rdfService = new RDFService();
		ArrayList<User> users = rdfService.getUsersVisitingVenueByName(venueName);

		String entry = "";	
		entry +="<div id=\"user_results\"xmlns:sweb=\"http://www.smartweb.com/data/#\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmls:xs=\"http://www.w3.org/2001/XMLSchema#\">";
		for(User user:users) {
			System.out.println(venueName);
			entry += "<div class=\"user\" about=\"https://www.twitter.com/"+ user.getUsername() +"\"" + "typeof=\"foaf:Agent\">";
			entry +="<img property=\"foaf:depiction\" src=\""+user.getProfilePicURL()+"\" />";
			entry +="<h3 class =\"title\"  property=\"foaf:name\">" + user.getName() + "</h3><span class=\"screen_name\" property=\"sweb:screenName\">@"+ user.getUsername() +"</span>";
			entry +="<h3 property=\"sweb:id\" datatype=\"xs:integer\">" +"Id: "+ user.getId() + "</h3>";
			entry +="<h3 property=\"sweb:locationName\">"+ user.getLocation() + "</h3>";
			entry +="<h3 property=\"sweb:description\">" +user.getDescription()+"</h3>";
			if (user.getVisited() != null) {
				for(Venue venue:user.getVisited()) {
					entry +="<h3 property=\"sweb:visited\">" + venue.getName() +"</h3>";
				}				
			}
			entry += "</div>";
		}
		entry += "</div>";	
		
		Template page = new Template(entry, "Users results.");
		page.setDoctype(Template.Doctype.XHTML);
		out.write(page.getPage());
		out.close();
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
		RDFService rdfService = new RDFService();
		ArrayList<User> users = rdfService.getUsersVisitingVenueByName(venueName);

		// conver the venue in json 
		String usersAsJson = gson.toJson(users);
		
		// display the venue as json
		System.out.println(usersAsJson);
		response.getWriter().write(usersAsJson);
	}

}
