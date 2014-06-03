package topicfriend.client.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TopicManager {

	private int mTopicCounter = 0;
	private int mOwnerID = Consts.InvalidID;
	private Map<Integer, Topic> mTopicMap = new LinkedHashMap<Integer, Topic>();
	
	public int getOwnerID() { return mOwnerID; }
	
	public TopicManager(int ownerID) {
		mOwnerID = ownerID;
	}
	
	public Topic add(String title) {
		Topic topic = new Topic(mTopicCounter++, title);
		mTopicMap.put(topic.getID(), topic);
		return topic;
	}
	
	public Topic getByID(int id) {
		return mTopicMap.get(id);
	}
	
	public Topic removeByID(int id) {
		return mTopicMap.remove(id);
	}
	
	public void clear() {
		mTopicMap.clear();
	}
	
	public List<Topic> getAll() {
		List<Topic> list = new ArrayList<Topic>();
		for (Topic topic : mTopicMap.values()) {
			list.add(topic);
		}
		return list;
	}
}
