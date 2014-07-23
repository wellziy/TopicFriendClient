package topicfriend.client.appcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import topicfriend.client.base.Consts;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.OnChatFriendListener;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;

public class FriendChatManager 
{
	private HashMap<Integer, FriendChat> mFriendChatMap = new HashMap<Integer, FriendChat>();
	private AccountManager mAccountMan=null;
	
	///////////////////////////////
	//private
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
	
	/////////////////////
	//public
	public void init(AccountManager accountMan)
	{
		mAccountMan=accountMan;
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
}
