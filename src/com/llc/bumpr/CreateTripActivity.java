package com.llc.bumpr;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
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

public class CreateTripActivity extends SherlockFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	/** Reference to the map UI element */
	private GoogleMap gMap;

	/** Reference to the location client (Allows use of GPS) */
	private LocationClient mLocationClient;

	/** Request value to get current location (Using GPS) */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int RQS_GooglePlayServices = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_trip); // Set layout to show the create
												// trip page

		// Get map fragment!
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_request)).getMap();

		// Create new location client.
		mLocationClient = new LocationClient(this, this, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		//Inflate actinon bar sherlock
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
		Toast.makeText(getApplicationContext(),
				"Create Trip Called",
				Toast.LENGTH_SHORT).show();		
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
