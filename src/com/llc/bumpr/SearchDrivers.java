package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
import com.llc.bumpr.lib.EndlessListView;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class SearchDrivers extends SherlockFragmentActivity implements EndlessListView.EndlessListener,
				GooglePlayServicesClient.ConnectionCallbacks,
				GooglePlayServicesClient.OnConnectionFailedListener{
	
	/** Reference to the ActionBarSherlock action bar */
	private com.actionbarsherlock.app.ActionBar actionBar;
	/** Request value to allow Google Maps to display */
	private static final int RQS_GooglePlayServices = 1;
	/** Reference to the sliding menu UI element */
	private SlidingMenu slidingMenu;
	/** Reference to the Layout object holding the map fragment */ 
	private LinearLayout map;
	/** Reference to the Layout object holding the endless list view */
	private LinearLayout driverLayout;
	/** Reference to the search bar in the action bar */
	private SearchView searchView;
	
	/** The next page of data to load from the database into the endless list view of drivers */
	private int page;
	/** Reference the endless list view holding the list of available drivers */
	private EndlessListView driverList;
	/** Reference to the adapter that will populate the endless list */
	private EndlessAdapter endListAdp;
	
	/** Reference to the list view that will hold the sliding menu information */
	private ListView lvMenu;
	/** List that will hold the data to fill the sliding menu */
	private List<Pair<String, Object>> menuList;
	/** Reference to the adapter that will populate the sliding menu with it's data */
	private SlidingMenuListAdapter menuAdpt;
	
	int testCntr = 1;
	
	/** Reference to the map UI element */
	private GoogleMap gMap;
	/** Reference to the location client (Allows use of GPS) */
    private LocationClient mLocationClient;
    
    /** Request value to get current location (Using GPS) */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
		actionBar = getSupportActionBar();
		map = (LinearLayout) findViewById(R.id.ll_map_container);
		
		//map.setFocusable(true); //Needed to remove focus from ABS searchView
		gMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		driverLayout = (LinearLayout) findViewById(R.id.ll_driver_list);
		
		//Inflate listview view
		View slMenu = LayoutInflater.from(getApplication()).inflate(R.layout.sliding_menu, null);
		lvMenu = (ListView) slMenu.findViewById(R.id.menu_list);

		//Setup menu to be used by sliding menu
		menuList = new ArrayList<Pair<String,Object>>();
		initList();
		
		menuAdpt = new SlidingMenuListAdapter(this, menuList);
		lvMenu.setAdapter(menuAdpt);
		setMenuOnClickListener();
		
		// Set up sliding menu
		initSlidingMenu(slMenu);
		
		//Set up endless list view for driver list
		driverList = (EndlessListView) findViewById(R.id.lv_drivers);
		driverList.setLoadingView(R.layout.loading_layout);
		driverList.setListener(this);
		
		setEndlessListOnClickListener();
		
		//Create new location client.
        mLocationClient = new LocationClient(this, this, this);
	}
	/**
	 * Initializes and configures the sliding menu
	 * @param slMenu View reference that holds the sliding menu
	 */
	private void initSlidingMenu(View slMenu){
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
	}
	
	 @Override
     protected void onStart() {
             // TODO Auto-generated method stub
             super.onStart();
             
             if(isGooglePlayServicesAvailable()){
                     mLocationClient.connect();
                     //gMap.setMyLocationEnabled(true);
             }
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
            //Attempt to reconnect if result is ok!
                 mLocationClient.connect();
     }
     
     /**
      * Verifies the user has Google Maps installed.  If not, it requests them to install Google Maps
      * @return Boolean value signifying if Google Maps is available
      */
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
         else { //If it can be resolved, fix it
        	 if (GooglePlayServicesUtil.isUserRecoverableError(retCode))
                 GooglePlayServicesUtil.getErrorDialog(retCode, this,
                                 RQS_GooglePlayServices).show();
                 return false;
         }
    }
     /**
      * Connection to GPS failed. Attempt to resolve it.  Otherwise, catch the exception
      * @param connResult Result from the connection attempt
      */
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
     /**
      * Connected to GPS successfully.  Update current location and display marker on the map.
      */
     @Override
     public void onConnected(Bundle bundle) {
             // TODO Auto-generated method stub
             Location loc = mLocationClient.getLastLocation();
             LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
             CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
             gMap.animateCamera(camUpdate);
     }

     @Override
     public void onDisconnected() {
             // TODO Auto-generated method stub
             
     } 
	
     /**
      * Fills menu list object with the information to display in the sliding menu
      */
	private void initList() {
    	menuList.add(new Pair<String, Object>("Image", "Kyle Cooper"));//Pass User Object in future
    	menuList.add(new Pair<String, Object>("Text", "Create Review"));
    	menuList.add(new Pair<String, Object>("Text", "My Sent Requests"));
    	menuList.add(new Pair<String, Object>("Text", "My Received Requests"));
    	menuList.add(new Pair<String, Object>("Text", "Request"));
    	menuList.add(new Pair<String, Object>("Switch", "Driver Mode"));
    	menuList.add(new Pair<String, Object>("Text", "Edit Driver Settings"));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.it_search_bar).getActionView();
		searchView.setQueryHint("Enter destination here...");
		searchView.setIconifiedByDefault(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			// Search Listener
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				//Hide keyboard when enter pressed
				searchView.clearFocus();
				
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
	/**
	 * Called when a new location is entered in the destination search box.  Reset
	 * information in the endless list view and begin displaying new results!
	 */
	public void newSearch(){
		//Reset counter for new search (Set to 1)
		testCntr = 1;
		//Clear the list and add the new search results
		
		
		driverList.resetData(createItems());
	}
	
	//Files needed for implementing/testing endless list view
	/**
	 * Class to asynchronously load driver data from database and display it in the endless list view
	 */
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

	/**
	 * Loads new data from the database and adds it to the endless list view
	 */
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		Log.w("com.llc.bumpr", "Adding new data!");
		testCntr += 10;
		//Load more data
		FakeNetLoader f1 = new FakeNetLoader();
		f1.execute(new String[]{});
		
	}
	
	/**
	 * Adds new data to the list of current data in the endless list.
	 * @return Updated list of data to display in the endless list
	 */
	private List<Object> createItems() {
		// TODO Auto-generated method stub
		List<Object> items = new ArrayList<Object>();
		
		for (int i = testCntr; i <testCntr+10; i++)
			items.add("Driver " + i);
		return items;
	}
	
	/**
	 * Creates the listener for the onClick of the sliding menu
	 */
	private void setMenuOnClickListener() {
		lvMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent i = null;
				switch (position) {
				case 0:
					i = new Intent(getApplicationContext(), EditProfileActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 1:
					i = new Intent(getApplicationContext(), CreateReviewActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 2:
					i = new Intent(getApplicationContext(), MyRequests.class);
					i.putExtra("user", User.getActiveUser()); //Pass Incoming Requests
					break;
				case 3:
					i = new Intent(getApplicationContext(), MyRequests.class);
					i.putExtra("user", User.getActiveUser()); //Pass outgoing requests
					break;
				case 4:
					i = new Intent(getApplicationContext(), RequestActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 5:
					i = new Intent(getApplicationContext(), EditDriverActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 6:
					i = new Intent(getApplicationContext(), LoginActivity.class);
					//clear history and shit
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					Session session = Session.getSession();
					session.logout(new Callback<Response>() {

						@Override
						public void failure(RetrofitError arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void success(Response arg0, Response arg1) {
							// TODO Auto-generated method stub
						}
						
					});
				default:
					break;
				}
				
				if (i != null) {
					startActivity(i);
				}
			}
		});
	}
	
	private void setEndlessListOnClickListener(){
		driverList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				//Get data at position selected
				Object user = (Object)parent.getItemAtPosition(position);
				
				Toast.makeText(getApplicationContext(), user.toString() + gMap.getCameraPosition().target.toString(), Toast.LENGTH_SHORT).show();
			}
			
		});
	}

}
