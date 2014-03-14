package api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUserVenuesStream {
	private TwitterStream twitterStream;
	public void initConfiguration(long[] userID){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("H4VHRaf8ybmPhzzK47uQ")
		.setOAuthConsumerSecret("y6oxNsvuoauf4sPcGU45Ct5eVfryYlai5TUBU92Uxbk");

		TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(cb.build());
		twitterStream = twitterStreamFactory.getInstance(new AccessToken("1225017144-1l22gHEw6SpxoQQac1PmT5a3FjQnexJrMQmiFra", "WR2I8lHSBlqVKHV1a3t3CDElHKe0sHkVl1TCLyrVnrkLS"));
		twitterStream.addListener(userStreamListener);

		FilterQuery fq = new FilterQuery();

		fq.follow(userID);
		twitterStream.filter(fq);


	}

	UserStreamListener userStreamListener = new UserStreamListener() {

		@Override
		public void onException(Exception arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTrackLimitationNotice(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatus(Status status) {
			// TODO Auto-generated method stub
			FoursquareManager fm = new FoursquareManager();
			TwitterManager tm = new TwitterManager();
			ArrayList<String> urls = new ArrayList<String>();
			urls = tm.extractURL(status);
			for(String url : urls){
				String name = fm.getVenueName(url);
				if(name!=null)
					System.out.println(name);
			}

			
		}


		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserProfileUpdate(User arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListUpdate(User arg0, UserList arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListDeletion(User arg0, UserList arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUserListCreation(User arg0, UserList arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUnfavorite(User arg0, User arg1, Status arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUnblock(User arg0, User arg1) {
			// TODO Auto-generated method stub

		}



		@Override
		public void onFriendList(long[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFollow(User arg0, User arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFavorite(User arg0, User arg1, Status arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDirectMessage(DirectMessage message) {
			// TODO Auto-generated method stub
			System.out.println(message.getText());
		}

		@Override
		public void onDeletionNotice(long arg0, long arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onBlock(User arg0, User arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO Auto-generated method stub

		}
	};
}
