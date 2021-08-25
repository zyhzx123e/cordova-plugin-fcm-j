package com.gae.scaffolder.plugin;

import android.graphics.Color;
import android.app.Notification;
import android.app.NotificationChannel; 
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;
import java.lang.Exception;
import android.os.PowerManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Felipe Echanique on 08/06/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMPlugin";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "==> MyFirebaseMessagingService onMessageReceived");
		
		if( remoteMessage.getNotification() != null){
			Log.d(TAG, "\tNotification Title: " + remoteMessage.getNotification().getTitle());
			Log.d(TAG, "\tNotification Message: " + remoteMessage.getNotification().getBody());
		}
		String notificationTitle="";
        String notificationBody="";
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("wasTapped", false);
		for (String key : remoteMessage.getData().keySet()) {
                Object value = remoteMessage.getData().get(key);
                Log.d(TAG, "\tKey: " + key + " Value: " + value);
				data.put(key, value); 
        }
        notificationTitle=remoteMessage.getData().get("title")!=null? remoteMessage.getData().get("title").toString():"";
        notificationBody=remoteMessage.getData().get("body")!=null? remoteMessage.getData().get("body").toString():"";
		
		Log.d(TAG, "\tNotification Data: " + data.toString());
       
        sendNotification(notificationTitle, notificationBody, data);
         FCMPlugin.sendPushPayload( data );
         
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, Map<String, Object> data) {
        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		for (String key : data.keySet()) {
			intent.putExtra(key, data.get(key).toString());
		}
         PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                 PendingIntent.FLAG_ONE_SHOT);

           try {

            String chan_id="FCM_MSG";
            String chan_name="FCM_MSG_new_post";
            NotificationChannel notificationChannel = null;
			NotificationCompat.Builder notificationBuilder = null;
			 
	   
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                notificationChannel = new NotificationChannel(chan_id,
                        chan_name, NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.parseColor("#FFFFFF"));
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


                notificationBuilder = new NotificationCompat.Builder(this,chan_id)
                        .setSmallIcon(getApplicationInfo().icon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setContentText(messageBody)
                        .setAutoCancel(true).setPriority(Notification.PRIORITY_MAX)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

              


                if (notificationBuilder != null) {

                    // PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                    // PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RetailAIM:WakeLock");
                    // wakeLock.acquire();
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.createNotificationChannel(notificationChannel);

                    notificationManager.notify(0, notificationBuilder.build());
                     startForeground(0, notificationBuilder.build());


                    //trick to hide the foreground notification
//                    Notification notification;
//                    NotificationCompat.Builder bBuilder = new NotificationCompat.Builder(
//                            this,chan_id).setContentTitle("")
//                            .setPriority(Notification.PRIORITY_MIN)
//                            .setContentText("").setOngoing(true);
//                    notification = bBuilder.build();
//                    notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
//                    notification.flags |= Notification.FLAG_NO_CLEAR;
//                    notification.flags |= Notification.FLAG_ONGOING_EVENT;
//                    startForeground(notificationData.getId(), notification);
//                    stopForeground(true);
                    //trick to hide the foreground notification

                    //FCMPlugin.sendPushPayload( data );
                   // wakeLock.release();

                } else {
                    Log.d(TAG, "zyh notification");
                }
            }else {
				 
	     notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(getApplicationInfo().icon)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setContentText(messageBody)
                        .setAutoCancel(true).setPriority(Notification.PRIORITY_MAX)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);
  

                if (notificationBuilder != null) {


                    // PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                    // PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RetailAIM:WakeLock");
                    // wakeLock.acquire();

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0, notificationBuilder.build());
                    //wakeLock.release();

                } else {
                    Log.d(TAG, "zyh notification");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
