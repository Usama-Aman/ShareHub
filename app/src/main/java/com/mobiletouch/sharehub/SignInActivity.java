package com.mobiletouch.sharehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketTimeoutException;

import models.SignInResponse;
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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etEmail, etPassword;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SharedPreferences device_token;
    private String android_id;
    private AppCompatActivity mContext;
    private TextView tvForgotPassword;
    private Button btnLogin;
    private String deviceToken;
    private String FCM_Id;
    private ImageView btnToolbarBack;
    private LinearLayout llMainSignIn;
    private FrameLayout progressBar;
    private TextView tvSignInFooter;
    private TextView tvTermsAndConditions;
    private RelativeLayout clkSignUp;
    TextView tvSkip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_signin);

        mContext = this;
        initSharedPref();
        viewInitialize();
        initToolBar();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvForgotPassword:
                Intent forgot = new Intent(SignInActivity.this, ResetPasswordActivity.class);
                startActivity(forgot);
                //finish();
                break;

            case R.id.btnLogin:

                hideKeyboard();
                submitForm();
                break;

            case R.id.btnToolbarBack:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.ll_main_signin:
                AppUtils.hideKeyboard(this);
                break;
            case R.id.tvSkip:
               /* SharedPreference.count = SharedPreference.readSharedPreferenceInt(mContext, "cntSP", "cntKey");
                if (SharedPreference.count == 0) {
                    Intent intent = new Intent();
                    intent.setClass(SignInActivity.this, TemporaryAdsActivity.class);
                    startActivity(intent);
                    SharedPreference.count++;
                    SharedPreference.writeSharedPreference(mContext, SharedPreference.count, "cntSP", "cntKey");
                } else {
                    Intent main = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }*/

                Intent main = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(main);
                finish();

                break;

            case R.id.clkSignUp:
                Intent signUp = new Intent(SignInActivity.this, PhoneVerifyActivity.class);
                startActivity(signUp);
                break;

            case R.id.tvTermsAndConditions:
                Intent terms = new Intent(mContext, ContentPageActivity.class);
                terms.putExtra("pageName", "terms-and-conditions");
                terms.putExtra("pageTitle", mContext.getResources().getString(R.string.tv_termsandcondition));
                startActivity(terms);
                break;
        }
    }

    private void viewInitialize() {
        llMainSignIn = findViewById(R.id.ll_main_signin);
        tvSkip = findViewById(R.id.tvSkip);
        clkSignUp = findViewById(R.id.clkSignUp);
        progressBar = findViewById(R.id.progressBar);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvTermsAndConditions = findViewById(R.id.tvTermsAndConditions);
        tvSignInFooter = findViewById(R.id.tvSignInFooter);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    submitForm();
                    AppUtils.hideKeyboard(mContext);
                    return true;
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(this);
        llMainSignIn.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvTermsAndConditions.setOnClickListener(this);
        tvSkip.setOnClickListener(this);
        clkSignUp.setOnClickListener(this);
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
        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        deviceToken = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, DeviceUuidFactory.DEVICE_ID));
        FCM_Id = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, NotificationConfig.FCM_ID));

        /*Intent main = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(main);
        finish();*/

        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {

            String deviceType = Constants.DEVICE_TYPE;
            String device_token = FCM_Id;
            /*JsonObject paramObject = new JsonObject();
            paramObject.addProperty("user_email",""+);
            paramObject.addProperty("user_password",""+etPassword.getText().toString().trim());*/

            showProgressBar(true);
            getLoginUser(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            DialogFactory.showDropDownNotification(
                    mContext,
                    mContext.getString(R.string.tv_error),
                    mContext.getString(R.string.error_msg_email));
            requestFocus(etEmail);
            return false;
        } else if (!isValidEmail(email)) {
            DialogFactory.showDropDownNotification(
                    mContext,
                    mContext.getString(R.string.tv_error),
                    mContext.getString(R.string.error_msg_valid_email));
            requestFocus(etEmail);
            return false;
        } else {
            //etEmail.setFocusable(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(
                    mContext,
                    mContext.getString(R.string.tv_error),
                    mContext.getString(R.string.error_msg_password));
            requestFocus(etPassword);
            return false;
        } else {
            //etPassword.setFocusable(false);
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

    private void getLoginUser(String email, String password) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.loginUser(email, password, new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call,
                                   final retrofit2.Response<SignInResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()) {
                        if (response != null && response.body() != null && response.body().getData() != null) {

                            SharedPreference.saveLoginDefaults(mContext, response.body().getData());
                            SharedPreference.saveIntegerSharedPrefValue(mContext,
                                    Constants.pref_notification_counter, response.body().getData().getUserData().getUnreadNotifications());
                            Constants.Image = response.body().getData().getUserData().getUserPhoto();
                            SharedPreference.savePrefValue(mContext, "image", Constants.Image);

                            Constants.Name = response.body().getData().getUserData().getUserFullname();
                            SharedPreference.savePrefValue(mContext, "name", Constants.Name);

                            Constants.Phone = response.body().getData().getUserData().getUserMobileNumber();
                            SharedPreference.savePrefValue(mContext, "phone", Constants.Phone);

                            //SharedPreference.saveTotalBookings(mContext, response.body().getData().get(0).getTotalBookings());
                            //SharedPreference.saveBoolSharedPrefValue(mContext, Constants.USER_PHONE_VERIFY, true);
                            SharedPreference.count = SharedPreference.readSharedPreferenceInt(mContext, "cntSP", "cntKey");
                            URLogs.m("Ads counter: " + SharedPreference.count);

                            Intent main = new Intent(SignInActivity.this, ApplicationStartActivity.class);
                            startActivity(main);
                            finish();


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
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                try {
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


}
