package topicfriend.client.database;

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
	public int getSenderID() { return mSenderID; }
	public long getTimestamp() { return mTimestamp; } 
	public String getContent() { return mContent; }
	
}
