package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.FriendChatManager;
import topicfriend.client.appcontroller.FriendManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.appcontroller.AccountManager;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.Consts;
import topicfriend.client.util.TimeUtil;
import topicfriend.client.R;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class ChatFragment extends Fragment
{
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);
		
		this.initListView();
	}

	private void initListView() 
	{
		mListView = (ListView)getView().findViewById(R.id.listview_main);
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3)
			{
				// get Data by position
				@SuppressWarnings("unchecked")
				Map<String, Object> itemData = (Map<String, Object>) mListView.getAdapter().getItem(position);
				int participantID = (Integer) itemData.get(Consts.ParticipantID);
				
				// start DialogActivity
				Intent intent = new Intent(getActivity(), ChatFriendActivity.class);
				intent.putExtra(Consts.ParticipantID, participantID);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		this.refresh();
	}

	public void refresh()
	{
		List<FriendChat> fcArray = AppController.getInstance().getFriendChatManager().getAllFriendChat();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for (FriendChat fc : fcArray)
		{
			MessageInfo lastMessage = fc.getLastMessage();
			if (lastMessage == null)
			{
				continue;
			}
			
			FriendManager friendMan=AppController.getInstance().getFriendManager();
			UserInfo user = friendMan.getFriendInfoByID(fc.getFriendID());
			if (user == null) 
			{
				continue;
			}
			
			Bitmap bitmap = ResourceManager.getInstance().getBitmapFromAsset(user.getIcon());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", user.getName());
			map.put("msg", lastMessage.getContent());
			map.put("time", TimeUtil.convertTimestampToString(lastMessage.getTimetamp().getTime()));
			map.put("img", bitmap);
			map.put(Consts.ParticipantID, fc.getFriendID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_chat,
				new String[]{"name", "msg", "img", "time"},
				new int[]{R.id.name, R.id.msg, R.id.img, R.id.time});
		
		adapter.setViewBinder(new ViewBinder() 
		{
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) 
			{
				if( view instanceof ImageView && data instanceof Bitmap)
				{   
			        ImageView iv = (ImageView) view;   
			        iv.setImageBitmap((Bitmap) data);   
			        return true;   
			    }
				else 
				{
			        return false;   
			    }   
			}
		});
		
		mListView.setAdapter(adapter);
	}
}
