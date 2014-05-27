package servlets.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
/**
 * The class converts a JSON Request into a string
 * @author Tudor Sirbu
 * @author Claudiu Tarta
 * @author Cristi Gavrila
 *
 */

public class Util {
	/**
	 * The class returns a string which was obtained from a JSON Request
	 * @param request the JSON request
	 * @return the converted string
	 * @throws IOException
	 */
	public static StringBuilder jsonRequestToString(HttpServletRequest request) throws IOException {
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
	    
		return sb;
	}
	
}
