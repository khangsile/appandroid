package com.llc.bumpr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GMapV2Painter;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.User;

public class TripSummaryActivity extends SherlockFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	
	/** Reference to the user asking for a ride */
	private User user;
	/** Reference to the Request object sent by the user */
	private Request request;

	/** Reference to the Layout object holding the map fragment */
	private LinearLayout map;

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
		setContentView(R.layout.request); //Reuse the request page layout

		//Initialize the header by displaying the rider
		initialize();

		// Get map fragment!
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_request)).getMap();

		// Create new location client.
		mLocationClient = new LocationClient(this, this, this);
		
		// Set up list of points for trip to display route on the map
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(new LatLng(34.3445, -84.2312));
		points.add(new LatLng(34.3442, -84.231));
		points.add(new LatLng(33.99, -84.212));
		points.add(new LatLng(31.3, -83.1));
		//Paint route on the map
		GMapV2Painter painter = new GMapV2Painter(gMap, points);
		painter.setWidth(8);
		painter.paint();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//Connect to the location client and get the current location of the user
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
		//Get last location from user
		Location loc = mLocationClient.getLastLocation();
		//Create latlong from this location
		LatLng latLng = new LatLng(34.3445, -84.2312);
		//Set zoom level for the map
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		//Move map to this location
		gMap.animateCamera(camUpdate);
	}

	@Override
	public void onDisconnected() {
	}
	
	/**
	 * Set up the display header and other trip information
	 */
	protected void initialize() {
		//Get objects passed to this activity
		Bundle bundle = getIntent().getExtras();
		request = (Request) bundle.getParcelable("request");
		user = (User) bundle.getParcelable("user");

		//If user is null or request is null, throw exception
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		if(request == null) {
			throw new NullPointerException("Instance ('request') cannot be null");
		}

		//Get references to the view objects in the layout and fill these in with user details
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());

		CircularImageView userPhoto = (CircularImageView) findViewById(R.id.img_user);
		userPhoto.setImageResource(R.drawable.test_image);

		//Get references to view objects in the layout that need request details
		TextView toAddress = (TextView) findViewById(R.id.tv_toAddress);
		TextView toCityState = (TextView) findViewById(R.id.tv_toCityState);
		TextView fromAddress = (TextView) findViewById(R.id.tv_fromAddress);
		TextView fromCityState = (TextView) findViewById(R.id.tv_fromCityState);

		//Fill in request views with request information
		toAddress.setText("619 Braddock Ct.");
		toCityState.setText("Edgewood, KY 41017");
		fromAddress.setText("557 Lone Oak Dr.");
		fromCityState.setText("Lexington, KY 40503");
		
		Button hideButton = (Button) findViewById(R.id.btn_decline);
		Button completeButton = (Button) findViewById(R.id.btn_accept);
		
		//Remove hide button from view and Set text of complete button
		hideButton.setVisibility(View.GONE);
		completeButton.setText("Trip Complete");
		//Set on click listener
		completeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tripComplete(v);
			}
			
		});
	}
	
	/**
	 * Marks the request as complete!
	 */
	public void tripComplete(View v) {
		//ApiRequest apiRequest = request.
	}

}
