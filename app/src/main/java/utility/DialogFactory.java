package utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.mobiletouch.sharehub.GroupPeopleListingActivity;
import com.mobiletouch.sharehub.R;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.tapadoo.alerter.OnShowAlertListener;

public final class DialogFactory {

    public static void createSimpleOkDialog(Context context, String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setBackgroundResource(R.color.colorPrimaryDark);
        tv.setPadding(10, 10, 10, 10);
        tv.setGravity(Gravity.CENTER); // this is required to bring it to center.
        tv.setTextSize(20);
        tv.setTextColor(Color.WHITE);
        alertDialog.setCustomTitle(tv);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.CustomAlertDialogStyle;
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnShowListener(
                new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface arg0) {
                                          alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
                                      }
                                  }
        );
        alertDialog.show();
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert).setMessage(message);
        return alertDialog.create();
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

/*
    public static Snackbar showInternetErrorSnackBar(final Activity context, View rootView, Throwable throwable) {
        String message = "You are offline, Please connect!";
        if (throwable !=null){
            message = throwable.getLocalizedMessage();
        }
        Snackbar snack_error = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG); snack_error.show();
        View view = snack_error.getView();
        TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
        if (context != null){
            tv.setTextColor(ContextCompat.getColor(context, R.color.white));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
       */
/* if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setBackgroundColor(context.getColor(R.color.teal));
            }else {
                view.setBackgroundColor(Color.RED);
            }
        }*//*

        */
/*snack_error.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null) {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }
        });*//*

        return snack_error;
    }
*/
   /* public static TSnackbar showInternetErrorSnackBar(final Activity context, View rootView, Throwable throwable) {
        String message = "You are offline, Please connect!";
        if (throwable !=null){
            message = throwable.getLocalizedMessage();
        }
        TSnackbar snackError = TSnackbar.make(rootView, message, TSnackbar.LENGTH_LONG); snackError.show();
        View view = snackError.getView();
        TextView tv = (TextView) view.findViewById(R.id.snackbar_text);
        if (context !=null){
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            //tv.setGravity(Gravity.CENTER_HORIZONTAL);
            snackError.setActionTextColor(Color.parseColor("#00B473"));
        }
        snackError.setAction("Settings", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null) {
                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }
        });
        snackError.setDuration(5000);
        return snackError;
    }

    public static void showSimpleSnackBar(Activity context, View rootView, String message) {
        TSnackbar snackBar = TSnackbar.make(rootView, message, TSnackbar.LENGTH_LONG);
        View snackBarView = snackBar.getView();
        //snackBarView.setBackgroundColor(Color.parseColor("#CC00CC"));
        TextView textView = (TextView) snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        if (context != null){
            textView.setTextColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }else {
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
        snackBar.show();
    }*/

    public static void showDropDownNotification(Activity mContext, String title, String message) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorInt(Color.RED)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_error_white_alert)
                .setIconColorFilter(0)
                .setDuration(2000)
                .show();
    }

    public static void showDropDownSuccessNotification(Activity mContext, String title, String message) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(R.color.colorGreenSuccess)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_tick_white)
                .setIconColorFilter(0)
                .setDuration(2000)
                .show();
    }

    public static void showDropDownSuccessNotificationAndMoveToNextActivity(final Activity mContext, String title, final String message, final String groupID, final String groupName) {
        Alerter.create(mContext)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorInt(Color.GREEN)// or setBackgroundColorInt(Color.CYAN) // setBackgroundColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_tick_white)
                .setIconColorFilter(0)
                .setDuration(2000)
                .setOnShowListener(new OnShowAlertListener() {
                    @Override
                    public void onShow() {
                    }
                })
                .setOnHideListener(new OnHideAlertListener() {
                    @Override
                    public void onHide() {
                        Intent myIntent = new Intent(mContext, GroupPeopleListingActivity.class);
                        myIntent.putExtra("group_id", groupID);
                        myIntent.putExtra("group_name", groupName);
                        mContext.startActivity(myIntent);
                        mContext.finish();
                    }
                })
                .show();
    }
/*
    public static void showDropDownNotification(final Activity mContext, int id, int color, String title, String message) {
        final RelativeLayout rlNotificationLayout = (RelativeLayout) mContext.findViewById(id);
        rlNotificationLayout.setVisibility(View.VISIBLE);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vNotification = inflater.inflate(R.layout.error_messages_layout, null);
        rlNotificationLayout.addView(vNotification);

        LinearLayout bg_layout = (LinearLayout) vNotification.findViewById(R.id.animatedLayout);
        bg_layout.setBackgroundColor(color);

        TextView tv_Message = (TextView) vNotification.findViewById(R.id.tvMessageDetail);
        tv_Message.setText(message);

        TextView tv_title = (TextView) vNotification.findViewById(R.id.tvTitle);
        tv_title.setText(title);

        ImageView notificationImage = (ImageView) vNotification.findViewById(R.id.ivErrorIcon);

        if (tv_title.getText().toString().equalsIgnoreCase(mContext.getString(R.string.tv_error))) {
            notificationImage.setImageResource(R.drawable.ic_del);
        } else {
            notificationImage.setImageResource(R.drawable.ic_check);
        }

        notificationImage.setClickable(true);
        notificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlNotificationLayout.clearAnimation();
                rlNotificationLayout.setVisibility(View.GONE);
            }
        });

        if (vNotification != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Animation slide_down = AnimationUtils.loadAnimation(mContext,
                            R.anim.slide_down);
                    rlNotificationLayout.startAnimation(slide_down);
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (rlNotificationLayout.getVisibility() == View.VISIBLE) {
                                Animation slide_up = AnimationUtils.loadAnimation(mContext,
                                        R.anim.slide_up);
                                rlNotificationLayout.startAnimation(slide_up);
                                rlNotificationLayout.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }, 5000);
        }
    }
*/
}
