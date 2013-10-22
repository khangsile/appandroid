package com.llc.bumpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.llc.bumpr.R;
import com.llc.bumpr.adapters.EndlessAdapter;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.w;

import com.actionbarsherlock.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDrivers extends SherlockActivity implements EndlessListView.EndlessListener {

	private com.actionbarsherlock.app.ActionBar actionBar;
	private static final int RQS_GooglePlayServices = 1;
	private SlidingMenu slidingMenu;
	private LinearLayout map;
	private LinearLayout driverLayout;
	
	private int page;
	private EndlessListView driverList;
	private EndlessAdapter endListAdp;
	
	private ListView lvMenu;
	private List<HashMap<String, String>> menuList;
	private SimpleAdapter menuAdpt;
	int testCntr = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
		actionBar = getSupportActionBar();
		map = (LinearLayout) findViewById(R.id.ll_map_container);
		driverLayout = (LinearLayout) findViewById(R.id.ll_driver_list);
		
		//Inflate listview view
		View slMenu = LayoutInflater.from(getApplication()).inflate(R.layout.sliding_menu, null);
		lvMenu = (ListView) slMenu.findViewById(R.id.menu_list);

		//Setup menu to be used by sliding menu
		menuList = new ArrayList<HashMap<String,String>>();
		initList();
		menuAdpt = new SimpleAdapter(this, menuList, R.layout.sliding_menu_row_text, new String[] {"Section1"}, new int[] {R.id.tv_sliding_menu_text});
		lvMenu.setAdapter(menuAdpt);
		
		// Set up sliding menu
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		slidingMenu.setMenu(slMenu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Set up endless list view for driver list
		driverList = (EndlessListView) findViewById(R.id.lv_drivers);
		driverList.setLoadingView(R.layout.loading_layout);
		driverList.setListener(this);
	}
	
    private void initList() {
    	menuList.add(putData("Section1", "Test 0"));
    	menuList.add(putData("Section1", "Test 1"));
    	menuList.add(putData("Section1", "Test 2"));
    	menuList.add(putData("Section1", "Test 3"));
    	menuList.add(putData("Section1", "Test 4"));
    }
    
    private HashMap<String, String> putData(String key, String name){
    	HashMap <String, String> data = new HashMap<String, String>();
    	data.put(key, name);
    	return data;
    }

	// If slidingMenu showing, back closes menu. Otherwise, calls parent back
	// action
	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing())
			slidingMenu.toggle();
		else if (((LinearLayout.LayoutParams)map.getLayoutParams()).weight == .5){
			//Set Driver List weight
			LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams)driverLayout.getLayoutParams();
			driverPars.weight = 0.0f;
			driverLayout.setLayoutParams(driverPars);
			LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams)map.getLayoutParams();
			mapPars.weight = 1.0f;
			map.setLayoutParams(mapPars);
		}
		else
			super.onBackPressed();
	}

	// If menu button pressed, show or hide the sliding menu
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// to show/hide the SlidingMenu when the home icon on the actionbar is
	// pressed
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.slidingMenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// Verify user has good version of google play services. Necessary for
		// maps
		int retCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		// If successful, carry on. Otherwise, request them to download GP
		// Services
		if (retCode != ConnectionResult.SUCCESS)
			GooglePlayServicesUtil.getErrorDialog(retCode, this,
					RQS_GooglePlayServices).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		final SearchView searchView = (SearchView) menu.findItem(R.id.it_search_bar).getActionView();
		searchView.setQueryHint("Enter destination here...");
		searchView.setIconifiedByDefault(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			// Search Listener
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams)map.getLayoutParams();
				mapPars.weight = 0.5f;
				map.setLayoutParams(mapPars);
				//Set Driver List weight
				LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams)driverLayout.getLayoutParams();
				driverPars.weight = 0.5f;
				driverLayout.setLayoutParams(driverPars);
				
				if(endListAdp == null){
					endListAdp = new EndlessAdapter(getApplicationContext(), createItems(), R.layout.driver_row);
					driverList.setAdapter(endListAdp);
				}
				else
					newSearch(); //Reset endless list with new data
				
				//Hide keyboard when enter pressed
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				
				return false;
			}
		});

		return true;

	}
	
	//Added by KJC to clear data and add new search results for each search
	public void newSearch(){
		//Reset counter for new search (Set to 1)
		testCntr = 1;
		//Clear the list and add the new search results
		driverList.resetData(createItems());
	}
	
	//Files needed for implementing/testing endless list view
	private class FakeNetLoader extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(4000);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
			return createItems();
		}
		
		@Override
		protected void onPostExecute(List<String> result){
			super.onPostExecute(result);
			driverList.addNewData(result);
		}
		
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		Log.w("com.llc.bumpr", "Adding new data!");
		testCntr += 10;
		//Load more data
		FakeNetLoader f1 = new FakeNetLoader();
		f1.execute(new String[]{});
		
	}
	
	private List<String> createItems() {
		// TODO Auto-generated method stub
		List<String> items = new ArrayList<String>();
		
		for (int i = testCntr; i <testCntr+10; i++)
			items.add("Driver " + i);
		return items;
	}

}
