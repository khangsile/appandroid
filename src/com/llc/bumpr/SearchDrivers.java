package com.llc.bumpr;

import com.llc.bumpr.R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class SearchDrivers extends Activity {

	private static final int RQS_GooglePlayServices = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
	}
	
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//Verify user has good version of google play services. Necessary for maps
		int retCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable
				(getApplicationContext());
		//If successful, carry on.  Otherwise, request them to download GP Services
		if (retCode != ConnectionResult.SUCCESS)
			GooglePlayServicesUtil.getErrorDialog(retCode, this, RQS_GooglePlayServices).show();
	}

    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		
		return true;
		
	}

}
