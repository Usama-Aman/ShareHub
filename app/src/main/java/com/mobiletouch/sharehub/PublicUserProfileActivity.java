package com.mobiletouch.sharehub;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.EventDetailsFragment;
import fragments.UsersAttendingFragment;
import fragments.UsersEventFragment;
import models.CreatedEvent;
import models.FollowUserResponseData;
import models.JoinedEvent;
import models.LoginResult;
import models.UserProfileResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;

public class PublicUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity mContext;
    private LinearLayout llMainCode;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout rlMainContent;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private CircleImageView ivProfile;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvContact;
    private TextView tvFollow, tvUnFollow;
    private FrameLayout progressBar;
    private String userId;
    private Bundle bundleUserEvent, bundleUserAttending;
    ArrayList<CreatedEvent> usersEventListData = new ArrayList<CreatedEvent>();
    ArrayList<JoinedEvent> joinedEventsListData = new ArrayList<JoinedEvent>();
    private RelativeLayout rlFollow, rlUnFollow;
    private TextView tvFollowerCount;
    public static int FragmentState;
    String language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_public_user_profile);

        mContext = (AppCompatActivity) this;
        language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));

        getActivityData();
        initToolBar();
        viewInitialize();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(this);
                finish();
                EventDetailsFragment.eventID = EventDetailsFragment.isSaveEventId;
                break;

            case R.id.clMainContent:
                AppUtils.hideKeyboard(this);
                break;

            case R.id.rlFollow:
                rlUnFollow.setVisibility(View.VISIBLE);
                rlFollow.setVisibility(View.GONE);

                if (!AppUtils.isOnline(PublicUserProfileActivity.this)) {

                }
                try {
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("following_id", userId);
                    callFollowUser(paramObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.rlUnFollow:
                rlUnFollow.setVisibility(View.GONE);
                rlFollow.setVisibility(View.VISIBLE);

                if (!AppUtils.isOnline(PublicUserProfileActivity.this)) {

                }
                try {
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("following_id", userId);
                    callFollowUser(paramObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void initSharedPref() {
        LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData().getUserId() != null) {
            //userId = String.valueOf(userModel.getUserData().getUserId());
        }
    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
        }
    }

    // Initialized Views
    private void viewInitialize() {

        rlMainContent = (RelativeLayout) findViewById(R.id.rlMainContent);
        rlFollow = (RelativeLayout) findViewById(R.id.rlFollow);
        rlUnFollow = (RelativeLayout) findViewById(R.id.rlUnFollow);
        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvContact = (TextView) findViewById(R.id.tvContact);
        tvFollowerCount = (TextView) findViewById(R.id.tvFollowerCount);
        tvFollow = (TextView) findViewById(R.id.tvFollow);
        tvUnFollow = (TextView) findViewById(R.id.tvUnFollow);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);

        callCreateEventApiRequest();

        rlFollow.setOnClickListener(this);
        rlMainContent.setOnClickListener(this);
        rlUnFollow.setOnClickListener(this);
    }

    // Initialized ToolBar
    public void initToolBar() {
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        if (language.equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);


        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_profile));

        btnToolbarBack.setOnClickListener(this);
    }

    private void setUpTabLayoutContentData() {

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_users_event)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_users_attending)));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount()));

        // set properties of tab
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // View Pager Adapter
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        //set name of tabs
        private String[] tabTitles = new String[]{getResources().getString(R.string.tv_users_event), getResources().getString(R.string.tv_users_attending)};

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // return fragment to container view
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    UsersEventFragment usersEventFragment = new UsersEventFragment();
                    Bundle args = new Bundle();
                    args.putParcelableArrayList("Users_Event", usersEventListData);
                    usersEventFragment.setArguments(args);
                    return usersEventFragment;
                case 1:
                    UsersAttendingFragment usersAttendingFragment = new UsersAttendingFragment();
                    Bundle args_user = new Bundle();
                    args_user.putParcelableArrayList("Joined_Event", joinedEventsListData);
                    usersAttendingFragment.setArguments(args_user);
                    return usersAttendingFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //btnLogin.setEnabled(!progressVisible);
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void callCreateEventApiRequest() {
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {
            showProgressBar(true);
            getUserProfileData(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Network Call to get Public User profile
    private void getUserProfileData(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getUserProfileData(id, new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, final retrofit2.Response<UserProfileResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData() != null && response.body().getData().getUser() != null) {
                            Picasso.with(mContext)
                                    .load(response.body().getData().getUser().getFullImage())   //
                                    .placeholder(R.drawable.ic_placeholder) // optional
                                    .error(R.drawable.ic_placeholder)         // optional
                                    .into(ivProfile);
                            tvUserName.setText(response.body().getData().getUser().getUserFullname());
                            tvEmail.setText(response.body().getData().getUser().getEmail());
                            String number=response.body().getData().getUser().getUserMobileNumber();
                            if(number.contains("+")){
                                number = number.replace("+","");
                                number=number+"+";
                            }
                            if (language.equalsIgnoreCase("en")){
                                tvContact.setText(response.body().getData().getUser().getUserMobileNumber());
                            }else{
                                tvContact.setText(number);
                            }

                            tvFollowerCount.setText(response.body().getData().getFollowersCount() + getResources().getString(R.string.tv_Followers));
                            if (SharedPreference.isUserLoggedIn(mContext)) {
                                if (response.body().getData().getUser() != null && response.body().getData().getUser().getIs_following() != null &&
                                        response.body().getData().getUser().getIs_following() == 1) {
                                    rlUnFollow.setVisibility(View.VISIBLE);
                                    rlFollow.setVisibility(View.GONE);
                                } else {
                                    rlUnFollow.setVisibility(View.GONE);
                                    rlFollow.setVisibility(View.VISIBLE);
                                }
                            }else{
                                rlUnFollow.setVisibility(View.GONE);
                                rlFollow.setVisibility(View.GONE);
                            }
                        }

                        if (response.body().getData() != null && response.body().getData().getCreatedEvent() != null) {
                            usersEventListData.addAll(response.body().getData().getCreatedEvent());
                        }

                        if (response.body().getData() != null && response.body().getData().getJoinedEvent() != null) {
                            joinedEventsListData.addAll(response.body().getData().getJoinedEvent());
                        }
                        // Initialized Tabs Layout for Events
                        setUpTabLayoutContentData();

                        /*if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownSuccessNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_alert_success),
                                    response.body().getMessage());
                        }*/
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
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
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

    // Network call to follow or unfollow the user
    private void callFollowUser(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.followUserProfile(params, new Callback<FollowUserResponseData>() {
            @Override
            public void onResponse(Call<FollowUserResponseData> call, final retrofit2.Response<FollowUserResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {


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
            public void onFailure(Call<FollowUserResponseData> call, Throwable t) {
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
