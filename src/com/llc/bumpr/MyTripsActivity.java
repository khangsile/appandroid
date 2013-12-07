package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.llc.bumpr.adapters.MyRequestsAdapter;
import com.llc.bumpr.adapters.MyTripsAdapter;
import com.llc.bumpr.sdk.models.User;

public class MyTripsActivity extends BumprActivity {
	/** Reference to the ListView that holds all of the requests */
	private ListView trips;
	/** List of data to fill trip list view with  */
	private List<Object> tripDetails;
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
		
		//Get action bar sherlock reference and active user reference
		abs = getSupportActionBar();
		user = User.getActiveUser();
		
		//Get reference to list view to display requests
		trips = (ListView) findViewById(R.id.lv_my_requests);
		
		//Set the title of this activity to be the type of requests displayed
		abs.setTitle("My Trips");	
		
		//Set up request list, fill with requests, and then set up on click listener for each row item
		initList();
		initOnClickListener();
		
		//Create new adapter to display requests and use this adapter to display requests in the list view
		Log.i("com.llc.bumpr", tripDetails.size() + "");
		tripAdapter = new MyTripsAdapter(this, tripDetails, R.layout.trip_row);
		trips.setAdapter(tripAdapter);
		
		//Set padding of listview to 5dp
		final int px = (int)(5 * getResources().getDisplayMetrics().density + 0.5f);
		trips.setPadding(px, px, px, px);
	}

	private void initList() {
		// TODO Auto-generated method stub
		tripDetails = new ArrayList<Object>();
		//Pass trip objects later
		tripDetails.add("Orlando");
		tripDetails.add("Florence");
		tripDetails.add("Houston");
		tripDetails.add("Nashville");
		tripDetails.add("New York City");
	}
	
	private void initOnClickListener() {
		// TODO Auto-generated method stub
		trips.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
			}
			
			
		});
	}

	@Override
	protected void initializeMe(User activeUser) {
		// TODO Auto-generated method stub
	}

}
