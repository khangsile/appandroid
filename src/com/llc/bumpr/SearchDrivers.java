package com.llc.bumpr;

import java.util.logging.Logger;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.llc.bumpr.R;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;

import com.actionbarsherlock.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SearchDrivers extends SherlockActivity {

	com.actionbarsherlock.app.ActionBar actionBar;
	private static final int RQS_GooglePlayServices = 1;
	private SlidingMenu slidingMenu;
	private LinearLayout map;
	private LinearLayout driverLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
		actionBar = getSupportActionBar();
		map = (LinearLayout) findViewById(R.id.ll_map_container);
		driverLayout = (LinearLayout) findViewById(R.id.ll_driver_list);

		// Set up sliding menu
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

	// If slidingMenu showing, back closes menu. Otherwise, calls parent back
	// action
	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing())
			slidingMenu.toggle();
		else if (((LinearLayout.LayoutParams)map.getLayoutParams()).weight == .5){
			//Set Driver List weight
			LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams)driverLayout.getLayoutParams();
			driverPars.weight = 0.0f;
			driverLayout.setLayoutParams(driverPars);
			LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams)map.getLayoutParams();
			mapPars.weight = 1.0f;
			map.setLayoutParams(mapPars);
		}
		else
			super.onBackPressed();
	}

	// If menu button pressed, show or hide the sliding menu
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// to show/hide the SlidingMenu when the home icon on the actionbar is
	// pressed
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.slidingMenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Verify user has good version of google play services. Necessary for
		// maps
		int retCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		// If successful, carry on. Otherwise, request them to download GP
		// Services
		if (retCode != ConnectionResult.SUCCESS)
			GooglePlayServicesUtil.getErrorDialog(retCode, this,
					RQS_GooglePlayServices).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.it_search_bar).getActionView();
		searchView.setQueryHint("Enter destination here...");
		searchView.setIconifiedByDefault(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			// Search Listener
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams)map.getLayoutParams();
				mapPars.weight = 0.5f;
				map.setLayoutParams(mapPars);
				//Set Driver List weight
				LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams)driverLayout.getLayoutParams();
				driverPars.weight = 0.5f;
				driverLayout.setLayoutParams(driverPars);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				
				return false;
			}
		});

		return true;

	}

}
