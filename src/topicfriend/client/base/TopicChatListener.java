package topicfriend.client.base;

import topicfriend.netmessage.NetMessageChatRoom;

public interface TopicChatListener 
{
	public void onReceiveTopicChatMessage(NetMessageChatRoom msgChatRoom);
}
