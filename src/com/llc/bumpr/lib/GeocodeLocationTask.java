package com.llc.bumpr.lib;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit.Callback;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.koushikdutta.async.future.FutureCallback;

/**
 * Be careful with the Object[] parameter...Cast correctly.
 * @author KhangSiLe
 *
 */
public abstract class GeocodeLocationTask extends AsyncTask<Object, Void, List<Address>> {

	protected Context context;
	protected Callback<List<Address>> callback;
	protected FutureCallback<List<Address>> cb;
	
	// geocoder to process queries
    private Geocoder geocoder;
 
    // geocoder specific settings
    private final Integer RESPONSE_LIMIT = 3;	
	
	public GeocodeLocationTask(Context context, Callback<List<Address>> callback) {
		this.context = context;
		this.callback = callback;
	}
	
	public GeocodeLocationTask(Context context, FutureCallback<List<Address>> cb) {
		this.context = context;
		this.cb = cb;
	}
	
	@Override
	protected List<Address> doInBackground(Object... params) {
		// TODO Auto-generated method stub
		
		List<Address> addressList = null;
		int responseCount = 0;
		
		//retry loop
		while (addressList == null && responseCount <= RESPONSE_LIMIT) {
            
			try {
                // populate address list from query and return
                addressList = getAddressList(params);
            } catch (SocketTimeoutException e) {
            	e.printStackTrace();
                addressList = null;
            }  catch (IOException e) {
            	e.printStackTrace();
                addressList = null;
            }
            // add to the response count until the response limit has been hit
            responseCount++;
        }
		
		return addressList;
	}
	
	@Override
	protected void onPostExecute(List<Address> result){
		
		if (callback != null) {
			if (result == null) 
				callback.failure(null);
			else 
				callback.success(result, null);
		}
		
		if (cb != null) {
			cb.onCompleted(null, result);
		}
	}
	
	abstract protected List<Address> getAddressList(Object... params) throws SocketTimeoutException, IOException;
}