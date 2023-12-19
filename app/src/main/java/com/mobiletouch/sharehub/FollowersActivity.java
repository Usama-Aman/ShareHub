package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.FollowersAdapter;
import models.FollowersResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;

/***
 * Fragment to populate list of self user's profile fragment's Following events
 */
public class FollowersActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener  {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout progressBar;
    private TextView tvNoDataFound;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvMyEventList;

    private FollowersAdapter mAdapter;

    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;

    public ArrayList<FollowersResponse.FollowersData> myEventListData = new ArrayList<FollowersResponse.FollowersData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_followers);
        mContext = this;
        initToolBar();
        viewInitialize();
        getFollowers();


    }


    //toolbar initialization
    public void initToolBar() {

        toolBar = (Toolbar) findViewById(R.id.toolBar);

        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.tv_all_followers));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }


    private void viewInitialize() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        rvMyEventList = (RecyclerView) findViewById(R.id.rvProfileEventList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_toolbar_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void getFollowers(){

        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {
            showProgressBar(true);
            callGetFollowers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //refresh data on swipe down to Refresh
    public void updateData(ArrayList<FollowersResponse.FollowersData> dataList) {
        myEventListData.clear();
        myEventListData.addAll(dataList);
        getMyFollowingEventList();
    }


    //API call to get myEvent list
    private void getMyFollowingEventList() {


        if (myEventListData.size() > 0) {

            tvNoDataFound.setVisibility(View.GONE);
            rvMyEventList.setVisibility(View.VISIBLE);

            rvMyEventList.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvMyEventList.setLayoutManager(mLayoutManager);
            rvMyEventList.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new FollowersAdapter(this, myEventListData);
            rvMyEventList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else {

            rvMyEventList.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.VISIBLE);
        }

    }


    //API call on pressing attending or cancel button in list item
    private void callGetFollowers() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getFollowers( new Callback<FollowersResponse>() {
            @Override
            public void onResponse(Call<FollowersResponse> call, final Response<FollowersResponse> response) {


                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        updateData(response.body().getData());
                    } else {
                      if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {

                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<FollowersResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            t.getLocalizedMessage());

            }
        });
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
   getFollowers();
        mSwipeRefreshLayout.setRefreshing(false);
    }

}