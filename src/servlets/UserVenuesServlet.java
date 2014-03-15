package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.MiniStatus;
import servlets.util.TrackingForm;
import servlets.util.Util;
import twitter4j.Status;
import twitter4j.User;

import com.google.gson.Gson;

import api.TwitterManager;

/**
 * Servlet implementation class UserVenuesServlet
 */
@WebServlet("/UserVenuesServlet")
public class UserVenuesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserVenuesServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	
		out.println("<h2>Results</h2>");	

		Long userID = Long.parseLong(request.getParameter("user_id"));
		Integer days = Integer.parseInt(request.getParameter("days"));
		String submit = request.getParameter("submit");
		
		if(submit != null){	
			
			TwitterManager tm = new TwitterManager();
			

			
			if(days!=0){
				if(tm.getVenues(userID, days)!=null)
					out.println(tm.getVenues(userID, days));
				else
					out.println("Cant get the venues for that user!");		
			}
			else{

				long [] list = new long[1];
				list[0]=userID;
				tm.initConfiguration(list);
			}
		} else {	
			out.println("No text entered.");	
		}	
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
		TrackingForm tf = gson.fromJson(sb.toString(), TrackingForm.class);
		
		/* Get tweets according to the query parameters */
		TwitterManager tm = new TwitterManager();
		long[] list = new long[1];
		list[0]=2379054998L;
		tm.initConfiguration(list);
		ArrayList<String> venues = tm.getVenueNames();
		
		
		
	
		/* Create the response JSON */
		String json = gson.toJson(venues);
		response.getWriter().write(json.toString());
	}
	}


