package notifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;



import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent, int notifyId) {
        showNotificationMessage(title, message, timeStamp, intent, null, notifyId);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl, int notifyId) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        final int icon = R.mipmap.ic_launcher;
        // Open activity on click notification just put intent of that activity in the pendingIntent
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.putExtra("message", intent.getStringExtra("message"));
        resultIntent.putExtra("data", intent.getStringExtra("data"));

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_ONE_SHOT
                );

        final Notification.Builder mBuilder = new Notification.Builder(mContext);
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, notifyId, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, notifyId, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, notifyId, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            playNotificationSound();
        }
    }

    private void showSmallNotification(Notification.Builder mBuilder, int notifyId, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
//        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Notification.BigTextStyle inboxStyle = new Notification.BigTextStyle();
//        inboxStyle.bigText(message);
//
//        String channelId = notifyId + "10001";
//        String channelName = "GCAM Notifications Channel";
//
//        mBuilder = new Notification.Builder(mContext);
//        mBuilder.setSmallIcon(icon).setTicker("gCAM").setWhen(0)
//                .setAutoCancel(true)
//                .setContentTitle("gCAM")
//                .setContentIntent(resultPendingIntent)
//                .setWhen(System.currentTimeMillis())
//                .setContentText(message);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(
//                    channelId, channelName, importance);
//            mChannel.setSound(null, null);
//            mChannel.enableLights(false);
//            mChannel.setLightColor(Color.BLUE);
//            mChannel.enableVibration(false);
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(mChannel);
//
//            if (mBuilder != null)
//                mBuilder.setGroupSummary(true);
//
//        } else {
//            mBuilder.setStyle(inboxStyle);
//            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon));
//            mBuilder.setSound(alarmSound);
//        }
//        assert notificationManager != null;
//        notificationManager.notify(notifyId, mBuilder.build());
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            playNotificationSound();
//        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, "channel_id")
                .setContentTitle("shareHub")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(resultPendingIntent)
                .setContentInfo(message)
                .setTicker(message)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setPriority(NotificationCompat.PRIORITY_HIGH) //must give priority to High, Max which will considered as heads-up notification
                .setColor(Color.GREEN)

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(icon).setTicker("shareHub").setWhen(0);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(message);
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            notificationManager.createNotificationChannel(channel);

        }
    }

    private void showBigNotification(Bitmap bitmap, Notification.Builder mBuilder, int notifyId, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        String channelId = notifyId + "10001";
        String channelName = "shareHub Notifications Channel";

        mBuilder = new Notification.Builder(mContext);
        mBuilder.setSmallIcon(icon).setTicker("shareHub").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("shareHub")
                .setContentIntent(resultPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setContentText(message);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setSound(null, null);
            mChannel.enableLights(false);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);

            if (mBuilder != null)
                mBuilder.setGroupSummary(true);


        } else {
            mBuilder.setStyle(bigPictureStyle);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon));
            mBuilder.setSound(alarmSound);
        }
        assert notificationManager != null;
        notificationManager.notify(notifyId, mBuilder.build());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            playNotificationSound();
//        }
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {/*
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
*/
            final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
