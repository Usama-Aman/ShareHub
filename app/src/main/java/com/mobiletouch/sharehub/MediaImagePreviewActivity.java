package com.mobiletouch.sharehub;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import utility.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import utility.AppUtils;
import utility.URLs;

/**
 * Activity containing gallery and video fragments
 */
public class MediaImagePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity mContext;
    private PhotoView imageView;
    private ImageView ivDelete;
    private String imageUrl;
    int event_id, media_id;
    private FrameLayout progressBar;
    private TextView btnViewComments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_media_image_preview);

        mContext = (AppCompatActivity) this;
        getActivityData();
        viewInitialize();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    //view initialization
    private void viewInitialize() {
        imageView = (PhotoView) findViewById(R.id.ivPreview);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);
        btnViewComments = findViewById(R.id.button_viewComments);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);
        showProgressBar(true);

        try {
            if (AppUtils.isSet(imageUrl)) {
                Picasso.with(mContext)
                        .load(URLs.BaseUrlEventsMedia + imageUrl)   //
                        .placeholder(R.drawable.ic_placeholder) // optional
                        .error(R.drawable.ic_placeholder)         // optional
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                showProgressBar(false);
                            }

                            @Override
                            public void onError() {
                                // TODO Auto-generated method stub
                                showProgressBar(false);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ivDelete.setOnClickListener(this);
        btnViewComments.setOnClickListener(this);
    }

    //get intent data
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
            case R.id.button_viewComments://view comments on event's image's
                Intent intent = new Intent(mContext, EventCommentsActivity.class);
                intent.putExtra("event_id", Integer.toString(event_id));
                intent.putExtra("media_id", Integer.toString(media_id));
                startActivity(intent);
                break;
        }
    }

    //show progress dialog
    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

}
