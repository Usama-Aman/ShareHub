package com.mobiletouch.sharehub;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import adapters.PlacesAutoCompleteAdapter;
import adapters.ReportSpinnerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import imageParsing.ImageUtility;
import models.CategoriesListResponse;
import models.CategoryModel;
import models.CreateEventResponse;
import models.CreatedEvent;
import models.Event;
import models.GroupsModel;
import models.JoinedEvent;
import models.LoginResult;
import models.UserDetail;
import network.ApiClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.URLogs;

public class UpdateEventActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

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
    private String userId;
    private Bundle bundleUserEvent, bundleUserAttending;
    ArrayList<CreatedEvent> usersEventListData = new ArrayList<CreatedEvent>();
    ArrayList<JoinedEvent> joinedEventsListData = new ArrayList<JoinedEvent>();
    private RelativeLayout rlFollow;
    private TextView tvFollowerCount;
    Event eventListData;
    private FrameLayout progressBar;
    private ConstraintLayout clMainContent;
    private EditText etStartDate;
    private EditText etStartTime;
    private EditText etEndDate;
    private EditText etEndTime;
    private int mDay;
    private int mMonth;
    private int mYear;
    private String mDate;
    private boolean isStartDateClicked = false;
    private boolean isStartTimeClicked = false;
    private EditText etChooseFile;

    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private final int PIC_CROP = 1;
    private Uri imageUri = null;
    private String imagePath = "";
    private ImageUtility imageUtility;
    private ArrayList<String> imageList = new ArrayList<String>();
    private ArrayList<String> imagesListData = new ArrayList<String>();
    private static int imageListCounter = 0;
    StringBuilder imageResult = new StringBuilder();
    private String profileImagePath = null;
    private TextView tvNoFileChosen;

    private GoogleMap mMap;
    private String currentAddress;
    private SupportMapFragment mapFragment;

    GoogleApiClient mGoogleApiClient;
    // GoogleApiClient googleApiClient;
    Location mLastLocation;
    private ThreadPoolExecutor executor;
    private LatLng mLocation;
    private static double latitude, longitude;
    /* Lahore = 31.5546° N, 74.3572° E*/
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private AutoCompleteTextView etSearch;
    private AppCompatSpinner spinnerCategory;
    private ReportSpinnerAdapter mAdapter;
    ArrayList<String> category = new ArrayList<String>();
    ArrayList<CategoryModel> categoryListData = new ArrayList<CategoryModel>();
    ArrayList<UserDetail> usersListData = new ArrayList<UserDetail>();
    ArrayList<UserDetail> usersSelectedListData = new ArrayList<UserDetail>();
    ArrayList<GroupsModel> groupsListData = new ArrayList<GroupsModel>();
    private String selectedCategoryItem, selectedCategoryItemId;
    private Button btnAddPeople;
    private Button btnCreate;
    private EditText etDescription, etEventTitle;
    private EditText etDetail;
    private SwitchCompat scComments;
    private SwitchCompat switchApproval;
    private SwitchCompat switchPinCode;
    private EditText etPinCode;
    private LinearLayout llApprovalRequired;
    private LinearLayout llPinCode;
    private PlacesClient placesClient;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);

        setContentView(R.layout.activity_update_event);

        mContext = (AppCompatActivity) this;


        String apiKey = getString(R.string.google_maps_key);

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);

        initSharedPref();
        getActivityData();
        initToolBar();
        viewInitialize();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void initSharedPref() {
        LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData().getUserId() != null) {
            userId = String.valueOf(userModel.getUserData().getUserId());
        }
    }

    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            eventListData = bundle.getParcelable("eventListData");
        }
    }

    private void setActivityDataToFields() {
        if (eventListData != null) {

            llApprovalRequired.setVisibility(View.GONE);
            switchApproval.setChecked(false);
            llPinCode.setVisibility(View.GONE);
            switchPinCode.setChecked(false);
            etPinCode.setVisibility(View.GONE);


            URLogs.m("ISPrivateEvent:" + eventListData.getEventIsprivate());
            if (eventListData.getEventIsprivate() != null)
                if (eventListData.getEventIsprivate() == 1) {
                    if (eventListData.getEventIsApprovalRequired() != null && eventListData.getEventIsApprovalRequired().toString().equals("1")) {
                        llApprovalRequired.setVisibility(View.VISIBLE);
                        switchApproval.setChecked(true);
                    } else {
                        llApprovalRequired.setVisibility(View.VISIBLE);
                        switchApproval.setChecked(false);
                    }


                    URLogs.m("Check if pinCode required:" + eventListData.getEventIsPincodeRequired());

                    if (eventListData.getEventIsPincodeRequired() != null && eventListData.getEventIsPincodeRequired().toString().equals("1")) {
                        llPinCode.setVisibility(View.VISIBLE);
                        switchPinCode.setChecked(true);
                        etPinCode.setVisibility(View.VISIBLE);
                        if (AppUtils.isSet(String.valueOf(eventListData.getEventPincode())))
                            etPinCode.setText(eventListData.getEventPincode().toString());
                    } else {
                        llPinCode.setVisibility(View.VISIBLE);
                        switchPinCode.setChecked(false);
                        etPinCode.setVisibility(View.GONE);
                    }
                }


            if (AppUtils.isSet(eventListData.getEventTitle()))
                etEventTitle.setText(eventListData.getEventTitle());
            if (AppUtils.isSet(eventListData.getEventDescription()))
                etDescription.setText(eventListData.getEventDescription());
            if (AppUtils.isSet(eventListData.getEventStartDate()))
                etStartDate.setText(eventListData.getEventStartDate());
            if (AppUtils.isSet(eventListData.getEventStartTime()))
                etStartTime.setText(AppUtils.parseTimeReverse(eventListData.getEventStartTime()));
            if (AppUtils.isSet(eventListData.getEventCoverphoto()))
                tvNoFileChosen.setText(eventListData.getEventCoverphoto());
            if (AppUtils.isSet(eventListData.getEventLocation())) {
                etSearch.setText(eventListData.getEventLocation());
                etSearch.setSelection(etSearch.getText().length());
                etSearch.setFocusableInTouchMode(false);
                etSearch.setFocusable(false);
                URLogs.m("Getting Locations:" + eventListData.getEventLocation());
                new GetLatLongAsync(mContext, eventListData.getEventLocation()).execute();
            }
            if (AppUtils.isSet(eventListData.getEventDetails()))
                etDetail.setText(eventListData.getEventDetails());
            if (eventListData.getEventIscommentsAllowed() != null && eventListData.getEventIscommentsAllowed() == 1) {
                scComments.setChecked(true);
            } else {
                scComments.setChecked(false);
            }
        }
    }

    private void viewInitialize() {
        imageUtility = new ImageUtility(mContext);
        clMainContent = (ConstraintLayout) findViewById(R.id.clMainContent);
        llApprovalRequired = (LinearLayout) findViewById(R.id.llApprovalRequired);
        llPinCode = (LinearLayout) findViewById(R.id.llPinCode);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);

        etEventTitle = (EditText) findViewById(R.id.etEventTitle);
        etPinCode = (EditText) findViewById(R.id.etPinCode);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etDetail = (EditText) findViewById(R.id.etDetail);
        scComments = (SwitchCompat) findViewById(R.id.switchComments);
        switchApproval = (SwitchCompat) findViewById(R.id.switchApproval);
        switchPinCode = (SwitchCompat) findViewById(R.id.switchPinCode);

        switchPinCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    etPinCode.setVisibility(View.VISIBLE);
                } else {
                    etPinCode.setVisibility(View.GONE);
                }
            }
        });

        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etStartDate.setClickable(true);
        etStartDate.setFocusable(false);
        etStartDate.setFocusableInTouchMode(false);
        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etStartTime.setClickable(true);
        etStartTime.setFocusable(false);
        etStartTime.setFocusableInTouchMode(false);
        etChooseFile = (EditText) findViewById(R.id.etChooseFile);
        etChooseFile.setClickable(true);
        etChooseFile.setFocusable(false);
        etChooseFile.setFocusableInTouchMode(false);

        tvNoFileChosen = (TextView) findViewById(R.id.tvNoFileChosen);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        etSearch = (AutoCompleteTextView) findViewById(R.id.etVenueSearch);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        etSearch.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        etSearch.setLongClickable(false);
        etSearch.setTextIsSelectable(false);
        etSearch.setEnabled(true);
        etSearch.setFocusable(false);
        etSearch.setFocusableInTouchMode(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.places_ic_clear, 0);
        etSearch.setThreshold(3);
        etSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    if (AppUtils.isNetworkAvailable(mContext)) {
                        Intent autocompleteIntent =
                                new Autocomplete.IntentBuilder(getMode(), getPlaceFields())
                                        .build(mContext);
                        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE);
                    } else {
                        DialogFactory.showDropDownNotification(mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internet_connection));
                    }

                } catch (Exception e) {
                    // TODO: Handle the error.
                }

            }
        });
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = etSearch.getRight()
                            - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.place_autocomplete_button_padding);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        etSearch.setText("");
                        return true;
                    }
                }
                return false;
            }
        });


        /**********************************
         * Initializing the mGoogleMap here
         **********************************/
        if (mMap == null) {
            try {
                mapFragment = (SupportMapFragment) mContext.getSupportFragmentManager()
                        .findFragmentById(R.id.mapFragment);

                mapFragment.getMapAsync(this);

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        mAdapter = new ReportSpinnerAdapter(mContext, category);
        spinnerCategory.setAdapter(mAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategoryItem = (String) adapterView.getItemAtPosition(i);
                for (int j = 0; j < categoryListData.size(); j++) {
                    if (categoryListData.get(j).getEcatName().equals(selectedCategoryItem)) {
                        selectedCategoryItemId = String.valueOf(categoryListData.get(j).getEcatId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {
            showProgressBar(true);
            getCategoriesListData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        etStartDate.setOnClickListener(this);
        etStartTime.setOnClickListener(this);
        etChooseFile.setOnClickListener(this);
        // etSearch.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
    }

    public void initToolBar() {
        toolBar = findViewById(R.id.toolBar);
        btnToolbarBack = toolBar.findViewById(R.id.btn_toolbar_back);
        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);
        btnToolbarRight = toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_update_event));

        btnToolbarBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.etStartDate:
                isStartDateClicked = true;
                showStartDatePicker();
                break;

            case R.id.etStartTime:
                isStartTimeClicked = true;
                showStartTimePicker();
                break;

            case R.id.etChooseFile:
                imageSelectionBottomSheet();
                break;

            case R.id.etVenueSearch:
                etSearch.setFocusableInTouchMode(true);
                etSearch.requestFocus();
                if (etSearch.getText().toString() != null && etSearch.getText().length() > 0)
                    etSearch.setSelection(etSearch.getText().length());
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                break;

           /* case R.id.btnAddPeople:
                Intent intent = new Intent(mContext, AddUsersAndGroupsActivity.class);
                intent.putParcelableArrayListExtra("users", usersListData);
                intent.putParcelableArrayListExtra("groups", groupsListData);
                startActivity(intent);
                break;*/

            case R.id.btnCreate:
                AppUtils.hideKeyboard(mContext);
                requestCreateEventApiCall();
                break;
        }
    }

    private void showStartDatePicker() {
        Calendar nowDate = Calendar.getInstance();
        /*if (mDay != 0 && mYear != 0 && mMonth != 0 )
            nowDate.set(mYear,mMonth,mDay);*/
        DatePickerDialog dpd = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        mDay = dayOfMonth;
                        mMonth = month;
                        mYear = year;
                        mDate = year + "-" + month + "-" + dayOfMonth;
                        if (isStartDateClicked) {
                            etStartDate.setText(mDate);
                            isStartDateClicked = false;
                        }
                    }
                },
                nowDate.get(Calendar.YEAR),
                nowDate.get(Calendar.MONTH),
                nowDate.get(Calendar.DAY_OF_MONTH)
        );
        dpd.getDatePicker().setMinDate(nowDate.getTimeInMillis());
        if (mDay != 0 && mYear != 0 && mMonth != 0) {
            nowDate.set(mYear, mMonth, mDay);
        }
        dpd.show();
    }

    private void showStartTimePicker() {
        Calendar now = Calendar.getInstance();
        if (mDay != 0 && mYear != 0 && mMonth != 0) {
            now.set(mYear, mMonth, mDay);
        }
        TimePickerDialog tpd = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar now = Calendar.getInstance();

                        now.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        now.set(Calendar.MINUTE, minute);

                        String formatted = AppUtils.parseTimeFromTimePicker(String.valueOf(now.getTime()));
                        if (isStartTimeClicked) {
                            etStartTime.setText("" + AppUtils.parseTimeReverse(formatted));
                            isStartTimeClicked = false;
                        }
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        //tpd.setMinTime(now.getTime().getHours(),now.getTime().getMinutes(),now.getTime().getSeconds());
        tpd.show();
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

    private void requestCreateEventApiCall() {
        try {
            LatLng location = AppUtils.getLocationFromAddress(mContext, etSearch.getText().toString());

            final RequestBody eventId = RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(eventListData.getEventId()));
            final RequestBody eventTitle = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etEventTitle.getText().toString());
            final RequestBody eventDesc = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etDescription.getText().toString());
            final RequestBody eventStartDate = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etStartDate.getText().toString());
            final RequestBody eventStartTime = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etStartTime.getText().toString());
            final RequestBody eventSearchLoc = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etSearch.getText().toString());
            final RequestBody eventLat = RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(location.latitude));
            final RequestBody eventLon = RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(location.longitude));
            final RequestBody eventDetail = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etDetail.getText().toString());
            final RequestBody eventCategory = RequestBody.create(okhttp3.MediaType.parse("text/plain"), selectedCategoryItemId);
            final RequestBody eventComments;
            if (scComments.isChecked()) {
                eventComments = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "1");
            } else {
                eventComments = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "0");
            }
            final RequestBody eventIsPrivate;
            if (eventListData.getEventIsApprovalRequired() != null && eventListData.getEventIsApprovalRequired().toString().equals("1") &&
                    eventListData.getEventIsPincodeRequired() != null && eventListData.getEventIsPincodeRequired().toString().equals("1")) {
                eventIsPrivate = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "1");
            } else {
                eventIsPrivate = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "0");
            }
            final RequestBody isApprovalRequired;
            if (switchApproval.isChecked()) {
                isApprovalRequired = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "1");
            } else {
                isApprovalRequired = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "0");
            }

            final RequestBody eventPincode;
            final RequestBody isPinCodeRequired;
            if (switchPinCode.isChecked()) {
                URLogs.m("PinCode required");
                isPinCodeRequired = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "1");
                eventPincode = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etPinCode.getText().toString().trim());
            } else {
                URLogs.m("PinCode not required");
                isPinCodeRequired = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "0");
                eventPincode = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");
            }


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
                            getEditEventData(eventId, eventTitle, eventDesc, eventStartDate, eventStartTime
                                    , eventSearchLoc, eventLat, eventLon, eventDetail, eventCategory, eventComments, eventIsPrivate
                                    , eventPincode, isApprovalRequired, isPinCodeRequired, image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                final RequestBody image = null;
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
                            getEditEventData(eventId, eventTitle, eventDesc, eventStartDate, eventStartTime
                                    , eventSearchLoc, eventLat, eventLon, eventDetail, eventCategory, eventComments, eventIsPrivate
                                    , eventPincode, isApprovalRequired, isPinCodeRequired, image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCategoriesListData() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.categoriesList(new Callback<CategoriesListResponse>() {
            @Override
            public void onResponse(Call<CategoriesListResponse> call, final retrofit2.Response<CategoriesListResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().getCategories().size() > 0) {
                            for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                if (i == 0)
                                    category.add("Select Category");
                                else
                                    category.add(response.body().getData().getCategories().get(i).getEcatName());
                            }
                            categoryListData.addAll(response.body().getData().getCategories());
                            usersListData.addAll(response.body().getData().getUsers());
                            groupsListData.addAll(response.body().getData().getGroups());
                            mAdapter.notifyDataSetChanged();

                            setActivityDataToFields();

                            if (response.body().getData().getCategories() != null && response.body().getData().getCategories().size() > 0) {
                                for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                    if (response.body().getData().getCategories().get(i).getEcatId() == eventListData.getEcatId())
                                        spinnerCategory.setSelection(i);
                                }
                            }
                        } else {
                            /*rvNearbyList.setVisibility(View.GONE);
                            tvNoNearByFound.setVisibility(View.VISIBLE);*/
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
            public void onFailure(Call<CategoriesListResponse> call, Throwable t) {
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

    private void getEditEventData(RequestBody event_id, RequestBody event_title, RequestBody event_description, RequestBody event_start_date,
                                  RequestBody event_start_time,
                                  RequestBody event_location, RequestBody event_venue_lat, RequestBody event_venue_long,
                                  RequestBody event_detail, RequestBody ecat_id, RequestBody event_iscomments_allowed, RequestBody event_isprivate,
                                  RequestBody event_pincode, RequestBody event_isapproval_required,
                                  RequestBody event_ispinode_required, RequestBody images) {
        URLogs.m(event_ispinode_required + ":Updating event:" + event_pincode);
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.editEvent(event_id, event_title, event_description, event_start_date, event_start_time
                , event_location, event_venue_lat, event_venue_long, event_detail, ecat_id, event_iscomments_allowed, event_isprivate
                , event_pincode, event_isapproval_required, event_ispinode_required, images, new Callback<CreateEventResponse>() {
                    @Override
                    public void onResponse(Call<CreateEventResponse> call, final retrofit2.Response<CreateEventResponse> response) {
                        try {
                            showProgressBar(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus()) {

                                if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                                    DialogFactory.showDropDownSuccessNotification(
                                            mContext,
                                            mContext.getString(R.string.tv_alert_success),
                                            response.body().getMessage());

                                    finish();
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
                    public void onFailure(Call<CreateEventResponse> call, Throwable t) {
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

    private void imageSelectionBottomSheet() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
        View sheetView = mContext.getLayoutInflater().inflate(R.layout.image_upload_bottomsheet_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView tvCamera = sheetView.findViewById(R.id.tvOpenCamera);
        TextView tvGallery = sheetView.findViewById(R.id.tvGallery);
        Button btnCancel = sheetView.findViewById(R.id.btnCancel);
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
        values.put(MediaStore.Images.Media.DESCRIPTION, mContext.getPackageName());
        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = returnedIntent.getData();
                        imagePath = imageUtility.getImagePath(selectedImage, mContext);
                        Bitmap bitmap = imageUtility.uriToBitmap(selectedImage);
                        //ivUserImage.setImageBitmap(bitmap);

                        /*Picasso.with(mContext)
                                .load(selectedImage)   //
                                .transform(new RoundedCornersTransform())
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
                                .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
                                .into(ivUserImage);*/

                        profileImagePath = imagePath;
                        if (AppUtils.isSet(profileImagePath))
                            tvNoFileChosen.setText(profileImagePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver‌​(), imageUri);
                        InputStream image_stream = mContext.getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
                        imagePath = imageUtility.getImagePath(imageUri, mContext);
                        imagePath = AppUtils.compressImage(imagePath);
                        //bitmap = updateOrientation(imagePath, bitmap);
                        imagePath = saveImage(bitmap, 100);
                        //ivUserImage.setImageBitmap(bitmap);
                        /*Picasso.with(mContext)
                                .load("file:" + imagePath)   //
                                .transform(new RoundedCornersTransform())
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
                                .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
                                .into(ivUserImage);*/
                        profileImagePath = imagePath;
                        if (AppUtils.isSet(profileImagePath))
                            tvNoFileChosen.setText(profileImagePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case AUTOCOMPLETE_REQUEST_CODE:

                if (resultCode == AutocompleteActivity.RESULT_OK) {
                    AppUtils.hideKeyboard(mContext);
                    com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(returnedIntent);
                    if (place != null) {
                        //Log.e("Location Result", "Place: " + place.getName() + ", " + place.getId());
                        AppUtils.hideKeyboard(mContext);
                        etSearch.setText(place.getName());
                        etSearch.setSelection(etSearch.getText().length());
                        etSearch.setFocusableInTouchMode(false);
                        etSearch.setFocusable(false);
                        new GetLatLongAsync(mContext, place.getName()).execute();
                    } else {
                        DialogFactory.showDropDownNotification(mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internet_connection));
                    }

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    AppUtils.hideKeyboard(mContext);
                    etSearch.setFocusableInTouchMode(false);
                    Status status = Autocomplete.getStatusFromIntent(returnedIntent);
                    //Log.e("Location Result", status.getStatusMessage());
                } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    AppUtils.hideKeyboard(mContext);
                    etSearch.setFocusableInTouchMode(false);
                    //Log.e("Location Result", "RESULT_CANCELED");
                }
                break;
        }
    }


    class GetLatLongAsync extends AsyncTask<Void, Integer, LatLng> {
        String TAG = getClass().getSimpleName();
        Context context;
        String address;

        public GetLatLongAsync(Context context, String address) {
            this.context = context;
            this.address = address;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected LatLng doInBackground(Void... arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");

            LatLng latLng = getLocationFromAddress(context, address);
            return latLng;
        }

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            Log.d(TAG + " onPostExecute", "" + latLng);
            if (latLng != null) {
                if (mMap != null) {
                    mMap.clear();
                    drawMarker(latLng, etSearch.getText().toString().trim(), null, 0);
                    CameraPosition camera_position = new CameraPosition.Builder().target(latLng).zoom(13).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position));
                }
            }
        }
    }

    public static Bitmap updateOrientation(String path, Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(path); //imgFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public String createImageFile(String type) {
        String path = null;
        File tempFile;
        try {
            File root = mContext.getExternalFilesDir(null);
            File imgDir = new File(root, "temp_dir");
            if (!imgDir.exists()) {
                imgDir.mkdirs();
            }
            tempFile = new File(imgDir, type + "chairBook" + ".png");
            tempFile.createNewFile();
            path = tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public String getImagePath(Uri selectedImage) {
        String filePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private String getRealPathFromURI(Uri tempUri) {
        Cursor cursor = mContext.getContentResolver().query(tempUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private Uri getImageUri(Activity activity, Bitmap photo2) {
        // TODO Auto-generated method stub
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo2.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), photo2, "Title", null);
        return Uri.parse(path);
    }

    /******************************************
     * Save image -> converting bitmap into file
     ******************************************/
    public String saveImage(Bitmap bitmap, int quality) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + File.separator + "IMG_ChairBook.jpg";
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

    /******************************
     * GoogleMap all functions
     *****************************/

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> address = new ArrayList<>();
        LatLng p1 = null;
        try {
            URLogs.m("Given Address:" + strAddress);
            address = geoCoder.getFromLocationName(strAddress, 5);
            if (address == null && address.size() != 0) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p1;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);

        View mapView = mContext.getSupportFragmentManager().findFragmentById(R.id.mapFragment).getView();
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {

            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            setLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        //if (mGoogleApiClient != null) {
        // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //}
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onDestroy();
        if (mMap != null)
            mMap.clear();
    }

    private void setLocation(LatLng latLng) {
        /*MarkerOptions marker = new MarkerOptions().position(latLng);//.title("Marker in Sydney");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin));
        mMap.addMarker(marker);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(location);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(mContext, "This permission is required to search location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void drawMarker(LatLng point, String Title, String ImageUrl, int pos) {
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position for the marker
        markerOptions.position(point);
        markerOptions.icon(AppUtils.bitmapDescriptorFromVector(mContext));
        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(Title);
        markerOptions.draggable(false);
        // Placing a marker on the touched position
        mMap.addMarker(markerOptions).setTag(pos);

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(Title, ImageUrl)))).setTag(pos);
//        } else {
//            mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin)).title(Title)).setTag(pos);
//        }
    }

    private Bitmap getMarkerBitmapFromView(String event, String image) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_pin, null);
        TextView markerTextView = customMarkerView.findViewById(R.id.txt_pin);
        ImageView EventView = customMarkerView.findViewById(R.id.img_event);

        markerTextView.setText(event);

        if (AppUtils.isSet(image)) {
            Picasso.with(mContext)
                    .load(image)   //
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(EventView);
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);

        return returnedBitmap;
    }


    private AutocompleteActivityMode getMode() {
        return AutocompleteActivityMode.OVERLAY;
    }

    private List<Place.Field> getPlaceFields() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        return fields;
    }


}