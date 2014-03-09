package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.TwitterManager;

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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();	
		out.println("<h2>Results</h2>");	

		String keywords = request.getParameter("keywords");
		String submit = request.getParameter("submit");
		Integer regionLat, regionLong, radius;
		
		try {
			regionLong = Integer.parseInt(request.getParameter("region_long"));
			regionLat = Integer.parseInt(request.getParameter("region_lat"));
			radius = Integer.parseInt(request.getParameter("radius"));
		} catch (NumberFormatException e) {
			radius = null;
			regionLat = null;
			regionLong = null;
		}
		
		if(!keywords.trim().isEmpty()){	
			TwitterManager tm = new TwitterManager();
			out.println(tm.query(keywords, regionLat, regionLong, radius));
		} else {	
			out.println("No keywords entered.");	
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
