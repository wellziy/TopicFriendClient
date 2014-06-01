package topicfriendclient.activity;

import topicfriendclient.database.AppController;

import com.example.topicfriend.R;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends Activity {

	private DiscoveryFragment discoveryFragment;
	private ChatFragment chatFragment;
	private FriendFragment friendFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ����ActionBar����ʾģʽ
		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		 // ʵ���� fragment action bar ���� fragment ����ʾ��  
        discoveryFragment = new DiscoveryFragment();  
        chatFragment = new ChatFragment(); 
        friendFragment = new FriendFragment();
		
        // ���tab�Լ���Ӧ��fragment
        actionbar.addTab(actionbar.newTab()
        		.setText("discovery")
        		.setTabListener(new MyTabsListener(discoveryFragment)));
        
        actionbar.addTab(actionbar.newTab()
        		.setText("chat")
        		.setTabListener(new MyTabsListener(chatFragment)));
        
        actionbar.addTab(actionbar.newTab()
        		.setText("friend")
        		.setTabListener(new MyTabsListener(friendFragment)));
        
        
        // init data and current user
        AppController.getInstance().initWithUid(999);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	// ʵ���� tabs �ļ�����   
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
}
