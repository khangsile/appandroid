package com.llc.bumpr;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GMapV2Direction;
import com.llc.bumpr.lib.GMapV2Painter;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.User;

public class RequestActivity extends SherlockFragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private User user;
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
		setContentView(R.layout.request);

		initialize();

		// Get map fragment!
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_request)).getMap();

		// Create new location client.
		mLocationClient = new LocationClient(this, this, this);
		
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(new LatLng(34.3445, -84.2312));
		points.add(new LatLng(34.3442, -84.231));
		points.add(new LatLng(33.99, -84.212));
		points.add(new LatLng(31.3, -83.1));
		GMapV2Painter painter = new GMapV2Painter(gMap, points);
		painter.setWidth(8);
		painter.paint();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
			// gMap.setMyLocationEnabled(true);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// Disconnect from client
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
		Location loc = mLocationClient.getLastLocation();
		LatLng latLng = new LatLng(34.3445, -84.2312);
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
		gMap.animateCamera(camUpdate);
	}

	@Override
	public void onDisconnected() {
	}

	protected void initialize() {
		Bundle bundle = getIntent().getExtras();
		request = (Request) bundle.getParcelable("request");
		user = (User) bundle.getParcelable("user");

		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}

		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());

		CircularImageView userPhoto = (CircularImageView) findViewById(R.id.img_user);
		userPhoto.setImageResource(R.drawable.test_image);

		TextView toAddress = (TextView) findViewById(R.id.tv_toAddress);
		TextView toCityState = (TextView) findViewById(R.id.tv_toCityState);
		TextView fromAddress = (TextView) findViewById(R.id.tv_fromAddress);
		TextView fromCityState = (TextView) findViewById(R.id.tv_fromCityState);

		toAddress.setText("619 Braddock Ct.");
		toCityState.setText("Edgewood, KY 41017");
		fromAddress.setText("557 Lone Oak Dr.");
		fromCityState.setText("Lexington, KY 40503");

	}

	public void acceptRequest(View v) {

	}

	public void declineRequest(View v) {

	}

}
