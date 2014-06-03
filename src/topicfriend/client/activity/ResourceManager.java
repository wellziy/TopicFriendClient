package topicfriend.client.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ResourceManager {
	
	private Context mContext = null;
	private Map<String, Bitmap> mBitmapCache = new HashMap<String, Bitmap>();
	private static ResourceManager sResourceManager = new ResourceManager();
	
	public static ResourceManager getInstance() {
		return sResourceManager;
	}
	
	public void init(Context context) {
		this.mContext = context;
	}
	
	public Bitmap getBitmapFromAsset(String fullname) {
		
		// lazy load
		if (mBitmapCache.get(fullname) == null) {
			if (mContext == null) return null;
			try {
				InputStream is =  mContext.getAssets().open(fullname);
				BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromStream(is, fullname);
				mBitmapCache.put(fullname, bitmapDrawable.getBitmap());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Bitmap bitmap = mBitmapCache.get(fullname);
		if (bitmap == null) {
			//TODO: remove the following recursion call
			bitmap = this.getBitmapFromAsset("cocos2dx_icon.png");
		}
		return bitmap;
	}
	
	public Bitmap removeBitmapFromCache(String fullname) {
		return mBitmapCache.remove(fullname);
	}
	
	public void clearAllBitmaps() {
		mBitmapCache.clear();
	}
}
