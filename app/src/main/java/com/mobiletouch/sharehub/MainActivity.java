package com.mobiletouch.sharehub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ThreadPoolExecutor;

import adapters.EventsListAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import fragments.CreateEventFragment;
import fragments.EventDetailsFragment;
import fragments.EventsFragment;
import fragments.MapFragment;
import fragments.NotificationFragment;
import fragments.SettingsFragment;
import me.grantland.widget.AutofitTextView;
import models.CheckDataModel;
import models.LoginResult;
import models.MapLocationResponse;
import network.ApiClient;
import notifications.NotificationConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.FragmentStackPref;
import utility.SharedPreference;
import utility.URLogs;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    int currentPosition = 0;
    private Adapter adapter;
    private String goToAppoinment;
    private RelativeLayout rl_main_activity;
    private ImageView btn_topbar_back;
    private ImageButton btn_topbar_search;
    private TextView tv_topbar_title, btn_topbar_close;
    private String catId;
    private String plan;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    GoogleApiClient googleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationManager locationManager;
    private GoogleMap gMap;
    private ThreadPoolExecutor executor;
    private AutoCompleteTextView etSearch;
    private LatLng mLocation;
    private MapFragment mapFragment;
    private static double latitude, longitude;
    /* Lahore = 31.5546° N, 74.3572° E*/
    private PlacePicker.IntentBuilder builder;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private String language;
    private TextView tvGoBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private AppCompatActivity mContext;
    private static final String TAG = "MainActivity";
    private static MainActivity instance;
    private static long back_pressed;
    private DrawerLayout drawerLayout;
    private Toolbar toolBar;
    private NavigationView navigationView;
    private String userId, userName, userEmail, userPicture, userAuthToken;
    private String isPhoneVerify;
    private String userStatus;
    private TextView tvUserName;
    private ImageView ivUserImage;
    private TextView tvToolbarClose;
    private String skipCard;
    private ImageView btnToolbarBack;
    private String notifyType, notifyMessage, notifyPostData, notifyBookingCount;
    private ConstraintLayout clMainProfile;
    //private StatusSpinnerAdapter mAdapter;
    private AppCompatSpinner spStatus;
    private TextView tvAddress;
    private String userLocation;
    private String selectedItem;
    private int mStatus = 0;
    private String userType;
    private String FCM_Id;
    private String regId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int bookingCount = 0;
    public static CircleImageView tabHome;
    AutofitTextView tvNotificationBadge;
    FragmentStackPref fragmentStackPref;
    public static Boolean isActivityOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_main);
        URLogs.m("Main onCreate");
        fragmentStackPref = new FragmentStackPref(this);
        EventsListAdapter.fromEventListAdapter = false;
        EventsListAdapter.fromState = "";
        ApplicationStartActivity.openId = 0;
        mContext = this;
        initialize();
        setupTabs();
        deepLinkClick();
        registerBroadcastReceiver();
        tabHome.setEnabled(true);
        CallGetEvents();
        getActivityData();
        isActivityOpen = true;
        //sendPushNotification();
        showAdsActivity();


    }


    public void deepLinkClick() {
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            String[] separated = uri.split("=");
            if (separated.length > 1) {
                EventDetailsFragment Frag = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putString("event_id", separated[1]);
                args.putString("isSelfProfile", "0");
                Frag.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.container, Frag, "Fragment").addToBackStack(null).commit();

            }
            Log.i("MyApp", "Deep link clicked " + uri);
        }
    }

    private void setCustomTab(TabLayout tabLayout, int index, String title, int icon) {
        TextView tab_home = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tab_home.setText(title);
        tab_home.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
        tabLayout.getTabAt(index).setCustomView(tab_home);
    }

    private void initialize() {
        tabLayout = findViewById(R.id.tabs);
        tabHome = findViewById(R.id.ivHome);

        // Api call for push notification.
        /*if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            String deviceToken = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, DeviceUuidFactory.DEVICE_ID));
            FCM_Id = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, NotificationConfig.FCM_ID));
            *//*if (SharedPreference.isUserLoggedIn(mContext) ) {
                final LoginResult user = SharedPreference.getUserDetails(mContext);
                if (user != null && user.getUserData() != null) {
                    userId = user.getUserData().getUserId();
                }
            }*//*
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("user_id", "" + userId);
            paramObject.addProperty("device_token", "" + FCM_Id);
            paramObject.addProperty("device_type", "Android");
            paramObject.addProperty("app_mode", "test");
            //Constants.showDialog("Loading...", mContext);
            //saveDeviceTokenForPush(paramObject);
        } catch (Exception e) {e.printStackTrace();}*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallGetEvents();
        // FCM Broadcast Receiver Registration
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationConfig.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationConfig.PUSH_NOTIFICATION));
        // NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    //This method is being used by request, alert, and profile tabs to return to first tab using back button
    public void setTab() {
        tabLayout.getTabAt(0).select();
    }

    private void replaceFragment(int position) {
        //Log.e("replaceFragment: ", "-----");
        UserProfileActivity.fromUserProfile = false;
        UserProfileActivity.fromUserProfileStatus = -1;
        EventsListAdapter.fromEventListAdapter = false;
        EventsListAdapter.fromState = "";
        if (position == 0) {

            EventsFragment eventsFragment = new EventsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, eventsFragment).commit();
        } else if (position == 1) {
            if (SharedPreference.isUserLoggedIn(mContext)) {
                CreateEventFragment createEventFragment = new CreateEventFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, createEventFragment).commit();
            } else {
                Intent act = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(act);
                finish();
            }
        } else if (position == 2) {

        } else if (position == 3) {
            if (SharedPreference.isUserLoggedIn(mContext)) {
                NotificationFragment notificationFragment = new NotificationFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, notificationFragment).commit();
            } else {
                Intent act = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(act);
                finish();
            }
        } else if (position == 4) {
            if (SharedPreference.isUserLoggedIn(mContext)) {
                SettingsFragment settingsFragment = new SettingsFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, settingsFragment).commit();
            } else {
                Intent act = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(act);
                finish();
            }
        }
    }

    public void openHomeFragment(View b) {
        EventsListAdapter.fromEventListAdapter = false;
        EventsListAdapter.fromState = "";
        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mapFragment).commitAllowingStateLoss();

        tabLayout.getTabAt(2).select();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_events)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_create_event)));
        tabLayout.addTab(tabLayout.newTab().setText(""));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_notification)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_settings)));

        RelativeLayout tabOne = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ((TextView) tabOne.getChildAt(1)).setText(getString(R.string.tab_events));
        ((ImageView) tabOne.getChildAt(0)).setImageResource(R.drawable.rb_events_selector);
        tabLayout.getTabAt(0).setCustomView(tabOne);


        RelativeLayout tabTwo = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ((TextView) tabTwo.getChildAt(1)).setText(getString(R.string.tab_create_event));
        ((ImageView) tabTwo.getChildAt(0)).setImageResource(R.drawable.rb_create_event_selector);

        tabLayout.getTabAt(1).setCustomView(tabTwo);

        RelativeLayout tabThree = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ((TextView) tabThree.getChildAt(1)).setText("");
        tabLayout.getTabAt(2).setCustomView(tabThree);

        RelativeLayout tabFour = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ((TextView) tabFour.getChildAt(1)).setText(getString(R.string.tab_notification));
        ((ImageView) tabFour.getChildAt(0)).setImageResource(R.drawable.rb_notification_selector);

        tvNotificationBadge = ((AutofitTextView) tabFour.getChildAt(2));
        tvNotificationBadge.setVisibility(View.VISIBLE);
        //Log.e("value ntif..", "..." + SharedPreference.getIntSharedPrefValue(mContext, Constants.pref_notification_counter, 0));
        if (SharedPreference.getIntSharedPrefValue(this, Constants.pref_notification_counter, 0) != 0) {
            tvNotificationBadge.setVisibility(View.VISIBLE);
            tvNotificationBadge.setText("" + SharedPreference.getIntSharedPrefValue(mContext, Constants.pref_notification_counter, 0));
        } else
            tvNotificationBadge.setVisibility(View.GONE);

        //tvNotificationBadge.setText("123456");

        tabLayout.getTabAt(3).setCustomView(tabFour);

        RelativeLayout tabFive = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ((TextView) tabFive.getChildAt(1)).setText(getString(R.string.tab_settings));
        ((ImageView) tabFive.getChildAt(0)).setImageResource(R.drawable.rb_settings_selector);

        tabLayout.getTabAt(4).setCustomView(tabFive);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                replaceFragment(tabLayout.getSelectedTabPosition());
                currentPosition = tabLayout.getSelectedTabPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                replaceFragment(tabLayout.getSelectedTabPosition());
                currentPosition = tabLayout.getSelectedTabPosition();
            }
        });

        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {

            if (fragmentStackPref.getFragmentStack().equalsIgnoreCase("event")) {
                EventsFragment eventsFragment = new EventsFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, eventsFragment).commitAllowingStateLoss();
                tabLayout.getTabAt(0).select();
            } else if (fragmentStackPref.getFragmentStack().equalsIgnoreCase("notification")) {

                NotificationFragment notificationFragment = new NotificationFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, notificationFragment).commitAllowingStateLoss();
                tabLayout.getTabAt(3).select();
            } else if (fragmentStackPref.getFragmentStack().equalsIgnoreCase("settings")) {

                SettingsFragment settingsFragment = new SettingsFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, settingsFragment).commitAllowingStateLoss();
                tabLayout.getTabAt(4).select();
            } else {

                MapFragment mapFragment = new MapFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, mapFragment).commitAllowingStateLoss();
                tabLayout.getTabAt(2).select();
            }
        }
    }


    private void openNotification() {
        int user_id = 0;
        int event_id = 0;
        String notificationType = "";
        int from_user = 0;
        int media_id = 0;
        Boolean islike = false;
        String mediaType = new String();
        String media_url = new String();
        try {
            JSONObject obj = new JSONObject(notifyPostData);
            if (obj != null) {
                JSONObject postData = null;
                postData = obj.getJSONObject("obj");
                user_id = postData.getInt("to_user_id");
                try {
                    JSONObject notificationData = new JSONObject(postData.getString("notification_data"));
                    event_id = notificationData.getInt("event_id");
                    if (notificationData.has("media")) {
                        JSONObject mediaData = new JSONObject(notificationData.getString("media"));
                        mediaType = mediaData.getString("emedia_type");
                        media_id = mediaData.getInt("emedia_id");
//                        islike = false;
                        media_url = mediaData.getString("fullImage");
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                from_user = postData.getInt("from_user_id");
                notificationType = postData.getString("notification_type");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (AppUtils.isSet(notificationType)) {
            if (notificationType.equalsIgnoreCase("follow") || notificationType.equalsIgnoreCase("attend")) {
                Intent intent = new Intent(mContext, PublicUserProfileActivity.class);
                intent.putExtra("userId", Integer.toString(from_user));
                mContext.startActivity(intent);
            } else if (notificationType.equalsIgnoreCase("requestToAttend") || notificationType.equalsIgnoreCase("AdminNotification")) {
                tabLayout.getTabAt(3).select();
                replaceFragment(3);
                currentPosition = 3;
            } else if (notificationType.equalsIgnoreCase("comment") ||
                    notificationType.equalsIgnoreCase("uploadMedia") || notificationType.equalsIgnoreCase("media_like")) {
                if (SharedPreference.isUserLoggedIn(mContext) && SharedPreference.getUserDetails(mContext) != null) {
                    LoginResult userModel = SharedPreference.getUserDetails(mContext);
                    if (userModel.getUserData().getUserId().equals(user_id)) {
                        CheckDataModel.isSelf = "1";
                    } else {
                        CheckDataModel.isSelf = "0";
                    }
                }
                Intent intent;
                if (mediaType.equalsIgnoreCase("video")) {
                    intent = new Intent(mContext, EventVideoCommentsActivity.class);
                    intent.putExtra("event_id", event_id);
                    intent.putExtra("media_id", media_id);
                    intent.putExtra("url", media_url);
                    intent.putExtra("islike", false);
                } else {
                    intent = new Intent(mContext, EventCommentsActivity.class);
                    intent.putExtra("event_id", event_id);
                    intent.putExtra("media_id", media_id);
                    intent.putExtra("url", media_url);
                    intent.putExtra("islike", false);
                    mContext.startActivity(intent);
                }
            } else {
                EventDetailsFragment optionsFrag = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putString("event_id", Integer.toString(event_id));
                args.putString("isSelfProfile", "1");
                optionsFrag.setArguments(args);
                mContext.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, optionsFrag, "OptionsFragment")
                        .addToBackStack(null).commit();
            }
        }
    }


    public void launchNewFragment(Fragment frg, int containerId) {
        updateTab(frg, containerId, true);
    }

    private void updateTab(Fragment fragment, int placeholder, boolean addToBackStack) {
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (ft != null) {
                ft.replace(placeholder, fragment);
                if (addToBackStack)
                    ft.addToBackStack(null);
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTitle(String title) {
        //instance.tv_topbar_title.setText(title);
        // this.im.setImageResource(imgae);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void logoutBottomSheet() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
        View sheetView = mContext.getLayoutInflater().inflate(R.layout.logout_bottomsheet_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView tvLogOut = sheetView.findViewById(R.id.tvLogOut);
        Button btnCancel = sheetView.findViewById(R.id.btnCancel);
        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();

                if (!AppUtils.isOnline(mContext)) {
                    DialogFactory.showDropDownNotification(mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_internet_connection));
                    return;
                }
                try {
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("user_id", "" + userId);
                    Constants.showDialog("Loading...", mContext);
                    //setLogoutTheUser(paramObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete code here;
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Do something

            }
        });
        mBottomSheetDialog.show();
    }

    // FCM Push Notification Broadcast Receivers
    private void registerBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationConfig.REGISTRATION_COMPLETE)) {
                    // fcm successfully registered now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationConfig.TOPIC_GLOBAL);
                    displayFireBaseRegId();
                } else if (intent.getAction().equals(NotificationConfig.PUSH_NOTIFICATION)) {
                    /****** new push notification is received *****/
                    if (SharedPreference.getIntSharedPrefValue(mContext, Constants.pref_notification_counter, 0) != 0) {
                        tvNotificationBadge.setVisibility(View.VISIBLE);
                        tvNotificationBadge.setText("" + SharedPreference.getIntSharedPrefValue(mContext, Constants.pref_notification_counter, 0));
                    } else
                        tvNotificationBadge.setVisibility(View.GONE);                /*    notifyMessage = intent.getStringExtra("message");
                    notifyPostData = intent.getStringExtra("data");
                    openNotification();*/
                }
            }
        };
    }

    private void displayFireBaseRegId() {
        regId = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, NotificationConfig.FCM_ID));
        URLogs.m("FireBase reg id: " + regId);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationConfig.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationConfig.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }*/

    @Override
    protected void onPause() {
        URLogs.m("Main Pausing");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        URLogs.m("Main Stops");
        //
        handler.removeCallbacks(r);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        URLogs.m("Main onDestroy");
        MainActivity.isActivityOpen = false;
        SharedPreference.saveSortingSettings(this, null);
        super.onDestroy();
    }


    //Call Api to get map pointers
    public void CallGetEvents() {

        if (ApplicationStartActivity.openId == 0) {
        }
        if (!AppUtils.isOnline(this)) {
            DialogFactory.showDropDownNotification(this,
                    this.getString(R.string.alert_information),
                    this.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            GetEventsPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetEventsPoints() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsMarkerData(new Callback<MapLocationResponse>() {
            @Override
            public void onResponse(Call<MapLocationResponse> call, Response<MapLocationResponse> response) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        MapFragment.EventListDataMap.clear();
                        MapFragment.EventListDataMap.addAll(response.body().getData());
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    MainActivity.this,
                                    MainActivity.this.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                MainActivity.this,
                                MainActivity.this.getString(R.string.alert_information),
                                MainActivity.this.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                MainActivity.this,
                                MainActivity.this.getString(R.string.alert_information),
                                MainActivity.this.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<MapLocationResponse> call, Throwable t) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            MainActivity.this,
                            MainActivity.this.getString(R.string.alert_information),
                            MainActivity.this.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            MainActivity.this,
                            MainActivity.this.getString(R.string.alert_information),
                            MainActivity.this.getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            MainActivity.this,
                            MainActivity.this.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }


   /* private void getStatus(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getStatus(params, new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, final retrofit2.Response<StatusResponse> response) {
                try {Constants.dismissDialog();} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()!= null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownSuccessNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_success),
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
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                try {
                    Constants.dismissDialog();} catch (Exception e) {e.printStackTrace();}
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
    }*/

    /*private void setLogoutTheUser(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.setLogoutTheUser(params, new Callback<PushNotifyResponse>() {
            @Override
            public void onResponse(Call<PushNotifyResponse> call, final retrofit2.Response<PushNotifyResponse> response) {
                try {Constants.dismissDialog();} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()!= null && response.body().getStatus()) {

                        if (Constants.FB_IS_LOGIN) {
                            LoginManager.getInstance().logOut();
                            Constants.FB_IS_LOGIN = false;
                        }

                        FragmentManager fm = getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        //removeLogOutButton();
                        SharedPreference.logoutDefaults(mContext);
                        //launchNewFragment(new FrgDashboardMap(), R.id.content_frame);
                        Intent signIn = new Intent(MainActivity.this, SplashLoginActivity.class);
                        signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(signIn);
                        finishAffinity();

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
            public void onFailure(Call<PushNotifyResponse> call, Throwable t) {
                try {
                    Constants.dismissDialog();} catch (Exception e) {e.printStackTrace();}
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
    }*/

   /* private void saveDeviceTokenForPush(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.saveDeviceTokenForPush(params, new Callback<PushNotifyResponse>() {
            @Override
            public void onResponse(Call<PushNotifyResponse> call, final retrofit2.Response<PushNotifyResponse> response) {
                try {Constants.dismissDialog();} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response != null && response.body() != null && response.body().getStatus()!= null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            *//*DialogFactory.showDropDownSuccessNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_success),
                                    response.body().getMessage());*//*
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
            public void onFailure(Call<PushNotifyResponse> call, Throwable t) {
                try {
                    Constants.dismissDialog();} catch (Exception e) {e.printStackTrace();}
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

    Handler handler;

    public void showAdsActivity() {

        handler = new Handler();
        SharedPreference.count = SharedPreference.readSharedPreferenceInt(mContext, "cntSP", "cntKey");
        URLogs.m("Ads counter:Main  " + SharedPreference.count);
        if (SharedPreference.count == 0) {
            handler.postDelayed(
                    r, 20000);

        }

    }

    Runnable r = new Runnable() {
        public void run() {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, TemporaryAdsActivity.class);
            startActivity(intent);
            SharedPreference.count++;
            SharedPreference.writeSharedPreference(mContext, SharedPreference.count, "cntSP", "cntKey");
        }
    };

    private void getActivityData() {

        if (SharedPreference.getIntSharedPrefValue(this, Constants.pref_notification_counter, 0) != 0) {
            tvNotificationBadge.setVisibility(View.VISIBLE);
            tvNotificationBadge.setText("" + SharedPreference.getIntSharedPrefValue(mContext, Constants.pref_notification_counter, 0));
        } else
            tvNotificationBadge.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            /*****************************************************************
             * Notifications bundle data from MyFireBaseMessagingService class
             *****************************************************************/
            notifyMessage = bundle.getString("message");
            notifyPostData = bundle.getString("data");
            openNotification();
        }
        comingFromShare();
        // onNewIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

   /*     if (intent != null) {
            if (intent.getData() != null) {
                setIntent(intent);
                comingFromShare();
            }
        }*/
    }

    private void comingFromShare() {

        if (getIntent() != null) {

            try {
                if (getIntent().hasExtra("comingFromShare")) {
                    if (getIntent().getBooleanExtra("comingFromShare", false)) {
                        if (getIntent().hasExtra("event_id")) {
                            if (getIntent().getStringExtra("event_id") != null) {
                                if (AppUtils.isSet(getIntent().getStringExtra("event_id"))) {
                                    moveToEventDetail(getIntent().getStringExtra("event_id"));
                                }
                            }
                        }
                    }
                }

            /*    FirebaseDynamicLinks.getInstance()
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

                                    moveToEventDetail(id);
                                }

                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "getDynamicLink:onFailure", e);
                            }
                        });
*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void moveToEventDetail(String eventID) {
        AppUtils.hideKeyboard(this);
        EventDetailsFragment.isSaveEventId = eventID;

        EventDetailsFragment optionsFrag = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventID);
        args.putString("isSelfProfile", "eventList");
        optionsFrag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, optionsFrag, "OptionsFragment")
                .addToBackStack(null).commit();
    }
}
