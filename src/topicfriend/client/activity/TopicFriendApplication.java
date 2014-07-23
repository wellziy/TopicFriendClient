package topicfriend.client.activity;

import topicfriend.client.appcontroller.ResourceManager;
import android.app.Application;

public class TopicFriendApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		// initialize some singletons
		ResourceManager.getInstance().init(this);
		
		System.out.println("custom application initialize!");
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		ResourceManager.purgeInstance();
		
		System.out.println("custom application finalize!");
	}
	
	
}
