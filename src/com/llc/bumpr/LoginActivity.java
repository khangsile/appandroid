package com.llc.bumpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.llc.bumpr.sdk.lib.BumprClient;
import com.llc.bumpr.sdk.lib.BumprError;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class LoginActivity extends Activity {
	/** Reference to the current context */
	private Context context;

	// Constants needed for GCM registration
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
		
	/** Google Project # required for GCM messages */
	String SENDER_ID = getString(R.string.gcm_sender_id);

	/** Tag used to log messages */
	static final String TAG = "com.llc.bumpr GCM";

	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";

	/** Reference to the SharedPreferences file with saved login details */
	private SharedPreferences savedLogin;

	/** Holds reference to the email edit text box in the layout */
	EditText email;

	/** Holds the reference to the password edit text box in the layout */
	EditText password;

	/** Reference to a Google Cloud Messaging object */
	private GoogleCloudMessaging gcm;
	
	/** AtomicInteger to keep track of messages sent */
	private AtomicInteger msgId = new AtomicInteger();
	
	/** Reference to shared preferences where GCM details are stored */
	private SharedPreferences prefs;
	
	/** String to hold the GCM registration ID */
	private String regId;

	// Test Git Push
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Set base url to connect to Tony's server for testing
		
		BumprClient.setBaseURL("http://192.168.1.156:3000/api/v1");

		// Get shared preferences with saved login details
		savedLogin = getSharedPreferences(LOGIN_PREF, 0);

		context = getApplicationContext();

		// Start dialog while it registers user to GCM and tries to log them in
		final ProgressDialog pd = ProgressDialog.show(LoginActivity.this,
				"Please Wait", "Checking for saved login...", false, true);
		// Only allow the app to continue if Google Play Services is available!
		if (checkPlayServices(pd)) {
			// Google Cloud Messaging Registration
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId(context); // Retrieve user registration id

			Log.i(TAG, regId);

			// If no registration id, register in the background!
			if (TextUtils.isEmpty(regId)) {
				registerInBackground();
			}
			// See if user details are saved on this phone. If so, login for them
			checkSavedLogin(pd);
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
		pd.dismiss();

		// Get references to text fields in the layout
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
			String email = savedLogin.getString("email", ""); // Get email
			String password = savedLogin.getString("password", ""); // Get password
			String authToken = savedLogin.getString("auth_token", "");
			Session.getSession().login(email, password,
					getRegistrationId(this), new Callback<User>() {
						// Login through backend. Hopefully avoid this in the
						// future
						@Override
						public void failure(RetrofitError arg0) { // Should not get here
							pd.dismiss();
						}

						@Override
						public void success(User arg0, Response arg1) {
							// If successful, create new intent, set flags, and
							// start the activity
							Intent i = new Intent(getApplicationContext(),
									SearchDrivers.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); 
							// Remove Login
							startActivity(i);// Start activity
							pd.dismiss(); // dismiss dialog
						}
					});
		}
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If the result is empty, the app needs to register with GCM service.
	 * 
	 * @param context2 Application context
	 * @return registration ID or empty string if there is no existing
	 * registration ID
	 */
	private String getRegistrationId(Context context2) {
		// TODO Auto-generated method stub
		// Get shared preferences and check if GCM registration id is stored
		prefs = getGCMPreferences(context2);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		// If no registration stored, return empty string
		if (TextUtils.isEmpty(registrationId)) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated. If so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new app
		// version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context2);
		if (registeredVersion != currentVersion) {
			// If new version, return empty string
			Log.i(TAG, "App version changed.");
			return "";
		}
		// Return registration id if all tests were passed
		return registrationId;
	}

	/**
	 * Get Application's current version
	 * 
	 * @param context2 Application context
	 * @return Application's version code from the {@code PackageManager}
	 */
	private int getAppVersion(Context context2) {
		// TODO Auto-generated method stub
		try {
			// Check package info and return the version
			PackageInfo packageInfo = context2.getPackageManager()
					.getPackageInfo(context2.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// This should never take place
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Get GCM Shared Preferences instance
	 * 
	 * @param context2 Application context
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context2) {
		// TODO Auto-generated method stub
		return getSharedPreferences(LoginActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				String msg = "";
				try {
					// If no Gcm instance, create a new instance for this
					// context
					if (gcm == null)
						gcm = GoogleCloudMessaging.getInstance(context);
					// register the user to GCM and store their registration id
					regId = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regId;

					// You should send the registration ID to your server over
					// HTTP, so it can use GCM/HTTP or CCS to send messages to your
					// app. The request to your server should be authenticated if
					// your app is using accounts.

					// Not needed currently. We are sending up the GCM API key
					// upon login sendRegistrationIdToBackend();

					// Store registration id here in shared preferences
					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// If there is an error, require user to click a button to
					// register again
					// Or hit back button to exit
				}
				// return GCM API key to onPostExecute
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null); // Execute the async task

	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}
	 * 
	 * @param context Application's context
	 * @param regid Registration ID
	 */
	private void storeRegistrationId(Context context2, String regid) {
		// Get GCM preferences
		prefs = getGCMPreferences(context2);
		int appVersion = getAppVersion(context); // Get the app version
		Log.i(TAG, "Saving regId on app version " + appVersion);

		// Write the app version and GCM API key to shared preferences and store
		// them
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regid);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 * 
	 * @param pd Reference to the displayed progress dialog
	 */
	private boolean checkPlayServices(final ProgressDialog pd) {
		// Check if GOogle Play Services are available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If not successful attempt to recover from the error and have them
		// download google play services
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				pd.dismiss();
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else { // Otherwise, the device is not support
				Log.i(TAG, "This device is not supported.");
				pd.dismiss();
				finish();
			}
			// If the error is not recoverable, return false
			return false;
		}
		// If google play services are installed and activated, return true
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// checkPlayServices(); //Verify Google Play Services is available
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void authenticate(Callback<User> cb) {
		// Get information stored in text boxes
		String email = ((EditText) findViewById(R.id.et_email)).getText()
				.toString();
		String password = ((EditText) findViewById(R.id.et_password)).getText()
				.toString();

		// Perform login
		Session session = Session.getSession();
		session.login(email, password, getRegistrationId(this), cb);
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
		// make call to authenticate
		authenticate(new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// dismiss dialog and display failed login text
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Login Failed",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(User user, Response arg1) {
				// Store details upon successful login
				SharedPreferences.Editor loginEditor = savedLogin.edit();
				loginEditor.putString("email", email.getText().toString());
				loginEditor.putString("password", password.getText().toString());
				loginEditor.putString("auth_token", Session.getSession().getAuthToken());
				loginEditor.commit();

				// dismiss dialog and create new intent
				dialog.dismiss();
				Intent i = new Intent(getApplicationContext(),
						SearchDrivers.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				// Remove Login from stack
				startActivity(i); // start new intent
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
		// start Facebook Login
		List<String> permissions = new ArrayList<String>();
		permissions.add("email"); //get permission to see user's email account
		//Welcome to nested callback hell. 
		openActiveSession(this, true, new com.facebook.Session.StatusCallback() {

			@Override
			public void call(com.facebook.Session session, SessionState state, Exception exception) {
				if (state.isOpened()) {
					session.getAccessToken();
					com.facebook.Request.newMeRequest(session, new com.facebook.Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, com.facebook.Response response) {
							//login using the constructed FbLogin HashMap
							Session.getSession().login(constructFbLogin(com.facebook.Session.getActiveSession(), user), 
									new Callback<User>() {

										@Override
										public void failure(RetrofitError arg0) { // do nothing for now
										}

										@Override
										public void success(User arg0, Response arg1) {											
											Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
											startActivity(i);
										}
								
							});
						}
					}).executeAsync();
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
	
	/**
	 * Constructs the HashMap to send to the server for Facebook login
	 * @param session The current Facebook user session
	 * @param user the current user as a Facebook GraphUser
	 * @return the HashMap to send to the server for Facebook login
	 */
	private HashMap<String, Object> constructFbLogin(com.facebook.Session session, GraphUser user) {
		HashMap<String, Object> login = new HashMap<String, Object>();
		login.put("provider", "facebook");
		login.put("uid", user.getId());
		
		HashMap<String, Object> info = new HashMap<String, Object>();
		info.put("email", user.getProperty("email"));
		info.put("first_name", user.getFirstName());
		info.put("last_name", user.getLastName());
		info.put("image", "http://graph.facebook.com/" + user.getId() + "/picture?type=square");
		login.put("info", info);
		
		HashMap<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("token", session.getAccessToken());
		credentials.put("expires_at", session.getExpirationDate().getTime());
		credentials.put("expires", true);
		login.put("credentials", credentials);
		
		return login;
	}
}
