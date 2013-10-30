package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.llc.bumpr.adapters.MyRequestsAdapter;

public class MyRequests extends SherlockActivity {
	/** Reference to the ListView that holds all of the requests */
	private ListView requests;
	private List<Object> tripRequests;
	private MyRequestsAdapter requestAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_requests);
		
		requests = (ListView) findViewById(R.id.lv_my_requests);
		
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
