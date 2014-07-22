package topicfriend.client.activity;

import topicfriend.client.appcontroller.AppActivityManager;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.FriendChatManager;
import topicfriend.client.appcontroller.NetworkManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.Consts;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.R;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.NetMessageID;


import android.os.Bundle;
import android.R.interpolator;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private DiscoveryFragment discoveryFragment;
	private ChatFragment chatFragment;
	private FriendFragment friendFragment;
	private NetworkManager networkManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppActivityManager.getInstance().onActivityCreate(this);
		setContentView(R.layout.activity_main);
		
		// 设置ActionBar的显示模式
		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		 // 实例化 fragment action bar 是用 fragment 来显示的  
        discoveryFragment = new DiscoveryFragment();  
        chatFragment = new ChatFragment(); 
        friendFragment = new FriendFragment();
		
        // 添加tab以及相应的fragment
        actionbar.addTab(actionbar.newTab()
        		.setText("discovery")
        		.setTabListener(new MyTabsListener(discoveryFragment)));
        
        actionbar.addTab(actionbar.newTab()
        		.setText("chat")
        		.setTabListener(new MyTabsListener(chatFragment)));
        
        actionbar.addTab(actionbar.newTab()
        		.setText("friend")
        		.setTabListener(new MyTabsListener(friendFragment)));
        
        
        // init resource manager
        DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		ResourceManager.ScreenWidth = (int) metric.widthPixels;
		ResourceManager.ScreenHeight = (int) metric.heightPixels;
		
		networkManager = AppController.getInstance().getNetworkManager();
		this.initModel();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		AppActivityManager.getInstance().onActivityDestroy(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()) {
		case R.id.action_profile:
			
			Intent intent = new Intent(this, PersonalInfoActivity.class);
			intent.putExtra(Consts.UserID, AppController.getInstance().getOwnerID());
			startActivity(intent);
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initModel() {
		networkManager.setMessageHandler(NetMessageID.CHAT_FRIEND, new NetMessageHandler() {
			@Override
			public void handleMessage(int connection, NetMessage msg) {
				NetMessageChatFriend chatMsg = (NetMessageChatFriend) msg;
				FriendChatManager channelManager = AppController.getInstance().getFriendChatManager();
				FriendChat channel = channelManager.getByID(chatMsg.getFriendID());
				channel.push(chatMsg.getFriendID(), chatMsg.getContent());
				if (channelManager.getChatFriendListener() != null) {
					channelManager.getChatFriendListener().onChatFriend(chatMsg);
				}
			}
		});
	}
	
	// 实例化 tabs 的监听类   
	class MyTabsListener implements ActionBar.TabListener {  
	    public Fragment fragment;  

	    public MyTabsListener(Fragment fragment) {  
	        this.fragment = fragment;  
	    }  
	  
	    @Override  
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        
	    }  
	  
	    @Override  
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {  
	        ft.replace(R.id.view_container, fragment);  
	    }  
	     
	    @Override  
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {  
	        ft.remove(fragment);  
	    }  
	}

	// 返回按钮，结束程序
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		// finished all activity including LoginActivity, 
		// which is the enter and exit entances of this application
		AppActivityManager.purgeInstance();
	}
	
	
	
}
