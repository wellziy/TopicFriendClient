package topicfriendclient.database;

public class User {

	public enum Relation{
		RELATION_FRIEND,
		RELATION_STRANGER
	};
	
	// attributes
	private int mUid;
	private Relation mRelation;
	private String mNickname;
	private String mSignature;
	
	// getters and setters
	public int getID() { return mUid; }
	public Relation getRelation() { return mRelation; }
	public String getNickname() { return mNickname; }
	public String getSignature() { return mSignature; }
	
	public void setRelation(Relation r) { mRelation = r; }
	
	// relation judgment
	public boolean isFriend() { return mRelation == Relation.RELATION_FRIEND; }
	public boolean isStranger() { return mRelation == Relation.RELATION_STRANGER; }
	
	// constructors
	public User(int uid, String nickname, String signature) {
		mUid = uid;
		mRelation = Relation.RELATION_STRANGER;
		mNickname = nickname;
		mSignature = signature;
	}
}
