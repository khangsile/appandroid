package com.llc.bumpr;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.PlacesAutoCompleteAdapter;
import com.llc.bumpr.lib.StringLocationTask;
import com.llc.bumpr.sdk.lib.Coordinate;
import com.llc.bumpr.sdk.models.Trip;

public class CreateTripActivity extends SherlockFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		OnItemClickListener {
	//Assign these here to make them final
	/** Reference to the start address */
	private AutoCompleteTextView startAdd;
	
	/** Reference to the end address */
	private AutoCompleteTextView endAdd;
	
	/** Reference to the trip tags*/
	private EditText tripTags;
	
	/** Reference to the create trip button*/
	private Button createBtn;
	
	/** Reference to the map UI element */
	private GoogleMap gMap;

	/** Reference to the location client (Allows use of GPS) */
	private LocationClient mLocationClient;
	
	/** Reference to the application context */
	private Context context;

	/** Request value to get current location (Using GPS) */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int RQS_GooglePlayServices = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_trip); // Set layout to show the create
												// trip page
		
		//Set context
		context = getApplicationContext();

		// Get map fragment!
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_request)).getMap();

		// Create new location client.
		mLocationClient = new LocationClient(this, this, this);
		
		// Fill references to UI elements in view
		startAdd = (AutoCompleteTextView) findViewById(R.id.et_start_loc);
		endAdd = (AutoCompleteTextView) findViewById(R.id.et_end_loc);
		tripTags = (EditText) findViewById(R.id.et_tags);
		createBtn = (Button) findViewById(R.id.btn_create);
		
		//Set places autocomplete adapters
		startAdd.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.auto_complete_list_item));
		endAdd.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.auto_complete_list_item));
		
		//Set on item click listeners
		startAdd.setOnItemClickListener(this);
		endAdd.setOnItemClickListener(this);
		
		//Set on focus change listeners
		startAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if (hasFocus){ //Resize text views
					startAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.8f));
					endAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.2f));
				}
				else{
					startAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5f));
					endAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5f));
				}
			}
		});
		
		endAdd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if (hasFocus){
					endAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.8f));
					startAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.2f));
				}
				else{
					endAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5f));
					startAdd.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.5f));
				}
			}
		});
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		//Inflate action bar sherlock
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.create_trip_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.it_calendar: //Calendar button pressed
			Toast.makeText(getApplicationContext(),
					"Calendar Pressed",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.it_user_cnt: //Add user button pressed
			Toast.makeText(getApplicationContext(),
					"Set user count pressed",
					Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Submit trip information to the application server to create the trip
	 * @param v View that is calling this function
	 */
	public void createTrip(View v){
		
		final String start = ((EditText) findViewById(R.id.et_start_loc)).getText().toString();
		final String end = ((EditText) findViewById(R.id.et_end_loc)).getText().toString();
		
		Object[] starts = {start};
		
		/* This is ugly. Any suggestions on different implementations?
		 * 1. Grab the locations as they are entering in the data. Allow users to 
		 * move the location on the map manually
		 * if it is not desired location. 
		 */
		new StringLocationTask(this, new Callback<List<Address>>() {

			@Override
			public void failure(RetrofitError arg0) {
				// do nothing
			}

			@Override
			public void success(List<Address> arg0, Response arg1) {
				final Coordinate startLoc = new Coordinate(arg0.get(0).getLatitude(), arg0.get(0).getLongitude());
				startLoc.title = start;
				
				Object[] ends = {end};
				new StringLocationTask(getApplicationContext(), new Callback<List<Address>>() {

					@Override
					public void failure(RetrofitError arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void success(List<Address> arg0, Response arg1) {
						Coordinate endLoc = new Coordinate(arg0.get(0).getLatitude(), arg0.get(0).getLongitude());
						endLoc.title = end;
						
						Trip t = new Trip.Builder()
									.setStart(startLoc)
									.setEnd(endLoc)
									.build();
						t.post(getApplicationContext(), new FutureCallback<String>() {

							@Override
							public void onCompleted(Exception arg0, String arg1) {
								// Do something.
							}
							
						});
					}
					
				}).execute(ends);
			}
			
		}).execute(starts);		
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Connect to the location client and get the current location of the
		// user
		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
			// gMap.setMyLocationEnabled(true);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// Disconnect from client to stop getting user's location
		mLocationClient.disconnect();
		super.onStop();
	}

	// Handle results returned to the FragmentActivity by Google Play services
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST
				&& resultCode == Activity.RESULT_OK)
			// Attempt to reconnect if result is ok!
			mLocationClient.connect();
	}

	/**
	 * Verifies the user has Google Maps installed. If not, it requests them to
	 * install Google Maps
	 * 
	 * @return Boolean value signifying if Google Maps is available
	 */
	private boolean isGooglePlayServicesAvailable() {
		// Verify user has good version of google play services. Necessary for
		// maps
		int retCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		// If successful, carry on.
		if (ConnectionResult.SUCCESS == retCode)
			return true;
		// Otherwise, request them to download GP Services
		else { // If it can be resolved, fix it
			if (GooglePlayServicesUtil.isUserRecoverableError(retCode))
				GooglePlayServicesUtil.getErrorDialog(retCode, this,
						RQS_GooglePlayServices).show();
			return false;
		}
	}

	/**
	 * Connection to GPS failed. Attempt to resolve it. Otherwise, catch the
	 * exception
	 * 
	 * @param connResult
	 *            Result from the connection attempt
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connResult) {
		// If Google Play Services can resolve the errors, allow it to resolve
		// the errors!
		if (connResult.hasResolution())
			try {
				// Start activity that tries to resolve the error
				connResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				// Thrown if Google Play Services canceled the original
				// PendingIntent
				e.printStackTrace();
			}
	}

	/**
	 * Connected to GPS successfully. Update current location and display marker
	 * on the map.
	 */
	@Override
	public void onConnected(Bundle bundle) {
		// If GPS connected successfully, location client get last location
		Location loc = mLocationClient.getLastLocation();
		LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		// Set map center and zoom level
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
		gMap.animateCamera(camUpdate);
	}

	@Override
	public void onDisconnected() {
	}
}
