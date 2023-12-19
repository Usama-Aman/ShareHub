package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.SocketTimeoutException;

import fragments.StartMapFragment;
import models.GeneralResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.DialogFactory;
import utility.URLogs;

public class ApplicationStartActivity extends AppCompatActivity {
    public static int openId;
    private AppCompatActivity mContext;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_start);

        mContext = (AppCompatActivity) this;
        latLng = AppUtils.getGPSTrackerLocation(mContext);
        Log.d("latlng", latLng.toString());

        openId = 1;
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            updateLocation(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //showAdsActivity();
        Fragment fragment = new StartMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapcontainer, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();


        /**************************************************************
         *  Api call for registering device with push notification.
         **************************************************************/


    }

    private void updateLocation(String lat, String lon) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.updateLocation(lat, lon, new Callback<GeneralResponse>() {

            @Override
            public void onResponse(Call<GeneralResponse> call, final retrofit2.Response<GeneralResponse> response) {
                if (response.isSuccessful()) {

                    Log.d("NAME", "success->");
                    if (response != null && response.body() != null && response.body().getStatus() != null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {


                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            URLogs.m(response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        URLogs.m(mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        URLogs.m(mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                if (t instanceof IOException)
                    URLogs.m(mContext.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    URLogs.m(mContext.getString(R.string.alert_request_timeout));
                else
                    URLogs.m(t.getLocalizedMessage());
            }
        });
    }



}
