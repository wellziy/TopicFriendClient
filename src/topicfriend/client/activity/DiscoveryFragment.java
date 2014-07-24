package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.base.Consts;
import topicfriend.client.base.TopicChatListener;
import topicfriend.netmessage.NetMessageChatRoom;
import topicfriend.netmessage.data.TopicInfo;
import topicfriend.netmessage.data.UserInfo;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DiscoveryFragment extends Fragment implements TopicChatListener
{
	private ListView mListView;
	private ProgressDialog mMatchingProgressDialog=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_discovery, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		initListView();
		mMatchingProgressDialog=new ProgressDialog(getActivity());
		mMatchingProgressDialog.setCancelable(false);
		mMatchingProgressDialog.setTitle("Matching Online User...");
		mMatchingProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				AppController.getInstance().getTopicChatManager().reqLeaveRoom();
				mMatchingProgressDialog.dismiss();
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		AppController.getInstance().getTopicChatManager().addTopicChatListener(this);
		this.refresh();
	}

	@Override
	public void onStop()
	{
		AppController.getInstance().getTopicChatManager().removeTopicChatListener(this);
		super.onStop();
	}
	
	private void initListView()
	{
		mListView = (ListView)getView().findViewById(R.id.listview_main);
		
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,long arg3) 
			{
				
				Map<String, Object> itemData = (Map<String, Object>) mListView.getAdapter().getItem(position);
				int topicID = (Integer) itemData.get(Consts.TopicID);
				TopicInfo topic = AppController.getInstance().getTopicManager().getTopicInfoByID(topicID);
				if (topic != null)
				{
					match(topic);
				}
			}
			
		});
	}
	
	private void refresh() 
	{
		List<TopicInfo> topicArray = AppController.getInstance().getTopicManager().getAllTopicInfo();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for (TopicInfo topic : topicArray) 
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", topic.getTitle());
			map.put("description", topic.getDescription());
			map.put(Consts.TopicID, topic.getID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_discovery,
				new String[]{"title","description"},
				new int[]{R.id.textview_title,R.id.textview_description});
		
		mListView.setAdapter(adapter);
	}
	
	private void match(TopicInfo topicInfo)
	{
		System.out.println("match at topic "+topicInfo.getTitle());
		
		mMatchingProgressDialog.show();
		AppController.getInstance().getTopicChatManager().reqJoinTopic(topicInfo.getID());
	}
	
	@Override
	public void onReceiveTopicChatMessage(NetMessageChatRoom msgChatRoom)
	{
		showToast("Receive message: "+msgChatRoom.getContent());
	}

	@Override
	public void onTopicMatchSucceed(UserInfo matchedUserInfo)
	{
		//dismiss the progress dialog
		mMatchingProgressDialog.dismiss();
		
		//start chat room activity
		Intent intent=new Intent(getActivity(),ChatRoomActivity.class);
		startActivity(intent);
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
		Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
	}
}
