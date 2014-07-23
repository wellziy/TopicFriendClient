package topicfriend.client.base;

import topicfriend.netmessage.NetMessageChatFriend;

public interface FriendChatListener
{
	public void onReceiveFriendMessage(NetMessageChatFriend msg);
}
