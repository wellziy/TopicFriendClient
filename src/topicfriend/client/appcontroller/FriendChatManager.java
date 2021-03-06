package topicfriend.client.appcontroller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import topicfriend.client.base.FriendChat;
import topicfriend.client.base.FriendChatListener;
import topicfriend.client.db.MessageDAO;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;
import android.os.Handler;

public class FriendChatManager implements NetMessageHandler
{
	private HashMap<Integer, FriendChat> mFriendChatMap = new HashMap<Integer, FriendChat>();
	private ArrayList<FriendChatListener> mFriendChatListener=new ArrayList<FriendChatListener>();
	
	/////////////////////
	//public
	public void markFriendChatMessageRead(int fid)
	{
		FriendChat fc=mFriendChatMap.get(fid);
		if(fc!=null)
		{
			fc.clearUnreadCount();
		}
	}
	
	public void resetLoginState()
	{
		mFriendChatMap.clear();
		mFriendChatListener.clear();
	}
	
	public void addMessage(MessageInfo msgInfo,boolean hasRead)
	{
		AccountManager accountMan=AppController.getInstance().getAccountManager();
		
		UserInfo loginUser=accountMan.getLoginUserInfo();
		assert(loginUser!=null);
		
		int friendID=(msgInfo.getSenderID()==loginUser.getID()?
				msgInfo.getTargetID():msgInfo.getSenderID());
		addMessageToMap(friendID, msgInfo, hasRead);
	}
	
	public void addMessageInfoList(ArrayList<MessageInfo> messageList,boolean hasRead)
	{
		AccountManager accountMan=AppController.getInstance().getAccountManager();
		
		UserInfo loginUser=accountMan.getLoginUserInfo();
		assert(loginUser!=null);
		
		for (MessageInfo message : messageList) 
		{
			addMessage(message, hasRead);
		}
	}
	
	public FriendChat getFriendChatByFriendID(int fid) 
	{ 
		return mFriendChatMap.get(fid);
	}
	
	public FriendChat createFriendChat(int fid)
	{
		FriendChat fc=new FriendChat(fid);
		mFriendChatMap.put(fid, fc);
		return fc;
	}
	
	public List<FriendChat> getAllFriendChat()
	{
		List<FriendChat> fcArray = new ArrayList<FriendChat>(mFriendChatMap.values());
		return fcArray;
	}
	
	public List<FriendChat> getAllFriendChatOrderByLastMessageTS()
	{
		ArrayList<FriendChat> res=new ArrayList<FriendChat>();
		for(FriendChat fc: mFriendChatMap.values())
		{
			if(fc.getLastMessage()!=null)
			{
				res.add(fc);
			}
		}
		
		Collections.sort(res,new Comparator<FriendChat>() 
		{
			@Override
			public int compare(FriendChat lhs, FriendChat rhs)
			{
				Timestamp t1=lhs.getLastMessage().getTimetamp();
				Timestamp t2=rhs.getLastMessage().getTimetamp();
				return t2.compareTo(t1);
			}
		});
		
		return res;
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
	
	public void sendFriendChatMessage(int fid,String content)
	{
		NetMessageChatFriend msgChatFriend=new NetMessageChatFriend(fid, content, null);
		AppController.getInstance().getNetworkManager().sendDataOne(msgChatFriend);
		
		//store the message
		AccountManager accountMan=AppController.getInstance().getAccountManager();
		MessageInfo msgInfo=new MessageInfo(accountMan.getUserID(), fid, new Timestamp(System.currentTimeMillis()), content);
		addMessage(msgInfo, true);
		
		//save message to db
		MessageDAO msgDAO=new MessageDAO(AppController.getInstance().getContext());
		msgDAO.insertMessageInfo(accountMan.getLoginUserInfo().getID(),msgInfo);
	}
	
	///////////////////////////
	//private
	private void handleMessageChatFriend(int connection,NetMessage msg)
	{
		AccountManager accountMan=AppController.getInstance().getAccountManager();
		
		//store the message
		NetMessageChatFriend msgChatFriend=(NetMessageChatFriend)msg;
		MessageInfo msgInfo=new MessageInfo(msgChatFriend.getFriendID(),accountMan.getUserID(),msgChatFriend.getTimestamp(),msgChatFriend.getContent());
		addMessage(msgInfo, false);
		
		//save message to db
		MessageDAO msgDAO=new MessageDAO(AppController.getInstance().getContext());
		msgDAO.insertMessageInfo(accountMan.getLoginUserInfo().getID(),msgInfo);
		
		//notify all listeners
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
	
	private void addMessageToMap(int fid,MessageInfo msgInfo,boolean hasRead)
	{
		FriendChat fc=mFriendChatMap.get(fid);
		if(fc!=null)
		{
			fc.addMessage(msgInfo, hasRead);
		}
		else
		{
			fc=new FriendChat(fid);
			fc.addMessage(msgInfo, hasRead);
			mFriendChatMap.put(fid, fc);
		}
	}
}
