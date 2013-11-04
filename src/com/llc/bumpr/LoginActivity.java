package com.llc.bumpr;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class LoginActivity extends Activity {
	private ProgressDialog pd;
	private Context context;
	
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	String SENDER_ID = "130758040838";
	
	/** Tag used to log messages */
	static final String TAG = "com.llc.bumpr GCM";
	
	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";
	
	/** Reference to the SharedPreferences file with saved login details */
	SharedPreferences savedLogin;
	
	/** Holds reference to the email edit text box in the layout*/
	EditText email;
	
	/** Holds the reference to the password edit text box in the layout*/
	EditText password;
	
	
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	
	String regId;
	
	// Test Git Push
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		savedLogin = getSharedPreferences (LOGIN_PREF, 0);
		
		email = (EditText) findViewById(R.id.et_email);
		password = (EditText) findViewById(R.id.et_password);
		
		context = getApplicationContext();

		//Only allow the app to continue if Google Play Services is available!
		if(checkPlayServices()){
			//Google Cloud Messaging Registration
			gcm = GoogleCloudMessaging.getInstance(this);
			regId = getRegistrationId(context); //Retrieve user registration id
			
			Log.i(TAG, regId);
			
			//if(regId.isEmpty()){ //If no registration id, register in the background!
			if(TextUtils.isEmpty(regId)){
				registerInBackground();
			}
			
			checkSavedLogin();
			
			final View activityRootView = findViewById(R.id.root);
			activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							int heightDiff = activityRootView.getRootView()
									.getHeight() - activityRootView.getHeight();
							if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
								activityRootView.setOnTouchListener(new OnTouchListener() {
									@Override
									// set OnTouchListener to entire screen
									// to grab touch events
									public boolean onTouch(View arg0, MotionEvent arg1) {
										// close keyboard
										final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
										imm.hideSoftInputFromWindow(activityRootView.getWindowToken(),0);
										return false;
									}
								});
							}
						}
					});
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}
	
	/**
	 * Checks to see if a user login is saved. If so, automatically take the user to the searchDriver activity
	 */
	private void checkSavedLogin() {
		// TODO Auto-generated method stub
		if (!savedLogin.getString("email", "").contentEquals("") && !savedLogin.getString("password", "").contentEquals("")){
			Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Remove Login from stack
			startActivity(i);
		}
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If the result is empty, the app needs to register with GCM service.
	 * @param context2 Application context
	 * @return registration ID or empty string if there is no existing registration ID
	 */
	private String getRegistrationId(Context context2) {
		// TODO Auto-generated method stub
		prefs = getGCMPreferences(context2);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		//if(registrationId.isEmpty()) {
		if(TextUtils.isEmpty(registrationId)){
			Log.i(TAG, "Registration not found.");
			return "";
		}
		//Check if app was updated.  If so, it must clear the registration ID
		//since the existing regID is not guaranteed to work with the new app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context2);
		if(registeredVersion != currentVersion){
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Get Application's currrent version
	 * @param context2 Application context
	 * @return Application's version code from the {@code PackageManager}
	 */
	private int getAppVersion(Context context2) {
		// TODO Auto-generated method stub
		try{
			PackageInfo packageInfo = context2.getPackageManager()
					.getPackageInfo(context2.getPackageName(),0);
			return packageInfo.versionCode;
		} catch(NameNotFoundException e) {
			//This should never take place
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Get GCM Shared Preferences instance
	 * @param context2 Application context
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context2) {
		// TODO Auto-generated method stub
		return getSharedPreferences(LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void,Void,String>() {
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				String msg = "";
				try{
					if (gcm == null)
						gcm = GoogleCloudMessaging.getInstance(context);
					regId = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regId;
					
					// You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
					sendRegistrationIdToBackend();
					
					storeRegistrationId(context, regId);
				} catch(IOException ex){
					msg = "Error :" + ex.getMessage();
					//If there is an error, require user to click a button to register again
					//Or hit back button to exit
				}
				return msg;
			}
			
			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
		
	}

	/**
	 * Send registration ID to our server over HTTP so he has the reg ID to send
	 * push notifications down to the server
	 */
	private void sendRegistrationIdToBackend() {
		//Send reg id to Tony's DB to store in backend
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}
	 * 
	 * @param context Application's context
	 * @param regid Registration ID
	 */
	private void storeRegistrationId(Context context2, String regid){
		prefs = getGCMPreferences(context2);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regid);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkPlayServices(); //Verify Google Play Services is available
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void authenticate(Callback<User> cb) {
		String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
		String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
		
		Session session = Session.getSession();
		session.login(email, password, cb);
	}
	
	/**
	 * Reacts to the button press to log the user in
	 * @param v The view that calls the method
	 */
	public void login(View v) {
				
		final View activityRootView = findViewById(R.id.root);
		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activityRootView.getWindowToken(), 0);
		
		
		//Start loading dialog to show action is taking place
		final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "Please Wait", "Logging in...", false, true);
		authenticate(new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(User arg0, Response arg1) {
				// TODO Auto-generated method stub

				//Add user details to shared preferences upon successful login
				
				/*
				 * Issue with passing user to new intent.  Android doesn't allow SharedPrefs editor to putObjects 
				 */
				SharedPreferences.Editor loginEditor = savedLogin.edit();
				loginEditor.putString("email", email.getText().toString());
				loginEditor.putString("password", password.getText().toString());
				loginEditor.commit();
				
				dialog.dismiss();
				Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Remove Login from stack
				startActivity(i);
			}
			
		});
		
	}

	public void toRegistration(View v) {
		Intent i = new Intent(this, RegistrationActivity.class);
		startActivity(i);
	}

	// Kyle Test
	public void toUser(View v) {
		Intent i = new Intent(this, UserProfile.class);
		startActivity(i);
	}

	/**
	 * @author Khang Le
	 * Test user profile 
	 */
	public void loginWithFacebook(View v) {
		authenticate(new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
				Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
				log.info(arg0.getMessage());
			}

			@Override
			public void success(final User user, Response arg1) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), UserProfile.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("user", user);
				i.putExtras(bundle);
				startActivity(i);
			}
			
		});
	}
}

