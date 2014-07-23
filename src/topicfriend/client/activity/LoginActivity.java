package topicfriend.client.activity;

import topicfriend.client.R;
import topicfriend.client.appcontroller.AccountManager;
import topicfriend.client.appcontroller.AppActivityManager;
import topicfriend.client.appcontroller.AppController;
import topicfriend.client.appcontroller.NetworkManager;
import topicfriend.client.appcontroller.ResourceManager;
import topicfriend.client.base.ConnectionListener;
import topicfriend.client.base.LoginListener;
import topicfriend.client.netwrapper.NetMessageReceiver;
import topicfriend.netmessage.NetMessageError;
import topicfriend.netmessage.NetMessageLoginSucceed;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
public class LoginActivity extends Activity 
{
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] { "foo@example.com:hello", "bar@example.com:world" };

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

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		// create AppActivityManager to manage all activities
		AppController.getInstance().getAppActivityManager().onActivityCreate(this);
		this.initApplication();
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
			{
				if (id == R.id.login || id == EditorInfo.IME_NULL) 
				{
					attemptLogin(false);
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				attemptLogin(false);
			}
		});
		findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				attemptLogin(true);
			}
		});
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		
		AppActivityManager actMan = AppController.getInstance().getAppActivityManager();
		actMan.onActivityDestroy(this);
		actMan.popAllActivities();
		
		NetworkManager netMan=AppController.getInstance().getNetworkManager();
		netMan.destroyNetwork();
		
		// destroyed this application
		AppController.purgeInstance();
		NetMessageReceiver.getInstance().purgeInstance();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(final boolean isRegister) 
	{
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword))
		{
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} 
		else if (mPassword.length() < 4)
		{
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check user name valid
		if (TextUtils.isEmpty(mEmail))
		{
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}
		

		if (cancel)
		{
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		else 
		{
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			
			final NetworkManager netMan=AppController.getInstance().getNetworkManager();
			if(!netMan.isConnected())
			{
				ConnectionListener listener=new ConnectionListener()
				{
					@Override
					public void onConnectionLost() 
					{
						netMan.removeConnectionListener(this);
					}
					
					@Override
					public void onConnectSucceed() 
					{
						netMan.removeConnectionListener(this);
						doLogin(isRegister);
					}
					
					@Override
					public void onConnectFailed()
					{
						netMan.removeConnectionListener(this);
						Toast.makeText(LoginActivity.this, "can not connect to server", Toast.LENGTH_LONG).show();
					}
				};
				netMan.addConnectionListener(listener);
			}
		}
	}

	
	//register or login
	private void doLogin(boolean isRegister)
	{
		final AccountManager accountMan=AppController.getInstance().getAccountManager();
		LoginListener listener=new LoginListener()
		{
			@Override
			public void onLoginSucceed(NetMessageLoginSucceed msgLoginSucceed)
			{
				showProgress(false);
				accountMan.removeLoginListener(this);
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
			}
			
			@Override
			public void onLoginError(NetMessageError msgError)
			{
				showProgress(false);
				accountMan.removeLoginListener(this);
				Toast.makeText(getApplicationContext(), msgError.getErrorStr(), Toast.LENGTH_LONG).show();
			}
		};
		accountMan.addLoginListener(listener);
		
		if (isRegister) 
		{
			accountMan.register(mEmail, mPassword);
		}
		else
		{
			accountMan.login(mEmail, mPassword);
		}
	}
	
	private void initApplication() 
	{
		// create AppController and init network and connect to server
		NetworkManager netMan=AppController.getInstance().getNetworkManager();
		netMan.initNetwork();
		
        // init resource manager
        DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		ResourceManager.ScreenWidth = (int) metric.widthPixels;
		ResourceManager.ScreenHeight = (int) metric.heightPixels;
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show)
	{
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation)
						{
							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter()
					{
						@Override
						public void onAnimationEnd(Animator animation) 
						{
							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} 
		else
		{
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
