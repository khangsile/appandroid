package com.llc.bumpr;

import java.util.logging.Logger;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.llc.bumpr.sdk.lib.BumprError;
import com.llc.bumpr.sdk.models.Registration;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class RegistrationActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
	}	
	
	public void register(View v) {
		String firstname = ((EditText)findViewById(R.id.et_firstname)).getText().toString().trim();
		String lastname = ((EditText)findViewById(R.id.et_lastname)).getText().toString().trim();
		String email = ((EditText)findViewById(R.id.et_email)).getText().toString().trim();
		String password = ((EditText)findViewById(R.id.et_password)).getText().toString().trim();
		String passwordConfirmation = ((EditText)findViewById(R.id.et_password_confirmation)).getText().toString().trim();
		
		Registration r = new Registration.Builder()
							.setPassword(password)
							.setPasswordConfirmation(passwordConfirmation)
							.setFirstName(firstname)
							.setLastName(lastname)
							.setEmail(email)
							.build();
		
		Session session = Session.getSession();
		session.register(r, new Callback<User>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				try {
					BumprError error = BumprError.errorToBumprError(arg0);
					Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
					log.info(error.getMessage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void success(User arg0, Response arg1) {
				// TODO Auto-generated method stub
				Intent i =  new Intent(getApplicationContext(), SearchDrivers.class);
				startActivity(i);
			}
			
		});
	}
}

