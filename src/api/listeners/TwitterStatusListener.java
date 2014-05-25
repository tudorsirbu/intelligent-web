package api.listeners;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import api.TwitterManager;
/**
 * This class consists of methods that use the Twitter Streaming API in order to listen for statuses 
 * that contain a Foursquare check-in, and store the status. 
 * The class works in tight connection with TwitterManager.
 * 
 * 
 * @author Tudor-Daniel Sirbu
 * @author Claudiu Tarta
 * @author Florin-Cristian Gavrila
 * 
 */
public class TwitterStatusListener implements StatusListener {

	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatus(Status tweet) {
		TwitterManager tm = TwitterManager.getInstance();
		tm.handleNewStatusInStream(tweet);
		System.out.println(tweet.getText());
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub

	}

}
