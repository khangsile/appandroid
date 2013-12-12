package com.llc.bumpr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.GMapV2Painter;
import com.llc.bumpr.lib.LatLngLocationTask;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class TripSummaryActivity extends BumprActivity /*implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener*/ {
	
	/** Reference to the user asking for a ride */
	private User user;
	/** Reference to the Trip object sent by the user */
	private Trip trip;
	/** Reference to a Request object sent by the user */
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

		//Grab GoogleMap view to tell when the view is finished being created
		View mapFrag =  getSupportFragmentManager().findFragmentById(R.id.map_request).getView();
		
		// Get map fragment!
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_request)).getMap();

		// Create new location client.
		//mLocationClient = new LocationClient(this, this, this);
		
		// Set up list of points for trip to display route on the map
		ArrayList<LatLng> points = new ArrayList<LatLng>();
		points.add(new LatLng(trip.getStart().lat, trip.getStart().lon));
		points.add(new LatLng(trip.getEnd().lat, trip.getEnd().lon));
		
		//Paint route on the map
		GMapV2Painter painter = new GMapV2Painter(gMap, points);
		painter.setWidth(8);
		painter.paint();
		
		mapFrag.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						// TODO Auto-generated method stub
						setMapZoom();
					}
					
				});
	}

	/**
	 * Zoom the map to display the route
	 */
	protected void setMapZoom() {
		LatLng start = new LatLng(trip.getStart().lat, trip.getStart().lon);
		LatLng end = new LatLng(trip.getEnd().lat, trip.getEnd().lon);
		
		LatLngBounds bounds = new LatLngBounds.Builder().include(start).include(end).build();
		gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
	}
	
	/**
	 * Set up the display header and other trip information
	 */
	protected void initialize() {
		//Get objects passed to this activity
		Bundle bundle = getIntent().getExtras();
		
		//Get the Request object passed and retrieve the trip associated with it 
		request = (Request) bundle.getParcelable("request");
		if(request != null)
			trip = request.getTrip();
		else //If no request object exists, grab the Trip object passed
			trip = (Trip) bundle.getParcelable("trip");
		
		user = (User) bundle.getParcelable("user");

		//If user is null or request is null, throw exception
		if (user == null) {
			throw new NullPointerException("Instance ('user') cannot be null");
		}
		
		if(trip == null) {
			throw new NullPointerException("Instance ('request') cannot be null");
		}

		//Get references to the view objects in the layout and fill these in with user details
		TextView userName = (TextView) findViewById(R.id.tv_user_name);
		userName.setText(user.getFirstName() + " " + user.getLastName());

		CircularImageView userPhoto = (CircularImageView) findViewById(R.id.img_user);
		userPhoto.setImageResource(R.drawable.test_image);
		
		//Assign trip price and passenger count
		double cost = trip.getCost(); //Get cost and display with two decimal points
		((TextView) findViewById(R.id.tv_tripPrice)).setText("$ " + new DecimalFormat("##.##").format(cost));
		((TextView) findViewById(R.id.tv_tripPassengers)).setText(trip.getMinSeats() + " seats reserved");

		//Fill in request views with request information
		new LatLngLocationTask(this, new Callback<List<Address>>() {

			@Override
			public void failure(RetrofitError arg0) { // do nothing
			}

			@Override
			public void success(List<Address> arg0, Response arg1) {
				if (arg0.isEmpty()) return;

				TextView toAddress = (TextView) findViewById(R.id.tv_toAddress);
				TextView toCityState = (TextView) findViewById(R.id.tv_toCityState);

				toAddress.setText(arg0.get(0).getAddressLine(0));
				toCityState.setText(arg0.get(0).getLocality());
			}
			
		}).execute(new LatLng(trip.getEnd().lat, trip.getEnd().lon));
		
		new LatLngLocationTask(this, new Callback<List<Address>>() {

			@Override
			public void failure(RetrofitError arg0) { // do nothing
			}

			@Override
			public void success(List<Address> arg0, Response arg1) {
				if (arg0.isEmpty()) return;				

				TextView fromAddress = (TextView) findViewById(R.id.tv_fromAddress);
				TextView fromCityState = (TextView) findViewById(R.id.tv_fromCityState);
				
				fromAddress.setText(arg0.get(0).getAddressLine(0));
				fromCityState.setText(arg0.get(0).getLocality());
			}
			
		}).execute(new LatLng(trip.getStart().lat, trip.getStart().lon));
		
		String type = (String) bundle.get("type");
		
		if(!type.equals("response")){ //Only use one of the buttons!
			Button hideButton = (Button) findViewById(R.id.btn_decline);
			Button completeButton = (Button) findViewById(R.id.btn_accept);
			
			//Remove hide button from view and Set text of complete button
			hideButton.setVisibility(View.GONE);
			completeButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,1f));
			
			if(type.equals("request")){
				completeButton.setText("Request a Seat");
				//Set on click listener
				completeButton.setOnClickListener(new View.OnClickListener() {
		
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sendRequest(v);
					}
				});
			} else{
				/*completeButton.setText("Trip Complete");
				//Set on click listener
				completeButton.setOnClickListener(new View.OnClickListener() {
		
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						tripComplete(v);
					}
				});*/
				completeButton.setVisibility(View.GONE);
			}
		}
	}
	
	/**
	 * Send the request for the trip to the user
	 * @param v
	 */
	protected void sendRequest(View v) {
		ApiRequest request = Request.postRequest(this, trip, new FutureCallback<String>() {

			@Override
			public void onCompleted(Exception arg0, String arg1) {
				if (arg0 == null) {
					finish();
				} else {
					arg0.printStackTrace();
					// Something bad happened. We'll say something later.
					finish(); // Temp.
				}
			}
			
		});
		Session.getSession().sendRequest(request);
	}

	/**
	 * Marks the request as complete!
	 */
	public void tripComplete(View v) {
		finish(); //Close trip summary
	}
	
	public void acceptRequest(View v) {
		answerRequest(true);
	}

	public void declineRequest(View v) {
		answerRequest(false);
	}
	
	/**
	 * Sends a response to accept or decline the request
	 * @param accept the answer to the request
	 */
	public void answerRequest(final boolean accept) {
		ApiRequest apiRequest = request.respondTo(this, accept, new FutureCallback<String>() {

			@Override
			public void onCompleted(Exception arg0, String arg1) {
				Log.i("com.llc.bumr", accept + "");
				finish();
			}
			
		});
		Session.getSession().sendRequest(apiRequest); 
	}

	@Override
	protected void initializeMe(User activeUser) {
	}

}
