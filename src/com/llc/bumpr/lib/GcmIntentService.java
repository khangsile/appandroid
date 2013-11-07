package com.llc.bumpr.lib;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.llc.bumpr.LoginActivity;
import com.llc.bumpr.R;
import com.llc.bumpr.R.drawable;
import com.llc.bumpr.SearchDrivers;

public class GcmIntentService extends IntentService {
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String SENDER_ID = "130758040838";
	
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
		Bundle extras = intent.getExtras();
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
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			}else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
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
	 * @param msg Holds the message to the posted in the push notification
	 */
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SearchDrivers.class), Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Driving Request Received")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg)
        .setAutoCancel(true)
        .setOnlyAlertOnce(true)
        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE) //Make phone notify user and vibrate
        .setLights(0xFF0000FF,1000,2500) //Flash blue light for 1 second on and 2.5 seconds off
        .setPriority(Notification.PRIORITY_DEFAULT);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
