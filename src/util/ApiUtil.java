package util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;

public class ApiUtil {
	
	/**
	 * A regex pattern for short URLs that might contain an instagram link.
	 */
	public final static String shortURLPattern = "\\b(https?)://(instagram|t).com?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*";
	
	/**
	 * Extracts all the URLs in a Status and expands them.
	 * 
	 * @param status the status to expand
	 * @return a list of expanded URLs.
	 */
	public static List<String> expandStatus(Status status) {
		
		List<String> expandedUrls = new ArrayList<String>();
		String statusText = status.isRetweet() ? status.getSource() : status.getText();
		Pattern pattern = Pattern.compile(ApiUtil.shortURLPattern);
		
		Matcher matcher = pattern.matcher(statusText);
		while (matcher.find()) {
			try {
				expandedUrls.add(ApiUtil.getFullURL(matcher.group()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return expandedUrls;
	}
	
	/**
	 * Takes a shortURL and expands it to the original full URL.
	 * 	
	 * @param shortURL the shortURL to expand.
	 * @return the expanded URL
	 * @throws IOException
	 */
	private static String getFullURL (String shortURL) throws IOException {
		System.out.println("#########" + shortURL);
		URL shortUrl= new URL(shortURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection)shortUrl.openConnection();
		httpURLConnection.setInstanceFollowRedirects(false);
		httpURLConnection.connect();
		int responseCode = httpURLConnection.getResponseCode();
		String header = httpURLConnection.getHeaderField("Location");

		return header;
	}
	
}
