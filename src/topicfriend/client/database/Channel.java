package topicfriend.client.database;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import topicfriend.netmessage.data.MessageInfo;

public class Channel {
	
	int mOwnerID = Consts.InvalidID;
	int mParticipantID = Consts.InvalidID;
	private List<MessageInfo> mMsgArray = new ArrayList<MessageInfo>();
	
	// constructors
	public Channel(int ownerID, int participantID) {
		mOwnerID = ownerID;
		mParticipantID = participantID;
	}
	
	// Message Management
	public List<MessageInfo> getAll() {
		return mMsgArray;
	}
	
	public MessageInfo add(int senderID, String msg, long timestamp) {
		int targetID = mParticipantID;
		if (senderID == mParticipantID) {
			targetID = mOwnerID;
		}
		MessageInfo chatMsg = new MessageInfo(senderID, targetID, new Timestamp(timestamp), msg);
		mMsgArray.add(chatMsg);
		return chatMsg;
	}
	
	public MessageInfo push(int senderID, String msg) {
		return add(senderID, msg, TimeUtil.getCurrentTimestamp());
	}
	
	// misc
	public int getParticipantID() { 
		return mParticipantID; 
	}
	
	public MessageInfo getLastMessage() {
		if (mMsgArray.size() == 0) {
			return null;
		}
		else {
			return mMsgArray.get(mMsgArray.size()-1);
		}
	}
	
}
