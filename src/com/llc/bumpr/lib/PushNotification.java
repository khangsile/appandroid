package com.llc.bumpr.lib;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.llc.bumpr.sdk.models.User;

public class PushNotification {
	/** String holding the type of push notification */
	private String type;
	
	/** Integer to hold the request reqeust id */
	private int requestId;
	
	/** Reference to the user who sent the notification */
	private User user;
	
	/** String holding the text of the notification */
	private String message;
	
	/** Boolean holding whether a driver accepted or rejected a drive request (If 
	 * the notification is of type response)
	 */
	private boolean response;
	
	/* Add more details later, such as trip id, etc. */
	
	/**
	 * Default Constructor
	 */
	public PushNotification(){
		
	}
	
	/**
	 * Constructor to create an object holding the information for a push notification
	 * @param json The json representation of the push notification
	 * @throws JSONException Exception thrown from invalid json representation
	 */
	public PushNotification (JSONObject json) throws JSONException{
		type = json.getString("type");
		requestId = json.getInt("request_id");
		message = json.getString("message");
		
		if (type.equals("request")) {
			//Retrieve information for request type notification
			Log.i("com.llc.bumpr GCM", "Getting User");
			user = new User(json.getJSONObject("user"));
			Log.i("com.llc.bumpr GCM", user.toString());
		}
		else if (type.equals("response")){
			//Retrieve information for response type notification
			user = new User(json.getJSONObject("driver"));
			response = json.getBoolean("response");
		}
	}
	
	/**
	 * @return String value representing the type of push notification received
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return Integer value representing the request Id
	 */
	public int getRequestId() {
		return requestId;
	}

	/**
	 * @return Returns the User who sent the notification. For Requests, this is a user. For Responses, this is the driver who replied 
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @return Returns the message sent with the push notification
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return If the request was of the response type, return whether the request was accepted or rejected
	 */
	public boolean getResponse() {
		return response;
	}
}
