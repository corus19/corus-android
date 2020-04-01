package com.jamdoli.corus.fcm;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jamdoli.corus.activities.SplashScreen;
import com.jamdoli.corus.utils.AppHelper;
import com.jamdoli.corus.utils.DebugLogManager;
import com.jamdoli.corus.utils.MyAppContext;
import java.util.Map;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private DebugLogManager logManager ;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (MyAppContext.getInstance() == null) {
            MyAppContext.setInstance(TAG, MyAppContext.getInstance());
        }
        logManager = DebugLogManager.getInstance();
        logManager.logsForDebugging(TAG, "From: " + remoteMessage.getFrom());
        Map<String, String> fcmMessage = remoteMessage.getData();
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            logManager.logsForDebugging(TAG, "Message data payload: " + remoteMessage.getData());
            logManager.logsForDebugging(TAG, "Message ---  " + fcmMessage.get("message"));
            String type = fcmMessage.get("type") == null ? " " : fcmMessage.get("type");

            // define action to take on notification
            callTry(remoteMessage,"define here");

        }

        if (remoteMessage.getNotification() != null) {
            logManager.logsForDebugging(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(logManager!=null)
            logManager.logsForDebugging(TAG, "onDestroy()");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        logManager.logsForDebugging(TAG, "onDestroy()");
        super.onTaskRemoved(rootIntent);


    }

    public void callTry(RemoteMessage remoteMessage, String action){
        try {
            logManager.logsForDebugging(TAG, "Message ---  " + remoteMessage.getData().get("alert"));
            final JSONObject notificationObj = new JSONObject(remoteMessage.getData());
            Intent notificaitonIntent  = new Intent(this, SplashScreen.class);
            notificaitonIntent.putExtra("action",action);
            AppHelper.getInstance().showNotification(this,"Fyndr",remoteMessage.getData().get("alert"),notificaitonIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }
}