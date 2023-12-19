package com.mobiletouch.sharehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.ReportSpinnerAdapter;
import models.SignUpResponse;
import network.ApiClient;
import notifications.DeviceUuidFactory;
import notifications.NotificationConfig;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etUserName, etEmail, etPassword, etConfirmPassword, etPhone;
    private TextView tv_signup, btn_sigup;
    private ImageView iv_fb;
    private CheckBox ch_signup;
    private LinearLayout ll_agreement, ll_signin;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox ch_terms;
    private String deviceToken, FCM_Id;
    private ArrayList<String> AllUserData;
    boolean termsAndconditions = false;
    private TextView tv_continue, tv_topbar_title;
    private AppCompatActivity mContext;
    private LinearLayout ll_main_signup;
    private ImageView btn_topbar_back, btn_topbar_search;
    private TextView btn_topbar_close;
    /*private ArrayList<UserModel> userData;*/
    private TextView tvTerms;
    private TextView tvPolicy;
    private String language;
    private TextView tvGoBack;
    private Button btnSignUp;
    private TextInputLayout firstNameWrapper;
    private TextInputLayout lastNameWrapper;
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;
    private TextView tvRegisterInstructions;
    private TextView tvTermsAndConditions;
    private TextView tvLogIn;
    private TextInputLayout confirmPasswordWrapper;
    private ImageView btnToolbarBack;
    private AppCompatSpinner spGender;
    private ReportSpinnerAdapter mAdapter;
    private CheckBox cbHairStylist;
    private CheckBox cbUser;
    private EditText etCountryCode;
    private LinearLayout llMainSignUp;
    private String selectedItem;
    private AppCompatSpinner spCountryCode;
    private String selectedCountryItem;
    private FrameLayout progressBar;
    private String userCode, userName, userPhone;
    private ImageView ivBack;
    private RelativeLayout clkSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_sign_up);

        mContext = this;
        getActivityData();
        initSharedPref();
        viewInitialize();

    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userPhone = bundle.getString("phone");
            userCode = bundle.getString("code");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void viewInitialize() {

        llMainSignUp = findViewById(R.id.llMainSignUp);
        progressBar = findViewById(R.id.progressBar);
        btnSignUp = findViewById(R.id.btnSignUp);
        etName = findViewById(R.id.etName);
        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivBack = findViewById(R.id.ivBack);
        //etPhone = (EditText) findViewById(R.id.etPhone);

        tvLogIn = findViewById(R.id.tvLogInRed);
        clkSignIn = findViewById(R.id.clkSignIn);
        //spCountryCode = (AppCompatSpinner) findViewById(R.id.spinnerCountryCode);

        // Spinner Drop down elements
        /*ArrayList<String> countryCode = new ArrayList<String>();
        //genders.add("Select Code");
        countryCode.add("+92");
        countryCode.add("+961");

        mAdapter = new ReportSpinnerAdapter(mContext, countryCode);
        spCountryCode.setAdapter(mAdapter);

        spCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountryItem = (String) adapterView.getItemAtPosition(i);
                //Toast.makeText(mContext, "Your selected gender " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        llMainSignUp.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        clkSignIn.setOnClickListener(this);
    }

    private void initSharedPref() {
        pref = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        editor = pref.edit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.clkSignIn:
                Intent signIn = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(signIn);
                //finish();
                break;

            case R.id.btnSignUp:
                hideKeyboard();
                submitForm();
                //((MainActivity) getActivity()).launchNewFragment(new FrgMenuItm(), R.id.content_frame);
                break;

            case R.id.ivBack:
                hideKeyboard();
                Intent main = new Intent(SignUpActivity.this, SignInActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(main);
                finish();
                //((MainActivity) getActivity()).launchNewFragment(new FrgMenuItm(), R.id.content_frame);
                break;

            case R.id.btnToolbarBack:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.llMainSignUp:
                AppUtils.hideKeyboard(this);
                break;

        }
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validateUserName()) {
            return;
        }
        if (!validateName()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (!validateConfirmPassword()) {
            return;
        }

        deviceToken = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, DeviceUuidFactory.DEVICE_ID));
        FCM_Id = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, NotificationConfig.FCM_ID));

        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            String deviceType = Constants.DEVICE_TYPE;
            String device_token = FCM_Id;
            userName = etUserName.getText().toString().trim();
            String username = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String passwordConfirm = etConfirmPassword.getText().toString().trim();
            String mobile = userPhone;

            showProgressBar(true);
            getRegisterTheUser(userName, email, password, passwordConfirm, mobile, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_name));
            requestFocus(etName);
            return false;
        } else {
            //etName.setFocusable(false);
        }
        return true;
    }

    private boolean validateUserName() {
        if (etUserName.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_name));
            requestFocus(etUserName);
            return false;
        } else {
            //etName.setFocusable(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_email));
            requestFocus(etEmail);
            return false;
        } else if (!isValidEmail(email)) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_valid_email));
            requestFocus(etEmail);
            return false;
        } else {
            //etEmail.setFocusable(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_password));
            requestFocus(etPassword);
            return false;
        } else if (etPassword.getText().toString().length() < 6) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_password_limit));
            requestFocus(etPassword);
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        if (etConfirmPassword.getText().toString().trim().isEmpty()) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_password));
            requestFocus(etConfirmPassword);
            return false;
        } else if (etConfirmPassword.getText().toString().trim().length() < 6) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_password_limit));
            requestFocus(etConfirmPassword);
            return false;
        } else if (!etConfirmPassword.getText().toString().trim().equals(etPassword.getText().toString().trim())) {
            DialogFactory.showDropDownNotification(mContext, mContext.getString(R.string.tv_error), mContext.getString(R.string.error_msg_password_not_match));
            requestFocus(etConfirmPassword);
            return false;
        }
        return true;
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

    private void getRegisterTheUser(String fullname, String email, String password, String passConfirm, String mobile, String username) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.registerUser(fullname, email, password, passConfirm, mobile, username, new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, final retrofit2.Response<SignUpResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        AppUtils.showToast(mContext, "" + response.body().getMessage());
                        Constants.Image = response.body().getData().getUserData().getUserPhoto();
                        SharedPreference.savePrefValue(mContext, "image", Constants.Image);

                        Constants.Name = response.body().getData().getUserData().getUserFullname();
                        SharedPreference.savePrefValue(mContext, "name", Constants.Name);

                        Constants.Phone = response.body().getData().getUserData().getUserMobileNumber();
                        SharedPreference.savePrefValue(mContext, "phone", Constants.Phone);


                        SharedPreference.saveLoginDefaults(mContext, response.body().getData());
                        SharedPreference.saveIntegerSharedPrefValue(mContext,
                                Constants.pref_notification_counter, response.body().getData().getUserData().getUnreadNotifications());

                        Intent main = new Intent(SignUpActivity.this, ApplicationStartActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                        finishAffinity();

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
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*Crashlytics.log("SignUp");
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