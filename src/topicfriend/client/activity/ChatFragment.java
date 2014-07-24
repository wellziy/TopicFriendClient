package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.FriendManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.base.Consts;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.FriendChatListener;
import topicfriend.client.util.TimeUtil;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.data.MessageInfo;
import topicfriend.netmessage.data.UserInfo;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class ChatFragment extends Fragment implements FriendChatListener
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
				
				// start chat friend activity
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
		
		AppController.getInstance().getFriendChatManager().addFriendChatListener(this);
		this.refresh();
	}

	@Override
	public void onStop() 
	{
		AppController.getInstance().getFriendChatManager().removeFriendChatListener(this);
		super.onStop();
	}
	
	public void refresh()
	{
		List<FriendChat> fcArray = AppController.getInstance().getFriendChatManager().getAllFriendChatOrderByLastMessageTS();
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
			map.put("unreadcount", fc.getUnreadCount());
			map.put(Consts.ParticipantID, fc.getFriendID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_chat,
				new String[]{"name", "msg", "img", "time","unreadcount"},
				new int[]{R.id.name, R.id.msg, R.id.img, R.id.time,R.id.unreadcount_text});
		
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
				else if(view.getId()==R.id.unreadcount_text)
				{
					TextView textView=(TextView)view;
					int count=(Integer)data;
					textView.setText(""+count);
					if(count<=0)
					{
						view.setVisibility(View.INVISIBLE);
					}
					else
					{
						view.setVisibility(View.VISIBLE);
					}
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

	@Override
	public void onReceiveFriendMessage(NetMessageChatFriend msg)
	{
		refresh();
	}
}
