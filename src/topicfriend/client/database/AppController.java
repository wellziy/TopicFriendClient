package topicfriend.client.database;

import android.util.Log;
import topicfriend.client.network.NetworkManager;
import topicfriend.netmessage.data.UserInfo;

public class AppController {
	
	private static AppController sController = null;
	private AppController() {
		mNetManager = new NetworkManager();
		mUserManager = new UserManager();
		mChannelManager = new ChannelManager();
		mTopicManager = new TopicManager();
	};
	
	// Singleton
	public static synchronized AppController getInstance() {
		if (sController == null) {
			sController = new AppController();
		}
		return sController;
	}
	
	public static synchronized void purgeInstance() {
		sController = null;
	}
	
	// global data
	private int mCurrentUserID = -1;
	private UserManager mUserManager = null;
	private ChannelManager mChannelManager = null;
	private TopicManager mTopicManager = null;
	private NetworkManager mNetManager = null;
		
	public void initNetwork() {
		mNetManager.init();
	}
	
	public void destroyNetwork() {
		mNetManager.destroy();
	}
	
	public void initWithUid(int uid) {
		mCurrentUserID = uid;
		mUserManager.initWithUid(uid);
		mChannelManager.initWithUid(uid);
		mTopicManager.initWithUid(uid);
		
		////////////////////////////////////////////////////
//		mUserManager.add(new UserInfo(mCurrentUserID, 0, "Beadle", "signature", ""));
//		for (int i=0; i<15; ++i) {
//			UserInfo user = new UserInfo(i, 0, "name"+i, "signature"+i, "icon_"+(int)(1+Math.random()*8)+".jpg");
//			mUserManager.add(user);
//		}
//		
//		for (int i=0; i<15; ++i) {
//			Channel channel = mChannelManager.add(i);
//			for (int j=0; j<(int)(Math.random() * 8); ++j) {
//				channel.add(i, "message "+j+" from user "+i, TimeUtil.getCurrentTimestamp() + j*1000);
//			}
//			
//		}
//		
//		for (int i=0; i<10; ++i) {
//			mTopicManager.add("topic title "+i);
//		}
		
		
	}
	
	public int getOwnerID() { return mCurrentUserID; }
	public UserManager getUserManager() { return mUserManager; }
	public ChannelManager getChannelManager() { return mChannelManager; }
	public TopicManager getTopicManager() { return mTopicManager; }
	public NetworkManager getNetworkManager() { return mNetManager; }
	
	
	// helper methods
	public static void log(String str) {
		Log.w("client", str);
	}
	
}
