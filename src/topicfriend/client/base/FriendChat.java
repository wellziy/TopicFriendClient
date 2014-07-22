package topicfriend.client.base;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import topicfriend.client.util.TimeUtil;
import topicfriend.netmessage.data.MessageInfo;

public class FriendChat
{
	private int mFriendID=Consts.InvalidID;
	//the message is sorted by timestamp
	private List<MessageInfo> mMsgArray = new ArrayList<MessageInfo>();
	private int mUnreadCount=0;
	
	// constructors
	public FriendChat(int fid)
	{
		mFriendID=fid;
	}
	
	public int getFriendID()
	{
		return mFriendID;
	}
	
	public void addMessage(MessageInfo msgInfo,boolean hasRead)
	{
		mMsgArray.add(msgInfo);
		if(!hasRead)
		{
			mUnreadCount++;
		}
	}
	
	public int getUnreadCount()
	{
		return mUnreadCount;
	}
	
	public void clearUnreadCount()
	{
		mUnreadCount=0;
	}
	
	// Message Management
	public List<MessageInfo> getAllMessageInfo() 
	{
		return mMsgArray;
	}

	public MessageInfo getLastMessage() 
	{
		if (mMsgArray.size() == 0)
		{
			return null;
		}
		else 
		{
			return mMsgArray.get(mMsgArray.size()-1);
		}
	}
}
