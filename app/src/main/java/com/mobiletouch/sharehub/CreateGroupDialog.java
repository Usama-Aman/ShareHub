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
 * Dialog to create/update group
 */

public class CreateGroupDialog extends Dialog implements
        View.OnClickListener {

    public Activity mContext;
    public TextView button_add, button_cancel,heading_info;
    EditText et_name;
    private OnAddClickListener onAddClickListener;

    private String groupName;

    public interface OnAddClickListener {
        void onAdd(String paramToSend);
    }

    public CreateGroupDialog(Activity mContext, OnAddClickListener onAddClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.onAddClickListener = onAddClickListener;
    }

    public CreateGroupDialog(Activity mContext, String groupName, OnAddClickListener onAddClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.onAddClickListener = onAddClickListener;
        this.groupName = groupName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.create_group_dialoge);

        setCanceledOnTouchOutside(true);
        this.setCancelable(false);
        dialogSettings(findViewById(R.id.main), 0.30f, 0.90f);

        heading_info = findViewById(R.id.heading_info);
        button_add = findViewById(R.id.button_add);
        button_cancel = findViewById(R.id.button_cancel);
        et_name = findViewById(R.id.et_name);

        if (AppUtils.isSet(groupName)) {
            et_name.setText(groupName);
            button_add.setText(mContext.getString(R.string.tv_update));
            heading_info.setText(mContext.getString(R.string.tv_update_group));
        }

        button_add.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.button_add:
                onAddClickListener.onAdd(et_name.getText().toString());
                dismiss();
                break;
            default:
                break;
        }
    }

    public void dialogSettings(View parentView, float h, float w) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthLcl = (int) (displayMetrics.widthPixels * w);
        int heightLcl = (int) (displayMetrics.heightPixels * h);
        FrameLayout.LayoutParams paramsLcl = (FrameLayout.LayoutParams)
                parentView.getLayoutParams();
        paramsLcl.width = widthLcl;
        paramsLcl.height = heightLcl;
        paramsLcl.gravity = Gravity.CENTER;

        parentView.setLayoutParams(paramsLcl);

        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

}