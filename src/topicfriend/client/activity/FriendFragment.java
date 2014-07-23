package topicfriend.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.FriendChatManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.appcontroller.AccountManager;
import topicfriend.client.base.Consts;
import topicfriend.client.R;
import topicfriend.netmessage.data.UserInfo;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class FriendFragment extends Fragment
{
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_friend, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
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
				int participantID = (Integer) itemData.get(Consts.UserID);
				
//				// start DialogActivity
//				Intent intent = new Intent(getActivity(), DialogActivity.class);
//				intent.putExtra(Consts.ParticipantID, participantID);
//				startActivity(intent);
				// start PersonnalInfoActivity
				Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
				intent.putExtra(Consts.UserID, participantID);
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
		List<UserInfo> userArray = AppController.getInstance().getFriendManager().getAllFriendInfo();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for (UserInfo friend : userArray) 
		{
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", friend.getName());
			map.put("msg", friend.getSignature());
			map.put("img", ResourceManager.getInstance().getBitmapFromAsset(friend.getIcon()));
			map.put(Consts.UserID, friend.getID());
			list.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, R.layout.listitem_friend,
				new String[]{"name", "msg", "img"},
				new int[]{R.id.name, R.id.msg, R.id.img});
		
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
