package com.llc.bumpr;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.User;

public abstract class BumprActivity extends SherlockFragmentActivity {

	private final String SHARED_PREF_LOGIN = "bumprLogin";
	
	/*
	 * First attempt to get the access token. If it does not exist in memory (from the session),
	 * get it from the SharedPreferences. If it does not exist in sharedPreferences, do nothing. 
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (User.getActiveUser() == null) {
			Session session = Session.getSession();
			if (session.getAuthToken() == null || session.getAuthToken().trim().equals("")) {
				SharedPreferences prefs = getSharedPreferences(SHARED_PREF_LOGIN, 0);
				String token = prefs.getString("auth_token", "");
				session.setAuthToken(token);
				if (token.trim().equals("") || token == null) return;
			}
			getUser(session);
		} else {
			initializeMe(User.getActiveUser());
		}
	}
	
	private void getUser(Session session) {
		ApiRequest request = User.getMeRequest(getApplicationContext(), new FutureCallback<User>() {

			@Override
			public void onCompleted(Exception arg0, User arg1) {
				if (arg0 == null) {
					//Asynchronous initialization
					initializeMe(arg1);
				}
			}
		});
		
		session.sendRequest(request);
	}
	
	/**
	 * Method used for asynchronous loading of the active user if it 
	 * falls out of memory. This method should be implemented by all 
	 * child classes
	 * @param activeUser the active user
	 */
	protected abstract void initializeMe(User activeUser);
	
}
