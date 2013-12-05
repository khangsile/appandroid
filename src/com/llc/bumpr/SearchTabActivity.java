package com.llc.bumpr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.adapters.SlidingMenuListAdapter;
import com.llc.bumpr.fragments.SearchListFragment;
import com.llc.bumpr.fragments.SearchMapFragment;
import com.llc.bumpr.lib.StringLocationTask;
import com.llc.bumpr.popups.CalendarPopUp;
import com.llc.bumpr.popups.MinPeoplePopUp;
import com.llc.bumpr.popups.MinPeoplePopUp.OnSubmitListener;
import com.llc.bumpr.sdk.lib.Location;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.Trip.SearchRequest;
import com.llc.bumpr.sdk.models.User;


public class SearchTabActivity extends BumprActivity {	
	
	private ViewPager pager;
	
	private PagerAdapter pagerAdapter;
	
	private ArrayList<SherlockFragment> fragments = new ArrayList<SherlockFragment>();
	
	/** Reference to the sliding menu UI element */
	private SlidingMenu slidingMenu;
	
	/** Reference to the list view that will hold the sliding menu information */
	private ListView lvMenu;
	
	/** List that will hold the data to fill the sliding menu */
	private List<Pair<String, Object>> menuList;
	
	/** Reference to the adapter that will populate the sliding menu with it's data */
	private SlidingMenuListAdapter menuAdpt;
	
	/** Constant phrase to hold login details */
	public static final String LOGIN_PREF = "bumprLogin";
	
