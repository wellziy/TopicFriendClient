package topicfriend.client.activity;

import topicfriend.client.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;


public class SelectImageDialog extends Dialog{

	public interface DialogListener {
		public void onItemSelected(int index, String imageName);
	}
	
	private Activity mContext;
	private DialogListener mListener;
	private GridLayout mGridLayout;
	private int mImageCounter;
	
	public SelectImageDialog(Activity context, int theme) {
		super(context, theme);
		setContentView(R.layout.dialog_select_icon);
		setTitle("«Î—°‘ÒÕº∆¨");
		
		mImageCounter = 0;
		mContext = context;
		mGridLayout = (GridLayout)findViewById(R.id.grid_layout);
		
//		Button positiveButton = (Button)findViewById(R.id.button_positive);
//		Button negativeButton = (Button)findViewById(R.id.button_negative);
//		
//		positiveButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				dismiss();
//				if (mListener != null) {
//					mListener.onPositiveButtonClicked();
//				}
//			}
//		});
//		
//		negativeButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				dismiss();
//				if (mListener != null) {
//					mListener.onNegativeButtonClicked();
//				}
//			}
//		});
	}

	public void setListener(DialogListener listener) {
		mListener = listener;
	}
	
	public int addImage(String imagePath) {
		
		int index = mImageCounter++;
		ImageView imageView = new ImageView(mContext);
		imageView.setTag(new ImageTag(index, imagePath));
		imageView.setOnClickListener(imageClickedListener);
		mGridLayout.addView(imageView);
		
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		GridLayout.LayoutParams param = new GridLayout.LayoutParams();
		
		param.width = ResourceManager.ScreenWidth / 4;
		param.height = param.width;
		imageView.setLayoutParams(param);
		
		// for testing
		Bitmap bitmap = ResourceManager.getInstance().getBitmapFromAsset(imagePath);
		imageView.setImageBitmap(bitmap);
		
		return index;
	}
	
	
	private class ImageTag {
		public int index;
		public String path;
		
		public ImageTag(int i, String p) {
			index = i;
			path = p;
		}
	}
	
	private View.OnClickListener imageClickedListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			System.out.println("on image clicked");
			if (mListener != null) {
				ImageTag tag = (ImageTag) view.getTag();
				mListener.onItemSelected(tag.index, tag.path);
			}
		}
	};
	
	
}
