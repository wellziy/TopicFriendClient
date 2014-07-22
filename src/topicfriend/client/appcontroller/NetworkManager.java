package topicfriend.client.appcontroller;

import java.io.IOException;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

import topicfriend.client.base.Consts;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageID;
import topicfriend.network.Network;

public class NetworkManager
{
	private int mConnection = Network.NULL_CONNECTION;
	
	//get the connection of the client
	public int getConnection() 
	{ 
		return mConnection; 
	}
	
	public void init() 
	{
		// initialize network
		Network.initNetwork(1, 1, 5);
		
		NetMessageReceiver.getInstance().setMessageHandler(NetMessageID.ERROR, new NetMessageHandler()
		{
			@Override
			public void handleMessage(int connection, NetMessage msg) 
			{
				NetMessageError msgError=(NetMessageError)msg;
				final String errorStr=msgError.getErrorStr();
				System.out.println(errorStr);
			}
		});
	}
	
	public void destroy() 
	{
		Network.destroyNetwork();
		mConnection = Network.NULL_CONNECTION;
	}
	
	
	public void setMessageHandler(int messageID, NetMessageHandler messageHandler)
	{
		NetMessageReceiver.getInstance().setMessageHandler(messageID, messageHandler);
	}
	
	public void removeMessageHandler(int messageID)
	{
		NetMessageReceiver.getInstance().removeMessageHandler(messageID);
	}
	
	public void sendDataOne(ByteArrayBuffer buff) 
	{
		if (mConnection != Network.NULL_CONNECTION)
		{
			Network.sendDataOne(buff, mConnection);
		}
	}
	
	public void sendDataOne(NetMessage msg) 
	{
		sendDataOne(msg.toByteArrayBuffer());
	}
	
	public void connectToServer() 
	{
		if (!mConnectThread.isAlive()) 
		{
			mConnectThread.start();
		}
	}
	
	///////////////////////////////////////
	// private methods
	private Thread mConnectThread = new Thread()
	{
		@Override
		public void run()
		{
			super.run();
			
			if(mConnection!=Network.NULL_CONNECTION)
				return;
			
			String hostIP="222.200.185.43";
			int port=55555;
			
			try 
			{
				mConnection = Network.connectHostPort(hostIP, port, 1000);
				return;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			System.out.println("failed to connection host");
			mConnection = Network.NULL_CONNECTION;
		}
	};
}
