package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.FoursquareManager;
import api.TwitterManager;

/**
 * Servlet implementation class TrackingServlet
 */
public class UserVenues extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserVenues() {
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

		String userID = request.getParameter("user_id");
		Integer days = Integer.parseInt(request.getParameter("days"));
		String submit = request.getParameter("submit");
		
		if(submit != null){	
			
			TwitterManager tm = new TwitterManager();
			if(days!=0)
			out.println(tm.getVenues(userID, days));
			else
				out.println();
		} else {	
			out.println("No text entered.");	
		}	
		out.close();	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
