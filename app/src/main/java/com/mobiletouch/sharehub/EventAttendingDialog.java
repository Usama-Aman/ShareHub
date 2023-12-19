package com.mobiletouch.sharehub;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import utility.AppUtils;

/**
 * Created by aamir on 6/5/2018.
 */

public class EventAttendingDialog extends Dialog implements
        View.OnClickListener {

    public Activity mContext;
    public TextView btnSubmit, btnCancel, tvPinCodeHeading;
    EditText etPinCode;
    private OnAddClickListener onAddClickListener;

    private String groupName, tvTitle;

    public interface OnAddClickListener {
        void onAdd(String paramToSend);
    }

    public EventAttendingDialog(Activity mContext, OnAddClickListener onAddClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.onAddClickListener = onAddClickListener;
    }

    public EventAttendingDialog(Activity mContext, String tvTitle, OnAddClickListener onAddClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.onAddClickListener = onAddClickListener;
        this.groupName = groupName;
        this.tvTitle = tvTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.user_attending_dialog);
        setCanceledOnTouchOutside(true);
        this.setCancelable(false);
        dialogSettings(findViewById(R.id.clMainContent), 0.30f, 0.90f);

        tvPinCodeHeading = (TextView) findViewById(R.id.tvPinCodeHeading);
        if (AppUtils.isSet(tvTitle))
            tvPinCodeHeading.setText(tvTitle);
        btnSubmit = (TextView) findViewById(R.id.btnSubmit);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        etPinCode = (EditText) findViewById(R.id.etPinCode);

        /*if (AppUtils.isSet(groupName)) {
            et_name.setText(groupName);
            button_add.setText(mContext.getString(R.string.tv_update));
            heading_info.setText(mContext.getString(R.string.tv_update_group));
        }*/

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                dismiss();
                break;
            case R.id.btnSubmit:
                onAddClickListener.onAdd(etPinCode.getText().toString());
                dismiss();
                break;
            default:
                break;
        }
    }

    public void dialogSettings(View parentView, float h, float w) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels * w);
        int heightLcl = (int) (displayMetrics.heightPixels * h);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams) parentView.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.height = heightLcl;
        paramsLcl.gravity = Gravity.CENTER;
        parentView.setLayoutParams(paramsLcl);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}