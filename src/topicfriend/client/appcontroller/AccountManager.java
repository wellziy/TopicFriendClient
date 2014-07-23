package topicfriend.client.appcontroller;

import java.util.ArrayList;

import topicfriend.client.base.LoginListener;
import topicfriend.client.base.UserInfoUpdateListener;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.NetMessageLogin;
import topicfriend.netmessage.NetMessageLoginSucceed;
import topicfriend.netmessage.NetMessageRegister;
import topicfriend.netmessage.NetMessageUpdateUserInfo;
import topicfriend.netmessage.NetMessageUpdateUserInfoSucceed;
import topicfriend.netmessage.data.UserInfo;
import android.os.Handler;

public class AccountManager implements NetMessageHandler
{
	private UserInfo mLoginUserInfo=null;
	private ArrayList<LoginListener> mLoginListener=new ArrayList<LoginListener>();
	private ArrayList<UserInfoUpdateListener> mUserInfoUpdateListener=new ArrayList<UserInfoUpdateListener>();
	
	private String mLastUserName="";
	private String mLastPassword="";
	private UserInfo mNeedUpdateInfo=null;
	
	///////////////////////////////
	//public
	public AccountManager()
	{
	}
	
	//login listener management
	public void addLoginListener(LoginListener listener)
	{
		mLoginListener.add(listener);
	}
	
	public void removeLoginListener(LoginListener listener)
	{
		mLoginListener.remove(listener);
	}
	
	public void clearLoginListener()
	{
		mLoginListener.clear();
	}
	
	//user info update listener
	public void addUserInfoUpdateListener(UserInfoUpdateListener listener)
	{
		mUserInfoUpdateListener.add(listener);
	}
	
	public void removeUserInfoUpdateListener(UserInfoUpdateListener listener)
	{
		mUserInfoUpdateListener.remove(listener);
	}
	
	public void clearUserInfoUpdateListener()
	{
		mUserInfoUpdateListener.clear();
	}
	
	public void login(String userName,String password)
	{
		mLastUserName=userName;
		mLastPassword=password;
		
		//send login message here
		NetMessageLogin msgLogin=new NetMessageLogin(userName, password);
		AppController.getInstance().getNetworkManager().sendDataOne(msgLogin);
		registerMessagedHandler();
	}
	
	public void register(String userName,String password)
	{
		mLastUserName=userName;
		mLastPassword=password;
		
		//send register message here
		NetMessageRegister msgRegister=new NetMessageRegister(userName, password, UserInfo.SEX_MALE);
		AppController.getInstance().getNetworkManager().sendDataOne(msgRegister);
		registerMessagedHandler();
	}
	
	public boolean isUserLogin()
	{
		return mLoginUserInfo!=null;
	}
	
	public String getLastUserName()
	{
		return mLastUserName;
	}
	
	public String getLastPassword()
	{
		return mLastPassword;
	}
	
	public String getUserName()
	{
		return mLoginUserInfo.getName();
	}
	
	public int getUserSex()
	{
		return mLoginUserInfo.getSex();
	}
	
	public String getUserSignature()
	{
		return mLoginUserInfo.getSignature();
	}
	
	public String getUserIcon()
	{
		return mLoginUserInfo.getIcon();
	}
	
	public int getUserID()
	{
		return mLoginUserInfo.getID();
	}
	
	public UserInfo getLoginUserInfo()
	{
		return mLoginUserInfo;
	}
	
	public void reqUpdateUserInfo(UserInfo newInfo)
	{
		mNeedUpdateInfo=newInfo;
		registerMessagedHandler();
		
		NetMessageUpdateUserInfo msgUpdateUserInfo=new NetMessageUpdateUserInfo(newInfo);
		AppController.getInstance().getNetworkManager().sendDataOne(msgUpdateUserInfo);
	}

	@Override
	public void handleMessage(final int connection, final NetMessage msg)
	{
		Handler handler=AppController.getInstance().getHandler();
		handler.post(new Runnable()
		{
			@Override
			public void run() 
			{
				handleMessageInUIThread(connection, msg);
			}
		});
	}
	
