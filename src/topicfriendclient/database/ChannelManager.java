package topicfriendclient.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChannelManager {

	private int mOwnerID;
	private Map<Integer, Channel> mChannelMap = new LinkedHashMap<Integer, Channel>();
	
	public ChannelManager(int ownerID) {
		mOwnerID = ownerID;
	}
	
	public int getOwnerID() { return mOwnerID; }
	
	public Channel add(int participantID) {
		Channel tmpChannel = getByID(participantID);
		if (tmpChannel != null) {
			System.out.println("the channel has been added");
		}
		else {
			tmpChannel = new Channel(participantID);
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
