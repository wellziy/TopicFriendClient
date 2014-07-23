package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.base.TopicChatListener;
import topicfriend.netmessage.NetMessageChatRoom;
import topicfriend.netmessage.data.UserInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatRoomActivity extends Activity implements TopicChatListener
{
	private Button mSendButton;
	private ListView mMessageListView;
	private EditText mSendContentEdit;
	private Button mExitButton;
	private Button mLikeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		
		mSendButton=(Button) this.findViewById(R.id.send_button);
		mMessageListView=(ListView)this.findViewById(R.id.message_list);
		mSendContentEdit=(EditText)this.findViewById(R.id.send_content_edit);
		mExitButton=(Button)this.findViewById(R.id.exit_button);
		mLikeButton=(Button)this.findViewById(R.id.like_button);
		
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		AppController.getInstance().getTopicChatManager().addTopicChatListener(this);
		
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
		showToast("Receive message: "+msgChatRoom.getContent());
	}

	@Override
	public void onTopicMatchSucceed(UserInfo matchedUserInfo)
	{
		//nothing to do here
	}

	@Override
	public void onOtherExitTopicChat(UserInfo matchedUserInfo) 
	{
		showToast("Other exit topic chat");
	}

	@Override
	public void onBecameNewFriend(UserInfo newFriendInfo)
	{
		showToast("BecameNewFriend with "+newFriendInfo.getName());
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
}
