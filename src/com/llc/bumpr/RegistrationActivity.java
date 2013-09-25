package com.llc.bumpr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.bumpr.R;

public class RegistrationActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
	}	
	
	public void register(View v) {
		String firstname = ((EditText)findViewById(R.id.et_firstname)).toString().trim();
		String lastname = ((EditText)findViewById(R.id.et_lastname)).toString().trim();
		String email = ((EditText)findViewById(R.id.et_email)).toString().trim();
		String password = ((EditText)findViewById(R.id.et_password)).toString().trim();
		
		if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
			
		}
	}
}

