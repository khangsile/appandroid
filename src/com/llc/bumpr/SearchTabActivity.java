package com.llc.bumpr;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.llc.bumpr.fragments.SearchListFragment;
import com.llc.bumpr.fragments.SearchMapFragment;


public class SearchTabActivity extends SherlockFragmentActivity {	
	private ViewPager pager;
	
	private PagerAdapter pagerAdapter;
	
	private ArrayList<SherlockFragment> fragments = new ArrayList<SherlockFragment>();
	
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
        
        menu.getItem(0).expandActionView();
        
        MenuInflater inflater = getSupportMenuInflater();
 		inflater.inflate(R.menu.search_menu, menu);
		
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.it_calendar: //Calendar button pressed
			Toast.makeText(getApplicationContext(),
					"Calendar Pressed",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.it_user_cnt: //Add user button pressed
			Toast.makeText(getApplicationContext(),
					"Set user count pressed",
					Toast.LENGTH_SHORT).show();
			return true;
		//case R.id.it_toggle:
		//	int currentItem = pager.getCurrentItem();
		//	pager.setCurrentItem((currentItem + 1) % fragments.size());
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    /********************** BUTTON ****************************/
    
    public void toggle(View v) {
    	int currentItem = pager.getCurrentItem();
		pager.setCurrentItem((currentItem + 1) % fragments.size());
    }
    
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
}
