package topicfriend.client.database;

public class AppController {
	
	private static AppController sController = new AppController();
	private AppController() {};
	
	// Singleton
	public static AppController getInstance() {
		return sController;
	}
	
	// global data
	private int mCurrentUserID = -1;
	private UserManager mUserManager = null;
	private ChannelManager mChannelManager = null;
	private TopicManager mTopicManager = null;
	
	public void initWithUid(int uid) {
		mCurrentUserID = uid;
		mUserManager = new UserManager(uid);
		mChannelManager = new ChannelManager(uid);
		mTopicManager = new TopicManager(uid);
		
		////////////////////////////////////////////////////
		mUserManager.add(new User(mCurrentUserID, "Beadle", "signature", ""));
		for (int i=0; i<15; ++i) {
			User user = new User(i, "name"+i, "signature"+i, "icon_"+(int)(1+Math.random()*8)+".jpg");
			if (Math.random() > 0.5f) {
				user.setRelation(User.Relation.RELATION_FRIEND);
			}
			mUserManager.add(user);
		}
		
		for (int i=0; i<15; ++i) {
			Channel channel = mChannelManager.add(i);
			for (int j=0; j<(int)(Math.random() * 8); ++j) {
				channel.add(i, "message "+j+" from user "+i, TimeUtil.getCurrentTimestamp() + j*1000);
			}
			
		}
		
		for (int i=0; i<10; ++i) {
			mTopicManager.add("topic title "+i);
		}
		
		
	}
	
	public int getOwnerID() { return mCurrentUserID; }
	public UserManager getUserManager() { return mUserManager; }
	public ChannelManager getChannelManager() { return mChannelManager; }
	public TopicManager getTopicManager() { return mTopicManager; }
	 
}
