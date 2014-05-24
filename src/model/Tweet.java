package model;

import java.util.ArrayList;
import java.util.Date;

import util.Remover;
import util.StoplistBuilder;

public class Tweet {
	private String text;
	private Date date; 
	private String userId;
	
	public Tweet(String userId, String tweet, Date date){
		this.userId = userId;
		this.text = tweet;
		this.date = date;
	}
	
	public String getTweet(){ return this.text;}
	public Date getDate(){ return this.date;}
	
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
				addKeyword(keywords, word);
			}		
		}
		
		return keywords;
	}
	
	private void addKeyword(ArrayList<Keyword> keywords, String keyword){
		// assume the keyword isn't already in the list of keywords
		boolean exists = false;
		
		// loop through the list of keywords
		for(Keyword k : keywords){
			// if the keyword is in the current list
			if(k.getKeyword().equals(keyword)){
				// increment its count
				k.setCount(k.getCount()+1);
				
				// mark the keyword as already in the list
				exists = true;
			}
		}
		
		// if the keyword is not already in the list add it
		Keyword k = new Keyword(this.userId, keyword, 1, this.date);
		keywords.add(k);
	}
}
