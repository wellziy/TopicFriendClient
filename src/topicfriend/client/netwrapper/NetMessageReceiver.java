package topicfriend.client.netwrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.util.ByteArrayBuffer;

import topicfriend.netmessage.NetMessage;
import topicfriend.network.Network;

public class NetMessageReceiver 
{
	private static NetMessageReceiver s_instance=null;
	
	private HashMap<Integer,NetMessageHandler> m_messageHandlerMap;
	private BadConnectionHandler m_badConnectionHandler;
	
	private Thread m_receiveThread;
	private boolean m_isActive;
	
	////////////////////////////////
	//public
	public static synchronized NetMessageReceiver getInstance()
	{
		if(s_instance==null)
		{
			s_instance=new NetMessageReceiver();
		}
		return s_instance;
	}

	public synchronized void setBadConnectionHandler(BadConnectionHandler handler)
	{
		m_badConnectionHandler=handler;
	}
	
	public synchronized void setMessageHandler(int messageID,NetMessageHandler handler)
	{
		m_messageHandlerMap.put(messageID, handler);
	}
	
	public synchronized void removeMessageHandlerByID(int messageID)
	{
		m_messageHandlerMap.remove(messageID);
	}
	
	public synchronized void removeMessageHandler(NetMessageHandler handler)
	{
		Set<Entry<Integer, NetMessageHandler>> entrySet = m_messageHandlerMap.entrySet();
		Iterator<Entry<Integer, NetMessageHandler>> iterator = entrySet.iterator();
		while(iterator.hasNext())
		{
			Entry<Integer, NetMessageHandler> next = iterator.next();
			if(next.getValue()==handler)
			{
				iterator.remove();
			}
		}
	}
	
	public synchronized void removeBadConnectionHandler()
	{
		m_badConnectionHandler=null;
	}
	
	public synchronized void purgeInstance()
	{
		m_isActive=false;
		try 
		{
			m_receiveThread.join();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		s_instance=null;
	}
	
	////////////////////////////////
	//private
	private NetMessageReceiver()
	{
		m_messageHandlerMap=new HashMap<Integer, NetMessageHandler>();
		m_badConnectionHandler=null;
		
		m_isActive=true;
		m_receiveThread=new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				while(m_isActive)
				{
					//handle receive message
					ByteArrayBuffer buf=new ByteArrayBuffer(100);
					int recvConnection=Network.receiveData(buf, Network.NULL_CONNECTION);
					if(recvConnection!=Network.NULL_CONNECTION)
					{
						NetMessage msg=NetMessage.fromJsonString(new String(buf.buffer()));
						if(msg==null)
						{
							Network.makeBadConnection(recvConnection);
						}
						else
						{
							synchronized (NetMessageReceiver.this)
							{
								NetMessageHandler handler=m_messageHandlerMap.get(msg.getMessageID());
								if(handler!=null)
								{
									handler.handleMessage(recvConnection, msg);
								}
							}
						}
					}
					
					//handle bad connection
					int badConnection=Network.getBadConnectionWithoutRemove();
					if(badConnection!=Network.NULL_CONNECTION)
					{
						synchronized (NetMessageReceiver.this)
						{
							if(m_badConnectionHandler!=null)
							{
								m_badConnectionHandler.handleBadConnection(badConnection);
							}
						}
						Network.disconnect(badConnection);
					}
				}
			}
		});
		m_receiveThread.start();
	}
}
