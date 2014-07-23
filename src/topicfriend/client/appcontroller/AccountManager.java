package topicfriend.client.appcontroller;

import java.util.ArrayList;
import java.util.List;

import topicfriend.netmessage.data.UserInfo;

public class AccountManager 
{
	private UserInfo m_loginUserInfo=null;
	
	public AccountManager()
	{
	}
	
	//must init the manager before using it
	public void init(UserInfo userInfo)
	{
		m_loginUserInfo=userInfo;
	}
	
	public boolean isUserLogin()
	{
		return m_loginUserInfo!=null;
	}
	
	public String getUserName()
	{
		return m_loginUserInfo.getName();
	}
	
	public int getUserSex()
	{
		return m_loginUserInfo.getSex();
	}
	
	public String getUserSignature()
	{
		return m_loginUserInfo.getSignature();
	}
	
	public String getUserIcon()
	{
		return m_loginUserInfo.getIcon();
	}
	
	public int getUserID()
	{
		return m_loginUserInfo.getID();
	}
	
	public UserInfo getLoginUserInfo()
	{
		return m_loginUserInfo;
	}
	
	public void setUserInfo(UserInfo userInfo)
	{
		m_loginUserInfo=userInfo;
	}
}










