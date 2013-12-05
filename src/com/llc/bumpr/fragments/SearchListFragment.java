package com.llc.bumpr.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.llc.bumpr.R;
import com.llc.bumpr.adapters.SearchTripsAdapter;
import com.llc.bumpr.sdk.lib.Location;
import com.llc.bumpr.sdk.models.Trip;

public class SearchListFragment extends SherlockFragment {

	private ListView listView;
    private ArrayList<Color> colors;
    private ArrayList<Trip> trips;
    private SearchTripsAdapter adapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_list_fragment, container, false);	
		
		return view;
	}
 
    public static Bundle createBundle(String title ) {
        Bundle bundle = new Bundle();
        return bundle;
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        listView = (ListView) getActivity().findViewById(R.id.lv_search_trip);
    	
   	 	trips = new ArrayList<Trip>();
   	 	for (int i=0; i<12; i++) 
        trips.add(new Trip.Builder().setDriverId(1)
       		 .setStart(new Location(12, 12))
       		 .setEnd(new Location(12, 12))
       		 .build());
        colors = new ArrayList<Color>();
        
        adapter = new SearchTripsAdapter(getActivity(), R.layout.trip_row, trips, colors);
        listView.setAdapter(adapter);
    }

    /********************** INTERFACE *************************/
    
    public interface onTripSelectedListener {
    	public void onTripSelected(Trip trip);
    }
    
}
