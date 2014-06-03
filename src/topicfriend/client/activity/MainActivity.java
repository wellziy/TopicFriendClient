package topicfriend.client.activity;

import topicfriend.client.database.AppController;
import topicfriend.client.database.Consts;
import topicfriend.client.database.ResourceManager;
import topicfriend.client.R;


import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private DiscoveryFragment discoveryFragment;
	private ChatFragment chatFragment;
	private FriendFragment friendFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
        
        
        // init data and current user
        AppController.getInstance().initWithUid(999);
        
        // init resource manager
        DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		ResourceManager.ScreenWidth = (int) metric.widthPixels;
		ResourceManager.ScreenHeight = (int) metric.heightPixels;
		
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
}
