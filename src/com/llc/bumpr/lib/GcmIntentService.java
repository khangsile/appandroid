package com.llc.bumpr.lib;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.llc.bumpr.R;
import com.llc.bumpr.RequestActivity;
import com.llc.bumpr.SearchDrivers;
import com.llc.bumpr.SearchTabActivity;
import com.llc.bumpr.TripSummaryActivity;
import com.llc.bumpr.sdk.models.Request;
import com.llc.bumpr.sdk.models.Trip;
import com.llc.bumpr.sdk.models.User;

public class GcmIntentService extends IntentService {
	//Constants needed for GCM (internal)
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String SENDER_ID = "130758040838";
	
	//Tag used for commenting (Internal)
	static final String TAG = "com.llc.bumpr GCM";

	/**
	 * Create a new Intent Service
	 */
	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		//Get extra objects passed to this intent
		Bundle extras = intent.getExtras();
		//Create Google Cloud Messaging object
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		//The getMessageType() intent parameter must be the intent you 
		//received in your BroadcastReceiver
		String messageType = gcm.getMessageType(intent); 
		if(!extras.isEmpty()) {//Has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type.  Since it is likely GCM will
			 * be extended in the future with new message types, just ignore any message
			 * types you're not interested in, or that you don't recognize
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.e(TAG, "Send error: " + extras.toString()); //Log error
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.e(TAG, "Deleted messages on server: " + extras.toString()); //Log deleted message
			}else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
				try {
					JSONObject json = new JSONObject(extras.get("message").toString());
					Log.i(TAG, json.toString());
					sendNotification(new PushNotification(json, getApplicationContext())); 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		//Release the wake lock provided by the WakefulBroadcastReceiver
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
	/**
	 * Puts message into a notification and posts it to the users phone
	 * @param pushNotification Holds the message to the posted in the push notification
	 */
    private void sendNotification(PushNotification pushNotification) {
    	/* Log Printing for testing */
    	Log.i(TAG, pushNotification.getType());
		Log.i(TAG, Integer.toString(pushNotification.getRequestId()));
		Log.i(TAG, pushNotification.getUser().getFirstName() + " " + pushNotification.getUser().getLastName());
    	/* Finished Printing */

		//Create notification manager
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        //If type is request
        if (pushNotification.getType().equals("request")){
        	//Create intent to handle this Notification
        	Intent intent = new Intent(this, TripSummaryActivity.class);
        	//Get objects to pass to the activity
        	User requester = pushNotification.getUser();
        	User tripHost = User.getActiveUser();
        	//Create request object
        	Trip trip = pushNotification.getTrip();
        	
        	//Attach objects to intent
        	intent.putExtra("user", requester);
        	intent.putExtra("trip", trip);
        	intent.putExtra("request", pushNotification.getRequest());
        	intent.putExtra("type", "response");
        	//Sent intent as pending intent
        	PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	//Create notification
        	NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.appa_yip)
            .setContentTitle("Seat Request Received")
            .setStyle(new NotificationCompat.BigTextStyle()
            .bigText(requester.getFirstName() + " " + requester.getLastName() + " has request a seat in your upcoming trip."))
            .setContentText(requester.getFirstName() + " " + requester.getLastName() + " has request a seat.")
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE) //Make phone notify user and vibrate
            .setLights(0xFF0000FF,1000,2500) //Flash blue light for 1 second on and 2.5 seconds off
            .setPriority(Notification.PRIORITY_DEFAULT);

        	//Set pending intent to open upon click, and display notification to the phone
            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
        else if (pushNotification.getType().equals("response")){ //If type is response
        	Log.i(TAG, Boolean.toString(pushNotification.getAccepted()));
        	if (pushNotification.getAccepted()){
        		//If the Driver accepted the ride request
        		/* Take the user to a Trip Summary page where they can mark the trip completed */
        		Log.i(TAG, "Inside Good Response");
        		Intent intent = new Intent(this, TripSummaryActivity.class);
            	//Get objects to pass to the activity
            	User host = pushNotification.getUser();
            	User requester = User.getActiveUser();
            	//Create request object
            	Trip trip = pushNotification.getTrip();
            	Log.i(TAG, "3");
            	//attach objects to intent
            	intent.putExtra("user", host);
            	intent.putExtra("trip", trip);
            	intent.putExtra("type", "accepted");
            	intent.putExtra("request", pushNotification.getRequest());
        		
        		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        		//Build the notification and set the accepted text
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.appa_yip)
                .setContentTitle("Seat Request Accepted")
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(host.getFirstName() + " " + host.getLastName() + " has accepted your request for a seat!"))
                .setContentText(host.getFirstName() + " " + host.getLastName() + " has accepted your request!")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE) //Make phone notify user and vibrate
                .setLights(0xFF0000FF,1000,2500) //Flash blue light for 1 second on and 2.5 seconds off
                .setPriority(Notification.PRIORITY_DEFAULT);
                //Set the content and display the notification to the phone
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        	}
        	else {
        		//If the Driver rejected the ride request, take the user back to the search Driver page
        		Log.i(TAG, "Rejected response notification");
        		Intent intent = new Intent(this, SearchTabActivity.class);
        		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        		//Build Notification with rejected text
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.appa_yip)
                .setContentTitle("Driving Request Rejected")
                .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(pushNotification.getUser().getFirstName() + " " + pushNotification.getUser().getLastName() + " has declined your request for " +
                		"a seat.  Press here to continue searching for a ride!"))
                .setContentText(pushNotification.getUser().getFirstName() + " " + pushNotification.getUser().getLastName() + " has declined your request for a seat.")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE) //Make phone notify user and vibrate
                .setLights(0xFF0000FF,1000,2500) //Flash blue light for 1 second on and 2.5 seconds off
                .setPriority(Notification.PRIORITY_DEFAULT);
                //Set content and display the notification to the phone
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        	}
        }
        
        //Otherwise, the Push Notification of any other type is not currently handled 
        return;
    }

}
