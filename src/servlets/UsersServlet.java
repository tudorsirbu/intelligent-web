package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.UserService;
import servlets.util.Template;
import twitter4j.Status;
import twitter4j.User;
import api.FoursquareManager;
import api.TwitterManager;

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
			content += user.getName();
			content += "<img src=\""+ user.getProfilePicURL() +"\"/>";
			content += user.getLocation();
			content += user.getUsername();
		} else {	
			out.println("No user found.");	
		}
		
		Template page = new Template(content, "Users results.");
		
		out.write(page.getPage());
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
