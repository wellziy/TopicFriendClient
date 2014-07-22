package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.database.AppController;
import topicfriend.client.database.Channel;
import topicfriend.client.database.ChannelManager;
import topicfriend.client.database.Consts;
import topicfriend.client.database.OnChatFriendListener;
import topicfriend.client.database.ResourceManager;
import topicfriend.client.database.UserManager;
import topicfriend.client.network.NetworkManager;
import topicfriend.client.R;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.data.MessageInfo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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


public class DialogActivity extends Activity implements OnChatFriendListener{

	// ListView
	private ListView listView;
	private ArrayList<Map<String, Object>> data;
	
	// Send
	private Button sendButton;
	private EditText sendEditText;
	
	// Channel
	private Channel channel;
	private ChannelManager channelManager;
	private UserManager userManager;
	private NetworkManager networkManager;
	private Handler handler = new Handler();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppActivityManager.getInstance().onActivityCreate(this);
		setContentView(R.layout.activity_dialog);

		Intent intent = getIntent();
		int participantID = intent.getIntExtra(Consts.ParticipantID, Consts.InvalidID);
		if (participantID == Consts.InvalidID) {
			this.finish();
		}
		
		// initialize managers
		channelManager = AppController.getInstance().getChannelManager();
		userManager = AppController.getInstance().getUserManager();
		networkManager = AppController.getInstance().getNetworkManager();
		
		// get or create a channel with the participant
		channel = channelManager.getByID(participantID);
		if (channel == null) {
			channel = channelManager.add(participantID);
		}
		
		channelManager.setChatFriendListener(this);
		
		this.initListView();
		this.initSend();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		channelManager.setChatFriendListener(null);
		AppActivityManager.getInstance().onActivityDestroy(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog, menu);
		return true;
	}

	private void initListView() {
		listView = (ListView)findViewById(R.id.listview_main);
		data = new ArrayList<Map<String,Object>>();

		List<MessageInfo> msgArray = channel.getAll();
		for (MessageInfo chatMsg : msgArray) {
			boolean isRight = false;
			if (chatMsg.getSenderID() == channelManager.getOwnerID()) 
				isRight = true;
			this.addMessageToListView(userManager.getByID(chatMsg.getSenderID()).getIcon(), 
					chatMsg.getContent(), isRight);
		}
		
	}
	
	private void initSend() {
		
		sendButton = (Button)findViewById(R.id.button_send);
		sendEditText = (EditText)findViewById(R.id.edittext_send);
		
		sendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = sendEditText.getText().toString();
				addMessage(channelManager.getOwnerID(), msg);
				sendEditText.setText("");
			}
		});
		
	}
	
	public void addMessage(int senderID, String msg) {
		boolean isRight = false;
		if (senderID == channelManager.getOwnerID()) {
			isRight = true;
			// send message to server
			networkManager.sendDataOne(new NetMessageChatFriend(channel.getParticipantID(), msg));
		}
		
		channel.push(senderID, msg);
		addMessageToListView(userManager.getByID(senderID).getIcon(), msg, isRight);
	}

	public void addMessageToListView(String icon, String msg, boolean isRight) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("img", icon);
		map.put("msg", msg);
		map.put("isRight", isRight);
		data.add(map);

		ChatDataAdapter adapter = new ChatDataAdapter(this);
		
		listView.setAdapter(adapter);
		listView.setSelection(listView.getBottom());
	}
	
	
	public class ChatDataAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		public ChatDataAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// get all view controls
			View layout = view;
			if (view == null) {
				layout = (RelativeLayout) mInflater.inflate(R.layout.listitem_dialog_message, null);
			}
			ImageView iconImageView = (ImageView) layout.findViewById(R.id.img);
			TextView msgTextView = (TextView) layout.findViewById(R.id.msg);
			
			// inflate data
			boolean isRight = (Boolean) data.get(position).get("isRight");
			String iconName = (String) data.get(position).get("img");
			String msgString = (String) data.get(position).get("msg");

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
			if (isRight) {
				iconlayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				msglayoutParam.addRule(RelativeLayout.LEFT_OF, R.id.img);
			}
			else {
				iconlayoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				msglayoutParam.addRule(RelativeLayout.RIGHT_OF, R.id.img);
			}
			
			// set layout parameters
			iconImageView.setLayoutParams(iconlayoutParam);
			msgTextView.setLayoutParams(msglayoutParam);
			
			return layout;
		}
		
	}


	@Override
	public void onChatFriend(NetMessageChatFriend msg) {
		final NetMessageChatFriend finalMsg = msg;
		handler.post(new Runnable() {
			@Override
			public void run() {
				addMessage(finalMsg.getFriendID(), finalMsg.getContent());
			}
		});
	}
	
}
