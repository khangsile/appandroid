package com.llc.bumpr.lib;

import java.util.ArrayList;

import org.w3c.dom.Document;

import retrofit.Callback;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Paints route on a google map object among other things
 * @author KhangSiLe
 *
 */
public class GMapV2Painter {

	private int width = 5;
	private int color = Color.BLUE;
	private GoogleMap gMap;
	private ArrayList<LatLng> points; // must be >= 2
	private Document document;
	private GMapV2Direction md;
	//public static int count = 0;
	
	public static void getDistance(final ArrayList<LatLng> points, final Callback<Integer> cb) {
		new AsyncTask<Object, Void, Integer>() {

			@Override
			protected Integer doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				Integer total = 0;
				GMapV2Direction md = new GMapV2Direction();
				
				for(int i=0; i+1<points.size(); i++) {
					LatLng start = points.get(i);
					LatLng end = points.get(i+1);
					
					Document document = getDocument(start, end);
					Integer distance = md.getDistanceValue(document);
					total += distance;
				}
				
				return total;
			}
			
			@Override
			protected void onPostExecute(Integer distance) {
				cb.success(distance, null);
			}
			
		}.execute();
	}
	
	private static Document getDocument(LatLng start, LatLng end) {
		GMapV2Direction md = new GMapV2Direction();		
		return md.getDocument(start, end, GMapV2Direction.MODE_DRIVING);
	}
	
	public GMapV2Painter(GoogleMap gMap, ArrayList<LatLng> points) {
		if (points == null) throw new NullPointerException("arg 'points' cannot be null");
		if (points.size() < 2) throw new IllegalArgumentException("arg 'points' must be of size greater than 1");
		if (gMap == null) throw new NullPointerException("arg 'gMap' cannot be null");
		
		this.gMap = gMap;
		this.points = new ArrayList<LatLng>(points);
		this.md = new GMapV2Direction();
	}
	
	// possibly refactor to do individual stopping sections rather than the entire thing at once
	public void paint() {
		//count++;
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
					//count--;
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
		if (this.document == null) {
			this.document = getDocument(start, end);
		}
		
		return md.getDirection(this.document);
	}
	
	private PolylineOptions extendLine(PolylineOptions line, ArrayList<LatLng> points) {
		for(int i = 0 ; i < points.size() ; i++) {          
			line.add(points.get(i));
		}
		
		return line;
	}
	
}
