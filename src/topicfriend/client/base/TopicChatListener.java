package topicfriend.client.base;

import topicfriend.netmessage.NetMessageChatRoom;
import topicfriend.netmessage.data.UserInfo;

public interface TopicChatListener 
{
	public void onReceiveTopicChatMessage(NetMessageChatRoom msgChatRoom);
	public void onTopicMatchSucceed(UserInfo matchedUserInfo);
	public void onOtherExitTopicChat(UserInfo matchedUserInfo);
	public void onBecameNewFriend(UserInfo newFriendInfo);
}
