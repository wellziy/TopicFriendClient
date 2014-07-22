package topicfriend.client.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import topicfriend.netmessage.data.MessageInfo;

public class ChannelManager {

	private int mOwnerID;
	private OnChatFriendListener mChatFriendListener = null;
	private Map<Integer, Channel> mChannelMap = new LinkedHashMap<Integer, Channel>();
	
	public ChannelManager() {
		
	}
	
	public void initWithUid(int ownerID) {
		mOwnerID = ownerID;
	}
	
	public void refreshData(ArrayList<MessageInfo> messageList) {
		for (MessageInfo message : messageList) {
			int participantID = message.getSenderID();
			if (participantID == mOwnerID) {
				participantID = message.getTargetID();
			}
			
			Channel channel = getByID(participantID);
			if (channel == null) {
				channel = add(participantID);
			}
			
			channel.add(message.getSenderID(), message.getContent(), message.getTimetamp().getTime());
		}
	}
	
	public int getOwnerID() { 
		return mOwnerID; 
	}
	
	public OnChatFriendListener getChatFriendListener() {
		return mChatFriendListener;
	}

	public void setChatFriendListener(OnChatFriendListener mChatFriendListener) {
		this.mChatFriendListener = mChatFriendListener;
	}

	public Channel add(int participantID) {
		Channel tmpChannel = getByID(participantID);
		if (tmpChannel != null) {
			System.out.println("the channel has been added");
		}
		else {
			tmpChannel = new Channel(mOwnerID, participantID);
			mChannelMap.put(participantID, tmpChannel);
		}
		return tmpChannel;
	}
	
	public Channel getByID(int participantID) {
		return mChannelMap.get(participantID);
	}
	
	public List<Channel> getAll() {
		List<Channel> channelArray = new ArrayList<Channel>();
		for (Channel channel : mChannelMap.values()) {
			channelArray.add(channel);
		}
		return channelArray;
	}
	
	public Channel removeByID(int participantID) {
		return mChannelMap.remove(participantID);
	}


	
}
