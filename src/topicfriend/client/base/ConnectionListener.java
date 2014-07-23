package topicfriend.client.base;

public interface ConnectionListener 
{
	public void onConnectFailed();
	public void onConnectSucceed();
	public void onConnectionLost();
}
