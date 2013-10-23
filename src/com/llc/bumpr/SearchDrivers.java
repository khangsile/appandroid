package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.llc.bumpr.adapters.EndlessAdapter;
import com.llc.bumpr.adapters.SlidingMenuListAdapter;

public class SearchDrivers extends SherlockFragmentActivity implements EndlessListView.EndlessListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private com.actionbarsherlock.app.ActionBar actionBar;
	private static final int RQS_GooglePlayServices = 1;
	private SlidingMenu slidingMenu;
	private LinearLayout map;
	private LinearLayout driverLayout;
	
	private int page;
	private EndlessListView driverList;
	private EndlessAdapter endListAdp;
	
	private ListView lvMenu;
	private List<Pair<String, Object>> menuList;
	private SlidingMenuListAdapter menuAdpt;
	int testCntr = 1;
	
	private GoogleMap gMap;
	private LocationClient mLocationClient;
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
		driverLayout = (LinearLayout) findViewById(R.id.ll_driver_list);
		actionBar = getSupportActionBar();
		map = (LinearLayout) findViewById(R.id.ll_map_container);
		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		//Inflate listview view
		View slMenu = LayoutInflater.from(getApplication()).inflate(R.layout.sliding_menu, null);
		lvMenu = (ListView) slMenu.findViewById(R.id.menu_list);

		//Setup menu to be used by sliding menu
		menuList = new ArrayList<Pair<String,Object>>();
		initList();
		
		menuAdpt = new SlidingMenuListAdapter(this, menuList);
		lvMenu.setAdapter(menuAdpt);
		
		// Set up sliding menu
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(slMenu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Set up endless list view for driver list
		driverList = (EndlessListView) findViewById(R.id.lv_drivers);
		driverList.setLoadingView(R.layout.loading_layout);
		driverList.setListener(this);
		
		//Create new location client.
		mLocationClient = new LocationClient(this, this, this);
		gMap.setMyLocationEnabled(true);
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		if(isGooglePlayServicesAvailable())
			mLocationClient.connect();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		//Disconnect from client
		mLocationClient.disconnect();
		super.onStop();
	}
	
	//Handle results returned to the FragmentActivity by Google Play services
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(requestCode==CONNECTION_FAILURE_RESOLUTION_REQUEST && resultCode==Activity.RESULT_OK)
	       //Attempt to reconnect if is result is ok!
	    	mLocationClient.connect();
	}


	private void initList() {
    	menuList.add(new Pair<String, Object>("Image", "Kyle Cooper"));//Pass User Object in future
    	menuList.add(new Pair<String, Object>("Text", "Home"));
    	menuList.add(new Pair<String, Object>("Text", "Profile"));
    	menuList.add(new Pair<String, Object>("Switch", "Driver Mode"));
    	menuList.add(new Pair<String, Object>("Text", "Logout"));
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

	private boolean isGooglePlayServicesAvailable() {
		// TODO Auto-generated method stub
		// Verify user has good version of google play services. Necessary for
		// maps
		int retCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		// If successful, carry on.
		if (ConnectionResult.SUCCESS == retCode)
			return true;
		//Otherwise, request them to download GP Services
		else {
			GooglePlayServicesUtil.getErrorDialog(retCode, this,
					RQS_GooglePlayServices).show();
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		final SearchView searchView = (SearchView) menu.findItem(R.id.it_search_bar).getActionView();
		searchView.setQueryHint("Enter destination here...");
		searchView.setIconifiedByDefault(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			// Search Listener
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				//Hide keyboard when enter pressed
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				searchView.clearFocus(); //***Clearing focus has ugly animation, can we disable this animation?? ***//
				
				LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams)map.getLayoutParams();
				mapPars.weight = 0.5f;
				map.setLayoutParams(mapPars);
				//Set Driver List weight
				LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams)driverLayout.getLayoutParams();
				driverPars.weight = 0.5f;
				driverLayout.setLayoutParams(driverPars);
				
				if(endListAdp == null){
					endListAdp = new EndlessAdapter(getApplicationContext(), createItems(), R.layout.driver_row);
					driverList.setAdapter(endListAdp);
				}
				else
					newSearch(); //Reset endless list with new data
				
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
	
	//Added by KJC to clear data and add new search results for each search
	public void newSearch(){
		//Reset counter for new search (Set to 1)
		testCntr = 1;
		//Clear the list and add the new search results
		driverList.resetData(createItems());
	}
	
	//Files needed for implementing/testing endless list view
	private class FakeNetLoader extends AsyncTask<String, Void, List<Object>> {

		@Override
		protected List<Object> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(1500);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
			return createItems();
		}
		
		@Override
		protected void onPostExecute(List<Object> result){
			super.onPostExecute(result);
			driverList.addNewData(result);
		}
		
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		Log.w("com.llc.bumpr", "Adding new data!");
		testCntr += 10;
		//Load more data
		FakeNetLoader f1 = new FakeNetLoader();
		f1.execute(new String[]{});
		
	}
	
	private List<Object> createItems() {
		// TODO Auto-generated method stub
		List<Object> items = new ArrayList<Object>();
		
		for (int i = testCntr; i <testCntr+10; i++)
			items.add("Driver " + i);
		return items;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connResult) {
		// TODO Auto-generated method stub
		//If Google Play Services can resolve the errors, allow it to resolve the errors!
		if(connResult.hasResolution())
			try{
				//Start activity that tries to resolve the error
				connResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			}catch(IntentSender.SendIntentException e){
				//Thrown if Google Play Services canceled the original PendingIntent
				e.printStackTrace();
			}
	}

	//Connection to location services completed.  Get current location now
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Location loc = mLocationClient.getLastLocation();
		LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(latLng,10);
		gMap.animateCamera(camUpdate);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
