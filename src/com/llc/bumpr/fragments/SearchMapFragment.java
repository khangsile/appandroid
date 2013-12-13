package com.llc.bumpr.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.llc.bumpr.R;
import com.llc.bumpr.lib.Colors;
import com.llc.bumpr.lib.GMapV2Painter;
import com.llc.bumpr.sdk.models.Trip;

public class SearchMapFragment extends SearchTabFragment {
	
	private GoogleMap map;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_map_fragment, container, false);	
		
		return view;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        map = ((SupportMapFragment) getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.map_search)).getMap();
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(38, -97) , 3.5f) );   
    }

	/*************************** INHERITED ****************************/
	
	@Override
	public void listChanged(List<Trip> trips) {
		if (trips.isEmpty()) return;
		
		int index = 0; 
		
		final ProgressDialog pd = ProgressDialog.show(getActivity(),
				"Please Wait", "Loading routes...", false, true);
		for (Trip trip : trips) {
			ArrayList<LatLng> points = new ArrayList<LatLng>();
			points.add(new LatLng(trip.getStart().lat, trip.getStart().lon));
			points.add(new LatLng(trip.getEnd().lat, trip.getEnd().lon));
			
			GMapV2Painter painter = new GMapV2Painter(map, points);
			painter.setWidth(8);
						
			painter.setColor(Colors.getColor(index));
			painter.paint();
			
			index++;
		}
		pd.dismiss();
		
	}
}
