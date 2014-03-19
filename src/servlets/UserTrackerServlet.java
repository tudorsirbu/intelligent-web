package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DatabaseConnection;
import model.InvertedIndexService;
import model.User;
import servlets.util.TrackingForm;
import servlets.util.UserTrackerForm;
import twitter4j.Status;
import api.DiscussionsTracker;
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
		UserTrackerForm form = gson.fromJson(sb.toString(), UserTrackerForm.class);

		/* Get tweets according to the query parameters */
		DiscussionsTracker d = new DiscussionsTracker();
		d.usersQuery(toLongArray(form.getUserIds()));

		// get data from db
		DatabaseConnection db = new DatabaseConnection();
		InvertedIndexService invertedIndexService = new InvertedIndexService(db.getConnection());
		ArrayList<User> users = invertedIndexService.getKeywords(this.toLongArray(form.getUserIds()), form.getDaysSince(), form.getKeywords());

		/* Create the response JSON */
		String json = gson.toJson(users);
		response.getWriter().write(json.toString());

		// close db connection
		db.disconnect();
	}


	private long[] toLongArray(String ids){
		// split the string
		String[] idsArray = ids.split(",");
		long[] user_ids = new long[idsArray.length];

		// for each string id
		for(int i=0; i<idsArray.length; i++){
			// convert it to long type and add it to the array
			user_ids[i] = Long.parseLong(idsArray[i].trim());
		}
		return user_ids;
	}

}
