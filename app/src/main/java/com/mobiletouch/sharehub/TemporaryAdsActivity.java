package com.mobiletouch.sharehub;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import utility.photoview.PhotoView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import models.AdsResponseData;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.GPSTracker;
import utility.URLogs;

public class TemporaryAdsActivity extends AppCompatActivity {
    Button btClose;
    PhotoView ivAds;
    AppCompatActivity mContext;
    GPSTracker gpsTracker;
    double lat = 0.0;
    double lng = 0.0;
    FrameLayout progressBar;
    Boolean showAds = false;
    String fromDate;
    String toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_ads);
        gpsTracker = new GPSTracker(this);
        mContext = (AppCompatActivity) this;
        lat = gpsTracker.getLatitude();
        lng = gpsTracker.getLongitude();

        URLogs.m("in ads Activity");
        viewInitialize();
        showAds();


    }

    private void viewInitialize() {
        btClose = findViewById(R.id.btClose);
        progressBar = findViewById(R.id.progressBar);
        ivAds = findViewById(R.id.ivPreview);

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startMainActivity();
            }
        });
        ivAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();

            }
        });


    }

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showAds() {
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("latitude", lat);
        paramObject.addProperty("longitude", lng);

        showProgressBar(true);

        callShowAds(paramObject);
    }

    private void callShowAds(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.showAds(params, new Callback<AdsResponseData>() {
            @Override
            public void onResponse(Call<AdsResponseData> call, final Response<AdsResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {

                    if (response.body() != null && response.body().getStatus()) {

                        if (response.body().getData() != null) {
                            if (AppUtils.isSet(response.body().getData().getFullImage())) {

                                showAds = false;
                                fromDate = "";
                                toDate = "";

                                if (AppUtils.isSet(response.body().getData().getValidFrom()))
                                    fromDate = response.body().getData().getValidFrom();

                                if (AppUtils.isSet(response.body().getData().getValidTo()))
                                    toDate = response.body().getData().getValidTo();


                                if (AppUtils.isSet(fromDate) && AppUtils.isSet(toDate)) {
                                    if (AppUtils.validateAdsDate(fromDate, toDate))
                                        showAds = true;
                                } else if (AppUtils.isSet(fromDate) && !AppUtils.isSet(toDate)) {
                                    if (AppUtils.validateAdsStartDate(fromDate))
                                        showAds = true;
                                } else if (!AppUtils.isSet(fromDate) && AppUtils.isSet(toDate)) {
                                    if (AppUtils.validateAdsEndDate(toDate))
                                        showAds = true;
                                }

                                URLogs.m("Successful: " + fromDate + "  ToDate: " + toDate + " showAds: " + showAds);
                                if (showAds) {

                                    Picasso.with(mContext)
                                            .load(response.body().getData().getFullImage())   //
                                            .placeholder(R.drawable.ic_placeholder) // optional
                                            .error(R.drawable.ic_placeholder)         // optional
                                            .into(ivAds);
                                    return;
                                }
                            }
                        }
                    }
                }
                startMainActivity();
            }

            @Override
            public void onFailure(Call<AdsResponseData> call, Throwable t) {
                try {
                    showProgressBar(false);
                    startMainActivity();
                } catch (Exception e) {
                    startMainActivity();
                }
            }
        });

    }

    public void startMainActivity() {

        if (!MainActivity.isActivityOpen) {
            Intent act = new Intent(TemporaryAdsActivity.this, MainActivity.class);
            startActivity(act);
            finish();
        } else
            finish();


    }

}
