package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.R.layout;
import topicfriend.client.R.menu;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.TextUtils;
import android.view.Menu;

public class PersonalInfoActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.beforePreferenceSceneCreate();
		
		//setContentView(R.layout.activity_personal_info);
		addPreferencesFromResource(R.xml.pref_personal_info);

		// bind all preferences to a special OnPreferenceChangeListener
		bindPreferenceSummaryToValue(findPreference("list_sex"));
		bindPreferenceSummaryToValue(findPreference("edit_nickname"));
		bindPreferenceSummaryToValue(findPreference("edit_signature"));
		
		SelectImagePreference selectImagePreference = (SelectImagePreference) findPreference("select_image_icon");
		selectImagePreference.setActivity(this);
		bindPreferenceSummaryToValue(selectImagePreference);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		this.afterPreferenceSceneDestroy();
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
		
		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
		Editor editor = sharedPrefs.edit();
		
		editor.putString("edit_nickname", "test nickname");
		editor.putString("list_sex", ""+0);
		editor.putString("edit_signature", "test signature");
		
		editor.commit();
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
			
			
			
//			if (preference instanceof ListPreference) {
//				// For list preferences, look up the correct display value in
//				// the preference's 'entries' list.
//				ListPreference listPreference = (ListPreference) preference;
//				int index = listPreference.findIndexOfValue(stringValue);
//
//				// Set the summary to reflect the new value.
//				preference
//						.setSummary(index >= 0 ? listPreference.getEntries()[index]
//								: null);
//
//			} else if (preference instanceof RingtonePreference) {
//				// For ringtone preferences, look up the correct display value
//				// using RingtoneManager.
//				if (TextUtils.isEmpty(stringValue)) {
//					// Empty values correspond to 'silent' (no ringtone).
//					preference.setSummary(R.string.pref_ringtone_silent);
//
//				} else {
//					Ringtone ringtone = RingtoneManager.getRingtone(
//							preference.getContext(), Uri.parse(stringValue));
//
//					if (ringtone == null) {
//						// Clear the summary if there was a lookup error.
//						preference.setSummary(null);
//					} else {
//						// Set the summary to reflect the new ringtone display
//						// name.
//						String name = ringtone
//								.getTitle(preference.getContext());
//						preference.setSummary(name);
//					}
//				}
//
//			} 
//			else {
//				// For all other preferences, set the summary to the value's
//				// simple string representation.
//				preference.setSummary(stringValue);
//			}
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
