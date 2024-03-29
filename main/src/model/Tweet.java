package model;

import java.util.ArrayList;
import java.util.Date;

import twitter4j.Status;
import util.Remover;
import util.StoplistBuilder;

/**
 * The class stores details about a tweet and creates an inverted index
 * on the stored tweet if requested. 
 * @author Tudor Sirbu
 * @author Cristi Gavrila
 * @author Claudiu Tarta
 *
 */

public class Tweet {
	private String text;
	private Date date; 
	private String userId;
	
	/**
	 * Constructor that creates a Tweet using a user ID, the text of the tweet
	 * and the date when it was posted
	 * @param userId The id of the user who posted the tweet
	 * @param tweet the text of the tweet
	 * @param date the date when the tweet was posted
	 */
	public Tweet(String userId, String tweet, Date date){
		this.userId = userId;
		this.text = tweet;
		this.date = date;
	}
	
	/**
	 * Constructor that creates a Tweet from a Twitter4j.Status 
	 * @param status a status returned by Twitter4j
	 */
	public Tweet(Status status){
		this.userId = String.valueOf(status.getUser().getId());
		this.text = status.getText();
		this.date = status.getCreatedAt();
	}
	/**
	 * Returns the text of the tweet stored by this type of object
	 * @return the text of the tweet
	 */
	public String getTweet(){ return this.text;}
	
	/**
	 * Returns the date when this tweet has been posted
	 * @return the date of the posted tweet
	 */
	public Date getDate(){ return this.date;}
	
	/**
	 * The method creates an inverted index for the current tweet.
	 * @return a list of Keyword objects that were found in this tweet
	 */
	public ArrayList<Keyword> getInvertedIndex(){
		// the keywords and their count for this tweet
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		
		// remove unnnecessary terms like screen names, hashtags
		Remover remover = new Remover(this.text); 
		remover.removeAll();
		
		// split the string into words
		String[] words = remover.getText().split(" ");
		
		// check if any words should be removed if they do not pass the stoplist test
		ArrayList<String> stoplist = (new StoplistBuilder()).getStoplist();
		for(String word:words){
			if(!stoplist.contains(word.toLowerCase()) && !word.isEmpty()){
				// add the word to the list of keywords or increment its count
				addKeyword(keywords, word.toLowerCase());
			}		
		}
		return keywords;
	}
	/**
	 * The method either adds a keyword to a list of keywords or it 
	 * increments it if it is already in the list
	 * @param keywords the list of Keywords where the keyword needs to be inserted
	 * @param keyword the keyword to be inserted.
	 */
	private void addKeyword(ArrayList<Keyword> keywords, String keyword){
		// assume the keyword isn't already in the list of keywords
		boolean exists = false;
		
		// loop through the list of keywords
		for(Keyword k : keywords){
			// if the keyword is in the current list
			if(k.getKeyword().toLowerCase().equals(keyword.toLowerCase())){
				// increment its count
				k.setCount(k.getCount()+1);
				
				// mark the keyword as already in the list
				exists = true;
			}
		}
		
		// if the keyword is not already in the list add it
		if(!exists){
			Keyword k = new Keyword(this.userId, keyword.toLowerCase(), 1, this.date);
			keywords.add(k);
		}
	}
}
