package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Status;
import api.TwitterManager;

import com.google.gson.Gson;

/**
 * Servlet implementation class UserTrackerServlet
 */
@WebServlet("/UserTrackerServlet")
public class UserTrackerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserTrackerServlet() {
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
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Gson gson = new Gson();
		
		/* Build the string containing the JSON object so that it can be parsed by gson */
		StringBuilder sb = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    try {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }
	    } finally {
	        reader.close();
	    }
	    
	    /* Parse the JSON object it got from the request */
		TrackingForm tf = gson.fromJson(sb.toString(), TrackingForm.class);
		
		/* Get tweets according to the query parameters */
		TwitterManager tm = new TwitterManager();
		List<Status> tweets = tm.query(tf.getKeywords(), tf.getRegionLat(), tf.getRegionLong(), tf.getRadius());
		
		/* Create the response JSON */
		String json = gson.toJson(tweets);
		response.getWriter().write(json.toString());
	}

}
