package utility;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;

import models.LoginResult;

public class CustomDialogManager extends Dialog implements
        android.view.View.OnClickListener {

    public Activity mContext;
    public Dialog mDialog;
    public Button yes, no;
    private TextView tvClear;
    private TextView tvDone;
    private LinearLayout llLocation;
    private RadioGroup rgGender;
    private RadioGroup rgRating;
    private String currentAddress;
    private TextView tvStateTxt;
    private String userGender;
    private String userRatings;
    private OnDoneClickListener onDoneClickListener;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbMostViewed;
    private RadioButton rbHighestRatings;
    private String userLocation;
    private String stateText;

    public interface OnDoneClickListener {
        void onDone(String paramToSend);
    }

    public CustomDialogManager(Activity mContext, OnDoneClickListener onDoneClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.onDoneClickListener = onDoneClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_alert_layout);

        tvClear = (TextView) findViewById(R.id.tvClear);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvDone.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        tvStateTxt = (TextView) findViewById(R.id.tvStateTxt);
        llLocation = (LinearLayout) findViewById(R.id.llLocation);
        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        rgRating = (RadioGroup) findViewById(R.id.rgRating);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbMostViewed = (RadioButton) findViewById(R.id.rbMostViewed);
        rbHighestRatings = (RadioButton) findViewById(R.id.rbHighestRatings);

        final LoginResult userModel = SharedPreference.getUserDetails(mContext);
        /*if (userModel != null && userModel.getUserData().getUserLocation() != null) {
            userLocation = userModel.getUserData().getUserLocation();
        }*/

        stateText = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, "currentAddress"));
        if (AppUtils.isSet(stateText)) {
            tvStateTxt.setText(stateText);
            tvDone.setEnabled(true);
            tvDone.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }else if (AppUtils.isSet(userLocation)){
            tvStateTxt.setText(userLocation);
            tvDone.setEnabled(true);
            tvDone.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }else {
            tvDone.setEnabled(false);
            tvDone.setTextColor(mContext.getResources().getColor(R.color.colorGray));
        }

        rbMale.setChecked(true);
        if (rbMale.isChecked()){
            userGender = "male";
        }
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.rbFemale:
                        userGender = "female";
                        break;
                    case R.id.rbMale:
                        userGender = "male";
                        break;
                }
            }
        });
        rbHighestRatings.setChecked(true);
        if (rbHighestRatings.isChecked()){
            userRatings = "highRating";
        }
        rgRating.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.rbHighestRatings:
                        userRatings = "highRating";
                        break;
                    case R.id.rbMostViewed:
                        userRatings = "mostView";
                        break;
                }
            }
        });

        llLocation.setOnClickListener(this);
        tvClear.setOnClickListener(this);
        tvDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llLocation:
                /*Intent intent = new Intent(mContext, LocationFilterActivity.class);
                mContext.startActivity(intent);*/
                break;
            case R.id.tvClear:
                SharedPreference.saveSharedPrefValue(mContext, "currentAddress", "");
                dismiss();
                break;
            case R.id.tvDone:
                if (AppUtils.isSet(userGender))
                    SharedPreference.saveSharedPrefValue(mContext, "gender", userGender);
                if (AppUtils.isSet(userRatings))
                    SharedPreference.saveSharedPrefValue(mContext, "ratings", userRatings);
                if (!AppUtils.isSet(stateText))
                    SharedPreference.saveSharedPrefValue(mContext, "currentAddress", userLocation);
                onDoneClickListener.onDone("");
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

}