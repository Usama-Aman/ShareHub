package com.mobiletouch.sharehub;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import fragments.EventDetailsFragment;
import notifications.NotificationConfig;
import rebus.permissionutils.AskagainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;
import utility.AppUtils;
import utility.Constants;
import utility.FragmentStackPref;
import utility.SharedPreference;
import utility.URLogs;

public class ShareActivity extends AppCompatActivity implements FullCallback {
    private static final String TAG = "ShareActivity";

    boolean comingFromShare = false;
    String event_Id = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static int SPLASH_TIME_OUT = 800; //800;
    private String regId;

    FragmentStackPref fragmentStackPref;

    private void getActivityData() {
        comingFromShare = false;
        event_Id = "";
        comingFromShare();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        fragmentStackPref = new FragmentStackPref(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getActivityData();
        displayFireBaseRegId();
        /**** FCM Push Notification Code *****/
        openActivity();
        /**** FCM Push Notification Code End ****/
        printKeyHash(this);

    }

    public void openActivity() {
        fragmentStackPref.storeSessionFragment("map");

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission()) {
                        PermissionManager.with(ShareActivity.this)
                                .permission(PermissionEnum.ACCESS_FINE_LOCATION, PermissionEnum.WRITE_EXTERNAL_STORAGE,
                                        PermissionEnum.CALL_PHONE, PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.CAMERA)
                                .askagain(true)
                                .askagainCallback(new AskagainCallback() {
                                    @Override
                                    public void showRequestPermission(UserResponse response) {
                                        showDialog(response);
                                    }
                                })
                                .callback(ShareActivity.this)
                                .ask();
                    } else {
                        Intent act = new Intent(ShareActivity.this, MainActivity.class);
                        if (comingFromShare) {
                            act.putExtra("comingFromShare", comingFromShare);
                            act.putExtra("event_id", event_Id);
                        }

                        startActivity(act);
                        finish();
                    }
                } else {

                    Intent act = new Intent(ShareActivity.this, MainActivity.class);
                    if (comingFromShare) {
                        act.putExtra("comingFromShare", comingFromShare);
                        act.putExtra("event_id", event_Id);
                    }
                    startActivity(act);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    boolean onNetIntent = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onNetIntent = true;
        if (intent != null) {
            if (intent.getData() != null) {
                setIntent(intent);
                comingFromShare();
            }
        }
    }

    private void comingFromShare() {

        if (getIntent() != null) {

            try {
                FirebaseDynamicLinks.getInstance()
                        .getDynamicLink(getIntent())
                        .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                            @Override
                            public void onSuccess(PendingDynamicLinkData data) {

                                //Log.e("share.....", "-------");
                                // Get deep link from result (may be null if no link is found)
                                Uri deepLink = null;
                                if (data != null) {
                                    deepLink = data.getLink();
                                }

                                String id = "";
                                if (deepLink != null) {

                                    id = deepLink.getQueryParameter("event_id");
                                }
                                //Log.e("id:  ", "------:" + id);
                                if (AppUtils.isSet(id)) {
                                    comingFromShare = true;
                                    event_Id = id;
                                    // moveToEventDetail(id);
                                }

                                if (onNetIntent) {
                                    Intent act = new Intent(ShareActivity.this, MainActivity.class);
                                    if (comingFromShare) {
                                        act.putExtra("comingFromShare", comingFromShare);
                                        act.putExtra("event_id", event_Id);
                                    }
                                    startActivity(act);
                                    finish();
                                }
                                onNetIntent = false;
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "getDynamicLink:onFailure", e);
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();
            //Retrieving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            //Log.e("Package Name=", context.getApplicationContext().getPackageName());
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                // String key = new String(Base64.encodeBytes(md.digest()));
                //Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            //Log.e("Name not found", e1.toString());

        } catch (NoSuchAlgorithmException e) {
            //Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            //Log.e("Exception", e.toString());
        }
        return key;
    }


    /***************************************
     * Marshmallow Or Above Permissions Code
     ***************************************/
    private boolean checkPermission() {
        PermissionEnum permissionEnum = PermissionEnum.ACCESS_FINE_LOCATION;
        boolean granted = PermissionUtils.isGranted(ShareActivity.this, PermissionEnum.ACCESS_FINE_LOCATION);
        //Toast.makeText(Splash.this, permissionEnum.toString() + " isGranted [" + granted + "]", Toast.LENGTH_SHORT).show();
        return granted == true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(requestCode, permissions, grantResults);
    }

    @Override
    public void result(ArrayList<PermissionEnum> permissionsGranted, ArrayList<PermissionEnum> permissionsDenied, ArrayList<PermissionEnum> permissionsDeniedForever, ArrayList<PermissionEnum> permissionsAsked) {
        List<String> msg = new ArrayList<>();
        for (PermissionEnum permissionEnum : permissionsGranted) {
            msg.add("Location Permission is Granted");
        }
        for (PermissionEnum permissionEnum : permissionsDenied) {
            msg.add(permissionEnum.toString() + " is Denied");
        }
        for (PermissionEnum permissionEnum : permissionsDeniedForever) {
            msg.add(permissionEnum.toString() + " is DeniedForever");
        }
        for (PermissionEnum permissionEnum : permissionsAsked) {
            msg.add(" You asked for ...");
        }
        String[] items = msg.toArray(new String[msg.size()]);

        Intent act = new Intent(ShareActivity.this, MainActivity.class);
        if (comingFromShare) {
            act.putExtra("comingFromShare", comingFromShare);
            act.putExtra("event_id", event_Id);
        }
        startActivity(act);
    }

    private void showDialog(final AskagainCallback.UserResponse response) {
        new AlertDialog.Builder(ShareActivity.this)
                .setTitle("Permission needed")
                .setMessage("This app really need to use this permission, Do you want to authorize it?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        response.result(true);

                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        response.result(false);
                    }
                })
                .show();
    }


    private void displayFireBaseRegId() {
        regId = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, NotificationConfig.FCM_ID));
        URLogs.m("FireBase reg id: " + regId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationConfig.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationConfig.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

}
