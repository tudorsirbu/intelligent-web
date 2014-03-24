package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import servlets.util.Util;
import twitter4j.Status;
import twitter4j.User;
import api.TwitterManager;

import com.google.gson.Gson;

/**
 * Servlet implementation class RetweetsServlet
 */
public class RetweetsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetweetsServlet() {
        super();
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
		String statusId = gson.fromJson(sb.toString(), String.class);
		
		/* Get tweets according to the query parameters */
		TwitterManager tm = TwitterManager.getInstance();
		List<Status> retweets = tm.retweetsForStatus(statusId);
		
		System.out.println(retweets);
		
		List<MiniStatus> processedRetweets = new ArrayList<MiniStatus>();
		for (Status retweet:retweets) {
			UserService us = new UserService(new DatabaseConnection().getConnection());
			User user = retweet.getUser();
			us.insertUser(new model.User(String.valueOf(user.getId()), user.getName(), user.getScreenName(), user.getLocation(), user.getDescription(), user.getProfileImageURL(), null));
			
			processedRetweets.add(new MiniStatus(retweet));
		}
		
		/* Create the response JSON */
		String json = gson.toJson(processedRetweets);
		response.getWriter().write(json.toString());
	}

}
