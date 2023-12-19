package com.mobiletouch.sharehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import java.util.ArrayList;

import models.PhoneVerifyResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.DialogFactory;

public class VerifyCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText etDigitOne, etDigitTwo, etDigitThree, etDigitFour;
    private ArrayList<String> AllUserData;
    private int count = 25;
    private int mTimerConstant = 1080;
    private CountDownTimer mTimer;
    private String auth_token;
    private AppCompatActivity mContext;
    private ImageView btnToolbarBack;
    private Button btnVerify;
    private TextView tvResend;
    private EditText etCode;
    private LinearLayout llMainContent;
    private String userPhone;
    private TextView tvSecond;
    private TextView tvSecondsHeading;
    private FrameLayout progressBar;
    private String userId;
    private String userName;
    private String userVerifyCode;
    private Toolbar toolBar;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        mContext = this;
        initSharedPref();
        getActivityData();
        viewInitialize();
        setupNewOrderView();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mTimer.cancel();
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivBack:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.btnVerify:
                SendCodeValidation();
                AppUtils.hideKeyboard(this);
                break;

            case R.id.tvResend:
                AppUtils.hideKeyboard(this);
                tvResend.setVisibility(View.GONE);
                tvSecond.setVisibility(View.VISIBLE);
                tvSecondsHeading.setVisibility(View.VISIBLE);
                setupNewOrderView();

                if (!AppUtils.isOnline(mContext)) {
                    DialogFactory.showDropDownNotification(mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_internet_connection));
                    return;
                }
                try {
                    showProgressBar(true);
                    getPhoneVerify(userPhone);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.llMainContent:
                AppUtils.hideKeyboard(this);
                break;
        }
    }

    private void initSharedPref() {
        /*LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData() != null) {
            userId = String.valueOf(userModel.getUserData().getUserId());
        }*/
    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userPhone = bundle.getString("phone");
            userVerifyCode = bundle.getString("code");
        }
    }

    private void viewInitialize() {
        llMainContent = findViewById(R.id.llMainContent);
        btnVerify = findViewById(R.id.btnVerify);
        ivBack = findViewById(R.id.ivBack);
        progressBar = findViewById(R.id.progressBar);
        tvSecondsHeading = findViewById(R.id.tvSecondsHeading);
        tvResend = findViewById(R.id.tvResend);
        tvSecond = findViewById(R.id.tvSecond);
        etDigitOne = findViewById(R.id.et_digit_one);
        /*etDigitOne.setFocusable(true);
        etDigitOne.setFocusableInTouchMode(true);*/
        etDigitTwo = findViewById(R.id.et_digit_two);
        etDigitThree = findViewById(R.id.et_digit_three);
        etDigitFour = findViewById(R.id.et_digit_four);

        final String getDigitOne = etDigitOne.getText().toString().trim();
        final String getDigitTwo = etDigitTwo.getText().toString().trim();
        final String getDigitThree = etDigitThree.getText().toString().trim();
        final String getDigitFour = etDigitFour.getText().toString().trim();

        etDigitFour.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.length() == 1){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitFour, InputMethodManager.RESULT_HIDDEN);
                }else*/
                if (s.length() == 0) {
                    etDigitThree.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitThree, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        //etDigitFour.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        etDigitThree.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    etDigitFour.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitFour, InputMethodManager.SHOW_IMPLICIT);
                } else if (s.length() == 0) {
                    etDigitTwo.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitTwo, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        //etDigitThree.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        etDigitTwo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    etDigitThree.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitThree, InputMethodManager.SHOW_IMPLICIT);
                } else if (s.length() == 0) {
                    etDigitOne.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitOne, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        //etDigitTwo.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        etDigitOne.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    etDigitTwo.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etDigitTwo, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        //etDigitOne.setTransformationMethod(new AsteriskPasswordTransformationMethod());


        etDigitFour.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && etDigitFour.getText().length() == 0) {
                    etDigitThree.requestFocus();
                }
                return false;
            }
        });
        etDigitThree.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && etDigitThree.getText().length() == 0) {
                    etDigitTwo.requestFocus();
                }
                return false;
            }
        });
        etDigitTwo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL && etDigitTwo.getText().length() == 0) {
                    etDigitOne.requestFocus();
                }
                return false;
            }
        });

       /* etDigitOne.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    return true;

                }
                return false;
            }
        });
        etDigitTwo.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    return true;
                }
                return false;
            }
        });
        etDigitThree.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    return true;
                }
                return false;
            }
        });
        etDigitFour.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    return true;
                }
                return false;
            }
        });*/

        llMainContent.setOnClickListener(this);
        tvResend.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
    }

    public void initToolBar() {
        toolBar = findViewById(R.id.toolBar);
        btnToolbarBack = toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        btnToolbarRight = toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setVisibility(View.GONE);
        //tvToolbarTitle.setText(mContext.getString(R.string.tv_create_event));

        btnToolbarBack.setOnClickListener(this);
    }

    public void SendCodeValidation() {
        String getDigitOne = etDigitOne.getText().toString().trim();
        String getDigitTwo = etDigitTwo.getText().toString().trim();
        String getDigitThree = etDigitThree.getText().toString().trim();
        String getDigitFour = etDigitFour.getText().toString().trim();

        if (getDigitOne.length() > 0 && getDigitTwo.length() > 0 && getDigitThree.length() > 0 && getDigitFour.length() > 0) {
            if (userVerifyCode.equals("" + getDigitOne + getDigitTwo + getDigitThree + getDigitFour)) {
                Intent main = new Intent(mContext, SignUpActivity.class);
                main.putExtra("name", userName);
                main.putExtra("code", userVerifyCode);
                main.putExtra("phone", userPhone);
                startActivity(main);
                finish();
            } else {
                DialogFactory.showDropDownNotification(mContext,
                        mContext.getString(R.string.alert_information),
                        "Please enter your valid code");
            }
        } else {
            if (getDigitOne.length() == 0 && getDigitTwo.length() == 0
                    && getDigitThree.length() == 0 && getDigitFour.length() == 0) {
                DialogFactory.showDropDownNotification(mContext,
                        mContext.getString(R.string.alert_information),
                        "Please enter your valid code");
            }
        }
    }

    private boolean validateCode() {
        if (etCode.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(
                    mContext,
                    mContext.getString(R.string.tv_error),
                    mContext.getString(R.string.error_msg_code));
            requestFocus(etCode);
            return false;
        } else {
            //etCode.setFocusable(false);
        }
        return true;
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
                            //SharedPreference.saveLoginDefaults(mContext, response.body().getData());
                            userVerifyCode = String.valueOf(response.body().getData());
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

/*  private void getVerifyCode(String userId, String code) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.verifyCode(userId, code, new Callback<VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<VerifyCodeResponse> call, final retrofit2.Response<VerifyCodeResponse> response) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()!= null && response.body().getStatus()) {
                        mTimer.cancel();
                        SharedPreference.saveLoginDefaults(mContext, response.body().getData());
                        Intent i = new Intent(VerifyCodeActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }else {
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
            public void onFailure(Call<VerifyCodeResponse> call, Throwable t) {
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
    }
*/

    private void setupNewOrderView() {
        mTimer = new CountDownTimer(1000 * mTimerConstant, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvSecond.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                //btnVerify.setText("Resend");
                tvSecond.setVisibility(View.GONE);
                tvSecondsHeading.setVisibility(View.GONE);
                tvResend.setVisibility(View.VISIBLE);
                //setupNewOrderView();
                /*Intent goSendCode = new Intent(VerifyCodeActivity.this, SignInActivity.class);
                startActivity(goSendCode);
                finish();*/
            }
        };
        mTimer.start();
    }


}
