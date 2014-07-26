package topicfriend.client.appcontroller;

import android.content.Context;
import android.os.Handler;

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
	private Context mContext=null;
	
	private AppController()
	{
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
	
	public void initContext(Context context)
	{
		mContext=context;
	}
	
	public void resetLoginState()
	{
		mAccountManager.resetLoginState();
		mFriendChatManager.resetLoginState();
		mFriendManager.resetLoginState();
		mTopicChatManager.resetLoginState();
		mTopicManager.resetLoginState();
		mNetManager.resetLoginState();
	}
	
	public static synchronized void purgeInstance() 
	{
		sController = null;
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
	
	//TODO: move resource out of application
	public ResourceManager getResourceManager()
	{
		return mResourceManager;
	}
	
	public AppActivityManager getAppActivityManager()
	{
		return mActivityManager;
	}
	
	public Handler getHandler()
	{
		return mHandler;
	}
	
	public Context getContext()
	{
		return mContext;
	}
}
