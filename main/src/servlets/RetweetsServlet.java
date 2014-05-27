package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import servlets.util.MiniStatus;
import servlets.util.Util;
import twitter4j.Status;
import twitter4j.User;
import util.RDFBuilder;
import api.TwitterManager;

import com.google.gson.Gson;

/**
 * Servlet implementation class that deals with getting the retweets of a tweet and the users that retweeted the tweet.
 * The users are stored in the triple store.
 * 
 * The class works with Twitter Manager and Foursquare Manager.
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
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
			// save data in the rdf
			RDFBuilder rdfBuilder = new RDFBuilder();
			User user = retweet.getUser();
			rdfBuilder.addUser(user);
			rdfBuilder.save();
			rdfBuilder.close();
			
			processedRetweets.add(new MiniStatus(retweet));
		}
		
		/* Create the response JSON */
		String json = gson.toJson(processedRetweets);
		response.getWriter().write(json.toString());
	}

}
