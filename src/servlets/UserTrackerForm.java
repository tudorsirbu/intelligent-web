package servlets;
/**
 * The class stores the information collected from a form completion
 * @author tudorsirbu
 *
 */
public class UserTrackerForm {
	/* Form fields */
	
	// the ids of the users
	private long[] ids;
	
	// number of most frequent keywords
	private int keywordsCount;
	
	// number of days
	private int lastDays;
	
	public UserTrackerForm(String ids, int keywordsCount, int lastDays){
		this.lastDays = lastDays;
		this.keywordsCount = keywordsCount;
		
	}
	
	/**
	 * The method converts a strings of ids separated through commas
	 * into an array of longs and sets it to the local variable ids.
	 * @param ids the string with ids separated by commas
	 */
	public void convertToIds(String ids){
		// split the string
		String[] idsArray = ids.split(",");
		
		// for each string id
		for(int i=0; i<idsArray.length; i++){
			// convert it to long type and add it to the array
			this.ids[i] = Long.parseLong(idsArray[i]);
		}
	}

	public long[] getIds() {
		return ids;
	}

	public void setIds(long[] ids) {
		this.ids = ids;
	}

	public int getKeywordsCount() {
		return keywordsCount;
	}

	public void setKeywordsCount(int keywordsCount) {
		this.keywordsCount = keywordsCount;
	}

	public int getLastDays() {
		return lastDays;
	}

	public void setLastDays(int lastDays) {
		this.lastDays = lastDays;
	}
	
	
	
}
