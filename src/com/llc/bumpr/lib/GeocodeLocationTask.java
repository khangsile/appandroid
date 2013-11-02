package com.llc.bumpr.lib;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public abstract class GeocodeLocationTask extends AsyncTask<Object, Void, List<Address>> {

	private Context context;
	private Callback<List<Address>> callback;
	
	// geocoder to process queries
    private Geocoder geocoder;
 
    // geocoder specific settings
    private final Integer RESPONSE_LIMIT = 3;	
	
	public GeocodeLocationTask(Context context, Callback<List<Address>> callback) {
		this.context = context;
		this.callback = callback;
	}
	
	@Override
	protected List<Address> doInBackground(Object... params) {
		// TODO Auto-generated method stub
		geocoder = new Geocoder(context, Locale.getDefault());
		
		List<Address> addressList = new ArrayList<Address>();
		int responseCount = 0;
		
		//retry loop
		while (addressList == null && responseCount <= RESPONSE_LIMIT) {
            
			addressList = getAddressList(params);
            // add to the response count until the response limit has been hit
            responseCount++;
        }
		
		return addressList;
	}
	
	@Override
	protected void onPostExecute(List<Address> result){
		super.onPostExecute(result);
		callback.success(result, null);
	}
	
	abstract protected List<Address> getAddressList(Object... params);
}
