package topicfriend.client.appcontroller;

import android.os.Handler;
import android.util.Log;

public class AppController
{
	//static instance
	private static AppController sController = null;
	//all managers
	private AccountManager mAccountManager=new AccountManager();
	private FriendManager mFriendManager=new FriendManager();
	private FriendChatManager mFriendChatManager=new FriendChatManager();
	private TopicManager mTopicManager=new TopicManager();
	private TopicChatManager mTopicChatManager=new TopicChatManager();
	private ResourceManager mResourceManager=new ResourceManager();
	private NetworkManager mNetManager=new NetworkManager();
	private AppActivityManager mActivityManager=new AppActivityManager();
	//shared handler
	private Handler mHandler=new Handler();
	
	private AppController()
	{
		mFriendChatManager.init(mAccountManager);
		mTopicChatManager.init(mAccountManager);
	}
	
	// Singleton
	public static synchronized AppController getInstance() 
	{
		if (sController == null)
		{
			sController = new AppController();
		}
		return sController;
	}
	
	public static synchronized void purgeInstance() 
	{
		sController = null;
	}
	
	//must call before connect to host
	public void initNetwork() 
	{
		mNetManager.init();
	}
	
	//must call before exit the app
	public void destroyNetwork() 
	{
		mNetManager.destroy();
	}
	
	public AccountManager getAccountManager() 
	{ 
		return mAccountManager; 
	}
	
	public FriendManager getFriendManager()
	{
		return mFriendManager;
	}
	
	public FriendChatManager getFriendChatManager()
	{ 
		return mFriendChatManager; 
	}
	
	public TopicManager getTopicManager() 
	{ 
		return mTopicManager; 
	}
	
	public TopicChatManager getTopicChatManager()
	{
		return mTopicChatManager;
	}
	
	public NetworkManager getNetworkManager()
	{ 
		return mNetManager; 
	}
	
	public ResourceManager getResourceManager()
	{
		return mResourceManager;
	}
	
	public Handler getHandler()
	{
		return mHandler;
	}
}
