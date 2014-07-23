package topicfriend.client.appcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.base.Consts;
import topicfriend.netmessage.data.TopicInfo;

public class TopicManager 
{
	private HashMap<Integer, TopicInfo> mTopicMap = new HashMap<Integer, TopicInfo>();
	
	public TopicManager() 
	{
	}
	
	public void addTopicInfoList(ArrayList<TopicInfo> topicList) 
	{
		mTopicMap.clear();
		for (TopicInfo topic : topicList)
		{
			mTopicMap.put(topic.getID(), topic);
		}
	}
	
	public TopicInfo getTopicInfoByID(int id)
	{
		return mTopicMap.get(id);
	}
	
	public TopicInfo removeTopicInfoByID(int id)
	{
		return mTopicMap.remove(id);
	}
	
	public void clearTopicInfo() 
	{
		mTopicMap.clear();
	}
	
	public List<TopicInfo> getAllTopicInfo() 
	{
		List<TopicInfo> list = new ArrayList<TopicInfo>(mTopicMap.values());
		return list;
	}
}
