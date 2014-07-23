package topicfriend.client.activity;

import topicfriend.client.R;

import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.NetMessageUpdateUserInfo;
import topicfriend.client.R.layout;
import topicfriend.client.R.menu;
import topicfriend.client.appcontroller.AppActivityManager;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.AccountManager;
import topicfriend.client.base.Consts;
import topicfriend.client.base.UserInfoUpdateListener;
import topicfriend.netmessage.data.UserInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class PersonalInfoActivity extends PreferenceActivity implements OnPreferenceChangeListener 
{
	private boolean mCanEdit = false;
	private UserInfo mUserInfo=null;
	
	private ListPreference listSexPreference = null;
	private EditTextPreference editTextNicknamePreference = null;
	private EditTextPreference editTextSignaturePreference = null;
	private SelectImagePreference selectImagePreference = null;
	private Preference buttonChatPreference = null;
	private UserInfoUpdateListener mUserInfoUpdateListener=new UserInfoUpdateListener()
	{
		@Override
		public void onUserInfoUpdated(UserInfo oldInfo, UserInfo newInfo) 
		{
			Toast.makeText(getApplicationContext(), "update user info succeed", Toast.LENGTH_LONG).show();
			mUserInfo=newInfo;
		}
		
		@Override
		public void onUserInfoUpdateFailed(NetMessageError msg) 
		{
			Toast.makeText(getApplicationContext(), "update user info failed", Toast.LENGTH_LONG).show();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		
		// get can edit
		int userID = getIntent().getIntExtra(Consts.UserID, Consts.InvalidID);
		if (userID == AppController.getInstance().getAccountManager().getUserID())
		{
			mCanEdit = true;
			mUserInfo=AppController.getInstance().getAccountManager().getLoginUserInfo();
		}
		else 
		{
			mCanEdit = false;
			mUserInfo=AppController.getInstance().getFriendManager().getFriendInfoByID(userID);
		}
		
		
		this.beforePreferenceSceneCreate();
		
		//setContentView(R.layout.activity_personal_info);
		if (mCanEdit) addPreferencesFromResource(R.xml.pref_personal_info);
		else addPreferencesFromResource(R.xml.pref_personal_info_other);

		// bind all preferences to a special OnPreferenceChangeListener
		listSexPreference = (ListPreference) findPreference("list_sex");
		editTextNicknamePreference = (EditTextPreference) findPreference("edit_nickname");
		editTextSignaturePreference = (EditTextPreference) findPreference("edit_signature");
		selectImagePreference = (SelectImagePreference) findPreference("select_image_icon");
		if (!mCanEdit) buttonChatPreference = (Preference) findPreference("button_chat");
		
		this.afterPreferenceSceneCreate();
		AppController.getInstance().getAccountManager().addUserInfoUpdateListener(mUserInfoUpdateListener);
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		
		AppController.getInstance().getAccountManager().removeUserInfoUpdateListener(mUserInfoUpdateListener);
		this.afterPreferenceSceneDestroy();
		AppController.getInstance().getAppActivityManager().onActivityDestroy(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_info, menu);
		return true;
	}


	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private void beforePreferenceSceneCreate() 
	{
		
		//TODO: download information from server

		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
		Editor editor = sharedPrefs.edit();
		editor.putString("edit_nickname", mUserInfo.getName());
		editor.putString("list_sex", ""+mUserInfo.getSex());
		editor.putString("edit_signature", mUserInfo.getSignature());	
		editor.putString("select_image_icon", mUserInfo.getIcon());
		editor.commit();
		
	}
	
	private void afterPreferenceSceneCreate() 
	{
		// initialize Image Select Preference
		final UserInfo user = mUserInfo;
		selectImagePreference.setActivity(this);
		selectImagePreference.changeImage(user.getIcon());
		
		bindPreferenceSummaryToValue(listSexPreference);
		bindPreferenceSummaryToValue(editTextNicknamePreference);
		bindPreferenceSummaryToValue(editTextSignaturePreference);
		bindPreferenceSummaryToValue(selectImagePreference);
		
		if (mCanEdit == true) 
		{

		}
		else
		{
			listSexPreference.setSelectable(false);
			editTextNicknamePreference.setSelectable(false);
			editTextSignaturePreference.setSelectable(false);
			selectImagePreference.setSelectable(false);
			
			buttonChatPreference.setOnPreferenceClickListener(new OnPreferenceClickListener()
			{
				@Override
				public boolean onPreferenceClick(Preference preference)
				{
					
					Intent intent = new Intent(PersonalInfoActivity.this, DialogActivity.class);
					intent.putExtra(Consts.ParticipantID, user.getID());
					startActivity(intent);
					
					return true;
				}
			});
		}
	}
	
	private void afterPreferenceSceneDestroy() 
	{
	}
	
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value)
		{
			String stringValue = value.toString();

			AppController app = AppController.getInstance();
			UserInfo ownerUserInfo = mUserInfo;
			boolean hasChanged = false;
			
			if (preference instanceof EditTextPreference)
			{
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
				
				
				if (preference.getKey().equals("edit_nickname")) 
				{
					hasChanged = !(((EditTextPreference) preference).getText().equals(ownerUserInfo.getName()));
				}
				else if (preference.getKey().equals("edit_signature"))
				{
					hasChanged = !((EditTextPreference) preference).getText().equals(ownerUserInfo.getSignature());
				}
				
			}
			else if (preference instanceof ListPreference) 
			{
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index]:null);
				if (preference.getKey() == "list_sex")
				{
					hasChanged = !(listPreference.getValue().equals(""+ownerUserInfo.getSex()));
				}				
			}
			else if (preference instanceof SelectImagePreference) 
			{
				hasChanged = !(((String)(value)).equals(ownerUserInfo.getIcon()));
			}
			
			if (mCanEdit && hasChanged) 
			{
				UserInfo userInfo = new UserInfo(
						mUserInfo.getID(),
						listSexPreference.getValue().equals("0")? UserInfo.SEX_MALE: UserInfo.SEX_FEMALE,
						editTextNicknamePreference.getText(), 
						editTextSignaturePreference.getText(), 
						selectImagePreference.getSelectedImageName());
				
				AppController.getInstance().getAccountManager().reqUpdateUserInfo(userInfo);
			}
			
			return true;
		}
	};
	
	
	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private void bindPreferenceSummaryToValue(Preference preference) 
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),
				""));
	}
}
