package topicfriend.client.appcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Handler;

import topicfriend.client.base.Consts;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.FriendChatListener;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;

public class FriendChatManager implements NetMessageHandler
{
	private HashMap<Integer, FriendChat> mFriendChatMap = new HashMap<Integer, FriendChat>();
	private AccountManager mAccountMan=null;
	private ArrayList<FriendChatListener> mFriendChatListener=new ArrayList<FriendChatListener>();
	
	/////////////////////
	//public
	public void init(AccountManager accountMan)
	{
		mAccountMan=accountMan;
	}
	
	public void markFriendChatMessageRead(int fid)
	{
		FriendChat fc=mFriendChatMap.get(fid);
		if(fc!=null)
		{
			fc.clearUnreadCount();
		}
	}
	
	public void addMessage(MessageInfo msgInfo,boolean hasRead)
	{
		UserInfo loginUser=mAccountMan.getLoginUserInfo();
		assert(loginUser!=null);
		
		int friendID=(msgInfo.getSenderID()==loginUser.getID()?
				msgInfo.getTargetID():msgInfo.getSenderID());
		addMessageToMap(friendID, msgInfo, hasRead);
	}
	
	public void addNewMessageInfoList(ArrayList<MessageInfo> messageList)
	{
		UserInfo loginUser=mAccountMan.getLoginUserInfo();
		assert(loginUser!=null);
		
		for (MessageInfo message : messageList) 
		{
			addMessage(message, false);
		}
	}
	
	public FriendChat getFriendChatByFriendID(int fid) 
	{ 
		return mFriendChatMap.get(fid);
	}
	
	public List<FriendChat> getAllFriendChat()
	{
		List<FriendChat> channelArray = new ArrayList<FriendChat>();
		for (FriendChat channel : mFriendChatMap.values())
		{
			channelArray.add(channel);
		}
		return channelArray;
	}
	
	public FriendChat removeFriendChatByID(int fid) 
	{
		return mFriendChatMap.remove(fid);
	}
	
	//friend chat listener
	public void addFriendChatListener(FriendChatListener listener)
	{
		mFriendChatListener.add(listener);
	}
	
	public void removeFriendChatListener(FriendChatListener listener)
	{
		mFriendChatListener.remove(listener);
	}
	
	public void clearFriendChatListener()
	{
		mFriendChatListener.clear();
	}

	//register message handler
	public void registerMessageHandler()
	{
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.CHAT_FRIEND, this);
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
				handleMessageUIThread(connection, msg);
			}
		});
	}
	
	///////////////////////////
	//private
	private void handleMessageChatFriend(int connection,NetMessage msg)
	{
		NetMessageChatFriend msgChatFriend=(NetMessageChatFriend)msg;
		MessageInfo msgInfo=new MessageInfo(msgChatFriend.getFriendID(),mAccountMan.getUserID(),msgChatFriend.getTimestamp(),msgChatFriend.getContent());
		addMessage(msgInfo, false);
		for(int i=0;i<mFriendChatListener.size();i++)
		{
			FriendChatListener listener=mFriendChatListener.get(i);
			listener.onReceiveFriendMessage(msgChatFriend);
		}
	}
	
	private void handleMessageUIThread(int connection,NetMessage msg)
	{
		switch(msg.getMessageID())
		{
		case NetMessageID.CHAT_FRIEND:
			handleMessageChatFriend(connection, msg);
			break;
		}
	}
	
	private void addMessageToMap(int id,MessageInfo msgInfo,boolean hasRead)
	{
		FriendChat fc=mFriendChatMap.get(id);
		if(fc!=null)
		{
			fc.addMessage(msgInfo, hasRead);
		}
		else
		{
			fc=new FriendChat(id);
			fc.addMessage(msgInfo, hasRead);
			mFriendChatMap.put(id, fc);
		}
	}
}
