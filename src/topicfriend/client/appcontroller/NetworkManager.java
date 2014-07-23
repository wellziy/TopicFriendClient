package topicfriend.client.appcontroller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import topicfriend.client.base.ConnectionListener;
import topicfriend.client.netwrapper.BadConnectionHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.network.Network;
import android.os.Handler;

public class NetworkManager implements BadConnectionHandler
{
	private int mConnection = Network.NULL_CONNECTION;
	private ArrayList<ConnectionListener> mConnectionListener=new ArrayList<ConnectionListener>();
	private Thread mConnectThread = new Thread(new Runnable()
	{
		@Override
		public void run() 
		{
			String hostIP="222.200.185.43";
			int port=55555;
			int connection=Network.NULL_CONNECTION;
			
			try 
			{
				connection = Network.connectHostPort(hostIP, port, 1000);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			final int finalConnection=connection;
			Handler handler=AppController.getInstance().getHandler();
			handler.post(new Runnable()
			{
				@Override
				public void run() 
				{
					handleConnectSucceedOrFailed(finalConnection);
				}
			});
		}
	});
	
	public void initNetwork() 
	{
		// initialize network
		Network.initNetwork(1, 1, 5);
	}
	
	public void destroyNetwork() 
	{
		Network.destroyNetwork();
		mConnection = Network.NULL_CONNECTION;
	}
	
	public boolean isConnected()
	{
		return mConnection!=Network.NULL_CONNECTION;
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
		if(isConnected())
		{
			return;
		}
		mConnectThread.start();
	}
	
	@Override
	public void handleBadConnection(final int connection) 
	{
		Handler handler=AppController.getInstance().getHandler();
		handler.post(new Runnable()
		{
			@Override
			public void run()
			{
				handleBadConnectionInUIThread(connection);
			}
		});
	}
	
	//connection listener management
	public void addConnectionListener(ConnectionListener listener)
	{
		mConnectionListener.add(listener);
	}
	
	public void removeConnectionListener(ConnectionListener listener)
	{
		mConnectionListener.remove(listener);
	}
	
	public void clearConnectionListener()
	{
		mConnectionListener.clear();
	}
	
	/////////////////////////////////
	//private
	private void handleBadConnectionInUIThread(int connection)
	{
		ArrayList<ConnectionListener> copyListener=new ArrayList<ConnectionListener>(mConnectionListener);
		for(int i=0;i<copyListener.size();i++)
		{
			ConnectionListener listener=copyListener.get(i);
			listener.onConnectionLost();
		}
		mConnection=Network.NULL_CONNECTION;
	}
	
	private void handleConnectSucceedOrFailed(int connection)
	{
		mConnection=connection;
		//setup bad connection handler
		if(connection!=Network.NULL_CONNECTION)
		{
			NetMessageReceiver.getInstance().setBadConnectionHandler(this);
		}
		
		ArrayList<ConnectionListener> copyListener=new ArrayList<ConnectionListener>(mConnectionListener);
		for(int i=0;i<copyListener.size();i++)
		{
			ConnectionListener listener=copyListener.get(i);
			if(connection==Network.NULL_CONNECTION)
			{
				listener.onConnectFailed();
			}
			else
			{
				listener.onConnectSucceed();
			}
		}
	}
}
