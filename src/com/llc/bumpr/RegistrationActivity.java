package com.llc.bumpr;

import java.util.logging.Logger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.llc.bumpr.sdk.lib.BumprError;
import com.llc.bumpr.sdk.models.Registration;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class RegistrationActivity extends Activity {
	/** Reference to the SharedPreferences file with saved login details */
	private SharedPreferences savedLogin;
	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration); //Display the registration layout
        
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
		Registration r = new Registration.Builder()
							.setPassword(password)
							.setPasswordConfirmation(passwordConfirmation)
							.setFirstName(firstname)
							.setLastName(lastname)
							.setEmail(email)
							.build();
		
		//Use the active session to send the request to the server
		Session session = Session.getSession();
		session.register(r, new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				try {
					//Log the error that was caught
					Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
					log.info(arg0.getUrl());
					BumprError error = BumprError.errorToBumprError(arg0);
					log.info(error.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Display failed message
				Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(User arg0, Response arg1) {
				// TODO Auto-generated method stub
				//Add user details to shared preferences upon successful login
				
				/*
				 * Issue with passing user to new intent.  Android doesn't allow SharedPrefs editor to putObjects 
				 */
				
				/* Leave this for the time being. Got to think of a better way to integrate this (with DriverProfile in mind). */
				//Store details upon successful login in Shared Preferences
				SharedPreferences.Editor loginEditor = savedLogin.edit();
				loginEditor.putString("email", email);
				loginEditor.putString("password", password);
				loginEditor.commit();
				
				Intent i =  new Intent(getApplicationContext(), SearchDrivers.class); //Create intent to go to next
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//Remove Login from stack
				startActivity(i); //Start the intent
			}
			
		});
	}
}

