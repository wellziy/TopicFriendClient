package topicfriend.client.appcontroller;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

public class AppActivityManager 
{
	// activity management
	private List<Activity> mActivityList = null;
	
	public AppActivityManager() 
	{
		mActivityList = new ArrayList<Activity>();
	}
	
	// outer interfaces
	public void popActivity() 
	{
		// got the last activity and finish it
		Activity activity = mActivityList.get(mActivityList.size()-1);
		if (activity != null) 
		{
			activity.finish();
		}
	}
	
	public void popAllActivities()
	{
		// finished activity from down to top
		for (int i=mActivityList.size()-1; i>=0; --i) 
		{
			Activity activity = mActivityList.get(i);
			activity.finish();
		}
		mActivityList.clear();
	}
	
	public void onActivityDestroy(Activity activity)
	{
		mActivityList.remove(activity);
	}
	
	public void onActivityCreate(Activity activity)
	{
		mActivityList.add(activity);
	}
}
