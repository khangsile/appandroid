package com.llc.bumpr;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.llc.bumpr.adapters.GuestListAdapter;
import com.llc.bumpr.lib.GMapV2Painter;
import com.llc.bumpr.lib.LatLngLocationTask;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class FriendsListActivity extends BumprActivity {
	
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
	
	/** Listview to display the users */
	private ListView guestList;
	
	/** Reference to the GuestListAdapter */
	private GuestListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_guest_list);
		
		//Initialize the header by displaying the rider
		initialize();
	
		//Grab GoogleMap view to tell when the view is finished being created
		View mapFrag =  getSupportFragmentManager().findFragmentById(R.id.map_request).getView();
		
		// Get map fragment!
		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_request)).getMap();
		
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
	
	public void initialize() {
		//Get objects passed to this activity
		Bundle bundle = getIntent().getExtras();
		
		//Get the Request object passed and retrieve the trip associated with it 
		request = (Request) bundle.getParcelable("request");
		if(request != null)
			trip = request.getTrip();
		else //If no request object exists, grab the Trip object passed
			trip = (Trip) bundle.getParcelable("trip");
		
		if(trip == null) {
			throw new NullPointerException("Instance ('request') cannot be null");
		}
		
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
		
		guestList = (ListView) findViewById(R.id.lv_guest_list);
		adapter = new GuestListAdapter(getApplicationContext(), R.layout.trip_guest_row, trip);
		guestList.setAdapter(adapter);
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

	@Override
	protected void initializeMe(User activeUser) {
		// TODO Auto-generated method stub
		
	}

}
