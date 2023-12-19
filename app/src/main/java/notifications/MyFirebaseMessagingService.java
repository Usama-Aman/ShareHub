package notifications;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mobiletouch.sharehub.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import utility.AppUtils;
import utility.Constants;
import utility.SharedPreference;
import utility.URLogs;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        //Log.e(TAG, "sendRegistrationToServer: " + token);

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.e(TAG, "onTokenRefresh: " + refreshedToken);
        // Saving reg id to shared preferences
        SharedPreference.saveSharedPrefValue(this, NotificationConfig.FCM_ID, refreshedToken);
        SharedPreference.saveBoolSharedPrefValue(this, NotificationConfig.FCM_ID_FLAG, true);

        /*SharedPrefs sharedPrefs = new SharedPrefs(getApplicationContext());
        sharedPrefs.setString(PrefrenceConsts.GCM_ID,refreshedToken);
        sharedPrefs.setFlag(PrefrenceConsts.GCM_ID, true);*/
        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(NotificationConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e(TAG, "From: " + remoteMessage);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            // //Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            // handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                //Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(NotificationConfig.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        //Log.e(TAG, "push json: " + json.toString());
        String message = "";
        String title = "";
        String imageUrl = "";
        String timestamp = "";

        JSONObject postData = null;
        int notify_id = 0;


        try {

            JSONObject data = json.getJSONObject("notification");
            if (AppUtils.isSet(data.getString("body"))) {
                message = data.getString("body");
            }

            if (data.getJSONObject("obj") != null) {
                postData = data.getJSONObject("obj");
                message = postData.getString("message");
                notify_id = postData.getInt("noti_id");

            }
            if (AppUtils.isSet(data.getString("title"))) {
                title = data.getString("title");
            } else {
                title = "Share Hub";
            }

            //Log.e(TAG, "title: " + title);
            //Log.e(TAG, "postData: " + postData);

            int totalNotifications = SharedPreference.getIntSharedPrefValue(getApplicationContext(), Constants.pref_notification_counter, 0);
            totalNotifications = totalNotifications + 1;
            SharedPreference.saveIntegerSharedPrefValue(getApplicationContext(), Constants.pref_notification_counter, totalNotifications);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Intent pushNotificationBroadCast = new Intent(NotificationConfig.PUSH_NOTIFICATION);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotificationBroadCast);
                // app is in foreground, broadcast the push message
                //for sending intent after clicking on notification
                URLogs.m("not in background");
                Intent pushNotification = new Intent(getApplicationContext(), MainActivity.class);
                pushNotification.putExtra("message", message);
                pushNotification.putExtra("data", data.toString());
                //LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, pushNotification, notify_id);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, pushNotification, imageUrl, notify_id);
                }
            } else {
                // app is in background, show the notification in notification tray
                URLogs.m("in background");
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("data", data.toString());
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, notify_id);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl, notify_id);
                }
            }


        } catch (JSONException e) {
            //Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            //Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    //Showing notification with text only
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, int notifyId) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, notifyId);
    }

    //Showing notification with text and image
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl, int notifyId) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl, notifyId);
    }
}
