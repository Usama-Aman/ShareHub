package utility;

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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;

public class Confirm_Alert_Dialog extends Dialog implements
        View.OnClickListener {

    public Activity mContext;
    public TextView button_add, button_cancel;
    TextView tv_info;
    private OnAddClickListener onAddClickListener;

    private String textInfo;

    public interface OnAddClickListener {
        void onOkClick();
    }

    public Confirm_Alert_Dialog(Activity mContext, String textInfo, OnAddClickListener onAddClickListener) {
        super(mContext);
        this.mContext = mContext;
        this.onAddClickListener = onAddClickListener;
        this.textInfo = textInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(R.layout.confirm_alert);

        setCanceledOnTouchOutside(true);
        this.setCancelable(false);
        dialogSettings(findViewById(R.id.main), 0.20f, 0.80f);


        button_add = (TextView) findViewById(R.id.button_add);
        button_cancel = (TextView) findViewById(R.id.button_cancel);
        tv_info = (TextView) findViewById(R.id.tv_info);

        tv_info.setText(textInfo);
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
                onAddClickListener.onOkClick();
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