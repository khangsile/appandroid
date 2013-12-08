package com.llc.bumpr.lib;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.koushikdutta.async.future.FutureCallback;

/**
 * Takes a string an converts it to an address with LatLng coordinates
 * @author KhangSiLe
 *
 */
public class StringLocationTask extends GeocodeLocationTask {

	public StringLocationTask(Context context, Callback<List<Address>> callback) {
		super(context, callback);
	}
	
	public StringLocationTask(Context context, FutureCallback<List<Address>> cb) {
		super(context, cb);
	}
	
	@Override
	protected List<Address> getAddressList(Object... params) throws SocketTimeoutException, IOException {
		// TODO Auto-generated method stub
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
						
		List<Address> list = geocoder.getFromLocationName((String) (params[0]),  10); 
		return list;
	}

}
