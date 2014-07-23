package topicfriend.client.activity;

import java.util.ArrayList;

import topicfriend.client.R;
import topicfriend.client.appcontroller.ResourceManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SelectImagePreference extends Preference{
	
	private PreferenceActivity parent;
	private ImageView previewImageView;
	private SelectImageDialog mSelectImageDialog;
	private String mSelectImageName = "";
	
	public SelectImagePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SelectImagePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectImagePreference(Context context) {
		super(context);
	}

	void setActivity(PreferenceActivity parent) {
		this.parent = parent;
	}

	public String getSelectedImageName() {
		return mSelectImageName;
	}
	
	@Override
	public boolean isPersistent() {
		return false;
	}


	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		previewImageView = (ImageView)view.findViewById(R.id.pref_current_img);
		changeImage(mSelectImageName);
	}	

    @Override
    protected void onClick() {
        super.onClick();
        
        System.out.println("on select image preference clicked");

        if (mSelectImageDialog == null) {
        	mSelectImageDialog = new SelectImageDialog(parent, R.style.SelectImageDialog);
        	mSelectImageDialog.setListener(mDialogListener);
        	ArrayList<String> imageNameArray = new ArrayList<String>();
            for (int i=1; i<=8; ++i) {
            	String imageName = "icon_" + i + ".jpg";
            	imageNameArray.add(imageName);
            	//ResourceManager.getInstance().getBitmapFromAsset(imageName);
            	
            }
        	for (int i=0; i<imageNameArray.size(); ++i) {
        		mSelectImageDialog.addImage(imageNameArray.get(i));
        	}
        	
        }
        mSelectImageDialog.show();   
    }
	
	public void changeImage(String bitmapName) {
    	Bitmap bitmap = ResourceManager.getInstance().getBitmapFromAsset(bitmapName);
    	if (previewImageView != null) {
    		previewImageView.setImageBitmap(bitmap);
    	}
		mSelectImageName = bitmapName;
		//this.notifyChanged();
		if (getOnPreferenceChangeListener() != null) {
			this.getOnPreferenceChangeListener().onPreferenceChange(this, mSelectImageName);
		}
		
    }
    
	
    private SelectImageDialog.DialogListener mDialogListener = new SelectImageDialog.DialogListener() {
		@Override
		public void onItemSelected(int index, String imageName) {
			changeImage(imageName);
			mSelectImageDialog.dismiss();
		}
	};
    
    
}
