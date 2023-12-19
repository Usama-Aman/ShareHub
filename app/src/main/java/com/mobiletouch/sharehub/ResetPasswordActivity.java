package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.net.SocketTimeoutException;

import models.ForgotResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.DialogFactory;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name, et_email, et_password;
    private RelativeLayout ll_main_forgot;
    private AppCompatActivity mContext;
    private String language;
    private ImageView btnToolbarBack;
    private EditText etEmail;
    private TextView tvForgotInstruction;
    private Button btnResetPassword;
    private TextInputLayout emailWrapper;
    private LinearLayout llMainForgot;
    private FrameLayout progressBar;
    private Toolbar toolBar;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_reset_password);

        mContext = (AppCompatActivity) this;
        viewInitialize();
        initToolBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void viewInitialize() {
        llMainForgot = (LinearLayout) findViewById(R.id.llMainForgot);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);

        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    submitForm();
                    AppUtils.hideKeyboard(mContext);
                    return true;
                }
                return false;
            }
        });

        llMainForgot.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
    }

    public void initToolBar() {
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_forgot_pass));

        btnToolbarBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnResetPassword:
                AppUtils.hideKeyboard(mContext);
                submitForm();
                break;

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.llMainForgot:
                AppUtils.hideKeyboard(this);
                break;

        }
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            String email = etEmail.getText().toString().trim();

            showProgressBar(true);
            forgotPassword(email);
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etEmail:
                    validateEmail();
                    break;
            }
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

    private void forgotPassword(String email) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.forgotPassword(email, new Callback<ForgotResponse>() {
            @Override
            public void onResponse(Call<ForgotResponse> call, final retrofit2.Response<ForgotResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()) {

                        Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage() + "OK");
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
            public void onFailure(Call<ForgotResponse> call, Throwable t) {
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

