package com.llc.bumpr.lib;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.llc.bumpr.R;

public class GCMRegistrationManager {

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "Bumpr GCMRegistrationManager";
	private static final String APP_VERSION_PROPERTY = "app_version";
	private static final String REG_ID_PROPERTY = "registration_id";

	private Context context;
	private GoogleCloudMessaging gcm;
	private String registrationId;
	
	/************************* STATIC *******************************/
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 * 
	 * @param pd Reference to the displayed progress dialog
	 */
	public static boolean checkPlayServices(ProgressDialog dialog, Context context) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		// If not successful attempt to recover from the error and have them
		// download google play services
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				dialog.dismiss();
				return true;
			}  
			// Otherwise, the device is not support
			Log.i(TAG, "This device is not supported.");
			dialog.dismiss();
			
			return false;
		}
		
		dialog.dismiss();
		// If google play services are installed and activated, return true
		return true;
	}
	
	/************************* INSTANCE ********************************/
	
	public GCMRegistrationManager(Context context) {
		this.context = context;
		this.gcm = GoogleCloudMessaging.getInstance(context);
	}
	
	/**
	 * @return returns the registrationId that may/may not have been retrieved from
	 * GooglePlayServices
	 */
	public String getRegistrationId() {
		return registrationId;
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * If the result is empty, the app needs to register with GCM service.
	 * 
	 * @param context2 Application context
	 * @return registration ID or empty string if there is no existing
	 * registration ID
	 */
	public String getRegistrationIdFromGCM() {
		// TODO Auto-generated method stub
		// Get shared preferences and check if GCM registration id is stored
		SharedPreferences prefs = getGCMPreferences(context);
		registrationId = prefs.getString(REG_ID_PROPERTY, "");
		// If no registration stored, return empty string
		if (TextUtils.isEmpty(registrationId)) {
			return "";
		}
		
		// Check if app was updated. If so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new app
		// version.
		int registeredVersion = prefs.getInt(APP_VERSION_PROPERTY, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			// If new version, return empty string
			Log.i(TAG, "App version changed.");
			return "";
		}
		// Return registration id if all tests were passed
		Log.i(TAG, registrationId);
		
		return registrationId;
	}
	
	/**
	 * Get Application's current version
	 * 
	 * @param context2 Application context
	 * @return Application's version code from the {@code PackageManager}
	 */
	private int getAppVersion(Context context) {
		// TODO Auto-generated method stub
		try {
			// Check package info and return the version
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// This should never take place
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	
	/**
	 * Get GCM Shared Preferences instance
	 * 
	 * @param context2 Application context
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	public void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				String msg = "";
				try {
					// If no Gcm instance, create a new instance for this
					// context
					if (gcm == null)
						gcm = GoogleCloudMessaging.getInstance(context);
					// register the user to GCM and store their registration id
					registrationId = gcm.register(context.getString(R.string.gcm_sender_id));
					msg = "Device registered, registration ID=" + registrationId;

					// You should send the registration ID to your server over
					// HTTP, so it can use GCM/HTTP or CCS to send messages to your
					// app. The request to your server should be authenticated if
					// your app is using accounts.

					// Not needed currently. We are sending up the GCM API key
					// upon login
					// sendRegistrationIdToBackend();

					// Store registration id here in shared preferences
					storeRegistrationId(registrationId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();					
				}
				// return GCM API key to onPostExecute
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
			
		}.execute(null, null, null); // Execute the async task

	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}
	 * 
	 * @param context Application's context
	 * @param regid Registration ID
	 */
	private void storeRegistrationId(String regid) {
		// Get GCM preferences
		SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context); // Get the app version
		Log.i(TAG, "Saving regId on app version " + appVersion);

		// Write the app version and GCM API key to shared preferences and store them
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID_PROPERTY, regid);
		editor.putInt(APP_VERSION_PROPERTY, appVersion);
		editor.commit();
	}
	
}
