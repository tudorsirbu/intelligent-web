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
	private boolean indexable;
	
	/**
	 * Constructor
	 * @param id the id of the word
	 * @param word the word it self (eg. egg)
	 * @param indexable is it indexable? if 0 => no indexable / 1 => indexable
	 */
	public Word(String id, String word, int indexable) {
		this.id = id;
		this.word = word;
		if(indexable ==0 )
			this.indexable = false;
		else
			this.indexable = true;
	}
	/**
	 * Constructor
	 * @param id the id of the word
	 * @param word the actual word
	 * @param indexable will the word be indexed?
	 */
	public Word(String id, String word, boolean indexable){
		this.id = id;
		this.word = word;
		this.indexable = indexable;
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

	public boolean isIndexable(){
		return this.indexable;
	}
	
	public void setIndexable(boolean indexable){
		this.indexable = indexable;
	}

	public void setIndexable(int indexable){
		if(indexable ==0 )
			this.indexable = false;
		else
			this.indexable = true;
	}

}
