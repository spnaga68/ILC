//package com.example.developer.realmstudy.service;
//
//import android.annotation.SuppressLint;
//import android.app.IntentService;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.Taximobility.MainActivity;
//import com.Taximobility.MainHomeFragmentActivity;
//import com.Taximobility.R;
//import com.Taximobility.SplashAct;
//import com.Taximobility.util.SessionSave;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//
//import org.json.JSONObject;
//
//@SuppressLint("InlinedApi")
//public class GcmIntentService extends IntentService {
//    public static final int NOTIFICATION_ID = 1;
//    private NotificationManager mNotificationManager;
//    NotificationCompat.Builder builder;
//    public GcmIntentService() {
//        super("GcmIntentService");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {// Handling gcm message from
//        // pubnub
//        Bundle extras = intent.getExtras();
//        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//        String messageType = gcm.getMessageType(intent);
//        if (!extras.isEmpty()) {
//            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                //sendNotification("Send error: " + extras.toString());
//            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                //				sendNotification("Deleted messages on server: "
//                //						+ extras.toString());
//            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                Log.i("", "Received: " + extras.toString());
//                //  Toast.makeText(GcmIntentService.this, extras.toString(), Toast.LENGTH_SHORT).show();
//                generateNotification(this, extras.getString("message"), MainHomeFragmentActivity.class);
//            }
//        }
//        // Release the wake lock provided by the WakefulBroadcastReceiver.
//        GcmBroadcastReceiver.completeWakefulIntent(intent);
//    }
//
//    private void sendNotification(String msg) {
//        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Ripe Ride Passenger").setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        try {
//            Ringtone r = RingtoneManager.getRingtone(getBaseContext(), notification1);
//            r.play();
//        } catch (NullPointerException ex) {
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    public void generateNotification(Context context, String message, Class<?> class1) {
//        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        // Notification notification = new Notification(R.drawable.app_icon, message, System.currentTimeMillis());
//        String title = context.getString(R.string.app_name);
//        Intent notificationIntent = new Intent(this, SplashAct.class);
//        notificationIntent.putExtra("GCMnotification", message);
//        SessionSave.saveSession("GCMnotification", message, context);
//        int requestID = (int) System.currentTimeMillis();
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // notification.setLatestEventInfo(context, title, message, pendingIntent);
//        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        String Message = "";
//        try {
//            JSONObject jo = new JSONObject(message);
//            Message = getApplicationContext().getString(R.string.z_split_fare_with) + " " + jo.getString("passenger_name");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(this);
//        builder.setAutoCancel(false);
//        builder.setTicker(getString(R.string.app_name));
//        builder.setContentTitle(title);
//        builder.setContentText(Message);
//        builder.setSmallIcon(R.drawable.ic_launcher);
//        builder.setContentIntent(pendingIntent);
//        builder.setOngoing(true);
//        builder.setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());
//        //builder.setSubText("This is subtext...");   //API level 16
//        builder.setNumber(100);
//        builder.build();
//        Notification myNotication = builder.getNotification();
//        myNotication.flags |= Notification.FLAG_AUTO_CANCEL;
//        mNotificationManager.notify(NOTIFICATION_ID, myNotication);
//        Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        try {
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification1);
//            r.play();
//        } catch (NullPointerException ex) {
//            ex.printStackTrace();
//        }
//        if(MAIN_ACT!=null){
//            Handler h= new Handler();
//            if(h!=null)
//           h.post(new Runnable() {
//                @Override
//                public void run() {
//                    MAIN_ACT.checkGCM();
//                }
//            });
//
//        }
//    }
//}