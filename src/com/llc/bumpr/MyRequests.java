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
import com.llc.bumpr.adapters.MyRequestsAdapter;
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
				//Get driver of the row selected
				//final Request driver = (User) parent.getItemAtPosition(position); //Possible have request returned instead and extract user from request
				
				/*if(!request.getAccepted()) //If request rejected, no review can be made.
					Toast.makeText(getApplicationContext(),
							"Driver rejected this request.  You cannot review rejected requests.",
							Toast.LENGTH_SHORT).show();*/
				
				/*if(request.isComplete()) {
					//Take the user to the reivew page for the driver
				}
				else{
					//Take the user to the trip summary page
				}*/
				
				//Create review activity, pass it the driver who is being reviewed and start the activity
				Intent intent = new Intent(getApplicationContext(), CreateReviewActivity.class);
				intent.putExtra("user", user);
				startActivity(intent);
			}
			
		});
	}

	/**
	 * Get requests from server and send them to the list adapter to be displayed
	 */
	private void initList() {
		// TODO Auto-generated method stub
		//Initialize the list and add trip information to be displayed
		tripRequests = new ArrayList<Object>();
		tripRequests.add("4.83 Mi.");
		tripRequests.add("6.44 Mi.");
		tripRequests.add("3.30 Mi.");
		tripRequests.add("1.34 Mi.");
		tripRequests.add("7.98 Mi.");
	}
	
	

}
