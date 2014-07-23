package topicfriend.client.base;

import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.data.UserInfo;

public interface UserInfoUpdateListener 
{
	public void onUserInfoUpdated(UserInfo oldInfo,UserInfo newInfo);
	public void onUserInfoUpdateFailed(NetMessageError msg);
}
