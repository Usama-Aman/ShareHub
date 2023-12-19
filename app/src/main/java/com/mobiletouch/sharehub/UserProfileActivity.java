package com.mobiletouch.sharehub;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import fragments.FollowerEventFragment;
import fragments.GroupListingFragment;
import fragments.IAttendingEventFragment;
import fragments.MyEventFragment;
import imageParsing.ImageUtility;
import models.GeneralResponse;
import models.UserAttendingEventResponse;
import models.UserFollowersEventResponse;
import models.UserMyEventResponse;
import models.UserSelfProfileResponse;
import network.ApiClient;
import network.ApiClientMedia;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.FragmentCalls;
import utility.FragmentStackPref;
import utility.RoundedCornersTransform;
import utility.SharedPreference;
import utility.URLs;

/**
 * activity for self user profile with my event, following event and attending events of user
 * and uploading of user profile image
 */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, FragmentCalls {
    ImageView ivProfile;
    ImageButton btnEditProfile;
    TextView tvUserName;
    TextView tvEmail;
    TextView tvContact;
    TextView tvFollowers;
    ImageButton group_bt;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter viewPageAdapter;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private FrameLayout progressBar;
    private String profileImagePath = null;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private Uri imageUri = null;
    private String imagePath = "";
    private ImageUtility imageUtility;
    private ThreadPoolExecutor executor;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    BigInteger followers = new BigInteger("0");
    public ArrayList<UserMyEventResponse> myEventListData = new ArrayList<UserMyEventResponse>();
    public ArrayList<UserFollowersEventResponse> followersEventList = new ArrayList<UserFollowersEventResponse>();
    public ArrayList<UserAttendingEventResponse> attendingEventList = new ArrayList<UserAttendingEventResponse>();

    MyEventFragment myEventFragment;
    FollowerEventFragment followerEventFragment;
    IAttendingEventFragment iAttendingEventFragment;
    private boolean isProfileClicked = false;
    private FragmentStackPref fragmentStackPref;
    String language = "";

    RelativeLayout followerBt;

    public static boolean fromUserProfile = false;
    public static int fromUserProfileStatus = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromUserProfile = true;
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_user_profile);
        fragmentStackPref = new FragmentStackPref(this);
        language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language));

        initToolBar();
        viewInitialize();
        setViewListeners();
        setTabLayoutListeners();

    }

    public void proceedBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.remove(f).commit();
    }

    @Override
    public void onBackPressed() {
//        Fragment a = getSupportFragmentManager().findFragmentById(R.id.container);
//        if (a == null) {
//            super.onBackPressed();
//            finish();
//        } else {
//            proceedBackPressed();
//        }
        fromUserProfile = false;
        fragmentStackPref.storeSessionFragment("settings");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
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
        tvToolbarTitle.setText(getResources().getString(R.string.tv_user_profile_title));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isProfileClicked = bundle.getBoolean("isProfileClicked");
        }
    }

    //view initialization
    private void viewInitialize() {
        imageUtility = new ImageUtility(this);
        followerBt = (RelativeLayout) findViewById(R.id.follower_bt);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvContact = (TextView) findViewById(R.id.tvContact);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        group_bt = (ImageButton) findViewById(R.id.group_bt);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);
        btnEditProfile = (ImageButton) findViewById(R.id.btnEditProfile);

        ((TextView) findViewById(R.id.tv_heading_email)).setText(getResources().getString(R.string.tv_email) + ": ");
        ((TextView) findViewById(R.id.tv_heading_contact)).setText(getResources().getString(R.string.tv_contact) + ": ");

        tabLayout = findViewById(R.id.tabsProfile);
        viewPager = findViewById(R.id.viewPagerSelfEvents);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    public void setViewListeners() {
        followerBt.setOnClickListener(this);
        group_bt.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
    }

    //call to refresh data on swipe down
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        try {
            getProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    //tab layout initialization and applying listeners
    public void setTabLayoutListeners() {

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_myEvents)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_followersEvents)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_attending)));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPageAdapter = new PagerAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(viewPageAdapter);
        // set properties of tab
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    myEventFragment.updateData(myEventListData);
                } else if (tab.getPosition() == 1) {
                    followerEventFragment.updateData(followersEventList);
                } else if (tab.getPosition() == 2) {
                    iAttendingEventFragment.updateData(attendingEventList);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //apply fonts to tab layout
        changeTabsFont();
    }

    //apply custom fonts to tab layout
    private void changeTabsFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(font);
                    ((TextView) tabViewChild).setTextSize((int) getResources().getDimension(R.dimen._315sdp));
                }
            }
        }
    }

    //show progress dialog
    private void showProgressBar(final boolean progressVisible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    //get user profile data
    public void getProfile() {
        try {
            showProgressBar(true);
            getProfileDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfileDetail() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getUserProfileDetail(new Callback<UserSelfProfileResponse>() {
            @Override
            public void onResponse(Call<UserSelfProfileResponse> call, final retrofit2.Response<UserSelfProfileResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        if (AppUtils.isSet(response.body().getData().getUser().getUserPhoto())) {
                            Constants.Image = response.body().getData().getUser().getUserPhoto();
                            SharedPreference.savePrefValue(UserProfileActivity.this, "image", Constants.Image);

                            Picasso.with(UserProfileActivity.this)
                                    .load(URLs.BaseUrlUserImageUsersThumbs + response.body().getData().getUser().getUserPhoto())   //
                                    .transform(new RoundedCornersTransform())
                                    .placeholder(R.drawable.user_placeholder) // optional
                                    .error(R.drawable.user_placeholder)         // optional
                                    .into(ivProfile);
                        }
                        if (AppUtils.isSet(response.body().getData().getUser().getUserFullname())) {
                            Constants.Name = response.body().getData().getUser().getUserFullname();
                            SharedPreference.savePrefValue(UserProfileActivity.this, "name", Constants.Name);

                            tvUserName.setText(AppUtils.capitalize(response.body().getData().getUser().getUserFullname()));
                        }
                        if (AppUtils.isSet(response.body().getData().getUser().getEmail())) {
                            tvEmail.setText(response.body().getData().getUser().getEmail());
                        }
                        if (AppUtils.isSet(response.body().getData().getUser().getUserMobileNumber())) {
                            Constants.Phone = response.body().getData().getUser().getUserMobileNumber();
                            SharedPreference.savePrefValue(UserProfileActivity.this, "phone", Constants.Phone);
                            String number = response.body().getData().getUser().getUserMobileNumber();
                            if (number.contains("+")) {
                                number = number.replace("+", "");
                                number = number + "+";
                            }
                            if (language.equalsIgnoreCase("en")) {
                                tvContact.setText(response.body().getData().getUser().getUserMobileNumber());
                            } else {
                                tvContact.setText(number);
                            }
                        }

                        followers = response.body().getData().getFollowersCount();
                        tvFollowers.setText(followers + " " + getString(R.string.tv_followers));

                        if (response.body().getData().getCreatedEvent().size() > 0) {
                            myEventListData.clear();
                            myEventListData.addAll(response.body().getData().getCreatedEvent());
                        }

                        if (response.body().getData().getFollowersCreatedEvent().size() > 0) {
                            followersEventList.clear();
                            followersEventList.addAll(response.body().getData().getFollowersCreatedEvent());
                        }

                        if (response.body().getData().getJoinedEvent().size() > 0) {
                            attendingEventList.clear();
                            attendingEventList.addAll(response.body().getData().getJoinedEvent());
                        }

                        if (viewPageAdapter.getCurrentFragment() instanceof MyEventFragment) {
                            ((MyEventFragment) viewPageAdapter.getCurrentFragment()).updateData(myEventListData);
                        } else if (viewPageAdapter.getCurrentFragment() instanceof FollowerEventFragment) {
                            ((FollowerEventFragment) viewPageAdapter.getCurrentFragment()).updateData(followersEventList);
                        } else if (viewPageAdapter.getCurrentFragment() instanceof IAttendingEventFragment) {
                            ((IAttendingEventFragment) viewPageAdapter.getCurrentFragment()).updateData(attendingEventList);
                        }

                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    UserProfileActivity.this,
                                    getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                UserProfileActivity.this,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                UserProfileActivity.this,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<UserSelfProfileResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            UserProfileActivity.this,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            UserProfileActivity.this,
                            getString(R.string.alert_information),
                            getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            UserProfileActivity.this,
                            getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    //My event fragment
    //Following event fragment
    //Attending event fragment
    // View Pager Adapter
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        //set name of tabs
        private String[] tabTitles = new String[]{getResources().getString(R.string.tv_myEvents), getResources().getString(R.string.tv_followersEvents), getResources().getString(R.string.tv_attending)};


        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
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
                    myEventFragment = new MyEventFragment();
                    return myEventFragment;
                case 1:
                    followerEventFragment = new FollowerEventFragment();
                    return followerEventFragment;
                case 2:
                    iAttendingEventFragment = new IAttendingEventFragment();
                    return iAttendingEventFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        // To update fragment in ViewPager, we should override getItemPosition() method,
        // in this method, we call the fragment's public updating method.
        public int getItemPosition(Object object) {


            if (object instanceof MyEventFragment) {
                ((MyEventFragment) object).updateData(myEventListData);
            } else if (object instanceof FollowerEventFragment) {
                ((FollowerEventFragment) object).updateData(followersEventList);
            } else if (object instanceof IAttendingEventFragment) {
                ((IAttendingEventFragment) object).updateData(attendingEventList);
            }
            return super.getItemPosition(object);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follower_bt:
                Intent intentFollower = new Intent(this, FollowersActivity.class);
                startActivity(intentFollower);
                break;
            case R.id.btn_toolbar_back:
                onBackPressed();
                break;
            case R.id.group_bt:
                GroupListingFragment groupsFragment = new GroupListingFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                fragmentTransaction
                        .replace(R.id.container, groupsFragment).commit();
                break;
            case R.id.ivProfile:
                selectImageWithRealQuality();
                break;
            case R.id.btnEditProfile:
                Intent intent = new Intent(this, UserProfileEditActivity.class);
                startActivity(intent);
                break;
        }
    }

    /*********************************************************
     * Image selection with original quality also no cropping
     *********************************************************/
    private void selectImageWithRealQuality() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.image_upload_bottomsheet_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView tvCamera = (TextView) sheetView.findViewById(R.id.tvOpenCamera);
        TextView tvGallery = (TextView) sheetView.findViewById(R.id.tvGallery);
        Button btnCancel = (Button) sheetView.findViewById(R.id.btnCancel);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();

                openCamera();
            }
        });

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();

                openGallery();
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

    //Gallery image selection
    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    //Camera Capturing
    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, R.string.app_name);
        values.put(MediaStore.Images.Media.DESCRIPTION, this.getPackageName());
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        imagePath = imageUtility.getImagePath(selectedImage, this);
                        Bitmap bitmap = imageUtility.uriToBitmap(selectedImage);
                        ivProfile.setImageBitmap(bitmap);
//                        Picasso.with(this)
//                                .load(selectedImage)   //
//                                .transform(new RoundedCornersTransform())
//                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
//                                .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
//                                .into(ivProfile);

                        profileImagePath = imagePath;

                        try {
                            showProgressBar(true);
                            requestUpdateImageApiCall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver‌​(), imageUri);
                        InputStream image_stream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
                        imagePath = imageUtility.getImagePath(imageUri, this);
                        imagePath = AppUtils.compressImage(imagePath);
                        //bitmap = updateOrientation(imagePath, bitmap);
                        imagePath = saveImage(bitmap, 100);
                        ivProfile.setImageBitmap(bitmap);
//                        Picasso.with(this)
//                                .load("file:" + imagePath)   //
//                                .transform(new RoundedCornersTransform())
//                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
//                                .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
//                                .into(ivProfile);
                        profileImagePath = imagePath;
                        try {
                            showProgressBar(true);
                            requestUpdateImageApiCall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    /******************************************
     * Save image -> converting bitmap into file
     ******************************************/
    public String saveImage(Bitmap bitmap, int quality) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + File.separator + "IMG_User_GCAM_" + getCurrentDate() + ".jpg";
        File f = new File(imagePath);
        f.createNewFile();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void requestUpdateImageApiCall() {
        if (!AppUtils.isOnline(this)) {
            DialogFactory.showDropDownNotification(this,
                    getString(R.string.alert_information),
                    getString(R.string.alert_internet_connection));
        }
        try {

            if (AppUtils.isSet(profileImagePath)) {
                profileImagePath = AppUtils.compressImage(profileImagePath);
                final RequestBody image = RequestBody.create(okhttp3.MediaType.parse("image/*"), new File(profileImagePath));

                executor = new ThreadPoolExecutor(
                        5,
                        10,
                        30,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>()
                );
                executor.allowCoreThreadTimeOut(true);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            showProgressBar(true);
                            uploadImageCall(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                DialogFactory.showDropDownNotification(
                        UserProfileActivity.this,
                        getString(R.string.tv_error),
                        getString(R.string.alert_select_image));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //API call to upload image on server
    private void uploadImageCall(RequestBody image) {
        ApiClientMedia apiClient = ApiClientMedia.getInstance();
        apiClient.uploadImage(image, new Callback<GeneralResponse>() {
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
                            AppUtils.showToast(UserProfileActivity.this, response.body().getMessage());
                            getProfile();
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    UserProfileActivity.this,
                                    getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                UserProfileActivity.this,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                UserProfileActivity.this,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
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
                            UserProfileActivity.this,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            UserProfileActivity.this,
                            getString(R.string.alert_information),
                            getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            UserProfileActivity.this,
                            getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    //saving image with dateTime stamp
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        fromUserProfile = true;
        getProfile();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fromUserProfile = false;
    }

    @Override
    public void showDialog(Boolean isShow) {
        showProgressBar(isShow);
    }

}