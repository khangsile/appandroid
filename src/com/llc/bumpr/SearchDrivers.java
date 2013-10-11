package com.llc.bumpr;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.llc.bumpr.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;

import com.actionbarsherlock.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchDrivers extends SherlockActivity {

	com.actionbarsherlock.app.ActionBar actionBar;
	private static final int RQS_GooglePlayServices = 1;
	private SlidingMenu slidingMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
		actionBar = getSupportActionBar();
		
		//Set up sliding menu
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(R.layout.sliding_menu);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	//If slidingMenu showing, back closes menu. Otherwise, calls parent back action
	@Override
	public void onBackPressed(){
		if(slidingMenu.isMenuShowing())
			slidingMenu.toggle();
		else
			super.onBackPressed();
	}
	
	//If menu button pressed, show or hide the sliding menu
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_MENU){
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//to show/hide the SlidingMenu when the home icon on the actionbar is pressed
	/*@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				this.slidingMenu.toggle();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}*/
	
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
		
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		
		return true;
		
	}

}
