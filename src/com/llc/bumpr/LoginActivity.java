package com.llc.bumpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
import com.facebook.model.GraphUser;
import com.llc.bumpr.lib.GCMRegistrationManager;
import com.llc.bumpr.sdk.lib.BumprClient;
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
	EditText email;

	/** Holds the reference to the password edit text box in the layout */
	EditText password;
	
	/** String to hold the GCM registration ID */
	private String regId;
	
	/** GCM Registration Manager object to perform the registration */
	GCMRegistrationManager gcm;

	// Test Git Push
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		BumprClient.setBaseURL("http://192.168.1.200:3000/api/v1");

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
					gcm.getRegistrationIdFromGCM(), new Callback<User>() {
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

	@Override
	protected void onResume() {
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
		session.login(email, password, gcm.getRegistrationIdFromGCM(), cb);
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
				//Build the created dialog
				AlertDialog dg = builder.create();
				//Display the dialog
				dg.show();
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
											AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
											builder.setTitle("Facebook Login Failed");
											builder.setMessage("An error occurred while logging you in via Facebook. Please try logging in using facebook later.");
											builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int arg1) {
													dialog.cancel();
												}
											});
											//Build the created dialog
											AlertDialog dg = builder.create();
											//Display the dialog
											dg.show();
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
	
	public void testButton(View v){
		Intent i = new Intent(getApplicationContext(), CreateTripActivity.class);
		startActivity(i);
	}
}
