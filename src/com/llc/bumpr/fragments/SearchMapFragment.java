package com.llc.bumpr.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.llc.bumpr.R;
import com.llc.bumpr.sdk.models.Trip;

public class SearchMapFragment extends SearchTabFragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_map_fragment, container, false);	
		
		return view;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
    }

	/*************************** INHERITED ****************************/
	
	@Override
	public void listChanged(List<Trip> trips) {
		// TODO Auto-generated method stub
		
	}
}
