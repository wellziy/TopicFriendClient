package topicfriend.client.appcontroller;

import java.util.ArrayList;

import topicfriend.client.base.Consts;
import topicfriend.client.base.TopicChatListener;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageChatRoom;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.NetMessageJoinTopic;
import topicfriend.netmessage.NetMessageLeaveRoom;
import topicfriend.netmessage.NetMessageLike;
import topicfriend.netmessage.NetMessageMatchSucceed;
import topicfriend.netmessage.NetMessageNewFriend;
import topicfriend.netmessage.data.TopicInfo;
import topicfriend.netmessage.data.UserInfo;
import android.os.Handler;

public class TopicChatManager implements NetMessageHandler
{
	private ArrayList<TopicChatListener> mTopicChatListener=new ArrayList<TopicChatListener>();
	private UserInfo mMatchedUserInfo=null;
	private int mLastMatchingTopicID=Consts.InvalidID;
	private int mMatchedTopicID=Consts.InvalidID;
	//TODO: not implement yet,the counter is used to check whether an user give up matching a topic and start to match another topic,
	//and the two topic may has the same topicID
	private int mMatchingCounter=0;
	
	public UserInfo getMatchedUserInfo()
	{
		return mMatchedUserInfo;
	}
	
	//topic chat listener management
	public void addTopicChatListener(TopicChatListener listener)
	{
		mTopicChatListener.add(listener);
	}
	
	public void removeTopicChatListener(TopicChatListener listener)
	{
		mTopicChatListener.remove(listener);
	}
	
	public void clearTopicChatListener()
	{
		mTopicChatListener.clear();
	}
	
	public int getMatchedTopicID()
	{
		return mMatchedTopicID;
	}
	
	public TopicInfo getMatchedTopicInfo()
	{
		return AppController.getInstance().getTopicManager().getTopicInfoByID(mMatchedTopicID);
	}
	
	public void registerMessageHandler()
	{
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.CHAT_ROOM, this);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.LEAVE_ROOM, this);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.NEW_FRIEND, this);
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.MATCH_SUCCEED, this);
	}
	
	public void removeMessageHandler()
	{
		NetMessageReceiver.getInstance().removeMessageHandler(this);
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
	
	public void reqJoinTopic(int topicID)
	{
		mMatchingCounter++;
		mLastMatchingTopicID=topicID;
		
		NetMessageJoinTopic msgJoinTopic=new NetMessageJoinTopic(topicID);
		AppController.getInstance().getNetworkManager().sendDataOne(msgJoinTopic);
	}
	
	public void reqLeaveRoom()
	{
		mMatchedTopicID=Consts.InvalidID;
		
		NetMessageLeaveRoom msgLeaveRoom=new NetMessageLeaveRoom();
		AppController.getInstance().getNetworkManager().sendDataOne(msgLeaveRoom);
	}
	
	public void reqLike()
	{
		NetMessageLike msgLike=new NetMessageLike();
		AppController.getInstance().getNetworkManager().sendDataOne(msgLike);
	}
	
	public void reqSendMessage(String content)
	{
		NetMessageChatRoom msgChatRoom=new NetMessageChatRoom(content);
		AppController.getInstance().getNetworkManager().sendDataOne(msgChatRoom);
	}
	
	////////////////////////////
	//private
	private void handleMessageInUIThread(int connection,NetMessage msg)
	{
		switch(msg.getMessageID())
		{
		case NetMessageID.CHAT_ROOM:
			handleMessageChatRoom(connection,msg);
			break;
			
		case NetMessageID.LEAVE_ROOM:
			handleMessageLeaveRoom(connection, msg);
			break;
			
		case NetMessageID.NEW_FRIEND:
			handleMessageNewFriend(connection, msg);
			break;
			
		case NetMessageID.MATCH_SUCCEED:
			handleMessageMatchSucceed(connection, msg);
			break;
		}
	}
	
	private void handleMessageChatRoom(int connection,NetMessage msg)
	{
		NetMessageChatRoom msgChatRoom=(NetMessageChatRoom)msg;
		
		ArrayList<TopicChatListener> copyListener=new ArrayList<TopicChatListener>(mTopicChatListener);
		for(int i=0;i<copyListener.size();i++)
		{
			TopicChatListener listener = copyListener.get(i);
			listener.onReceiveTopicChatMessage(msgChatRoom);
		}
	}
	
	private void handleMessageLeaveRoom(int connection,NetMessage msg)
	{
		NetMessageLeaveRoom msgLeaveRoom=(NetMessageLeaveRoom)msg;
		
		ArrayList<TopicChatListener> copyListener=new ArrayList<TopicChatListener>(mTopicChatListener);
		for(int i=0;i<copyListener.size();i++)
		{
			TopicChatListener listener=copyListener.get(i);
			listener.onOtherExitTopicChat(mMatchedUserInfo);
		}
		mMatchedUserInfo=null;
	}
	
	private void handleMessageNewFriend(int connection,NetMessage msg)
	{
		NetMessageNewFriend msgNewFriend=(NetMessageNewFriend)msg;
		
		//dispatch both like message
		{
			ArrayList<TopicChatListener> copyListener=new ArrayList<TopicChatListener>(mTopicChatListener);
			for(int i=0;i<copyListener.size();i++)
			{
				TopicChatListener listener=copyListener.get(i);
				listener.onTopicChatBothLike();
			}
		}
		
		//add the new friend into friend manager
		FriendManager friendMan=AppController.getInstance().getFriendManager();
		
		//TODO: dont need the user info from this message????just use the matched user info
		//UserInfo newFriendInfo=msgNewFriend.getNewFriendInfo();
		if(!friendMan.isMyFriend(mMatchedUserInfo.getID()))
		{
			friendMan.addFriend(mMatchedUserInfo.getID(), mMatchedUserInfo);
			
			ArrayList<TopicChatListener> copyListener=new ArrayList<TopicChatListener>(mTopicChatListener);
			for(int i=0;i<copyListener.size();i++)
			{
				TopicChatListener listener=copyListener.get(i);
				listener.onMadeNewFriend(mMatchedUserInfo);
			}
		}
	}
	
	private void handleMessageMatchSucceed(int connection,NetMessage msg)
	{
		NetMessageMatchSucceed msgMatchSucceed=(NetMessageMatchSucceed)msg;
		mMatchedTopicID=mLastMatchingTopicID;
		
		mMatchedUserInfo=msgMatchSucceed.getMatchedUserInfo();
		ArrayList<TopicChatListener> copyListener=new ArrayList<TopicChatListener>(mTopicChatListener);
		for(int i=0;i<copyListener.size();i++)
		{
			TopicChatListener listener=copyListener.get(i);
			listener.onTopicMatchSucceed(mMatchedUserInfo);
		}
	}
}