	/////////////////////////////////
	//private
	public void handleMessageInUIThread(int connection,NetMessage msg)
	{
		switch(msg.getMessageID())
		{
		case NetMessageID.LOGIN_SUCCEED:
			handleMessageLoginSucceed(connection, msg);
			break;
			
		case NetMessageID.ERROR:
			handleMessageError(connection, msg);
			break;
			
		case NetMessageID.UPDATE_USER_INFO_SUCCEED:
			handleMessageUpdateUserInfoSucceed(connection, msg);
			break;
		}
	}
	
	private void handleMessageLoginSucceed(int connection,NetMessage msg)
	{
		//remove message handler not to handle other message
		removeMessageHandler();
		
		NetMessageLoginSucceed msgLoginSucceed=(NetMessageLoginSucceed)msg;
		//set login user info
		mLoginUserInfo=msgLoginSucceed.getMyInfo();
		
		//add friend info list
		FriendManager friendMan=AppController.getInstance().getFriendManager();
		friendMan.addFriendInfoList(msgLoginSucceed.getFriendInfoList());
		//TODO: register friend manager message handler
		
		//add topic list
		TopicManager topicMan=AppController.getInstance().getTopicManager();
		topicMan.addTopicInfoList(msgLoginSucceed.getTopicList());
		
		//friend chat message
		FriendChatManager friendChatMan=AppController.getInstance().getFriendChatManager();
		friendChatMan.addNewMessageInfoList(msgLoginSucceed.getUnreadMessageList());
		friendChatMan.registerMessageHandler();
		
		//topic chat manager
		TopicChatManager topicChatMan=AppController.getInstance().getTopicChatManager();
		topicChatMan.registerMessageHandler();
		
		ArrayList<LoginListener> copyListener=new ArrayList<LoginListener>(mLoginListener);
		for(int i=0;i<copyListener.size();i++)
		{
			LoginListener listener=copyListener.get(i);
			listener.onLoginSucceed(msgLoginSucceed);
		}
	}
	
	private void handleMessageError(int connection,NetMessage msg)
	{
		//remove message handler not to handle other message
		removeMessageHandler();
		NetMessageError msgError=(NetMessageError)msg;
		
		if(mNeedUpdateInfo!=null)
		{
			//update user info failed
			ArrayList<UserInfoUpdateListener> copyListener=new ArrayList<UserInfoUpdateListener>(mUserInfoUpdateListener);
			for(int i=0;i<copyListener.size();i++)
			{
				UserInfoUpdateListener listener=copyListener.get(i);
				listener.onUserInfoUpdateFailed(msgError);
			}
			mNeedUpdateInfo=null;
		}
		else
		{
			//login failed
			ArrayList<LoginListener> copyListener=new ArrayList<LoginListener>(mLoginListener);
			for(int i=0;i<copyListener.size();i++)
			{
				LoginListener listener=copyListener.get(i);
				listener.onLoginError(msgError);
			}
		}
	}
	
	private void handleMessageUpdateUserInfoSucceed(int connection,NetMessage msg)
	{
		//remove message handler not to handle other message
		removeMessageHandler();
		NetMessageUpdateUserInfoSucceed msgSucceed=(NetMessageUpdateUserInfoSucceed)msg;
		
		UserInfo oldInfo=mLoginUserInfo;
		mLoginUserInfo=mNeedUpdateInfo;
		mNeedUpdateInfo=null;
		
		ArrayList<UserInfoUpdateListener> copyListener=new ArrayList<UserInfoUpdateListener>(mUserInfoUpdateListener);
		for(int i=0;i<copyListener.size();i++)
		{
			UserInfoUpdateListener listener=copyListener.get(i);
			listener.onUserInfoUpdated(oldInfo, mLoginUserInfo);
		}
	}
	
	private void registerMessagedHandler()
	{
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.LOGIN_SUCCEED,this);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.ERROR, this);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.UPDATE_USER_INFO_SUCCEED, this);
	}
	
	private void removeMessageHandler()
	{
		NetMessageReceiver.getInstance().removeMessageHandler(this);
	}
}










