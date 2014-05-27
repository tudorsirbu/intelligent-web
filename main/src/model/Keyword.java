package model;
import java.util.Date;

import twitter4j.User;
/**
 * The class stores a keyword found in a user's status by 
 * storing the actual keyword, the user id of the person who 
 * used it, how many times it was used an when.
 * @author tudorsirbu
 *
 */
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
	/**
	 * Constructor for a simple keyword which only has the keyword
	 * and how many times it appears.
	 * @param keyword the word(keyword)
	 * @param count how many times it was found
	 */
	public Keyword(String keyword, int count){
		this.keyword = keyword;
		this.count = count;
	}
	
	/**
	 * The user id of the person who used this keyword
	 * @return the user's id who used this keyword
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * Assigns a user to this keyword
	 * @param userId the user id of the user who used this keyword
	 */
	public void setUser(String userId) {
		this.userId = userId;
	}
	/**
	 * Returns the keyword found
	 * @return the word (keyword)
	 */
	public String getKeyword() {
		return keyword;
	}
	/**
	 * Sets the word(keyword)
	 * @param keyword
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	/**
	 * Returns how many times this keyword was used
	 * @return number of times it was used 
	 */
	public int getCount() {
		return count;
	}
	/**
	 * Sets how many times this keyword has been found
	 * @param count the number of times this keyword has been found.
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Returns the date when this keyword has been used
	 * @return the date when the keyword has been used
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * Sets the date when the keyword has been used
	 * @param date the date when the keyword has been used by the user
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * The method allows this type of objects to be compared.
	 */
	@Override
	public int compareTo(Keyword k) {
		if(this.count > k.getCount())
			return -1;
		else if(this.count == k.getCount())
			return 0;
		else 
			return 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		return result;
	}
	/**
	 * The method allows the comparsion of two Keyword objects
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Keyword other = (Keyword) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		return true;
	}
	
	
	
	

	
}