	/** SearchRequest Builder to create searches */
	SearchRequest request = new SearchRequest();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tab);
        
        fragments.add(new SearchListFragment());
        fragments.add(new SearchMapFragment());
        
        pager = (ViewPager) findViewById(R.id.pager);
        FragmentManager manager = getSupportFragmentManager();
        pagerAdapter = new ScreenSlidePagerAdapter(manager);
        pager.setAdapter(pagerAdapter);
        
        /*********************** SLIDING MENU *************************/
 		View slMenu = LayoutInflater.from(getApplication()).inflate(
 				R.layout.sliding_menu, null);

 		lvMenu = (ListView) slMenu.findViewById(R.id.menu_list);

 		menuList = new ArrayList<Pair<String, Object>>();
 		initList();

 		menuAdpt = new SlidingMenuListAdapter(this, menuList, User.getActiveUser());
 		lvMenu.setAdapter(menuAdpt);
 		setMenuOnClickListener(); 

 		initSlidingMenu(slMenu);
    }
	
	/**
	 * Inherited method
	 * Load items pertaining to the active user here
	 */
	@Override
	protected void initializeMe(User activeUser) {
		// TODO Auto-generated method stub
	}

	/**************************** SLIDING MENU ***************************/
	
	/**
	 * Fills menu list object with the information to display in the sliding
	 * menu
	 */
	private void initList() {
		//Add rows to the menu array list.  Use a pair to describe if the row should be displayed
		//as a image, text or switch row (First field) and the text for the row (Second field)
		menuList.add(new Pair<String, Object>("Image", User.getActiveUser().getFirstName()
				+ " " + User.getActiveUser().getLastName()));
		menuList.add(new Pair<String, Object>("Text", "Inbox"));
		menuList.add(new Pair<String, Object>("Text", "Outbox"));
		menuList.add(new Pair<String, Object>("Text", "Logout"));
	}
	
	/**
	 * Creates the listener for the onClick of the sliding menu
	 */
	private void setMenuOnClickListener() {
		//Set up listener for sliding menu
		lvMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = null;
				switch (position) {
				case 0: //Open the user settings page
					i = new Intent(getApplicationContext(), EditProfileActivity.class);
					i.putExtra("user", User.getActiveUser());
					break;
				case 1: //Open Inbox
					i = new Intent(getApplicationContext(), CreateTripActivity.class);
					i.putExtra("requestType", "Inbox");
					break;
				case 2: //Open Outbox
					i = new Intent(getApplicationContext(), MyRequests.class);
					i.putExtra("requestType", "Outbox");
					break;
				case 3: //Logout
					SharedPreferences savedLogin = getSharedPreferences(LOGIN_PREF, 0);
					Editor loginEditor = savedLogin.edit();
					loginEditor.remove("auth_token");
					loginEditor.commit();
					
					Session session = Session.getSession();
					session.logout(getApplicationContext(), new FutureCallback<String>() {

						@Override
						public void onCompleted(Exception arg0, String arg1) {
							if (arg0 != null) {
								arg0.printStackTrace();
								return;
							}
							
							Intent i = new Intent(getApplicationContext(), LoginActivity.class); //Create new intent
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(i);
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
	
	/**
	 * Initializes and configures the sliding menu
	 * 
	 * @param slMenu View reference that holds the sliding menu
	 */
	private void initSlidingMenu(View slMenu) {
		//Set up sliding menu preferences
		slidingMenu = new SlidingMenu(this);
		//Slide menu from left side of screen
		slidingMenu.setMode(SlidingMenu.LEFT);
		//Slide out when gesture recognized in margin
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		//Set visible preferences
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		//Set the sliding menu list view to the sliding menu
		slidingMenu.setMenu(slMenu);
		//Set the app icon to open the sliding menu also
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
    /******************************** MENU *******************************/
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Your Location");
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

                 @Override
                 public boolean onQueryTextSubmit(String query) {
                         request = new SearchRequest();
                         Object[] string = {query};
                         new StringLocationTask(getApplicationContext(), new Callback<List<Address>>() {

							@Override
							public void failure(RetrofitError arg0) {								
							}

							@Override
							public void success(List<Address> arg0,
									Response arg1) {
								if (arg0 == null) return;
								if (arg0.isEmpty()) return;
								
								Location end = new Location()
									.setLatitude(arg0.get(0).getLatitude())
									.setLongitude(arg0.get(0).getLongitude());
								
								request.setEnd(end);
								search();
							}
                        	 
                         }).execute(string);
                         
                         return false;
                 }

                 @Override
                 public boolean onQueryTextChange(String newText) {
                         return false;
                 }
                 
        });
         
        menu.add("Search")
            .setActionView(searchView)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                
        MenuInflater inflater = getSupportMenuInflater();
 		inflater.inflate(R.menu.search_menu, menu);
		
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
    	
        switch (item.getItemId()) { 
		case R.id.it_calendar: //Calendar button pressed
			CalendarPopUp cPopUp = new CalendarPopUp(this, null, new CalendarPopUp.OnSubmitListener() {

				@Override
				public void valueChanged(Date date) {
	
				}
				
			});
			cPopUp.showAtLocation(pager, Gravity.BOTTOM | Gravity.LEFT, 0, (int)px);
			cPopUp.setInstructions("Find all rides after the chosen date.");
			return true;
		case R.id.it_user_count: //Add user button pressed
			MinPeoplePopUp mPopUp = new MinPeoplePopUp(this, null, new OnSubmitListener() {

				@Override
				public void valueChanged(int value) {
					request.setMinSeats(value);
				}
				
			});
			mPopUp.showAtLocation(pager, Gravity.BOTTOM | Gravity.LEFT, 0, (int)px);
			mPopUp.setInstructions("Set the number of guests");
			return true;
		case android.R.id.home: //App icon button pressed - show sliding menu
			this.slidingMenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    /********************** BUTTON ****************************/
    
    public void toggle(View v) {
    	int currentItem = pager.getCurrentItem();
		pager.setCurrentItem((currentItem + 1) % fragments.size());
    }
    
    /********************** PAGER ADAPTER ***********************/
    
    /**
     * A simple pager adapter that represents 2 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public SherlockFragment getItem(int position) {
        	return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
    
    /******************* ACTIVITY DEFAULTS ********************/
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
		// Reset menu top row to hold the updated user's name (Verify the name has not changed)
		menuList.remove(0);
		menuList.add(0, new Pair<String, Object>("Image", User.getActiveUser().getFirstName()
				+ " " + User.getActiveUser().getLastName()));

		menuAdpt = new SlidingMenuListAdapter(this, menuList, User.getActiveUser());
		lvMenu.setAdapter(menuAdpt);
    }
    
    @Override
    public void onBackPressed() {
    	if(slidingMenu.isMenuShowing())
    		slidingMenu.toggle();
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
	
	/*************************** HELPER **********************/
	
	public void search() {
		request.setContext(this);
		request.setCallback(new FutureCallback<List<Trip>>() {

			@Override
			public void onCompleted(Exception arg0, List<Trip> arg1) {
				if (arg0 == null && arg1 != null) {
					if (arg1.isEmpty()) {
						//Sorry buddy. There ain't no trips.
					} else {
						Trip t = arg1.get(0);
						Log.i("Trip", t.getStart().lat + "");
					}
				} else {
					arg0.printStackTrace();
				}
			}
			
		});
		
		Session.getSession().sendRequest(request);
	}
}
