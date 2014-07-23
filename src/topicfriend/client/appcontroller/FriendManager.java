package topicfriend.client.appcontroller;

import java.util.ArrayList;
import java.util.HashMap;

import topicfriend.netmessage.data.UserInfo;

public class FriendManager
{
	public HashMap<Integer,UserInfo> m_friendList;
	
	public FriendManager()
	{
		m_friendList=new HashMap<Integer, UserInfo>();
	}
	
	public void addFriendInfoList(ArrayList<UserInfo> list)
	{
		for(int i=0;i<list.size();i++)
		{
			UserInfo friendInfo=list.get(i);
			m_friendList.put(friendInfo.getID(), friendInfo);
		}
	}
	
	public void addFriend(Integer id,UserInfo userInfo)
	{
		m_friendList.put(id, userInfo);
	}
	
	public void removeFriend(Integer id)
	{
		m_friendList.remove(id);
	}
	
	//may return null if the friend id not exists
	public UserInfo getFriendInfoByID(Integer id)
	{
		return m_friendList.get(id);
	}
	
	public ArrayList<UserInfo> getAllFriendInfo()
	{
		return new ArrayList<UserInfo>(m_friendList.values());
	}
}
