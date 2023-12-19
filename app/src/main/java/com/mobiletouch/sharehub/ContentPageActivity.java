package com.mobiletouch.sharehub;


import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketTimeoutException;

import models.ContentPageResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.URLogs;

/**
 * Activity for showing about-us,
 **/

public class ContentPageActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //Webview to load html data from API response
    WebView mWebView;
    private FrameLayout progressBar;
    private TextView tvNoDataFound;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private AppCompatActivity mContext;
    String pageName = "", pageTitle = "";
    String language = "";
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private WebSettings webSettings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contentpage);

        try {
            mContext = this;
            language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
            getActivityData();
            initToolBar();
            viewInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pageTitle = bundle.getString("pageTitle");
            pageName = bundle.getString("pageName");
        }
    }

    //toolbar initialization
    public void initToolBar() {

        toolBar = findViewById(R.id.toolBar);

        btnToolbarBack = toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        btnToolbarRight = toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(pageTitle);


        if (language.equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);


        tvToolbarTitle.setText(pageTitle);
        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }


    //views initialization
    private void viewInitialize() {
        mSwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        progressBar = findViewById(R.id.progressBar);
        mWebView = findViewById(R.id.wv_pp);

        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        tvNoDataFound = findViewById(R.id.tvNoDataFound);

    }


    //get content from server
    private void getContentPage() {
        tvNoDataFound.setVisibility(View.GONE);

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getContentPage(pageName, new Callback<ContentPageResponse>() {

            @Override
            public void onResponse(Call<ContentPageResponse> call, final retrofit2.Response<ContentPageResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        if (language.equalsIgnoreCase("en")) {//if content type is english
                            if (AppUtils.isSet(response.body().getData().getPageContent())) {
                                setContentToWebPage(response.body().getData().getPageContent());
                            }
                        } else if (language.equalsIgnoreCase("ar")) {//if content type is arabic
                            if (AppUtils.isSet(response.body().getData().getPageContentAr())) {
                                setContentToWebPage(response.body().getData().getPageContentAr());
                            }
                        }


                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                    tvNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ContentPageResponse> call, Throwable t) {
                try {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    //set received content to webpage
    public void setContentToWebPage(String content) {
        URLogs.m(content);
        mWebView.loadData(content, "text/html; charset=utf-8", "UTF-8");
        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tvNoDataFound.setVisibility(View.GONE);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(this);
                finish();
                break;
        }
    }

    //swipe to refresh list
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        try {
            showProgressBar(true);
            getContentPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            showProgressBar(true);
            getContentPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}