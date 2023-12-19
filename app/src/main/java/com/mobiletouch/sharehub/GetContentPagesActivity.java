package com.mobiletouch.sharehub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import utility.AppUtils;

public class GetContentPagesActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity mContext;
    private ImageView btnToolbarRight, btnToolbarBack;
    private TextView tvToolbarTitle;
    private Toolbar toolBar;
    private TextView tvToolbarClose;
    private AutoCompleteTextView etToolbarSearch;
    private String userId;
    private String hairstylistId;
    private WebView myWebView;
    private WebSettings webSettings;
    private String page;
    private FrameLayout progressBar;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_and_policy);

        mContext = (AppCompatActivity) this;
        getActivityData();
        //initSharedPref();
        initToolBar();
        viewInitialize();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void viewInitialize() {
        myWebView = (WebView) findViewById(R.id.myWebView);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);

        String str = "myWebView.loadData(response.body().getResult().getPageDescription(), \"text/html; charset=utf-8\", \"UTF-8\");\n" +
                "                            //myWebView.setWebViewClient(new MyWebViewClient());\n" +
                "                            webSettings = myWebView.getSettings();\n" +
                "                            webSettings.setJavaScriptEnabled(true);";
        myWebView.loadData(str, "text/html; charset=utf-8", "UTF-8");
        //myWebView.setWebViewClient(new MyWebViewClient());
        webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        /*if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("page_sef", "" + page);
            showProgressBar(true);
            //getContentPages(paramObject);
        } catch (Exception e) {e.printStackTrace();}*/
    }

    public void initToolBar() {
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        etToolbarSearch = (AutoCompleteTextView) toolBar.findViewById(R.id.et_toolbar_search);
        etToolbarSearch.setVisibility(View.GONE);
        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setVisibility(View.VISIBLE);

        if (AppUtils.isSet(name)){
            tvToolbarTitle.setText(name);
        }
        tvToolbarClose = (TextView) toolBar.findViewById(R.id.btn_toolbar_close);
        tvToolbarClose.setVisibility(View.GONE);

        btnToolbarBack.setOnClickListener(this);
    }

   /* private void initSharedPref() {
        LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData().getUserId() != null) {
            userId = userModel.getUserData().getUserId();
        }
    }*/

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            page = bundle.getString("page");
            name = bundle.getString("name");
        }
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

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    /*private void getContentPages(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getContentPages(params, new Callback<GetContentPagesResponse>() {
            @Override
            public void onResponse(Call<GetContentPagesResponse> call, final retrofit2.Response<GetContentPagesResponse> response) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (AppUtils.isSet(response.body().getResult().getPageDescription())) {
                            myWebView.loadData(response.body().getResult().getPageDescription(), "text/html; charset=utf-8", "UTF-8");
                            //myWebView.setWebViewClient(new MyWebViewClient());
                            webSettings = myWebView.getSettings();
                            webSettings.setJavaScriptEnabled(true);
                        }
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
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<GetContentPagesResponse> call, Throwable t) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
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
    }*/

    // Use When the user clicks a link from a web page in your WebView
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            /*if (Uri.parse(url).getHost().equals(Urls.BaseUrlPrivacyPolicy)) {
                return false;
            }*/
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
}
