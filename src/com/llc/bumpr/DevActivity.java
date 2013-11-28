package com.llc.bumpr;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.sdk.lib.BumprClient;
import com.llc.bumpr.sdk.models.Session;

public class DevActivity extends SherlockActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dev_activity);
	}
	
	public void changeIP(View v) {
		EditText ip = (EditText) findViewById(R.id.et_ip_address);
		String baseURL = ip.toString();
		Session.baseURL = baseURL + "/api/v1";
		BumprClient.setBaseURL(baseURL + "/api/v1");
		Log.i("BUMPRCLIENT", "changed to " + baseURL + "/api/v1");
	}
	
	public void exit(View v) {
		finish();
	}
	
}
