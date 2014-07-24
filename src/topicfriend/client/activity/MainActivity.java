package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.base.Consts;
import topicfriend.netmessage.data.UserInfo;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity 
{
	private DiscoveryFragment mDiscoveryFragment;
	private ChatFragment mChatFragment;
	private FriendFragment mFriendFragment;
	private ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		setContentView(R.layout.activity_main);
		
		//set action bar navigation mode
		mActionBar = getActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		updateActionBar();
		
		 //create fragments
        mDiscoveryFragment = new DiscoveryFragment();  
        mChatFragment = new ChatFragment(); 
        mFriendFragment = new FriendFragment();
		
        mActionBar.addTab(mActionBar.newTab()
        		.setText("discovery")
        		.setTabListener(new MyTabsListener(mDiscoveryFragment)));
        
        mActionBar.addTab(mActionBar.newTab()
        		.setText("chat")
        		.setTabListener(new MyTabsListener(mChatFragment)));
        
        mActionBar.addTab(mActionBar.newTab()
        		.setText("friend")
        		.setTabListener(new MyTabsListener(mFriendFragment)));
	}
	
	public void updateActionBar()
	{
		UserInfo loginUserInfo=AppController.getInstance().getAccountManager().getLoginUserInfo();
		Bitmap bitmap = ResourceManager.getInstance().getBitmapFromAsset(loginUserInfo.getIcon());
		mActionBar.setIcon(new BitmapDrawable(bitmap));
		mActionBar.setTitle(loginUserInfo.getName());
	}
	
	@Override
	protected void onResume() 
	{
		updateActionBar();
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		AppController.getInstance().getAppActivityManager().onActivityDestroy(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId()) 
		{
		case R.id.action_profile:
			
			Intent intent = new Intent(this, PersonalInfoActivity.class);
			intent.putExtra(Consts.UserID, AppController.getInstance().getAccountManager().getUserID());
			startActivity(intent);
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//implement TableListener for Tab events
	class MyTabsListener implements ActionBar.TabListener
	{  
	    public Fragment fragment;  

	    public MyTabsListener(Fragment fragment) 
	    {  
	        this.fragment = fragment;  
	    }  
	  
	    @Override  
	    public void onTabReselected(Tab tab, FragmentTransaction ft) 
	    {
	        
	    }  
	  
	    @Override  
	    public void onTabSelected(Tab tab, FragmentTransaction ft) 
	    {  
	        ft.replace(R.id.view_container, fragment);  
	    }  
	     
	    @Override  
	    public void onTabUnselected(Tab tab, FragmentTransaction ft)
	    {  
	        ft.remove(fragment);  
	    }  
	}

	//press back button,exit the application
	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		
		// finished all activity including LoginActivity, 
		// which is the enter and exit entrance of this application
		AppController.getInstance().getAppActivityManager().popAllActivities();
	}
}
