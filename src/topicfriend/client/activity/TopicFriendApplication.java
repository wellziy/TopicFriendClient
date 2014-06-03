package topicfriend.client.activity;

import topicfriend.client.database.AppController;
import topicfriend.client.database.ResourceManager;
import android.app.Application;

public class TopicFriendApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		
		// initialize some singletons
		ResourceManager.getInstance().init(this);
		AppController.getInstance();
		
		System.out.println("custom application initialize!");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		ResourceManager.getInstance().clearAllBitmaps();
	}

	
	
}
