package com.llc.bumpr.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.llc.bumpr.R;
import com.llc.bumpr.adapters.SearchTripsAdapter;
import com.llc.bumpr.sdk.models.Trip;

public class SearchListFragment extends SearchTabFragment {

	private ListView listView;
    private ArrayList<Color> colors;
    private ArrayList<Trip> trips;
    private SearchTripsAdapter adapter;
	
    private OnTripSelectedListener listener;
    
    public static Bundle createBundle(String title) {
        Bundle bundle = new Bundle();
        return bundle;
    }
    
    public SearchListFragment() {}
    
    public SearchListFragment setOnTripSelectedListener(OnTripSelectedListener listener) {
    	this.listener = listener;
    	return this;
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_list_fragment, container, false);	
		
		return view;
	}
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        listView = (ListView) getActivity().findViewById(R.id.lv_search_trip);
    	listView.setScrollingCacheEnabled(false);
        
   	 	trips = new ArrayList<Trip>();
        colors = new ArrayList<Color>();
        
        adapter = new SearchTripsAdapter(getActivity(), R.layout.search_trip_row, trips, colors);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Trip t = adapter.getItem(position);
				listener.onTripSelected(t);
			}
        });
    }
    
    /************************* INHERITED **********************/
    
    @Override
	public void listChanged(List<Trip> trips) {
    	adapter.clear();
    	adapter.addAll(trips);
    	adapter.notifyDataSetChanged();
	}

    /********************** INTERFACE *************************/
    
    public interface OnTripSelectedListener {
    	public void onTripSelected(Trip trip);
    }
    
}
