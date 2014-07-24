package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AccountManager;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.FriendManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.appcontroller.TopicChatManager;
import topicfriend.client.base.TopicChatListener;
import topicfriend.netmessage.NetMessageChatRoom;
import topicfriend.netmessage.data.TopicInfo;
import topicfriend.netmessage.data.UserInfo;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoomActivity extends Activity implements TopicChatListener
{
	private Button mSendButton;
	private LinearLayout mMessageLayout;
	private ScrollView mMessageScrollView;
	private EditText mSendContentEdit;
	private Button mExitButton;
	private Button mLikeButton;
	private TextView mTitleText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		
		mSendButton=(Button) this.findViewById(R.id.send_button);
		mMessageScrollView=(ScrollView)this.findViewById(R.id.message_scrollview);
		mMessageLayout=(LinearLayout)this.findViewById(R.id.message_layout);
		mSendContentEdit=(EditText)this.findViewById(R.id.send_content_edit);
		mExitButton=(Button)this.findViewById(R.id.exit_button);
		mLikeButton=(Button)this.findViewById(R.id.like_button);
		mTitleText=(TextView)this.findViewById(R.id.title_text);
		
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		TopicChatManager topicChatMan=AppController.getInstance().getTopicChatManager();
		topicChatMan.addTopicChatListener(this);
		TopicInfo topicInfo=topicChatMan.getMatchedTopicInfo();
		
		mTitleText.setText(topicInfo.getTitle());
		
		mLikeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mLikeButton.setEnabled(false);
				AppController.getInstance().getTopicChatManager().reqLike();
			}
		});
		
		mExitButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ChatRoomActivity.this.finish();
			}
		});
		
		mSendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String content=mSendContentEdit.getText().toString();
				
				AppController.getInstance().getTopicChatManager().reqSendMessage(content);
				AccountManager accountMan=AppController.getInstance().getAccountManager();
				UserInfo loginUserInfo=accountMan.getLoginUserInfo();
				
				appendMessageToLayout(loginUserInfo.getIcon(), content, false);
				scrollToBottom();
				
				mSendContentEdit.setText("");
			}
		});
	}
	
	@Override
	protected void onDestroy() 
	{	
		AppController.getInstance().getAppActivityManager().onActivityDestroy(this);
		AppController.getInstance().getTopicChatManager().removeTopicChatListener(this);
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		leaveRoom();
		super.onStop();
	}
	
	@Override
	public void onReceiveTopicChatMessage(NetMessageChatRoom msgChatRoom) 
	{
		//showToast("Receive message: "+msgChatRoom.getContent());
		TopicChatManager topicChatMan=AppController.getInstance().getTopicChatManager();
		UserInfo matchUserInfo=topicChatMan.getMatchedUserInfo();
		
		appendMessageToLayout(matchUserInfo.getIcon(), msgChatRoom.getContent(), true);
		scrollToBottom();
	}

	@Override
	public void onTopicMatchSucceed(UserInfo matchedUserInfo)
	{
		//nothing to do here
	}

	@Override
	public void onOtherExitTopicChat(UserInfo matchedUserInfo) 
	{
		mLikeButton.setClickable(false);
		showToast("The opposite side exit the topic chat!");
	}

	@Override
	public void onMadeNewFriend(UserInfo newFriendInfo)
	{
		showToast("Bingo,you make a new friend "+newFriendInfo.getName());
	}
	
	public void appendMessageToLayout(String iconPath,String content,boolean isLeft)
	{
		View view=null;
		LayoutInflater flater=LayoutInflater.from(this);
		if(isLeft)
		{
			view=flater.inflate(R.layout.msg_item_left, null);
		}
		else
		{
			view=flater.inflate(R.layout.msg_item_right, null);
		}
		
		ImageView iconImage=(ImageView) view.findViewById(R.id.icon_image);
		iconImage.setImageBitmap(ResourceManager.getInstance().getBitmapFromAsset(iconPath));
		int iconSize=ResourceManager.getInstance().ScreenWidth/6;
		iconImage.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
		TextView contentText=(TextView)view.findViewById(R.id.content_text);
		contentText.setText(content);
		
		mMessageLayout.addView(view);
	}
	
	////////////////////////////////
	//private
	private void showToast(String text)
	{
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
	
	private void leaveRoom()
	{
		AppController.getInstance().getTopicChatManager().reqLeaveRoom();
	}
	
	private void scrollToBottom()
	{
		mMessageScrollView.post(new Runnable()
		{
			@Override
			public void run()
			{
				mMessageScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void onTopicChatBothLike() 
	{
		TopicChatManager topicChatMan=AppController.getInstance().getTopicChatManager();
		UserInfo matchedUserInfo=topicChatMan.getMatchedUserInfo();
		FriendManager friendMan=AppController.getInstance().getFriendManager();
		if(friendMan.isMyFriend(matchedUserInfo.getID()))
		{
			showToast("Bingo,the opposite side is your friend "+matchedUserInfo.getName());
		}
	}
}
