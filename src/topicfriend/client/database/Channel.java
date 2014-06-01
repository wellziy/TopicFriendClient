package topicfriend.client.database;

import java.util.ArrayList;
import java.util.List;

public class Channel {
	
	int mParticipantID;
	private List<ChatMessage> mMsgArray = new ArrayList<ChatMessage>();
	
	// constructors
	public Channel(int participantID) {
		mParticipantID = participantID;
	}
	
	// Message Management
	public List<ChatMessage> getAll() {
		return mMsgArray;
	}
	
	public ChatMessage add(int senderID, String msg, long timestamp) {
		ChatMessage chatMsg = new ChatMessage(senderID, timestamp, msg);
		mMsgArray.add(chatMsg);
		return chatMsg;
	}
	
	public ChatMessage push(int senderID, String msg) {
		return add(senderID, msg, TimeUtil.getCurrentTimestamp());
	}
	
	// misc
	public int getParticipantID() { 
		return mParticipantID; 
	}
	
	public ChatMessage getLastMessage() {
		if (mMsgArray.size() == 0) {
			return null;
		}
		else {
			return mMsgArray.get(mMsgArray.size()-1);
		}
	}
	
}
