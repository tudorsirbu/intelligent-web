package model;
import java.util.Date;

import twitter4j.User;

public class Keyword implements Comparable<Keyword>{
	// the user to whom this keyword belongs
	private String userId;
	// the keyword
	private String keyword;
	// the number of times it appeared
	private int count;
	// the date when it appeared
	private Date date;
	
	
	/**
	 * Constructor for a keyword
	 * @param user the user to whom it belongs
	 * @param keyword the actual keyword
	 * @param count how many times it appeared 
	 * @param date on what date it apparead
	 */
	public Keyword(String userId, String keyword, int count, Date date) {
		this.userId = userId;
		this.keyword = keyword;
		this.count = count;
		this.date = date;
	}
	
	public Keyword(String keyword, int count){
		this.keyword = keyword;
		this.count = count;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUser(String userId) {
		this.userId = userId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int compareTo(Keyword k) {
		if(this.count > k.getCount())
			return -1;
		else if(this.count == k.getCount())
			return 0;
		else 
			return 1;
	}

	
}
