package com.llc.bumpr;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.androidtools.Conversions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.llc.bumpr.adapters.EndlessAdapter;
import com.llc.bumpr.adapters.SlidingMenuListAdapter;
import com.llc.bumpr.lib.CircularImageView;
import com.llc.bumpr.lib.EndlessListView;
import com.llc.bumpr.lib.GraphicsUtil;
import com.llc.bumpr.lib.LatLngLocationTask;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.lib.Coordinate;
import com.llc.bumpr.sdk.models.SearchQuery;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;
import com.llc.bumpr.services.DriverLocationService;

public class SearchDrivers extends SherlockFragmentActivity implements
		EndlessListView.EndlessListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	/** Reference to the ActionBarSherlock action bar */
	private com.actionbarsherlock.app.ActionBar actionBar;
	/** Request value to allow Google Maps to display */
	private static final int RQS_GooglePlayServices = 1;
	/** Reference to the sliding menu UI element */
	private SlidingMenu slidingMenu;
	/** Reference to the Layout object holding the map fragment */
	private LinearLayout map;
	/** Reference to the Layout object holding the endless list view */
	private LinearLayout driverLayout;
	/** Reference to the search bar in the action bar */
	private SearchView searchView;

	/**
	 * The next page of data to load from the database into the endless list
	 * view of drivers
	 */
	private int page;
	/** Reference the endless list view holding the list of available drivers */
	private EndlessListView driverList;
	/** Reference to the adapter that will populate the endless list */
	private EndlessAdapter endListAdp;

	/** Reference to ArrayList to hold Drivers that are loaded into the endless list */
	private List<User> drivers;
	
	/** Reference to the list view that will hold the sliding menu information */
	private ListView lvMenu;
	/** List that will hold the data to fill the sliding menu */
	private List<Pair<String, Object>> menuList;
	/**
	 * Reference to the adapter that will populate the sliding menu with it's
	 * data
	 */
	private SlidingMenuListAdapter menuAdpt;

	int testCntr = 1;

	/** Reference to the map UI element */
	private GoogleMap gMap;
	/** Reference to the location client (Allows use of GPS) */
	private LocationClient mLocationClient;
	/** Reference to last marker clicked */
	private Marker lastClicked = null;

	/** Request value to get current location (Using GPS) */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";

	/** Holds a reference to the current context */
	private Context context;

	/** A reference to the current user */
	private User user;
	
	/** Endless List work around for the current moment */
	private List<User> emptyList = new ArrayList<User>();
	
	/** Boolean object to identify if the activity has already been loaded yet or not */
	private boolean alreadyLoaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_driver);
		context = getApplicationContext();
		user = User.getActiveUser();

		actionBar = getSupportActionBar();
		map = (LinearLayout) findViewById(R.id.ll_map_container);

		gMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		driverLayout = (LinearLayout) findViewById(R.id.ll_driver_list);

		// Inflate listview view
		View slMenu = LayoutInflater.from(getApplication()).inflate(
				R.layout.sliding_menu, null);
		lvMenu = (ListView) slMenu.findViewById(R.id.menu_list);

		// Setup menu to be used by sliding menu
		menuList = new ArrayList<Pair<String, Object>>();
		initList();

		menuAdpt = new SlidingMenuListAdapter(this, menuList, user);
		lvMenu.setAdapter(menuAdpt);
		setMenuOnClickListener();

		// Set up sliding menu
		initSlidingMenu(slMenu);
		
		//Set up endless list view for driver list
		drivers = new ArrayList<User>();
		driverList = (EndlessListView) findViewById(R.id.lv_drivers);
		driverList.setLoadingView(R.layout.loading_layout);
		driverList.setListener(this);

		setEndlessListOnClickListener();

		// Create new location client.
		mLocationClient = new LocationClient(this, this, this);

		setMapsInfoWindowAdapter();
		setMapsOnClickListener();
	}
	
	private void setMapsInfoWindowAdapter() {
		// TODO Auto-generated method stub
		gMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			@Override
			public View getInfoContents(Marker marker) {
				// TODO Auto-generated method stub
				//Get view for layout file
				View v = getLayoutInflater().inflate(R.layout.driver_row, null);

				//Get references to all fields
				CircularImageView profImg = (CircularImageView)v.findViewById(R.id.iv_driver_prof_pic);
				ImageView profImgFixed = (ImageView) v.findViewById(R.id.iv_driver_prof_pic_normal);
				TextView drvName = (TextView)v.findViewById(R.id.tv_driver_name);
				TextView drvRate = (TextView)v.findViewById(R.id.tv_driver_rate);
				RatingBar drvRtg = (RatingBar)v.findViewById(R.id.rb_user_rating);
				TextView drvCnt = (TextView) v.findViewById(R.id.tv_driver_cnt);
				View blackSep = (View) v.findViewById(R.id.num_divider);
				
				//Get the driver position in the driver list adapter
				int driverNum = Integer.parseInt(marker.getTitle().substring(7, marker.getTitle().length())) - 1;
				// Get data at position selected and go to their request page
				final User user = (User) endListAdp.getItem(driverNum);
				
				drvName.setText(user.getFirstName() + " " + user.getLastName());
				drvRate.setText(user.getDriverProfile().getFee() + "");
                //float rating = user.getDriverProfile().getRating();
				drvRtg.setRating(3.2f);
                drvCnt.setText(Integer.toString(driverNum+1));
                
                //Change alignment of rating bar so the window doesn't take the entire screen
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)drvRtg.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params.addRule(RelativeLayout.RIGHT_OF, drvName.getId());
                params.setMargins(15,0,0,0);
                //Apply parameter changes
                drvRtg.setLayoutParams(params);
                
                blackSep.getLayoutParams().height = profImg.getLayoutParams().height;
                
              //profImg.setImageResource(R.drawable.test_image);
				profImg.setVisibility(View.INVISIBLE);
				profImgFixed.setVisibility(View.VISIBLE);
				
				//Set up image values
				float imageSize = Conversions.dpToPixels(context, 50);
                //Change this to the other image type
    			GraphicsUtil imageHelper = new GraphicsUtil();
                Bitmap bm = imageHelper.getCircleBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.test_image), 16);
    			//Resize image to the desired size
                profImgFixed.setImageBitmap(Bitmap.createScaledBitmap(bm, Math.round(imageSize), Math.round(imageSize), false));
				return v;
			}

			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}

	/**
	 * Sets up three on click listeners for the google maps. It recognizes clicks to the map,
	 * clicks to the info window, and clicks to the marker
	 */
	private void setMapsOnClickListener() {
		// TODO Auto-generated method stub
		gMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				lastClicked = null;
			}
			
		});
		
		gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				//Get the driver position in the driver list adapter
				int driverNum = Integer.parseInt(marker.getTitle().substring(7, marker.getTitle().length())) - 1;
				// Get data at position selected and go to their request page
				final User user = (User) endListAdp.getItem(driverNum);
				Log.i("Marker Click", user.toString());
				// Get Latitude and Logitude values of current location
				LatLng loc = gMap.getCameraPosition().target;
				// Initialize address list to hold addresses of current location
				List<Address> address = null;

				new LatLngLocationTask(context, new Callback<List<Address>>() {

					@Override
					public void failure(
							RetrofitError arg0) {
					}

					@Override
					public void success(List<Address> arg0, Response arg1) {
						// needs different request activity
						Intent intent = new Intent(context, UserProfile.class);
						intent.putExtra("user", user);
						startActivity(intent);
						Toast.makeText(getApplicationContext(),
								arg0.get(0).getAddressLine(0),
								Toast.LENGTH_SHORT).show();
					}

				}).execute(loc);
			}
			
		});
		
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				if(lastClicked != null){
					//Close old info window
					lastClicked.hideInfoWindow();
					
					if(lastClicked.equals(marker)){
						//If same marker was clicked, Set last clicked to null and mark event handled
						lastClicked = null;						
						return true;
					}
					else{
						//Show marker information box and mark this event handled
						marker.showInfoWindow();
						return true;
					}
				}
				//Show the marker information and set the marker to last clicked
				marker.showInfoWindow();
				lastClicked = marker;
				return true; //Event was handled
			}
			
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		user = User.getActiveUser();

		// Reset menu
		menuList.remove(0);
		menuList.add(0, new Pair<String, Object>("Image", user.getFirstName()
				+ " " + user.getLastName()));// Pass User Object in future

		menuAdpt = new SlidingMenuListAdapter(this, menuList, user);
		lvMenu.setAdapter(menuAdpt);
		
		//Clear points off map since they lost their reference.  New search required
		gMap.clear();
	}

	/**
	 * Initializes and configures the sliding menu
	 * 
	 * @param slMenu
	 *            View reference that holds the sliding menu
	 */
	private void initSlidingMenu(View slMenu) {
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
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
			// gMap.setMyLocationEnabled(true);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// Disconnect from client
		mLocationClient.disconnect();
		super.onStop();
	}

	// Handle results returned to the FragmentActivity by Google Play services
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST
				&& resultCode == Activity.RESULT_OK)
			// Attempt to reconnect if result is ok!
			mLocationClient.connect();
	}

	/**
	 * Verifies the user has Google Maps installed. If not, it requests them to
	 * install Google Maps
	 * 
	 * @return Boolean value signifying if Google Maps is available
	 */
	private boolean isGooglePlayServicesAvailable() {
		// TODO Auto-generated method stub
		// Verify user has good version of google play services. Necessary for
		// maps
		int retCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());
		// If successful, carry on.
		if (ConnectionResult.SUCCESS == retCode)
			return true;
		// Otherwise, request them to download GP Services
		else { // If it can be resolved, fix it
			if (GooglePlayServicesUtil.isUserRecoverableError(retCode))
				GooglePlayServicesUtil.getErrorDialog(retCode, this,
						RQS_GooglePlayServices).show();
			return false;
		}
	}

	/**
	 * Connection to GPS failed. Attempt to resolve it. Otherwise, catch the
	 * exception
	 * 
	 * @param connResult
	 *            Result from the connection attempt
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connResult) {
		// TODO Auto-generated method stub
		// If Google Play Services can resolve the errors, allow it to resolve
		// the errors!
		if (connResult.hasResolution())
			try {
				// Start activity that tries to resolve the error
				connResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				// Thrown if Google Play Services canceled the original
				// PendingIntent
				e.printStackTrace();
			}
	}

	/**
	 * Connected to GPS successfully. Update current location and display marker
	 * on the map.
	 */
	@Override
	public void onConnected(Bundle bundle) {
		// TODO Auto-generated method stub
		Location loc = mLocationClient.getLastLocation();
		LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
		CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
		gMap.animateCamera(camUpdate);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	/**
	 * Fills menu list object with the information to display in the sliding
	 * menu
	 */
	private void initList() {
		menuList.add(new Pair<String, Object>("Image", user.getFirstName()
				+ " " + user.getLastName()));
		menuList.add(new Pair<String, Object>("Text", "Create Review"));
		//menuList.add(new Pair<String, Object>("Text", "My Received Requests"));
		menuList.add(new Pair<String, Object>("Text", "My Sent Requests"));
		menuList.add(new Pair<String, Object>("Text", "Request"));
		menuList.add(new Pair<String, Object>("Switch", "Driver Mode"));
		menuList.add(new Pair<String, Object>("Text", "Logout"));
	}

	// If slidingMenu showing, back closes menu. Otherwise, calls parent back
	// action
	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing())
			slidingMenu.toggle();
		else if (((LinearLayout.LayoutParams) map.getLayoutParams()).weight == .5) {
			// Set Driver List weight
			LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams) driverLayout
					.getLayoutParams();
			driverPars.weight = 0.0f;
			driverLayout.setLayoutParams(driverPars);
			LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams) map
					.getLayoutParams();
			mapPars.weight = 1.0f;
			map.setLayoutParams(mapPars);
		} else
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.it_search_bar)
				.getActionView();
		searchView.setQueryHint("Enter destination here...");
		searchView.setIconifiedByDefault(false);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

			// Search Listener
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				//Hide keyboard when enter pressed
				Log.i("Search Driver", "New Search Started");
				searchView.clearFocus();
				
				//Start loading dialog to show action is taking place
				final ProgressDialog dialog = ProgressDialog.show(SearchDrivers.this, "Please Wait", "Finding drivers near you...", false, true);
				//Search for drivers
				LatLngBounds bounds = getMapBounds();
				searchDrivers(bounds.northeast, bounds.southwest, new Callback<List<User>>() {

					@Override
					public void failure(RetrofitError arg0) {
						// TODO Auto-generated method stub
						Log.i("Search Driver", (arg0 == null) ? "true" : "false");
						Log.i("Search Driver", "Connection Failed");
						//Close dialog and make toast
						dialog.dismiss();
						Toast.makeText(getApplicationContext(),
								"An error occurred searching for drivers...",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void success(List<User> users, Response arg1) {
						// TODO Auto-generated method stub
						drivers = users;
						LinearLayout.LayoutParams mapPars = (LinearLayout.LayoutParams)map.getLayoutParams();
						mapPars.weight = 0.5f;
						map.setLayoutParams(mapPars);
						//Set Driver List weight
						LinearLayout.LayoutParams driverPars = (LinearLayout.LayoutParams)driverLayout.getLayoutParams();
						driverPars.weight = 0.5f;
						driverLayout.setLayoutParams(driverPars);
						
						if(endListAdp == null){
							Log.i("Search Driver", "List Adapter Set");
							endListAdp = new EndlessAdapter(getApplicationContext(), createItems(drivers), R.layout.driver_row);
							driverList.setAdapter(endListAdp);
						}
						else{
							Log.i("Search Driver", "Reset Search");
							gMap.clear();
							newSearch(drivers); //Reset endless list with new data
						}
						
						for (int i = 0; i < drivers.size(); i++){
							//Display Marker
							Marker marker = gMap.addMarker(new MarkerOptions()
															.position(new LatLng(drivers.get(i).getDriverProfile().getPosition().lat, drivers.get(i).getDriverProfile().getPosition().lon))
															.title("Driver " + (i+1))
															.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
						}
						
						//Close Dialog
						dialog.dismiss();
					}
					
				});				

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

	// Added by KJC to clear data and add new search results for each search
	/**
	 * Called when a new location is entered in the destination search box.
	 * Reset information in the endless list view and begin displaying new
	 * results!
	 */
	public void newSearch(List<User> drivers){
		//Reset counter for new search (Set to 1)
		testCntr = 1;
		//Clear the list and add the new search results
		driverList.resetData(createItems(drivers));
	}

	// Files needed for implementing/testing endless list view
	/**
	 * Class to asynchronously load driver data from database and display it in
	 * the endless list view
	 */
	private class FakeNetLoader extends AsyncTask<String, Void, List<User>> {

		@Override
		protected List<User> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				//Load drivers from database
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<User> drivers = new ArrayList<User>();
			return createItems(drivers);
		}

		@Override
		protected void onPostExecute(List<User> result){
			super.onPostExecute(result);
			driverList.addNewData(result);
		}

	}

	/**
	 * Loads new data from the database and adds it to the endless list view
	 */
	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		
		driverList.addNewData(emptyList);
		//***Commenting out currently until endless list is used ***/
		/*Log.w("com.llc.bumpr", "Adding new data!");
		testCntr += 10;
		// Load more data
		FakeNetLoader f1 = new FakeNetLoader(); */
	}

	/**
	 * Adds new data to the list of current data in the endless list.
	 * 
	 * @return Updated list of data to display in the endless list
	 */
	private List<User> createItems(List<User> drivers2) {
		// TODO Auto-generated method stub
		//List<User> items = new ArrayList<User>();
		/*JSONObject object = new JSONObject();
		try {
			object.put("id", 3);
			object.put("fee", 2.55);
			object.put("lat", 23.3);
			object.put("lon", 23.4);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		for (int i = testCntr; i < testCntr + 10; i++) {
			try {
				User user = new User.Builder<User>(new User())
						.setFirstName("Khang").setLastName("Le")
						.setEmail("khangsile@gmail.com")
						.setDriverProfile(new Driver(object)).build();
				items.add(user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
		return drivers2;
	}

	/**
	 * Gets the bounds of the map view
	 * 
	 * @return LatLngBoudnds object which contains the coordinates for the
	 *         northeast corner and the southwest corner of the map view
	 */
	private LatLngBounds getMapBounds() {
		return gMap.getProjection().getVisibleRegion().latLngBounds;
	}

	/**
	 * Method to search for drivers
	 * 
	 * @param northeast
	 *            The northeast corner of the boundary
	 * @param southwest
	 *            The southwest corner of the boundary
	 */
	private void searchDrivers(LatLng northeast, LatLng southwest, Callback<List<User>> cb) {
		Log.i("Search Driver", "Inside Search Drivers");
		SearchQuery query = new SearchQuery.Builder<SearchQuery>(new SearchQuery())
								.setBottom(southwest.latitude)
								.setLeft(southwest.longitude)
								.setTop(northeast.latitude)
								.setRight(northeast.longitude)
								.build();
		Session.getSession().sendRequest(User.searchDrivers(query, cb));
		Log.i("Search Driver", "Completed Search Drivers");
	}

	/**
	 * Creates the listener for the onClick of the sliding menu
	 */
	private void setMenuOnClickListener() {
		lvMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i = null;
				switch (position) {
				case 0:
					i = new Intent(getApplicationContext(),
							EditProfileActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 1:
					i = new Intent(getApplicationContext(),
							CreateReviewActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				/*case 2:
					i = new Intent(getApplicationContext(), MyRequests.class);
					i.putExtra("user", User.getActiveUser()); // Pass Incoming Requests
					i.putExtra("requestType", "My Received Requests");
					break;*/
				case 2:
					i = new Intent(getApplicationContext(), MyRequests.class);
					i.putExtra("user", User.getActiveUser()); // Pass outgoing requests
					i.putExtra("requestType", "My Sent Requests");
					break;
				case 4:
					i = new Intent(getApplicationContext(),
							RequestActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 3: 
					ApiRequest api = User.getActiveUser().getDriverProfile().updateLocation(new Coordinate(-84.4, 38.05), new Callback<Response>() {

						@Override
						public void failure(RetrofitError arg0) {
							// TODO Auto-generated method stub
							Log.i("this", "there");
							Log.i("this", arg0.getMessage());
						}

						@Override
						public void success(Response arg0,
								Response arg1) {
							// TODO Auto-generated method stub
							Log.i("this", "here");
						}

					});

						Session.getSession().sendRequest(api);
					break;
				case 5:
					i = new Intent(getApplicationContext(), LoginActivity.class);
					// Remove saved email and password from shared preferences
					SharedPreferences savedLogin = getSharedPreferences(
							LOGIN_PREF, 0);
					Editor loginEditor = savedLogin.edit();
					loginEditor.remove("email");
					loginEditor.remove("password");
					loginEditor.commit();

					// clear history
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK);
					Session session = Session.getSession();
					session.logout(new Callback<Response>() {

						@Override
						public void failure(RetrofitError arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void success(Response arg0, Response arg1) {
							// TODO Auto-generated method stub
						}
					});
				default:
					break;
				}

				if (i != null) {
					startActivity(i);
				}
			}
		});
	}

	private void setEndlessListOnClickListener() {
		driverList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Get data at position selected
				final User user = (User) parent.getItemAtPosition(position);
				// Get Latitude and Logitude values of current location
				LatLng loc = gMap.getCameraPosition().target;
				// Initialize address list to hold addresses of current location
				List<Address> address = null;

				new LatLngLocationTask(context, new Callback<List<Address>>() {

					@Override
					public void failure(
							RetrofitError arg0) {
					}

					@Override
					public void success(List<Address> arg0, Response arg1) {
						// needs different request activity
						Intent intent = new Intent(context, UserProfile.class);
						intent.putExtra("user", user);
						startActivity(intent);
						Toast.makeText(getApplicationContext(),
								arg0.get(0).getAddressLine(0),
								Toast.LENGTH_SHORT).show();
					}

				}).execute(loc);
			}
		});
	}
}
