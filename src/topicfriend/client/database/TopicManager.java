package topicfriend.client.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import topicfriend.netmessage.data.TopicInfo;

public class TopicManager {

	private int mTopicCounter = 0;
	private int mOwnerID = Consts.InvalidID;
	private Map<Integer, TopicInfo> mTopicMap = new LinkedHashMap<Integer, TopicInfo>();
	
	public int getOwnerID() { return mOwnerID; }
	
	public TopicManager() {
		
	}
	
	public void initWithUid(int ownerID) {
		mOwnerID = ownerID;
	}
	
	public void refreshData(ArrayList<TopicInfo> topicList) {
		mTopicMap.clear();
		for (TopicInfo topic : topicList) {
			mTopicMap.put(topic.getID(), topic);
		}
	}
	
	public TopicInfo add(String title) {
		TopicInfo topic = new TopicInfo(mTopicCounter++, title, "");
		mTopicMap.put(topic.getID(), topic);
		return topic;
	}
	
	public TopicInfo getByID(int id) {
		return mTopicMap.get(id);
	}
	
	public TopicInfo removeByID(int id) {
		return mTopicMap.remove(id);
	}
	
	public void clear() {
		mTopicMap.clear();
	}
	
	public List<TopicInfo> getAll() {
		List<TopicInfo> list = new ArrayList<TopicInfo>();
		for (TopicInfo topic : mTopicMap.values()) {
			list.add(topic);
		}
		return list;
	}
}
