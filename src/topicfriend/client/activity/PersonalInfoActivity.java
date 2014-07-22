package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.R.layout;
import topicfriend.client.R.menu;
import topicfriend.client.database.AppController;
import topicfriend.client.database.Consts;
import topicfriend.client.database.UserManager;
import topicfriend.netmessage.data.UserInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Button;

public class PersonalInfoActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private boolean mCanEdit = false;
	private int mUserID = Consts.InvalidID;
	private UserManager userManager = null;
	
	private ListPreference listSexPreference = null;
	private EditTextPreference editTextNicknamePreference = null;
	private EditTextPreference editTextSignaturePreference = null;
	private SelectImagePreference selectImagePreference = null;
	private Preference buttonChatPreference = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		AppActivityManager.getInstance().onActivityCreate(this);
		
		userManager = AppController.getInstance().getUserManager();
		// get can edit
		mUserID = getIntent().getIntExtra(Consts.UserID, Consts.InvalidID);
		if (mUserID == AppController.getInstance().getOwnerID()) {
			mCanEdit = true;
		}
		else {
			mCanEdit = false;
		}
		
		
		this.beforePreferenceSceneCreate();
		
		//setContentView(R.layout.activity_personal_info);
		addPreferencesFromResource(R.xml.pref_personal_info);

		// bind all preferences to a special OnPreferenceChangeListener
		listSexPreference = (ListPreference) findPreference("list_sex");
		editTextNicknamePreference = (EditTextPreference) findPreference("edit_nickname");
		editTextSignaturePreference = (EditTextPreference) findPreference("edit_signature");
		selectImagePreference = (SelectImagePreference) findPreference("select_image_icon");
		buttonChatPreference = (Preference) findPreference("button_chat");
		
		this.afterPreferenceSceneCreate();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		this.afterPreferenceSceneDestroy();
		AppActivityManager.getInstance().onActivityDestroy(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_info, menu);
		return true;
	}


	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	private void beforePreferenceSceneCreate() {
		
		//TODO: download information from server

		UserInfo user = userManager.getByID(mUserID);
		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
		Editor editor = sharedPrefs.edit();
		editor.putString("edit_nickname", user.getName());
		editor.putString("list_sex", ""+0);
		editor.putString("edit_signature", user.getSignature());			
		editor.commit();
		
	}
	
	private void afterPreferenceSceneCreate() {
		// initialize Image Select Preference
		final UserInfo user = userManager.getByID(mUserID);
		selectImagePreference.setActivity(this);
		selectImagePreference.changeImage(user.getIcon());
		
		bindPreferenceSummaryToValue(listSexPreference);
		bindPreferenceSummaryToValue(editTextNicknamePreference);
		bindPreferenceSummaryToValue(editTextSignaturePreference);
		bindPreferenceSummaryToValue(selectImagePreference);
		
		if (mCanEdit == true) {
			
			
			buttonChatPreference.setTitle("");
			buttonChatPreference.setEnabled(false);
		}
		else {
			listSexPreference.setSelectable(false);
			editTextNicknamePreference.setSelectable(false);
			editTextSignaturePreference.setSelectable(false);
			selectImagePreference.setSelectable(false);
			
			buttonChatPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					
					Intent intent = new Intent(PersonalInfoActivity.this, DialogActivity.class);
					intent.putExtra(Consts.ParticipantID, user.getID());
					startActivity(intent);
					
					return true;
				}
			});
		}
	}
	
	private void afterPreferenceSceneDestroy() {
		
		//TODO: upload information to server
		
	}
	
	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof EditTextPreference) {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			else if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);
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
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}
	
	
}
