package com.llc.bumpr.lib;

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class GMapV2Painter {

	private int width = 5;
	private int color = Color.BLUE;
	private GoogleMap gMap;
	private ArrayList<LatLng> points; // must be >= 2
	
	public GMapV2Painter(GoogleMap gMap, ArrayList<LatLng> points) {
		if (points == null) throw new NullPointerException("arg 'points' cannot be null");
		if (points.size() < 2) throw new IllegalArgumentException("arg 'points' must be of size greater than 1");
		if (gMap == null) throw new NullPointerException("arg 'gMap' cannot be null");
		
		this.gMap = gMap;
		this.points = new ArrayList<LatLng>(points);
	}
	
	// possibly refactor to do individual stopping sections rather than the entire thing at once
	public void paint() {
		new AsyncTask<Object, Void, PolylineOptions>() {

			@Override
			protected PolylineOptions doInBackground(Object... arg0) {
				PolylineOptions line = new PolylineOptions().width(width).color(color);
				
				for(int i=0; i+1<points.size(); i++) {
					LatLng start = points.get(i);
					LatLng end = points.get(i+1);
					
					ArrayList<LatLng> directionPoints = getPoints(start, end);
					line = extendLine(line, directionPoints);
				}
				
				return line;
			}
			
			@Override
			protected void onPostExecute(PolylineOptions rectLine) {
					gMap.addPolyline(rectLine);
			}
			
		}.execute();
	}
	
	public void addPoint(LatLng point) {
		points.add(point);
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	private ArrayList<LatLng> getPoints(LatLng start, LatLng end) {
		GMapV2Direction md = new GMapV2Direction();
		
		Document doc = md.getDocument(start, end, GMapV2Direction.MODE_DRIVING);
		return md.getDirection(doc);
	}
	
	private PolylineOptions extendLine(PolylineOptions line, ArrayList<LatLng> points) {
		for(int i = 0 ; i < points.size() ; i++) {          
			line.add(points.get(i));
		}
		
		return line;
	}
	
}
