package com.mobiletouch.sharehub.service;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.app.JobIntentService;


import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.mobiletouch.sharehub.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interfaces.RetrofitInterface;
import models.Download;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import utility.URLs;


/**
 * Media download service for Oreo
 */
public class Download_JobService extends JobIntentService {

    public Download_JobService() {
        super();
    }

    static final int JOB_ID = 1000;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;

    List<String> imagesToDownload = new ArrayList<>();
    int i_loop = 0;


    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, Download_JobService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(Intent intent) {

        i_loop = 0;
        imagesToDownload = Arrays.asList(intent.getStringExtra("list").split("\\s*,\\s*"));
        filePaths = new ArrayList<>();

        showNotificationMessage();
        initDownload();
        playNotificationSound();

    }

    private void initDownload() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BaseUrlEventsMedia)
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<ResponseBody> request = retrofitInterface.downloadFile(imagesToDownload.get(i_loop));
        try {

            downloadFile(request.execute().body());

        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }


    ArrayList<String> filePaths = new ArrayList<>();

    private void downloadFile(ResponseBody body) throws IOException {
        try {

            int count;
            byte data[] = new byte[1024 * 4];
            long fileSize = body.contentLength();
            InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);

            File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File dir = new File(sdCard.getAbsolutePath() + "/ShareHub");

            if (!dir.exists())
                dir.mkdirs();

            File outputFile = new File(dir,
                    imagesToDownload.get(i_loop));

            filePaths.add(outputFile.getPath());
            OutputStream output = new FileOutputStream(outputFile);
            long total = 0;
            long startTime = System.currentTimeMillis();
            int timeCount = 1;
            while ((count = bis.read(data)) != -1) {

                total += count;
                totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));

                int progress = (int) ((total * 100) / fileSize);

                long currentTime = System.currentTimeMillis() - startTime;

                Download download = new Download();
                download.setTotalFileSize(totalFileSize);

                //  if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
                //  }

                output.write(data, 0, count);
            }
            onDownloadComplete();
            output.flush();
            output.close();
            bis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(Download download) {

        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText((i_loop + 1) + "/" + imagesToDownload.size() + " " + String.format(getString(R.string.alert_media_downloaded) + " (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());

        // //Log.e("file download....", "...." + ((i_loop + 1) + "/" + imagesToDownload.size() + " " + String.format("Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize())));
    }


    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText((i_loop + 1) + "/" + imagesToDownload.size() + getString(R.string.alert_media_file_download));
        notificationManager.notify(0, notificationBuilder.build());
        download.setProgress(0);

        if (i_loop != (imagesToDownload.size() - 1)) {
            i_loop++;
            initDownload();
        } else {

            notificationManager.cancel(0);
            showNotificationMessage();
            notificationBuilder.setContentTitle(getString(R.string.alert_media_download_complete));
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText((i_loop + 1) + "/" + imagesToDownload.size() + getString(R.string.alert_media_file_download));
            notificationManager.notify(0, notificationBuilder.build());

            if (filePaths != null && filePaths.size() != 0)
                for (int i = 0; i < filePaths.size(); i++) {
                    MediaScannerConnection.scanFile(this, new String[]{filePaths.get(i)}, new String[]{"image/jpeg"}, null);
                }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                if (filePaths != null && filePaths.size() != 0)
                    for (int i = 0; i < filePaths.size(); i++) {
                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(Uri.parse(filePaths.get(i)));
                        sendBroadcast(mediaScanIntent);
                    }

            } else {
                if (filePaths != null && filePaths.size() != 0)
                    for (int i = 0; i < filePaths.size(); i++) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
                        intent.setData(Uri.parse(filePaths.get(i)));
                        sendBroadcast(intent);
                    }
            }

            Intent intentUpdater = new Intent("broadCastDownloadStatus");
            Download_JobService.this.sendBroadcast(intentUpdater);


        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }


    private void showNotificationMessage() {


        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String channelId = "GCAM-Media-channel-01";
        String channelName = "GCAM Media Channel";
        int importance = NotificationManager.IMPORTANCE_LOW;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setSound(null, null);
            mChannel.enableLights(false);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);

        }

        notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(getString(R.string.alert_media_download))
                .setContentText(getString(R.string.alert_preparing_media_download))
                .setGroupSummary(true)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());


    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
