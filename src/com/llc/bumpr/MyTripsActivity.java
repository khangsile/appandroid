package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.MyTripsAdapter;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class MyTripsActivity extends BumprActivity {
	
	/** Reference to the ListView that holds all of the requests */
	private ListView trips;
	
	/** List of data to fill trip list view with  */
	private List<Trip> tripDetails;
	
	/** Reference to my request list adapter  */
	private MyTripsAdapter tripAdapter;
	
	/** Reference to ActionBar UI item  */
	private ActionBar abs;
	
	/** Reference to the active user */
	private User user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_requests);
		
		abs = getSupportActionBar();
		user = User.getActiveUser();
		
		trips = (ListView) findViewById(R.id.lv_my_requests);
		
		abs.setTitle("My Trips");	
		
		initList();
		initOnClickListener();
		
		//Create new adapter to display requests and use this adapter to display requests in the list view
		tripDetails = new ArrayList<Trip>();
		tripAdapter = new MyTripsAdapter(this, tripDetails, R.layout.trip_row);
		trips.setAdapter(tripAdapter);
		
		final int px = (int)(5 * getResources().getDisplayMetrics().density + 0.5f);
		trips.setPadding(px, px, px, px);
	}

	private void initList() {
		ApiRequest request = Trip.getTrips(this, new FutureCallback<List<Trip>>() {

			@Override
			public void onCompleted(Exception arg0, List<Trip> arg1) {
				if (arg0 != null) {
					arg0.printStackTrace();
					return;
				}
				
				tripAdapter.clear();
				tripAdapter.addAll(arg1);
				tripAdapter.notifyDataSetChanged();
			}
		});
		Session.getSession().sendRequest(request);
	}
	
	private void initOnClickListener() {
		trips.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Trip trip = (Trip) parent.getItemAtPosition(position);
				
				//Take you to trip guest list
				Intent intent = new Intent(getApplicationContext(), FriendsListActivity.class);
				intent.putExtra("trip", trip);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void initializeMe(User activeUser) {
		// TODO Auto-generated method stub
	}

}
