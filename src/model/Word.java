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

	public Word(String id, String word, int indexable) {
		this.id = id;
		this.word = word;
		if(indexable ==0 )
			this.indexable = false;
		else
			this.indexable = true;
	}
	
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
