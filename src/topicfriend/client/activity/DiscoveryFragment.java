package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.TopicManager;
import topicfriend.client.base.FriendChat;
import topicfriend.client.base.Consts;
import topicfriend.client.util.TimeUtil;
import topicfriend.netmessage.data.TopicInfo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DiscoveryFragment extends Fragment{
	
	private ListView mListView;
	private TopicManager topicManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_discovery, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		topicManager = AppController.getInstance().getTopicManager();
		this.initListView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		this.refresh();
	}

	private void initListView() {
		
		mListView = (ListView)getView().findViewById(R.id.listview_main);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				
				Map<String, Object> itemData = (Map<String, Object>) mListView.getAdapter().getItem(position);
				int topicID = (Integer) itemData.get(Consts.TopicID);
				TopicInfo topic = topicManager.getByID(topicID);
				if (topic != null) {
					match(topic);
				}
			}
			
		});
	}
	
	private void refresh() {
		
		List<TopicInfo> topicArray = topicManager.getAll();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for (TopicInfo topic : topicArray) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", topic.getTitle());
			map.put(Consts.TopicID, topic.getID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_discovery,
				new String[]{"title"},
				new int[]{R.id.textview_title});
		
		mListView.setAdapter(adapter);
	}
	
	private void match(TopicInfo topic) {
		System.out.println("match at topic "+topic.getTitle());
	}
	
}
