package topicfriend.client.appcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ResourceManager
{
	public static int ScreenWidth = 720;
	public static int ScreenHeight = 1280;
	
	private Context mContext = null;
	private Map<String, Bitmap> mBitmapCache = new HashMap<String, Bitmap>();
	private static ResourceManager sResourceManager = null;
	
	public static ResourceManager getInstance() 
	{
		if (sResourceManager == null) 
		{
			sResourceManager = new ResourceManager();
		}
		return sResourceManager;
	}
	
	public static void purgeInstance() 
	{
		sResourceManager = null;
	}
	
	public void init(Context context)
	{
		this.mContext = context;
	}
	
	public Bitmap getBitmapFromAsset(String fullname)
	{
		// lazy load
		if (mBitmapCache.get(fullname) == null) 
		{
			if (mContext == null) return null;
			try 
			{
				InputStream is =  mContext.getAssets().open(fullname);
				BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromStream(is, fullname);
				mBitmapCache.put(fullname, bitmapDrawable.getBitmap());
				
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		Bitmap bitmap = mBitmapCache.get(fullname);
		if (bitmap == null) 
		{
			//TODO: remove the following recursion call
			bitmap = this.getBitmapFromAsset("icon_default.jpg");
		}
		return bitmap;
	}
	
	public Bitmap removeBitmapFromCache(String fullname)
	{
		return mBitmapCache.remove(fullname);
	}
	
	public void clearAllBitmaps() 
	{
		mBitmapCache.clear();
	}
}
