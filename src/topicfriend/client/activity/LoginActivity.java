package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.database.AppController;
import topicfriend.client.database.Channel;
import topicfriend.client.database.ChannelManager;
import topicfriend.client.database.ResourceManager;
import topicfriend.client.network.NetworkManager;
import topicfriend.client.netwrapper.NetMessageHandler;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessage;
import topicfriend.netmessage.NetMessageChatFriend;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageID;
import topicfriend.netmessage.NetMessageLogin;
import topicfriend.netmessage.NetMessageLoginSucceed;
import topicfriend.netmessage.NetMessageRegister;
import topicfriend.netmessage.data.UserInfo;
import topicfriend.network.Network;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	// Model references
	private NetworkManager mNetManager = null;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// create AppActivityManager to manage all activities
		AppActivityManager.getInstance().onActivityCreate(this);
		this.initApplication();
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin(false);
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin(false);
					}
				});
		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						attemptLogin(true);
					}
				});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		AppActivityManager.getInstance().onActivityDestroy(this);
		AppActivityManager.purgeInstance();
		AppController.log("Application ended!");
		
		// destroyed this application
		AppController.getInstance().destroyNetwork();
		AppController.purgeInstance();
		NetMessageReceiver.getInstance().purgeInstance();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(boolean isRegister) {
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check user name valid
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}
		

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			doLogin(isRegister);
			
		}
	}

	
	
	private void doLogin(boolean isRegister) {
		if (isRegister) {
			NetMessageRegister msgRegister = new NetMessageRegister(mEmail, mPassword, UserInfo.SEX_MALE);
			mNetManager.sendDataOne(msgRegister);
		}
		else {
			NetMessageLogin msgLogin = new NetMessageLogin(mEmail, mPassword);
			mNetManager.sendDataOne(msgLogin);
		}
		mNetManager.setMessageHandler(NetMessageID.LOGIN_SUCCEED, new NetMessageHandler() {
			@Override
			public void handleMessage(int connection, NetMessage msg) {
				NetMessageLoginSucceed loginMsg = (NetMessageLoginSucceed) msg;
				// refresh all data
				AppController.getInstance().initWithUid(loginMsg.getMyInfo().getID());
				AppController.getInstance().getTopicManager().refreshData(loginMsg.getTopicList());
				AppController.getInstance().getUserManager().refreshData(loginMsg.getFriendInfoList());
				AppController.getInstance().getUserManager().add(loginMsg.getMyInfo());
				AppController.getInstance().getChannelManager().refreshData(loginMsg.getUnreadMessageList());

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						startActivity(new Intent(getApplicationContext(), MainActivity.class));
					}
				});
			}
		});
	}
	
	private void initApplication() {

		// create AppController and init network and connect to server
		AppController.getInstance().initNetwork();
		AppController.log("Application started!");
		// create NetReceiver
		NetMessageReceiver.getInstance();
		
		
		mNetManager = AppController.getInstance().getNetworkManager();
		mNetManager.setMessageHandler(NetMessageID.ERROR, new NetMessageHandler() {
			@Override
			public void handleMessage(int connection, NetMessage msg) {
				final String errorStr = ((NetMessageError) msg).getErrorStr();
				AppController.log("[network error]: "+errorStr);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_SHORT).show();
						showProgress(false);
					}
				});
			}
		});
		mNetManager.connectToServer();
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
