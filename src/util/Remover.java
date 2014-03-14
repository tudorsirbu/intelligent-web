package util;

/**
 * The class operates on a status and remove unnecessary words like
 * hashtags, usernames, links and so on.
 * @author tudorsirbu
 *
 */
public class Remover {
	private String statusText = "";
	
	public Remover(String s){
		this.statusText = s;
	}
	
	public void removeHashTags(){
		statusText = statusText.replaceAll("#[\\w]+", "");
	}
	
	public void removeScreenNames(){
		statusText = statusText.replaceAll("@[\\w]+", "");
	}
	
	public void removeUrls(){
		// remove urls that start with http, https, ftp or file eg. http://www.google.com
		statusText = statusText.replaceAll("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "");
		// remove urls that do not have the protocol. eg. www.google.com
		statusText = statusText.replaceAll("\\bwww.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", "");
	}
	
	public void removeNonWords(){
		statusText = statusText.replaceAll("[^a-zA-Z0-9\\s]", "");
	}
	
	public void removeRTs(){
		statusText= statusText.replaceAll("\\bRT\\b", "");
	}
	/**
	 * The method removes all the hashtags, screen names, urls and RTs from the given string
	 */
	public void removeAll(){
		removeHashTags();
		removeScreenNames();
		removeUrls();
		removeNonWords();
		removeRTs();
	}
	/**
	 * The method returns the stored text.
	 * @return
	 */
	public String getText(){
		return statusText.trim();
	}
	
	
	public static void main(String[] args){
		Remover r = new Remover("RT RT Hello #sheffield #university @tudor /= http://sheffield.ac.uk www.sheffield.ac.uk");
		r.removeAll();
		System.out.println(r.getText());
	}
}
