package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.MyRequestsAdapter;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.lib.Location;
import com.llc.bumpr.sdk.models.Driver;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class MyRequests extends SherlockActivity {
	/** Reference to the ListView that holds all of the requests */
	private ListView requests;
	/** List of data to fill trip list view with  */
	private List<Object> tripRequests;
	/** Reference to my request list adapter  */
	private MyRequestsAdapter requestAdapter;
	/** Reference to ActionBar UI item  */
	private ActionBar abs;
	/** Reference to the active user */
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_requests);
		
		//Get action bar sherlock reference and active user reference
		abs = getSupportActionBar();
		user = User.getActiveUser();
		
		//Get reference to list view to display requests
		requests = (ListView) findViewById(R.id.lv_my_requests);
		
		//Get extras passed into this activity
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		//Set the title of this activity to be the type of requests displayed
		abs.setTitle(extras.getString("requestType"));
		
		//Set up request list, fill with requests, and then set up on click listener for each row item
		initList();
		initOnClickListener();
		
		//Create new adapter to display requests and use this adapter to display requests in the list view
		requestAdapter = new MyRequestsAdapter(this, tripRequests, R.layout.my_requests_row);
		requests.setAdapter(requestAdapter);
	}

	/**
	 * Set up on click listener for the list adapter
	 */
	private void initOnClickListener() {
		// TODO Auto-generated method stub
		requests.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				Toast.makeText(getApplicationContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
				
/*				//Create review activity, pass it the driver who is being reviewed and start the activity
				final Driver d = new Driver.Builder(new Driver()).setId(1).build();
				
				final Trip t = new Trip.Builder()
				.setStart(new Location(-84.33, 32.13))
				.setEnd(new Location(-84.11, 32.01))
				.build();
				
				final Request r = new Request.Builder()
				.setDriverId(d.getId())
				.setUserId(User.getActiveUser().getId())
				.setTrip(t)
				.build();
							
				ApiRequest api = User.getUser(getApplicationContext(), 1, new FutureCallback<User>() { //Hardcode in user id of 1 for now.  Get user id later

					@Override
					public void onCompleted(Exception arg0, User user) {
						if(arg0 == null) {
							Intent intent = new Intent(getApplicationContext(), CreateReviewActivity.class);
							
							intent.putExtra("user", arg0);
							intent.putExtra("driver", d);
							intent.putExtra("request", r);
							startActivity(intent);
						} else {
							arg0.printStackTrace();
						}
						
					}
					
				});
				Session.getSession().sendRequest(api); */
			}
		});
	}

	/**
	 * Get requests from server and send them to the list adapter to be displayed
	 */
	private void initList() {
		//Initialize the list and add trip information to be displayed
		tripRequests = new ArrayList<Object>();
		tripRequests.add("Orlando, FL");
		tripRequests.add("Florence, KY");
		tripRequests.add("Houston, TX");
		tripRequests.add("Nashville, TN");
		tripRequests.add("New York City, NY");
	}
	
	

}
