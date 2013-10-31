package com.llc.bumpr;

import java.util.logging.Logger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class LoginActivity extends Activity {
	private ProgressDialog pd;
	private Context context = this;
	
	// Test Git Push
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Sets OnTouchListener to close keyboard by clicking off the screen
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

