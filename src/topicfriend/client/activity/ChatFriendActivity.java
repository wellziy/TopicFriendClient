package topicfriend.client.activity;

import java.util.List;

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
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChatFriendActivity extends Activity implements FriendChatListener
{
	// friend chat
	private int mFriendID;
	
	private Button mSendButton;
	private LinearLayout mMessageLayout;
	private ScrollView mMessageScrollView;
	private EditText mSendContentEdit;
	private Button mExitButton;
	private TextView mTitleText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatfriend);
		
		mSendButton=(Button) this.findViewById(R.id.send_button);
		mMessageScrollView=(ScrollView)this.findViewById(R.id.message_scrollview);
		mMessageLayout=(LinearLayout)this.findViewById(R.id.message_layout);
		mSendContentEdit=(EditText)this.findViewById(R.id.send_content_edit);
		mExitButton=(Button)this.findViewById(R.id.exit_button);
		mTitleText=(TextView)this.findViewById(R.id.title_text);
		
		Intent intent = getIntent();
		//init friend ID
		mFriendID = intent.getIntExtra(Consts.ParticipantID, Consts.InvalidID);
		if (mFriendID == Consts.InvalidID) 
		{
			this.finish();
		}
		
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		FriendChatManager friendChatMan= AppController.getInstance().getFriendChatManager();
		//mark the message read and start new activity
		friendChatMan.markFriendChatMessageRead(mFriendID);
		// get or create a channel with the participant
		friendChatMan.addFriendChatListener(this);
		
		//init title text
		UserInfo friendInfo=AppController.getInstance().getFriendManager().getFriendInfoByID(mFriendID);
		mTitleText.setText(friendInfo.getName());
		
		//init send button
		mSendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				String msg = mSendContentEdit.getText().toString();
				
				FriendChatManager friendChatMan=AppController.getInstance().getFriendChatManager();
				friendChatMan.sendFriendChatMessage(mFriendID, msg);
				
				UserInfo loginUserInfo=AppController.getInstance().getAccountManager().getLoginUserInfo();
				appendMessageToLayout(loginUserInfo.getIcon(),msg,false);
				scrollToBottom();
				
				mSendContentEdit.setText("");
			}
		});
		
		mExitButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ChatFriendActivity.this.finish();
			}
		});
		
		//fill message layout content with message
		fillMessageLayoutContent();
		scrollToBottom();
	}
	
	private void fillMessageLayoutContent()
	{
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
				appendMessageToLayout(loginUserInfo.getIcon(), chatMsg.getContent(), false);
			}
			else
			{
				UserInfo friendInfo=AppController.getInstance().getFriendManager().getFriendInfoByID(chatMsg.getSenderID());
				appendMessageToLayout(friendInfo.getIcon(), chatMsg.getContent(), true);
			}
		}
		
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
		int iconSize=Math.min(ResourceManager.getInstance().ScreenWidth,ResourceManager.getInstance().ScreenHeight)/6;
		iconImage.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
		TextView contentText=(TextView)view.findViewById(R.id.content_text);
		contentText.setText(content);
		
		mMessageLayout.addView(view);
	}

	@Override
	public void onReceiveFriendMessage(NetMessageChatFriend msg)
	{
		FriendManager friendMan=AppController.getInstance().getFriendManager();
		UserInfo friendInfo=friendMan.getFriendInfoByID(mFriendID);
		
		FriendChatManager friendChatMan=AppController.getInstance().getFriendChatManager();
		friendChatMan.markFriendChatMessageRead(mFriendID);
		
		appendMessageToLayout(friendInfo.getIcon(), msg.getContent(), true);
		scrollToBottom();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		
		FriendChatManager friendChatMan= AppController.getInstance().getFriendChatManager();
		friendChatMan.removeFriendChatListener(this);

		AppController.getInstance().getAppActivityManager().onActivityDestroy(this);
	}
	
	//////////////////////////////////
	//private
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
}
