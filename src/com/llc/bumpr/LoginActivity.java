package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.SessionState;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.lib.GCMRegistrationManager;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.lib.BumprClient;
import com.llc.bumpr.sdk.models.Login;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class LoginActivity extends Activity {
	/** Reference to the current context */
	private Context context;

	/** Tag used to log messages */
	static final String TAG = "com.llc.bumpr GCM";

	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";

	/** Reference to the SharedPreferences file with saved login details */
	private SharedPreferences savedLogin;

	/** Holds reference to the email edit text box in the layout */
	private EditText email;

	/** Holds the reference to the password edit text box in the layout */
	private EditText password;
	
	/** String to hold the GCM registration ID */
	private String regId;
	
	/** GCM Registration Manager object to perform the registration */
	GCMRegistrationManager gcm;

	// Test Git Push
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		BumprClient.setBaseURL("http://192.168.1.200" + ":3000/api/v1");

		// Get shared preferences with saved login details
		savedLogin = getSharedPreferences(LOGIN_PREF, 0);

		context = getApplicationContext();

		// Start dialog while it registers user to GCM and tries to log them in
		final ProgressDialog pd = ProgressDialog.show(LoginActivity.this,
				"Please Wait", "Checking for saved login...", false, true);
		// Only allow the app to continue if Google Play Services is available!
		if (GCMRegistrationManager.checkPlayServices(pd, context)) {
			// Google Cloud Messaging Registration
			gcm = new GCMRegistrationManager(context);
			regId = gcm.getRegistrationIdFromGCM(); // Retrieve user registration id
			Log.i(TAG, regId);

			// If no registration id, register in the background!
			if (TextUtils.isEmpty(regId)) {
				gcm.registerInBackground();
			}
			
			// See if user details are saved on this phone. If so, login for them
			checkSavedLogin(pd);
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
		pd.dismiss();

		email = (EditText) findViewById(R.id.et_email);
		password = (EditText) findViewById(R.id.et_password);
	}
	
	/**
	 * Gets the result from a previous activity. For now, this is solely used for Facebook login.
	 * If we would like to use this for other methods, we will need to use a case switch based on the
	 * requestCode (or resultCode). Can't remember. 
	 */
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      com.facebook.Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }

	/**
	 * Checks to see if a user login is saved. If so, automatically take the
	 * user to the searchDriver activity
	 * 
	 * @param pd
	 */
	private void checkSavedLogin(final ProgressDialog pd) {
		// Temporary until we can find a better way to store the user state --
		// Maybe store JSON of user object in future
		if (!savedLogin.getString("email", "").contentEquals("")
				&& !savedLogin.getString("password", "").contentEquals("")) {
			String email = savedLogin.getString("email", ""); 
			String password = savedLogin.getString("password", "");
			String authToken = savedLogin.getString("auth_token", "");

			Login login = new Login.Builder()
							.setEmail(email)
							.setPassword(password)
							.setRegistrationId(gcm.getRegistrationIdFromGCM())
							.setPlatform("android")
							.build();
			
			if (authToken == null || authToken.trim().equals("")) return;
			
			Session session = Session.getSession();
			session.setAuthToken(authToken);
			
			ApiRequest r = User.getMeRequest(this, new FutureCallback<User>() {

				@Override
				public void onCompleted(Exception arg0, User arg1) {
					if (arg0 == null) {
						Toast.makeText(getApplicationContext(), arg1.getFirstName(), Toast.LENGTH_LONG).show();
						Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); 
						startActivity(i);
					} else {
						arg0.printStackTrace();
					}
				}
				
			});
			
			session.sendRequest(r);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//checkPlayServices(); //Verify Google Play Services is available
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void authenticate(FutureCallback<User> cb) {
		String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
		String password = ((EditText) findViewById(R.id.et_password)).getText().toString();

		Session session = Session.getSession();

		Login login = new Login.Builder()
						.setEmail(email)
						.setPassword(password)
						.setRegistrationId(gcm.getRegistrationIdFromGCM())
						.setPlatform("android")
						.build();
		session.login(this, login, cb);
	}

	/**
	 * Reacts to the button press to log the user in
	 * 
	 * @param v The view that calls the method
	 */
	public void login(View v) {
		// Hide keyboard
		final View activityRootView = findViewById(R.id.root);
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);

		// Start loading dialog to show action is taking place
		final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this,
				"Please Wait", "Logging in...", false, true);
		authenticate(new FutureCallback<User>() {

			@Override
			public void onCompleted(Exception arg0, User arg1) {
				dialog.dismiss();

				if (arg0 == null) {
					// Store details upon successful login
					SharedPreferences.Editor loginEditor = savedLogin.edit();
					loginEditor.putString("email", email.getText().toString());
					loginEditor.putString("password", password.getText().toString());
					loginEditor.putString("auth_token", Session.getSession().getAuthToken());
					loginEditor.commit();

					Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
				} else {					
					//Create failed login dialog to inform user login failed
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setTitle("Login Failed");
					builder.setMessage("An error occurred while logging you in. Please re-enter your credentials or register for an account.");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.cancel();
						}
					});
					
					AlertDialog dg = builder.create();
				
					dg.show();

					arg0.printStackTrace();
				}
			}

		});

	}

	/**
	 * Start registration intent and move to that intent
	 * 
	 * @param v View object representing the view object that called this
	 * function
	 */
	public void toRegistration(View v) {
		Intent i = new Intent(this, RegistrationActivity.class);
		startActivity(i);
	}

	/**
	 * @author Khang Le 
	 */
	public void loginWithFacebook(View v) {
		List<String> permissions = new ArrayList<String>();
		permissions.add("email"); //Add extra permissions here
		
		openActiveSession(this, true, new com.facebook.Session.StatusCallback() {

			@Override
			public void call(com.facebook.Session session, SessionState state, Exception exception) {
				
				if (exception != null) exception.printStackTrace();
				if (state.isOpened()) {
					String token = session.getAccessToken();
					Login login = new Login.Builder()
									.setRegistrationId(gcm.getRegistrationIdFromGCM())
									.setPlatform("android")
									.setAccessToken(token)
									.build();

					Session.getSession().loginWithFacebook(getApplicationContext(), login, new FutureCallback<User>() {

						@Override
						public void onCompleted(Exception arg0, User arg1) {
							if (arg0 == null) {
								//Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
								//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);							
								//startActivity(i); // start new intent
								Intent i = new Intent(getApplicationContext(), SearchTabActivity.class);
								startActivity(i);
							} else {
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
								builder.setTitle("Facebook Login Failed");
								builder.setMessage("An error occurred while logging you in via Facebook. Please try logging in using facebook later.");
								builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								
									@Override
									public void onClick(DialogInterface dialog, int arg1) {
										dialog.cancel();
									}
								});
										
								AlertDialog dg = builder.create();
								dg.show();
							} 
						}
					});
				}
			}
		}, permissions);
	}
	
	/**
	 * http://stackoverflow.com/questions/17609287/how-to-get-email-id-from-facebook-sdk-in-android-applications
	 */
	private static com.facebook.Session openActiveSession(Activity activity, boolean allowLoginUI, com.facebook.Session.StatusCallback callback, List<String> permissions) {
	    com.facebook.Session.OpenRequest openRequest = new com.facebook.Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
	    com.facebook.Session session = new com.facebook.Session.Builder(activity).build();
	    if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
	        com.facebook.Session.setActiveSession(session);
	        session.openForRead(openRequest);
	        return session;
	    }
	    return null;
	}
	
	public void changeIP(View v) {
		Intent i = new Intent(this, DevActivity.class);
		startActivity(i);
	}
	
	public void testButton(View v){
		Intent i = new Intent(getApplicationContext(), CreateTripActivity.class);
		startActivity(i);
	}
}
