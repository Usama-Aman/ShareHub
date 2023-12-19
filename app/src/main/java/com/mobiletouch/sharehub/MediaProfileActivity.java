package com.mobiletouch.sharehub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import fragments.GalleryProfileFragment;
import fragments.VideoProfileFragment;
import models.EventMediaResponseDataDetail;
import models.GeneralResponse;
import models.UserSingleton;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Confirm_Alert_Dialog;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;

public class MediaProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    PagerAdapter pagerAdapter;

    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    Boolean isFromNotifications = false;
    private AppCompatActivity mContext;

    int event_id = 0;
    int isCreator = 0;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_media_profile);

        mContext = (AppCompatActivity) this;
        registerReceiver(this.broadCastDownloadStatus, new IntentFilter("broadCastDownloadStatus"));
        getActivityData();
        initToolBar();
        initViews();

    }


    //get intent data
    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           event_id = Integer.parseInt(bundle.getString("event_id"));
            isCreator = Integer.parseInt(bundle.getString("isCreator"));

            if (bundle.containsKey("fromNotifications"))
                isFromNotifications = bundle.getBoolean("fromNotifications");
        }
    }

    //show/hide media upload button if current login user is creator of the event
    public boolean getIsCreator() {
        if (isCreator == 0)
            return false;
        else
            return true;
    }

    //show/hide media upload button if current login user is creator of the event
    public void setDeleteVisible(Boolean show) {

        if (getIsCreator()) {
            if (show)
                btnToolbarRight.setVisibility(View.VISIBLE);
            else if (!show)
                btnToolbarRight.setVisibility(View.GONE);

        }
    }


    //toolbar initialization
    public void initToolBar() {

        toolBar = (Toolbar) findViewById(R.id.toolBar);

        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getString(R.string.text_media));

        btnToolbarRight.setImageResource(R.drawable.ic_media_delete);

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }


    //view initialization
    public void initViews() {

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);


        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_gallery)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_video)));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        // set properties of tab
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.getTabAt(0).select();
        //reduceMarginsInTabs(tabLayout,50);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if ((pagerAdapter.getCurrentFragment() instanceof GalleryProfileFragment)) {
                    if (((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).getListSize() != 0) {
                        ((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).resetList();
                        btnToolbarRight.setVisibility(View.GONE);
                    }
                    page = 0;
                } else if ((pagerAdapter.getCurrentFragment() instanceof VideoProfileFragment)) {
                    if (((VideoProfileFragment) pagerAdapter.getCurrentFragment()).getListSize() != 0) {
                        ((VideoProfileFragment) pagerAdapter.getCurrentFragment()).resetList();
                        btnToolbarRight.setVisibility(View.GONE);
                    }
                    page = 1;
                }

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
        private String[] tabTitles = new String[]{getResources().getString(R.string.text_gallery), getResources().getString(R.string.text_video)};

        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

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
                    GalleryProfileFragment fragment = new GalleryProfileFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("event_id", event_id);
                    fragment.setArguments(bundle1);
                    return fragment;
                case 1:

                    VideoProfileFragment videoProfileFragment = new VideoProfileFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("event_id", event_id);
                    videoProfileFragment.setArguments(bundle2);
                    return videoProfileFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                finish();
                break;
            case R.id.btn_toolbar_right://to remove media
                if (page == 0)
                    confirmDeleteMedia(getResources().getString(R.string.tv_delete_from_gallery_list));
                else if (page == 1)
                    confirmDeleteMedia(getResources().getString(R.string.tv_delete_from_video_list));
                break;

        }
    }


    //confirm to delete media
    public void confirmDeleteMedia(String message) {

        Confirm_Alert_Dialog confirmAlertDialog = new Confirm_Alert_Dialog(mContext, message, new Confirm_Alert_Dialog.OnAddClickListener() {
            @Override
            public void onOkClick() {

                if (!AppUtils.isOnline(mContext)) {
                    DialogFactory.showDropDownNotification(mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_internet_connection));
                } else {
                    try {
                        request_delete_media();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        confirmAlertDialog.show();
    }


    @Override
    public void onBackPressed() {

        if ((pagerAdapter.getCurrentFragment() instanceof GalleryProfileFragment)) {

            if (((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).getListSize() != 0) {
                if (((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).getList().get(0).getShowAll())
                    ((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).resetList();
                else
                    super.onBackPressed();
            } else {
                super.onBackPressed();
            }

        } else if ((pagerAdapter.getCurrentFragment() instanceof VideoProfileFragment)) {

            if (((VideoProfileFragment) pagerAdapter.getCurrentFragment()).getListSize() != 0) {
                if (((VideoProfileFragment) pagerAdapter.getCurrentFragment()).getList().get(0).getShowAll())
                    ((VideoProfileFragment) pagerAdapter.getCurrentFragment()).resetList();
                else
                    super.onBackPressed();
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }


    BroadcastReceiver broadCastDownloadStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            DialogFactory.showDropDownSuccessNotification(
                    mContext,
                    getString(R.string.tv_alert_success),
                    getString(R.string.tv_media_download));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (broadCastDownloadStatus != null)
                unregisterReceiver(broadCastDownloadStatus);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }


    /************************************************************************************************/


    private void showProgressBar(final boolean progressVisible) {
        if ((pagerAdapter.getCurrentFragment() instanceof GalleryProfileFragment)) {
            ((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).showProgressBar(progressVisible);
        } else if ((pagerAdapter.getCurrentFragment() instanceof VideoProfileFragment)) {
            ((VideoProfileFragment) pagerAdapter.getCurrentFragment()).showProgressBar(progressVisible);
        }
    }

    private void updateList() {
        if ((pagerAdapter.getCurrentFragment() instanceof GalleryProfileFragment)) {
            ((GalleryProfileFragment) pagerAdapter.getCurrentFragment()).reloadData();
        } else if ((pagerAdapter.getCurrentFragment() instanceof VideoProfileFragment)) {
            ((VideoProfileFragment) pagerAdapter.getCurrentFragment()).reloadData();
        }
    }


    public void request_delete_media() {

        if (UserSingleton.getInstance().getMediaList() != null && UserSingleton.getInstance().getMediaList().size() != 0) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (EventMediaResponseDataDetail media : UserSingleton.getInstance().getMediaList()) {
                if (media.getSelected()) {
                    arrayList.add(media.getEmediaId().toString());
                }
            }

            if (!arrayList.isEmpty()) {
                String mediaIds = android.text.TextUtils.join(",", arrayList);
                JsonObject paramObject = new JsonObject();

                paramObject.addProperty("event_id", event_id);
                paramObject.addProperty("media_id", "[" + mediaIds + "]");
                showProgressBar(true);
                addUsersToGroup(paramObject);
            } else {
                DialogFactory.showDropDownNotification(
                        mContext,
                        mContext.getString(R.string.alert_information),
                        mContext.getResources().getString(R.string.alert_select_media));
            }
        } else {
            DialogFactory.showDropDownNotification(
                    mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getResources().getString(R.string.alert_select_media));
        }

    }


    private void addUsersToGroup(final JsonObject memebers) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.deleteMedia(memebers, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final retrofit2.Response<GeneralResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            updateList();
                            setDeleteVisible(true);
                        }
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
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
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