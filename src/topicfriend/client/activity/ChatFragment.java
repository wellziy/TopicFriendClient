package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.database.AppController;
import topicfriend.client.database.Channel;
import topicfriend.client.database.ChannelManager;
import topicfriend.client.database.ChatMessage;
import topicfriend.client.database.Consts;
import topicfriend.client.database.TimeUtil;
import topicfriend.client.database.User;
import topicfriend.client.database.UserManager;
import topicfriend.client.R;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

public class ChatFragment extends Fragment{
	
	private ListView listView;
	private ChannelManager channelManager;
	private UserManager userManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		// init managers
		channelManager = AppController.getInstance().getChannelManager();
		userManager = AppController.getInstance().getUserManager();
		
		this.initListView();
	}

	private void initListView() {
		
		listView = (ListView)getView().findViewById(R.id.listview_main);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				// get Data by position
				@SuppressWarnings("unchecked")
				Map<String, Object> itemData = (Map<String, Object>) listView.getAdapter().getItem(position);
				int participantID = (Integer) itemData.get(Consts.ParticipantID);
				
				// start DialogActivity
				Intent intent = new Intent(getActivity(), DialogActivity.class);
				intent.putExtra(Consts.ParticipantID, participantID);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		this.refresh();
	}

	public void refresh() {
		List<Channel> channelArray = channelManager.getAll();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for (Channel channel : channelArray) {
			ChatMessage lastMessage = channel.getLastMessage();
			if (lastMessage == null) continue;
			
			User user = userManager.getByID(channel.getParticipantID());
			if (user == null) continue;
			
			Bitmap bitmap = ResourceManager.getInstance().getBitmapFromAsset(user.getIconName());
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", user.getNickname());
			map.put("msg", lastMessage.getContent());
			map.put("time", TimeUtil.convertTimestampToString(lastMessage.getTimestamp()));
			map.put("img", bitmap);
			map.put(Consts.ParticipantID, channel.getParticipantID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_chat,
				new String[]{"name", "msg", "img", "time"},
				new int[]{R.id.name, R.id.msg, R.id.img, R.id.time});
		
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if( view instanceof ImageView && data instanceof Bitmap){   
			        ImageView iv = (ImageView) view;   
			        iv.setImageBitmap((Bitmap) data);   
			        return true;   
			    }else {
			        return false;   
			    }   
			}
		});
		
		listView.setAdapter(adapter);
	}
	
}
