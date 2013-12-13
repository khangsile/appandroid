package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.SearchLocationAdapter;
import com.llc.bumpr.lib.PlacesAutoComplete;
import com.llc.bumpr.lib.StringLocationTask;
import com.llc.bumpr.sdk.models.User;

public class SearchLocationActivity extends BumprActivity {

	private ListView listView;
	private EditText search;
	
	private boolean running;
	private ArrayList<String> data;
	
	private SearchLocationAdapter adapter; 
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_location);

		getSupportActionBar().setTitle("");
		search = (EditText) findViewById(R.id.autotv_search);		
		listView = (ListView) findViewById(R.id.list_destinations);		
        adapter = new SearchLocationAdapter(this, R.layout.search_location_row, new ArrayList<String>());
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final String location = adapter.getItem(position);
				Object[] params = { location };
				
				new StringLocationTask(getApplicationContext(), new FutureCallback<List<Address>>() {

					@Override
					public void onCompleted(Exception arg0, List<Address> arg1) {
						if (arg0 != null) arg0.printStackTrace();
						if (arg1 == null) return;
						if (arg1.isEmpty()) {
							return;
						}
						
						Intent i = new Intent();
						i.putExtra("address", arg1.get(0));
						i.putExtra("query", compressTitle(location));
						setResult(200, i);
						finish();
					}
					
				}).execute(params);
			}
        	
        });
        
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(final Editable arg0) {
				running = true;
				new Thread() {
				
					public void run() {
						String text = arg0.toString();
						data = PlacesAutoComplete.autocomplete(text);
						running = false;
					}
					
				}.start();
				
				while(running) {
					// do something
				}
				
				adapter.clear();
				if (data != null) adapter.addAll(data);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			
		});
	}
	
	@Override
	protected void initializeMe(User activeUser) {
	}

	/************************* BUTTON **********************/
	
	public void clear(View v) {
		search.setText("");
	}
	
	/*************************** HELPER *********************/
	
	private String compressTitle(String title) {
		String[] titles = title.split(",");
		if (titles.length > 3) {
			return titles[1] + titles[2];
		} else if (titles.length > 2){
			return titles[0] + titles[1];
		} else {
			return titles[0];
		}
	}
	
}
