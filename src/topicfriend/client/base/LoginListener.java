package topicfriend.client.base;

import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageLoginSucceed;

public interface LoginListener
{
	public void onLoginSucceed(NetMessageLoginSucceed msgLoginSucceed);
	public void onLoginError(NetMessageError msgError);
}
