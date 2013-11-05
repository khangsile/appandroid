package com.llc.bumpr.lib;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

public class LatLngLocationTask extends GeocodeLocationTask {

	public LatLngLocationTask(Context context, Callback<List<Address>> callback) {
		super(context, callback);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected List<Address> getAddressList(Object... params) throws SocketTimeoutException, IOException {
		// TODO Auto-generated method stub
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
						
		LatLng location = (LatLng) params[0];
		List<Address> list = geocoder.getFromLocation(location.latitude, location.longitude, 10); 
		return list;
	}

}
