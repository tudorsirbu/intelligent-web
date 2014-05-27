package util;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 * The class loads from a file the stop list builder
 * @author cristi
 *
 */
public class StoplistBuilder {
	/**
	 * Returns a list of stopword.
	 * 
	 * @return a list of words that should be excluded from any shown keyword.
	 */
	public ArrayList<String> getStoplist(){
		// the list of words that should not be taken into account
		ArrayList<String> stoplist = new ArrayList<String>();
		
		// get the stoplist from the text file
		String file = "src/util/stoplist.txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
		try {
			// loop through the lines and create the stop list
			while ((line = br.readLine()) != null) {
			   stoplist.add(line.trim());
			}		
			// close the buffer
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stoplist;
	}
}
