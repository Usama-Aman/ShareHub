package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import models.GeneralResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;

/**
 * Fragment for Contact form
 **/


public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtMessage;
    private Button button_submit;
    private FrameLayout progressBar;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private AppCompatActivity mContext;
    String language = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        mContext = this;
        language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        viewInitialize();
        initSharedPref();
    }



    private void initSharedPref() {
    /*   LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData().getUserId() != null) {
            userId = userModel.getUserData().getUserId();
        }*/
    }

    public void initToolBar() {

        toolBar = findViewById(R.id.toolBar);

        btnToolbarBack = toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        btnToolbarRight = toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.tv_contactUS));


        if (language.equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);


        tvToolbarTitle.setText(getResources().getString(R.string.tv_contactUS));
        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    private void viewInitialize() {

        progressBar = findViewById(R.id.progressBar);
        mEtName = findViewById(R.id.etName);
        mEtEmail = findViewById(R.id.etEmail);
        mEtMessage = findViewById(R.id.etMessage);
        button_submit = findViewById(R.id.buttonSubmit);

        mEtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    validate();
                    return true;
                }
                return false;
            }
        });

        button_submit.setOnClickListener(this);

    }

    /**
     * method to submit/POST data to contact us API
     **/
    private void validate() {

        try {

            if (!validateNameORMessage(mEtName))
                return;

            if (!validateEmail())
                return;

            if (!validateNameORMessage(mEtMessage))
                return;

            try {

                JsonObject paramObject = new JsonObject();

                paramObject.addProperty("user_name", "" + mEtName.getText().toString().trim());
                paramObject.addProperty("user_email", "" + mEtEmail.getText().toString().trim());
                paramObject.addProperty("user_message", "" + mEtMessage.getText().toString().trim());

                showProgressBar(true);
                sendContactInformation(paramObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void sendContactInformation(JsonObject params) {

       ApiClient apiClient = ApiClient.getInstance();
        apiClient.contactUs(params, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call,
                                   final retrofit2.Response<GeneralResponse> response) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownSuccessNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_alert_success),
                                    response.body().getMessage());
                        }
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
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                AppUtils.hideKeyboard(this);
                validate();
                break;
            case R.id.btn_toolbar_back:
                onBackPressed();
                finish();
                break;

        }
    }


    private boolean validateEmail() {
        String email = mEtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            DialogFactory.showDropDownNotification(
                    this,
                    this.getString(R.string.tv_error),
                    this.getString(R.string.error_msg_email));
            requestFocus(mEtEmail);
            return false;
        } else if (!isValidEmail(email)) {
            DialogFactory.showDropDownNotification(
                    this,
                    this.getString(R.string.tv_error),
                    this.getString(R.string.error_msg_valid_email));
            requestFocus(mEtEmail);
            return false;
        }
        return true;
    }


    private boolean validateNameORMessage(EditText et) {
        String etText = et.getText().toString().trim();
        if (etText.isEmpty()) {
            DialogFactory.showDropDownNotification(
                    this,
                    this.getString(R.string.tv_error),
                    getString(R.string.error_msg_field_required));
            requestFocus(et);
            return false;
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


    private void showProgressBar(final boolean progressVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }
}
