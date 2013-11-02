package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.adapters.MyRequestsAdapter;

public class MyRequests extends SherlockActivity {
	/** Reference to the ListView that holds all of the requests */
	private ListView requests;
	/** List of data to fill trip list view with  */
	private List<Object> tripRequests;
	/** Reference to my request list adapter  */
	private MyRequestsAdapter requestAdapter;
	/** Reference to ActionBar UI item  */
	private ActionBar abs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_requests);
		
		//Get action bar sherlock reference
		abs = getSupportActionBar();
		
		requests = (ListView) findViewById(R.id.lv_my_requests);
		
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		
		abs.setTitle(extras.getString("requestType"));
		
		initList();
		
		requestAdapter = new MyRequestsAdapter(this, tripRequests, R.layout.my_requests_row);
		requests.setAdapter(requestAdapter);
	}

	private void initList() {
		// TODO Auto-generated method stub
		tripRequests = new ArrayList<Object>();
		tripRequests.add("4.83 Mi.");
		tripRequests.add("6.44 Mi.");
		tripRequests.add("3.30 Mi.");
		tripRequests.add("1.34 Mi.");
		tripRequests.add("7.98 Mi.");
	}
	
	

}
