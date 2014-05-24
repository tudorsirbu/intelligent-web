package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import servlets.util.TrackingForm;
import twitter4j.Status;
import twitter4j.User;
import util.ApiUtil;
import util.RDFBuilder;

import com.google.gson.Gson;

import api.FoursquareManager;
import api.TwitterManager;
import servlets.util.*;

/**
 * Servlet implementation class TrackingServlet
 */
public class TrackingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TrackingServlet() {
        super();
        // TODO Auto-generated constructor stub
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
		TrackingForm tf = gson.fromJson(sb.toString(), TrackingForm.class);
		
		/* Get tweets according to the query parameters */
		TwitterManager tm = TwitterManager.getInstance();
		List<Status> statuses = tm.query(tf.getKeywords(), tf.getRegionLat(), tf.getRegionLong(), tf.getRadius());
		
		List<MiniStatus> miniStatuses = new ArrayList<MiniStatus>();
		
		/* Iterate through the results and only get the information that is needed. */	
		for(Status status:statuses) 
			miniStatuses.add(new MiniStatus(status, ApiUtil.expandStatus(status)));
		
		RDFBuilder rdfBuilder = new RDFBuilder();
		rdfBuilder.addTweets(statuses);
		rdfBuilder.save();
		
		for (MiniStatus t:miniStatuses) {
			System.out.println(t.getText()+" "+t.getId());
		}
		
		/* Create the response JSON */
		String json = gson.toJson(statuses);
		response.getWriter().write(json.toString());
	}

}
