package com.llc.bumpr;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.llc.bumpr.R;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class LoginActivity extends Activity {

	// Test Git Push
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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

	public void authenticate() {
		String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
		String password = ((EditText) findViewById(R.id.et_password)).getText().toString();
		
		Session session = Session.getSession();
		session.login(email, password, new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(User arg0, Response arg1) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), SearchDrivers.class);
				startActivity(i);
			}
			
		});
	}
	
	public void login(View v) {
		authenticate();
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

	public void loginWithFacebook(View v) {
		Intent i = new Intent(this, SearchDrivers.class);
		startActivity(i);
	}
}
