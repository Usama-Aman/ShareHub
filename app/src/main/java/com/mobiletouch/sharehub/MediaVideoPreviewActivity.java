package com.mobiletouch.sharehub;


import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import utility.AppUtils;
import utility.URLs;

/***
 * Activity to preview full view of video with move to comments button
 */
public class MediaVideoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity mContext;
    private VideoView videoView;
    private ImageView ivDelete;
    private String imageUrl;
    private FrameLayout progressBar;
    private TextView btnViewComments;
    int event_id, media_id;

    private int position = 0;
    private MediaController mediaControls;

    int videoWidth;
    int videoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_media_video_preview);

        mContext = (AppCompatActivity) this;
        getActivityData();
        viewInitialize();
     //   getVideoAspectRatio();

    }

    private void getVideoAspectRatio() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this, Uri.parse(imageUrl));
        String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        videoWidth = Integer.parseInt(width);
        videoHeight = Integer.parseInt(height);
    }

    private boolean isVideoLandscaped() {
        if (videoWidth > videoHeight) {
            return true;
        } else return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    private void viewInitialize() {

        videoView = (VideoView) findViewById(R.id.videoView);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);
        btnViewComments = findViewById(R.id.button_viewComments);


        showProgressBar(true);

        // set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(mContext);
        }


        try {
            if (AppUtils.isSet(imageUrl)) {

                Uri uri = Uri.parse(URLs.BaseUrlEventsMedia + imageUrl);
                videoView.setVideoURI(uri);
                // set the media controller in the VideoView
                videoView.setMediaController(mediaControls);
                videoView.start();
                videoView.requestFocus();

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        showProgressBar(false);
                        return true;
                    }
                });

                // we also set an setOnPreparedListener in order to know when the video
                // file is ready for playback

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // if we have a position on savedInstanceState, the video
                        // playback should start from here
                        videoView.seekTo(position);
                        if (position == 0) {
                            videoView.start();
                        } else {
                            // if we come from a resumed activity, video playback will
                            // be paused
                            videoView.pause();
                        }

                       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        mediaPlayer.start();
                        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                           int arg2) {
                                // TODO Auto-generated method stub
                                showProgressBar(false);
                                mp.start();
                            }
                        });
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ivDelete.setOnClickListener(this);
        btnViewComments.setOnClickListener(this);
    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imageUrl = bundle.getString("url");
            event_id = bundle.getInt("event_id");
            media_id = bundle.getInt("media_id");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivDelete:
                finish();
                break;
            case R.id.button_viewComments:
                Intent intent = new Intent(mContext, EventCommentsActivity.class);
                intent.putExtra("event_id", Integer.toString(event_id));
                intent.putExtra("media_id", Integer.toString(media_id));
                startActivity(intent);
                break;
        }
    }


    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onStop() {
        if (null != videoView) {
            videoView.stopPlayback();
        }
        super.onStop();
    }

}
