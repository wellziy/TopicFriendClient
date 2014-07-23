package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.base.Consts;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity 
{
	private DiscoveryFragment discoveryFragment;
	private ChatFragment chatFragment;
	private FriendFragment friendFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		setContentView(R.layout.activity_main);
		
		//set action bar navigation mode
		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		 //create fragments
        discoveryFragment = new DiscoveryFragment();  
        chatFragment = new ChatFragment(); 
        friendFragment = new FriendFragment();
		
        actionbar.addTab(actionbar.newTab()
        		.setText("discovery")
        		.setTabListener(new MyTabsListener(discoveryFragment)));
        
        actionbar.addTab(actionbar.newTab()
        		.setText("chat")
        		.setTabListener(new MyTabsListener(chatFragment)));
        
        actionbar.addTab(actionbar.newTab()
        		.setText("friend")
        		.setTabListener(new MyTabsListener(friendFragment)));
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
