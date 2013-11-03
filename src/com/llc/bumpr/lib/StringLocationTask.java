package com.llc.bumpr.lib;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class StringLocationTask extends GeocodeLocationTask {

	public StringLocationTask(Context context, Callback<List<Address>> callback) {
		super(context, callback);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected List<Address> getAddressList(Object... params) throws SocketTimeoutException, IOException {
		// TODO Auto-generated method stub
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		
		if(!(params[0] instanceof String))
			throw new IllegalArgumentException("params[0] must be of instance String");
		
		List<Address> list = geocoder.getFromLocationName((String) params[0],  10); 
		return list;
	}

}
