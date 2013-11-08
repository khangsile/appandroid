package com.llc.bumpr.lib;

import org.json.JSONException;
import org.json.JSONObject;

import com.llc.bumpr.sdk.models.Request;
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
		//Values is all types of requests
		type = json.getString("type"); //Get Notification Type
		requestId = json.getInt("request_id"); //Get Request id
		
		if (type.equals("request")) {
			//Retrieve information for request type notification
			user = new User(json.getJSONObject("user")); //Get user requesting the trip
			trip = new Trip(json.getJSONObject("trip")); //Get trip object for the request
			request = new Request.Builder()
						.setId(requestId)
						.setDriverId(User.getActiveUser().getDriverProfile().getId())
						.setTrip(trip)
						.setUserId(user.getId())
						.build();
		}
		else if (type.equals("response")){
			//Retrieve information for response type notification
			user = new User(json.getJSONObject("user")); //Get driver who responded
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
	 * @return Request object
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
