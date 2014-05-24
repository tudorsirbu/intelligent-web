package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import servlets.util.Template;
import servlets.util.Util;
import twitter4j.ResponseList;
import twitter4j.Status;
import api.DiscussionsTracker;

import com.google.gson.Gson;

/**
 * Servlet implementation class UsersServlet
 */
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	

		String userId = request.getParameter("user_id");
		
		UserService us = new UserService(new DatabaseConnection().getConnection());
		model.User user = us.getUser(userId);

		String content = "";

		if(user != null){	
			content += "<div id=\"user\">";
			content += "<img src=\""+ user.getProfilePicURL() +"\"/>";
			content += "<h1>" + user.getName() + "</h1>" + "<h2> @" + user.getUsername() +"</h2>";		
			content += "<h3>" + user.getDescription() + "</h3>";
			content += "<h3>" + user.getLocation() +"</h3>";
			content += "<div style=\"clear:both;\"></div>";
			content += "<button data-href=\""+user.getId()+"\" class=\"get_tweets\"> Get tweets </button>";
			content += "</div>";
			content += "<div id=\"results\"></div>";
		} else {	
			content += "<div id=\"user\">";
			content += "<h1>The user ID provided isn't in the database.</h1>";
			content += "</div>";
		}
		
		Template page = new Template(content, "Users results.");
		out.write(page.getPage());
		out.close();
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
		String screenName = gson.fromJson(sb.toString(), String.class);
		
		/* Get tweets according to the query parameters */
		DiscussionsTracker dt = new DiscussionsTracker();
		ResponseList<Status> tweets = dt.getTweets(screenName);
		
		List<MiniStatus> processedTweets = new ArrayList<MiniStatus>();
		for (Status tweet:tweets) {
			processedTweets.add(new MiniStatus(tweet));
		}
		
		/* Create the response JSON */
		String json = gson.toJson(processedTweets);
		response.getWriter().write(json.toString());

	}

}
