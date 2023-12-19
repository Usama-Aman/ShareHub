package com.mobiletouch.sharehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketTimeoutException;

import adapters.ReportSpinnerAdapter;
import models.PhoneVerifyResponse;
import network.ApiClient;
import notifications.DeviceUuidFactory;
import notifications.NotificationConfig;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.URLogs;

public class PhoneVerifyActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPhone;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SharedPreferences device_token;
    private String android_id;
    private AppCompatActivity mContext;
    private TextView tvForgotPassword;
    private Button btnSignUp;
    private String deviceToken;
    private String FCM_Id;
    private ImageView btnToolbarBack;
    private LinearLayout llMainPhone, clkSignIn;
    private FrameLayout progressBar;
    private TextView tvSignInFooter;
    private TextView tvTermsAndConditions;
    private TextView spCountryCode;
    private ReportSpinnerAdapter mAdapter;
    public String selectedCountryItem;
    private ImageView ivBack;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_phone_verify);


        initSharedPref();
        viewInitialize();
        mContext = this;
        selectedCountryItem = getIntent().getStringExtra("code");
        if (selectedCountryItem != null) {
            spCountryCode.setText(selectedCountryItem);
        } else {
            selectedCountryItem = "+966";
            spCountryCode.setText("+966");

        }
        initToolBar();
    }

    @Override
    public void onBackPressed() {

        Intent main = new Intent(PhoneVerifyActivity.this, SignInActivity.class);
        startActivity(main);
        finish();
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSignUp:
                hideKeyboard();
                submitForm();
                break;

            case R.id.btnToolbarBack:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.ll_main_phone:
                AppUtils.hideKeyboard(this);
                break;

            case R.id.clkSignIn:
                onBackPressed();

                break;

        }
    }

    private void viewInitialize() {
        llMainPhone = findViewById(R.id.ll_main_phone);
        clkSignIn = findViewById(R.id.clkSignIn);
        progressBar = findViewById(R.id.progressBar);
        tvSignInFooter = findViewById(R.id.tvLogInRed);
        btnSignUp = findViewById(R.id.btnSignUp);
        etPhone = findViewById(R.id.etPhone);

        spCountryCode = findViewById(R.id.spinnerCountryCode);


        spCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CountriesList.class);
                startActivity(i);
            }
        });

        btnSignUp.setOnClickListener(this);
        llMainPhone.setOnClickListener(this);
        clkSignIn.setOnClickListener(this);
    }

    public void initToolBar() {
        //btnToolbarBack = (ImageView) findViewById(R.id.btnToolbarBack);
        //btnToolbarBack.setOnClickListener(this);
    }

    private void initSharedPref() {
        pref = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        editor = pref.edit();
    }

    private void submitForm() {


        if (!validatePhone()) {
            return;
        }

        deviceToken = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, DeviceUuidFactory.DEVICE_ID));
        FCM_Id = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, NotificationConfig.FCM_ID));

        /*Intent main = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(main);
        finish();*/

       /* if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }*/
        try {

            String deviceType = Constants.DEVICE_TYPE;
            String device_token = FCM_Id;
            String mobile = selectedCountryItem + etPhone.getText().toString().trim();
            showProgressBar(true);
            getPhoneVerify(mobile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean validatePhone() {
        if (etPhone.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_phone));
            requestFocus(etPhone);
            return false;
        } else {
            //etPhone.setFocusable(false);
        }
        return true;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

    private void getPhoneVerify(String phone) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.phoneVerify(phone, new Callback<PhoneVerifyResponse>() {
            @Override
            public void onResponse(Call<PhoneVerifyResponse> call,
                                   final retrofit2.Response<PhoneVerifyResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()) {
                        if (response != null && response.body() != null && response.body().getData() != null) {

                            Intent main = new Intent(mContext, VerifyCodeActivity.class);
                            main.putExtra("code", String.valueOf(response.body().getData()));
                            main.putExtra("phone", selectedCountryItem + etPhone.getText().toString().trim());
                            startActivity(main);

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
            public void onFailure(Call<PhoneVerifyResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                URLogs.m(t.getMessage());
           /*     Crashlytics.log("SignUp");
                Crashlytics.logException(t);*/
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
}
