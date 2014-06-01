package topicfriendclient.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriendclient.database.AppController;
import topicfriendclient.database.Channel;
import topicfriendclient.database.ChannelManager;
import topicfriendclient.database.ChatMessage;
import topicfriendclient.database.Consts;
import topicfriendclient.database.TimeUtil;
import topicfriendclient.database.User;
import topicfriendclient.database.UserManager;

import com.example.topicfriend.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", user.getNickname());
			map.put("msg", lastMessage.getContent());
			map.put("time", TimeUtil.convertTimestampToString(lastMessage.getTimestamp()));
			map.put("img", android.R.drawable.ic_menu_preferences);
			map.put(Consts.ParticipantID, channel.getParticipantID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_chat,
				new String[]{"name", "msg", "img", "time"},
				new int[]{R.id.name, R.id.msg, R.id.img, R.id.time});
		
		listView.setAdapter(adapter);
	}
	
}
