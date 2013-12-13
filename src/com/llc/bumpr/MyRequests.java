package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.MyRequestsAdapter;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public class MyRequests extends BumprActivity {
	/** Reference to the ListView that holds all of the requests */
	private ListView requests;
	/** List of data to fill trip list view with  */
	private List<Request> tripRequests;
	/** Reference to my request list adapter  */
	private MyRequestsAdapter requestAdapter;
	/** Reference to ActionBar UI item  */
	private ActionBar abs;
	/** Reference to the active user */
	private User user;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
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
		initList(extras.getString("requestType"));
		initOnClickListener();
		
		//Create new adapter to display requests and use this adapter to display requests in the list view
		tripRequests = new ArrayList<Request>();
		requestAdapter = new MyRequestsAdapter(this, tripRequests, R.layout.my_requests_row, extras.getString("requestType"));
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
				Request request = (Request) parent.getItemAtPosition(position);
				Toast.makeText(getApplicationContext(), "" + request.getAccepted(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Get requests from server and send them to the list adapter to be displayed
	 */
	private void initList(String type) {
		//Initialize the list and add trip information to be displayed
	
		String param = (type.equals("Inbox")) ? "incoming" : "outgoing";
		ApiRequest request = Request.getRequests(this, param, new FutureCallback<List<Request>>() {

			@Override
			public void onCompleted(Exception arg0, List<Request> arg1) {
				if (arg0 != null) return;
				
				requestAdapter.clear();
				requestAdapter.addAll(arg1);
				requestAdapter.notifyDataSetChanged();
			}
			
		});
		Session.getSession().sendRequest(request);
	}

	@Override
	protected void initializeMe(User activeUser) {
		// TODO Auto-generated method stub
		
	}
	
	

}
