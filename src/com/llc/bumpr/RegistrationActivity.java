package com.llc.bumpr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.lib.GCMRegistrationManager;
import com.llc.bumpr.sdk.models.Registration;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class RegistrationActivity extends Activity {
	/** Reference to the SharedPreferences file with saved login details */
	private SharedPreferences savedLogin;
	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";
	
	private GCMRegistrationManager manager;

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration); //Display the registration layout
        
        final ProgressDialog dialog = ProgressDialog.show(this,
				"Please Wait", "Checking for saved login...", false, true);
        if (GCMRegistrationManager.checkPlayServices(dialog, this)) {
        	manager = new GCMRegistrationManager(this);
        	manager.getRegistrationIdFromGCM();
        }
        
        //Get shared preferences with saved login details
      	savedLogin = getSharedPreferences (LOGIN_PREF, 0);
	}	
	
	/**
	 * Submit information to the application server to create a new user with the supplied information.
	 * @param v Reference to the view who called this function upon being clicked
	 */
	public void register(View v) {
		//Get references to the view objects in the layout
		final String firstname = ((EditText)findViewById(R.id.et_firstname)).getText().toString().trim();
		final String lastname = ((EditText)findViewById(R.id.et_lastname)).getText().toString().trim();
		final String email = ((EditText)findViewById(R.id.et_email)).getText().toString().trim();
		final String password = ((EditText)findViewById(R.id.et_password)).getText().toString().trim();
		final String passwordConfirmation = ((EditText)findViewById(R.id.et_password_confirmation)).getText().toString().trim();
		
		//Create a new registration object with the information provided
		Registration r = new Registration.Builder(new Registration())
							.setRegistrationId(manager.getRegistrationId())
							.setPassword(password)
							.setPasswordConfirmation(passwordConfirmation)
							.setFirstName(firstname)
							.setLastName(lastname)
							.setEmail(email)
							.build();
		
		//Use the active session to send the request to the server
		Session session = Session.getSession();
		session.register(this, r, new FutureCallback<User>() {

			@Override
			public void onCompleted(Exception arg0, User arg1) {
				if (arg0 == null) {
					SharedPreferences.Editor loginEditor = savedLogin.edit();
					loginEditor.putString("email", email);
					loginEditor.putString("password", password);
					loginEditor.putString("auth_token", Session.getSession().getAuthToken());
					loginEditor.commit();
					
					Intent i =  new Intent(getApplicationContext(), SearchDrivers.class); //Create intent to go to next
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Remove Login from stack
					startActivity(i); //Start the intent
				} else {
					Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
					arg0.printStackTrace();
				}
			}
			
		});
	}
}

