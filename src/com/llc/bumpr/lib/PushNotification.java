package com.llc.bumpr.lib;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.llc.bumpr.sdk.lib.ApiRequest;
import com.llc.bumpr.sdk.models.Driver;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Session;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class PushNotification {
	/** String holding the type of push notification */
	private String type;
	
	/** Integer to hold the request request id */
	private int requestId;
	
	/** Reference to the user who sent the notification */
	private User user;
	
	/** Trip object holding the trip information for the request*/
	private Trip trip;
	
	/** Request object holding the trip information and request id and driver id*/
	private Request request;
	
	/** Boolean holding whether a driver accepted or rejected a drive request (If 
	 * the notification is of type response)
	 */
	private boolean accepted;
	
	/** Context for the context of the push notification object */
	private Context context;
	
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
	public PushNotification (JSONObject json, Context context) throws JSONException{
		type = json.getString("type"); //Get Notification Type
		requestId = json.getInt("id"); //Get Request id
				
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-DD hh:mm:ss 'UTC'").create();
		request = gson.fromJson(json.toString(), Request.class);
		trip = request.getTrip();
		user = request.getUser();
		
		if (type.equals("response")){
			//Retrieve information for response type notification
			accepted = json.getBoolean("accepted"); //Get response message
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
	 * @return Request representing the request
	 */
	public Request getRequest() {
		return request;
	}
	
	/**
	 * @return Returns the User who sent the notification. For Requests, this is a user. For Responses, this is the driver who replied 
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @return If the request was of the response type, return whether the request was accepted or rejected
	 */
	public boolean getAccepted() {
		return accepted;
	}
	
	/**
	 * @return Reference to the trip being requested
	 */
	public Trip getTrip() {
		return trip;
	}
}
