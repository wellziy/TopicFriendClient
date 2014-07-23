package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AccountManager;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.FriendChatManager;
import topicfriend.client.appcontroller.FriendManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.base.Consts;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.FriendChatListener;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DialogActivity extends Activity implements FriendChatListener
{
	// ListView
	private ListView mListView;
	private ArrayList<Map<String, Object>> mListViewData;
	
	// Send
	private Button mSendButton;
	private EditText mSendEditText;
	
	// friend chat
	private int mFriendID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		setContentView(R.layout.activity_dialog);

		Intent intent = getIntent();
		
		//init friend ID
		mFriendID = intent.getIntExtra(Consts.ParticipantID, Consts.InvalidID);
		if (mFriendID == Consts.InvalidID) 
		{
			this.finish();
		}
		
		FriendChatManager friendChatMan= AppController.getInstance().getFriendChatManager();
		// get or create a channel with the participant
		friendChatMan.addFriendChatListener(this);
		
		this.initListView();
		this.initSend();
	}

	@Override
	public void onReceiveFriendMessage(NetMessageChatFriend msg)
	{
		FriendManager friendMan=AppController.getInstance().getFriendManager();
		UserInfo friendInfo=friendMan.getFriendInfoByID(mFriendID);
		
		addMessageToListView(friendInfo.getIcon(), msg.getContent(), false);
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		
		FriendChatManager friendChatMan= AppController.getInstance().getFriendChatManager();
		friendChatMan.removeFriendChatListener(this);

		AppController.getInstance().getAppActivityManager().onActivityDestroy(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog, menu);
		return true;
	}

	private void initListView()
	{
		mListView = (ListView)findViewById(R.id.listview_main);
		mListViewData = new ArrayList<Map<String,Object>>();

		AccountManager accountMan=AppController.getInstance().getAccountManager();
		UserInfo loginUserInfo=accountMan.getLoginUserInfo();
		
		FriendChatManager friendChatMan=AppController.getInstance().getFriendChatManager();
		FriendChat friendChat=friendChatMan.getFriendChatByFriendID(mFriendID);
		if(friendChat==null)
		{
			friendChat=friendChatMan.createFriendChat(mFriendID);
		}
		
		List<MessageInfo> msgArray = friendChat.getAllMessageInfo();
		for (MessageInfo chatMsg : msgArray) 
		{
			if (chatMsg.getSenderID() == loginUserInfo.getID()) 
			{
				addMessageToListView(loginUserInfo.getIcon(), chatMsg.getContent(), true);
			}
			else
			{
				UserInfo friendInfo=AppController.getInstance().getFriendManager().getFriendInfoByID(chatMsg.getSenderID());
				addMessageToListView(friendInfo.getIcon(), chatMsg.getContent(), false);
			}
		}
		
	}
	
	private void initSend()
	{
		mSendButton = (Button)findViewById(R.id.button_send);
		mSendEditText = (EditText)findViewById(R.id.edittext_send);
		
		mSendButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String msg = mSendEditText.getText().toString();
				
				FriendChatManager friendChatMan=AppController.getInstance().getFriendChatManager();
				friendChatMan.sendFriendChatMessage(mFriendID, msg);
				
				UserInfo loginUserInfo=AppController.getInstance().getAccountManager().getLoginUserInfo();
				addMessageToListView(loginUserInfo.getIcon(),msg,true);
				mSendEditText.setText("");
			}
		});
	}

	public void addMessageToListView(String icon, String msg, boolean isRight)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("img", icon);
		map.put("msg", msg);
		map.put("isRight", isRight);
		mListViewData.add(map);

		ChatDataAdapter adapter = new ChatDataAdapter(this);
		
		mListView.setAdapter(adapter);
		mListView.setSelection(mListView.getBottom());
	}
	
	
	public class ChatDataAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		
		public ChatDataAdapter(Context context) 
		{
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() 
		{
			return mListViewData.size();
		}

		@Override
		public Object getItem(int position) 
		{
			return mListViewData.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			// get all view controls
			View layout = view;
			if (view == null)
			{
				layout = (RelativeLayout) mInflater.inflate(R.layout.listitem_dialog_message, null);
			}
			ImageView iconImageView = (ImageView) layout.findViewById(R.id.img);
			TextView msgTextView = (TextView) layout.findViewById(R.id.msg);
			
			// inflate data
			boolean isRight = (Boolean) mListViewData.get(position).get("isRight");
			String iconName = (String) mListViewData.get(position).get("img");
			String msgString = (String) mListViewData.get(position).get("msg");

			iconImageView.setScaleType(ImageView.ScaleType.FIT_XY);
			iconImageView.setImageBitmap(ResourceManager.getInstance().getBitmapFromAsset(iconName));
			// set icon ImageView layout parameters
			int iconSize = ResourceManager.ScreenWidth / 6;
			RelativeLayout.LayoutParams iconlayoutParam = new RelativeLayout.LayoutParams(
					iconSize,
					iconSize);
			iconlayoutParam.setMargins(5, 5, 5, 5);
			

			// set msg TextView layout parameters
			RelativeLayout.LayoutParams msglayoutParam = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			msglayoutParam.setMargins(5, 5, 5, 5);
			msgTextView.setText(msgString);
			
			// layout
			if (isRight) 
			{
				iconlayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				msglayoutParam.addRule(RelativeLayout.LEFT_OF, R.id.img);
			}
			else
			{
				iconlayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				msglayoutParam.addRule(RelativeLayout.RIGHT_OF, R.id.img);
			}
			
			// set layout parameters
			iconImageView.setLayoutParams(iconlayoutParam);
			msgTextView.setLayoutParams(msglayoutParam);
			
			return layout;
		}
		
	}
}
