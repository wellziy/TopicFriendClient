package topicfriendclient;

public class ChatMessage {
	
	private int mSenderID;
	private long mTimestamp;
	private String mContent;
	
	public ChatMessage(int senderID, long timestamp, String content) {
		mSenderID = senderID;
		mTimestamp = timestamp;
		mContent = content;
	}
	
	// getters and setters
	int getSenderID() { return mSenderID; }
	long getTimestamp() { return mTimestamp; } 
	String getContent() { return mContent; }
	
}
