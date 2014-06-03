package topicfriend.client.database;

public class Topic {
	private String mTitle;
	private int mNumOfOnline;
	private int mTID;
	
	public String getTitle() { return mTitle; }
	public int getNumOfOnline() { return mNumOfOnline; }
	public int getID() { return mTID; }
	
	public Topic(int tid, String title) {
		mTID = tid;
		mTitle = title;
		mNumOfOnline = 0;
	}
	
}
