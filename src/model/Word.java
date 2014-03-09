package model;
/**
 * The class represents a word from a tweet
 * @author tudorsirbu
 *
 */
public class Word {
	// word details
	private String id;
	private String word;
	
	public Word(String id, String word) {
		super();
		this.id = id;
		this.word = word;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
