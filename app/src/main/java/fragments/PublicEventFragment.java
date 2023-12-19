package fragments;

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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.mobiletouch.sharehub.AddUsersAndGroupsActivity;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
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

import adapters.ReportSpinnerAdapter;
import adapters.SelectedGroupListAdapter;
import adapters.SelectedUserListAdapter;
import imageParsing.ImageUtility;
import models.CategoriesListResponse;
import models.CategoryModel;
import models.CreateEventResponse;
import models.GroupsModel;
import models.LoginResult;
import models.UserDetail;
import network.ApiClient;
import okhttp3.RequestBody;
import rebus.permissionutils.AskagainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;
import retrofit2.Call;
import retrofit2.Callback;
import singletons.UsersSingleton;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.SimpleDividerItemDecoration;
import utility.URLogs;

import static android.app.Activity.RESULT_OK;

//https://www.androidauthority.com/get-location-address-android-app-628764/
public class PublicEventFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, FullCallback {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private PlacesClient placesClient;
    public static View v;
    private Activity mContext;
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
    //   private FieldSelector fieldSelector;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
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
    // private SupportMapFragment mapFragment;

    GoogleApiClient mGoogleApiClient;
    //  GoogleApiClient googleApiClient;
    Location mLastLocation;
    private ThreadPoolExecutor executor;
    private LatLng mLocation;
    private static double latitude, longitude;
    /* Lahore = 31.5546° N, 74.3572° E*/


    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private AutoCompleteTextView etSearch;
    private AppCompatSpinner spinnerCategory;
    private ReportSpinnerAdapter mAdapter;
    ArrayList<String> category = new ArrayList<String>();
    ArrayList<CategoryModel> categoryListData = new ArrayList<CategoryModel>();
    ArrayList<UserDetail> usersListData = new ArrayList<UserDetail>();
    ArrayList<UserDetail> usersSelectedListData = new ArrayList<UserDetail>();
    ArrayList<GroupsModel> groupsSelectedListData = new ArrayList<GroupsModel>();
    ArrayList<GroupsModel> groupsListData = new ArrayList<GroupsModel>();
    private String selectedCategoryItem;
    private Button btnAddPeople;
    private Button btnCreate;
    private EditText etDescription, etEventTitle;
    private EditText etDetail;
    private SwitchCompat scComments;
    private String selectedCategoryItemId;
    private RecyclerView rvUsersList, rvGroupsList;
    private SelectedUserListAdapter mUAdapter;
    private RequestBody eventTitle;
    private RequestBody eventDesc;
    private RequestBody eventStartDate;
    private RequestBody eventStartTime;
    private RequestBody eventSearchLoc;
    private RequestBody eventLat;
    private RequestBody eventLon;
    private RequestBody eventDetail;
    private RequestBody eventCategory;
    private SelectedGroupListAdapter mGAdapter;
    private String languageCode;
    MapView mMapView;
    ImageView imgLocMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppUtils.multiLanguageConfiguration(getActivity());
        super.onCreate(savedInstanceState);

        // googleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Places.GEO_DATA_API).build();


        String apiKey = getString(R.string.google_maps_key);

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }

        // Retrieve a PlacesClient (previously initialized - see MainActivity)
        placesClient = Places.createClient(getActivity());
        URLogs.m("onCreate OnVisible Fragment");


        /*if (v != null) {
            v = null;
        }*/
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {*/
        alreadySetCurrent = false;
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_public_event, container, false);
        }
        URLogs.m("onCreateView OnVisible Fragment");
        mContext = getActivity();

/*
        fieldSelector =
                new FieldSelector(
                        findViewById(R.id.use_custom_fields), findViewById(R.id.custom_fields_list));*/
        initSharedPref();
        getActivityData();
        viewInitialize(v, savedInstanceState);

        /*} catch (InflateException e) {e.printStackTrace();}*/
        return v;
    }

    private void viewInitialize(View v, Bundle savedInstanceState) {
        imageUtility = new ImageUtility(mContext);
        languageCode = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        imgLocMarker = (ImageView) v.findViewById(R.id.imgLocMarker);
        clMainContent = (ConstraintLayout) v.findViewById(R.id.clMainContent);
        progressBar = (FrameLayout) v.findViewById(R.id.progressBar);

        //  imgLocMarker.setImageBitmap(getMarkerBitmapFromView("", null));


        etEventTitle = (EditText) v.findViewById(R.id.etEventTitle);
        etDescription = (EditText) v.findViewById(R.id.etDescription);
        etDetail = (EditText) v.findViewById(R.id.etDetail);
        scComments = (SwitchCompat) v.findViewById(R.id.scComments);

        etStartDate = (EditText) v.findViewById(R.id.etStartDate);
        etStartDate.setClickable(true);
        etStartDate.setFocusable(false);
        etStartDate.setFocusableInTouchMode(false);
        etStartTime = (EditText) v.findViewById(R.id.etStartTime);
        etStartTime.setClickable(true);
        etStartTime.setFocusable(false);
        etStartTime.setFocusableInTouchMode(false);

        etChooseFile = (EditText) v.findViewById(R.id.etChooseFile);
        etChooseFile.setClickable(true);
        etChooseFile.setFocusable(false);
        etChooseFile.setFocusableInTouchMode(false);

        tvNoFileChosen = (TextView) v.findViewById(R.id.tvNoFileChosen);
        btnAddPeople = (Button) v.findViewById(R.id.btnAddPeople);
        btnCreate = (Button) v.findViewById(R.id.btnCreate);
        etSearch = (AutoCompleteTextView) v.findViewById(R.id.etVenueSearch);
        etSearch.setText("");
        spinnerCategory = (AppCompatSpinner) v.findViewById(R.id.spinnerCategory);
        rvUsersList = v.findViewById(R.id.rvUsersList);
        rvGroupsList = v.findViewById(R.id.rvGroupsList);

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
                        locationSend = null;
                        if (mMap != null)
                            mMap.clear();
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
                URLogs.m("Map view null");
                if (mMapView == null) {
                    mMapView = v.findViewById(R.id.mapView);
                    mMapView.onCreate(savedInstanceState);
                    mMapView.onResume(); // needed to get the map to display immediately
                    // mapFragment = new SupportMapFragment();
                    // FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    // transaction.replace(R.id.map, mapFragment).commit();
                    mMapView.getMapAsync(this);
                }

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            URLogs.m("MapView not view null");
        }

        mAdapter = new ReportSpinnerAdapter(mContext, category);
        spinnerCategory.setAdapter(mAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategoryItem = (String) adapterView.getItemAtPosition(i);
                for (int j = 0; j < categoryListData.size(); j++) {
                    if (AppUtils.isSet(languageCode)) {
                        if (languageCode.equals("en")) {
                            if (categoryListData.get(j).getEcatName().equals(selectedCategoryItem)) {
                                selectedCategoryItemId = String.valueOf(categoryListData.get(j).getEcatId());
                            }
                        } else {
                            if (categoryListData.get(j).getEcat_name_ar().equals(selectedCategoryItem)) {
                                selectedCategoryItemId = String.valueOf(categoryListData.get(j).getEcatId());
                            }
                        }
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
            return;
        }
        try {

//            showProgressBar(true);
            getCategoriesListData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        etStartDate.setOnClickListener(this);
        etStartTime.setOnClickListener(this);
        etChooseFile.setOnClickListener(this);
        //  etSearch.setOnClickListener(this);
        btnAddPeople.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
    }

    private void initSharedPref() {
        LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData().getUserId() != null) {
            //userId = userModel.getUserData().getUserId();
            //userLocation = userModel.getUserData().getUserLocation();
        }
    }

    private void getActivityData() {
        Bundle bundle = mContext.getIntent().getExtras();
        if (bundle != null) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_toolbar_back:
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
               /* etSearch.setFocusableInTouchMode(true);
                etSearch.requestFocus();
                if (etSearch.getText().toString() != null && etSearch.getText().length() > 0)
                    etSearch.setSelection(etSearch.getText().length());
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);*/
                break;

            case R.id.btnAddPeople:
                Intent intent = new Intent(mContext, AddUsersAndGroupsActivity.class);
                intent.putParcelableArrayListExtra("users", usersListData);
                intent.putParcelableArrayListExtra("groups", groupsListData);
                startActivity(intent);
                break;

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

    RequestBody users;
    RequestBody groups;
    //   LatLng locationSend = null;

    private void requestCreateEventApiCall() {
        try {

            if (AppUtils.isSet(etEventTitle.getText().toString())) {
                eventTitle = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etEventTitle.getText().toString());
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_title));
                return;
            }

            if (AppUtils.isSet(etDescription.getText().toString())) {
                eventDesc = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etDescription.getText().toString());
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_description));
                return;
            }

            if (AppUtils.isSet(etStartDate.getText().toString())) {
                eventStartDate = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etStartDate.getText().toString());
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_start_date));
                return;
            }

            if (AppUtils.isSet(etStartTime.getText().toString())) {
                eventStartTime = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etStartTime.getText().toString());
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_start_time));
                return;
            }

            if (AppUtils.isSet(etSearch.getText().toString())) {
                eventSearchLoc = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etSearch.getText().toString());
                // LatLng location = AppUtils.getLocationFromAddress(mContext, etSearch.getText().toString());
                if (locationSend != null) {
                    eventLat = RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(locationSend.latitude));
                    eventLon = RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(locationSend.longitude));
                } else {
                    DialogFactory.showDropDownNotification(
                            mContext, mContext.getString(R.string.tv_error),
                            mContext.getString(R.string.error_msg_event_location));
                    return;
                }

            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_location));
                return;
            }


            if (AppUtils.isSet(etDetail.getText().toString())) {
                eventDetail = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etDetail.getText().toString());
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_detail));
                return;
            }

            if (AppUtils.isSet(selectedCategoryItem) && !selectedCategoryItem.equals(getString(R.string.tv_select_category))) {
                eventCategory = RequestBody.create(okhttp3.MediaType.parse("text/plain"), selectedCategoryItemId);
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_category));
                return;
            }

            final RequestBody eventComments;
            if (scComments.isChecked()) {
                eventComments = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "1");
            } else {
                eventComments = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "0");
            }
            final RequestBody eventIsPrivate = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "0");
            final RequestBody eventPincode = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");
            final RequestBody isApprovalRequired = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");
            final RequestBody isPinCodeRequired = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");

            if (usersSelectedListData != null && usersSelectedListData.size() > 0) {
                String userIds;
                ArrayList<String> arg = new ArrayList<String>();
                for (int i = 0; i < usersSelectedListData.size(); i++) {
                    arg.add(usersSelectedListData.get(i).getUserId().toString());
                }
                userIds = TextUtils.join(",", arg);
                users = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf("[" + userIds + "]"));
                groups = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");

            } else if (UsersSingleton.getInstance().getGroupsList() != null && UsersSingleton.getInstance().getGroupsList().size() > 0) {
                groups = RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(UsersSingleton.getInstance().getGroupsList()));
                users = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");

            } /*else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_group_user));
                return;
            }*/

//            else {
//            }


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
                            if (usersSelectedListData.size() > 0 || groupsSelectedListData.size() > 0) {
                                getCreateEventData(eventTitle, eventDesc, eventStartDate, eventStartTime
                                        , eventSearchLoc, eventLat, eventLon, eventDetail, eventCategory, eventComments, eventIsPrivate
                                        , groups, users, eventPincode, isApprovalRequired, isPinCodeRequired, image);
                            } else {
                                getCreateEventData(eventTitle, eventDesc, eventStartDate, eventStartTime
                                        , eventSearchLoc, eventLat, eventLon, eventDetail, eventCategory, eventComments, eventIsPrivate
                                        , groups, users, eventPincode, isApprovalRequired, isPinCodeRequired, image);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_msg_event_image));
                return;
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
//                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().getCategories().size() > 0) {
                            for (int i = 0; i < response.body().getData().getCategories().size(); i++) {
                                if (i == 0)
                                    category.add(getString(R.string.tv_select_category));
                                else if (AppUtils.isSet(languageCode)) {
                                    if (languageCode.equals("en")) {
                                        category.add(response.body().getData().getCategories().get(i).getEcatName());
                                    } else {
                                        category.add(response.body().getData().getCategories().get(i).getEcat_name_ar());
                                    }
                                }


                            }
                            categoryListData.addAll(response.body().getData().getCategories());
                            usersListData.addAll(response.body().getData().getUsers());
                            groupsListData.addAll(response.body().getData().getGroups());
                            mAdapter.notifyDataSetChanged();
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

    private void getCreateEventData(RequestBody event_title, final RequestBody event_description, RequestBody event_start_date,
                                    RequestBody event_start_time,
                                    RequestBody event_location, RequestBody event_venue_lat, RequestBody event_venue_long,
                                    RequestBody event_detail, RequestBody ecat_id, RequestBody event_iscomments_allowed, RequestBody event_isprivate,
                                    RequestBody event_groups, RequestBody event_users, RequestBody event_pincode,
                                    RequestBody event_isapproval_required, RequestBody event_ispinode_required, RequestBody images) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.createEvent(event_title, event_description, event_start_date, event_start_time
                , event_location, event_venue_lat, event_venue_long, event_detail, ecat_id, event_iscomments_allowed, event_isprivate
                , event_groups, event_users, event_pincode, event_isapproval_required, event_ispinode_required, images, new Callback<CreateEventResponse>() {
                    @Override
                    public void onResponse(Call<CreateEventResponse> call, final retrofit2.Response<CreateEventResponse> response) {
                        try {
                            showProgressBar(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus()) {

                                if (UsersSingleton.getInstance().getUsersList() != null && UsersSingleton.getInstance().getUsersList().size() > 0)
                                    UsersSingleton.getInstance().getUsersList().clear();
                                if (UsersSingleton.getInstance().getGroupsList() != null && UsersSingleton.getInstance().getGroupsList().size() > 0)
                                    UsersSingleton.getInstance().getGroupsList().clear();
                                if (v != null) {
                                    v = null;
                                }
                                if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                                    DialogFactory.showDropDownSuccessNotification(
                                            mContext,
                                            mContext.getString(R.string.tv_alert_success),
                                            response.body().getMessage());

                                    EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                                    Bundle args = new Bundle();
                                    args.putString("event_id", response.body().getData().getEventId().toString());

                                    if (SharedPreference.isUserLoggedIn(mContext) && SharedPreference.getUserDetails(mContext) != null) {
                                        LoginResult userModel = SharedPreference.getUserDetails(mContext);
                                        if (userModel.getUserData().getUserId().equals(response.body().getData().getUserId()))
                                            args.putString("isself", "1");
                                        else
                                            args.putString("isself", "0");
                                    }

                                    args.putString("isSelfProfile", "newEvent");
                                    eventDetailsFragment.setArguments(args);
                                    ((MainActivity) getActivity()).launchNewFragment(eventDetailsFragment, R.id.container);
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
        TextView tvCamera = (TextView) sheetView.findViewById(R.id.tvOpenCamera);
        TextView tvGallery = (TextView) sheetView.findViewById(R.id.tvGallery);
        Button btnCancel = (Button) sheetView.findViewById(R.id.btnCancel);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;


                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission()) {
                        PermissionManager.with(mContext)
                                .permission(PermissionEnum.WRITE_EXTERNAL_STORAGE,
                                        PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.CAMERA)
                                .askagain(true)
                                .askagainCallback(new AskagainCallback() {
                                    @Override
                                    public void showRequestPermission(UserResponse response) {
                                        showDialog(response);
                                    }
                                })
                                .callback(PublicEventFragment.this)
                                .ask();
                        return;
                    }
                }
                mBottomSheetDialog.dismiss();
                openCamera();
            }
        });

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;


                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission()) {
                        PermissionManager.with(mContext)
                                .permission(PermissionEnum.WRITE_EXTERNAL_STORAGE,
                                        PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.CAMERA)
                                .askagain(true)
                                .askagainCallback(new AskagainCallback() {
                                    @Override
                                    public void showRequestPermission(UserResponse response) {
                                        showDialog(response);
                                    }
                                })
                                .callback(PublicEventFragment.this)
                                .ask();
                        return;
                    }
                }
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

    boolean fromPlace = false;

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
                    AppUtils.hideKeyboardForOverlay(mContext);
                    Place place = Autocomplete.getPlaceFromIntent(returnedIntent);
                    if (place != null) {
                        fromPlace = true;
                        ////Log.e("Location Result", "Place: " + place.getName() + ", " + place.getId());
                        AppUtils.hideKeyboardForOverlay(mContext);
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
                    AppUtils.hideKeyboardForOverlay(mContext);
                    etSearch.setFocusableInTouchMode(false);
                    etSearch.setFocusable(false);
                    Status status = Autocomplete.getStatusFromIntent(returnedIntent);
                    ////Log.e("Location Result", status.getStatusMessage());
                } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    AppUtils.hideKeyboardForOverlay(mContext);
                    etSearch.setFocusableInTouchMode(false);
                    etSearch.setFocusable(false);
                    ////Log.e("Location Result", "RESULT_CANCELED");
                }
                break;
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
   /* private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                ////Log.e("place", "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };
*/
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        List<Address> address;
        LatLng p1 = null;
        try {
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
                    locationSend = latLng;
                    etSearch.setText(address);
                    if (mMap != null)
                        mMap.clear();
                    drawMarker(latLng, etSearch.getText().toString().trim(), null, 0);
                    CameraPosition camera_position = new CameraPosition.Builder().target(latLng).zoom(13).build();
                    if (mMap != null)
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position));

                }
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_some_thing_went_wrong));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        URLogs.m("onMapReady");
        mMap = googleMap;
        //gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                //  mVisitingMarker.setPosition(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        //  configureCameraIdle();
        //  mMap.setOnCameraIdleListener(onCameraIdleListener);


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
                if (mMap != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                locationSend = new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
                tries = 1;
                executeAsyncAddress(arg0.getPosition().latitude, arg0.getPosition().longitude, 1);
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
                if (mMap != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }
        });
        mMap.setOnMapClickListener(latLng -> {
            if (mMap != null)
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            locationSend = new LatLng(latLng.latitude, latLng.longitude);
            tries = 1;
            executeAsyncAddress(latLng.latitude, latLng.longitude, 2);
        });

        View mapView = null;
        if (mMapView != null)
            mapView = mMapView;
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            URLogs.m("On Map not null");
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
       /*     layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);*/
            layoutParams.setMargins(0, 30, 30, 0);
        } else {
            URLogs.m("On Map null");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //buildGoogleApiClient();
                if (mMap != null)
                    mMap.setMyLocationEnabled(true);
            }
        } else {
            //buildGoogleApiClient();
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }

  /*      if (locationSend != null) {
            ////Log.e("onMapReady set","----");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(locationSend));
            tries = 1;
            executeAsyncAddress(locationSend.latitude, locationSend.longitude);
        }
*/
    }

    GetLatLngAddressAsync asynAddress;

    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {


                if (mMap != null)
                    locationSend = mMap.getCameraPosition().target;
                if (locationSend != null)
                    URLogs.m("Location Find" + locationSend.latitude + "--" + locationSend.longitude);

                if (fromPlace) {
                    fromPlace = false;
                    return;
                }

                if (locationSend != null)
                    if (locationSend.latitude != 0 && locationSend.longitude != 0) {
                        tries = 1;
                        executeAsyncAddress(locationSend.latitude, locationSend.longitude, 3);
                    }
            }
        };
    }

    private void executeAsyncAddress(double lat, double lng, int call) {


        if (!isVisible() && CreateEventFragment.createdEventCurrentTab == 0)
            return;

        if (asynAddress != null)
            asynAddress.cancel(true);
        locationSend = new LatLng(lat, lng);
        asynAddress = new GetLatLngAddressAsync(mContext, lat, lng);
        asynAddress.execute();
    }

    LatLng locationSend = null;
    boolean alreadySetCurrent = false;

    @Override
    public void onConnected(Bundle bundle) {
        //URLogs.m("onConnected");
        ////Log.e("onConnected","----");

        if (!alreadySetCurrent)
            getCurrentLocation();
        alreadySetCurrent = true;
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
        if (ActivityCompat.checkSelfPermission(
                mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return;
        }

     /*   if (AppUtils.isSet(etSearch.getText().toString()) && AppUtils.isNetworkAvailable(mContext)) {
            etSearch.setSelection(etSearch.getText().length());
            etSearch.setFocusableInTouchMode(false);
            etSearch.setFocusable(false);
            new GetLatLongAsync(mContext, etSearch.getText().toString()).execute();
        }*/

        if (mMap != null)
            mMap.setMyLocationEnabled(true);

        if (mGoogleApiClient != null)
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        if (mLastLocation != null) {
            ////Log.e("current.....", "----: " + mLastLocation.getLatitude() + " -------  " + mLastLocation.getLongitude());
            setLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            CameraPosition camera_position = new CameraPosition.Builder().target((new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))).zoom(13).build();

            if (mMap != null)
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position));
            executeAsyncAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 4);
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
        URLogs.m("Public event onPause");
        // mapFragment.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        URLogs.m("Public event onResume");
        //  mapFragment.onResume();

        if (UsersSingleton.getInstance().getUsersList() != null && UsersSingleton.getInstance().getUsersList().size() > 0) {
            usersSelectedListData.clear();
            for (int i = 0; i < UsersSingleton.getInstance().getUsersList().size(); i++) {
                if (UsersSingleton.getInstance().getUsersList().get(i).isSelected()) {
                    usersSelectedListData.add(UsersSingleton.getInstance().getUsersList().get(i));
                }
            }
            rvUsersList.setVisibility(View.VISIBLE);
            mUAdapter = new SelectedUserListAdapter(mContext, usersSelectedListData, new SelectedUserListAdapter.UsersAdapterListener() {
                @Override
                public void onUserSelected(UserDetail contact) {

                }
            });

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            rvUsersList.setLayoutManager(mLayoutManager);
            rvUsersList.setItemAnimator(new DefaultItemAnimator());
            rvUsersList.addItemDecoration(new SimpleDividerItemDecoration(mContext));
            rvUsersList.setAdapter(mUAdapter);
        } else {
            rvUsersList.setVisibility(View.GONE);
        }

        if (UsersSingleton.getInstance().getGroupsList() != null && UsersSingleton.getInstance().getGroupsList().size() > 0) {
            groupsSelectedListData.clear();
            for (int i = 0; i < UsersSingleton.getInstance().getGroupsList().size(); i++) {
                if (UsersSingleton.getInstance().getGroupsList().get(i).isSelected()) {
                    groupsSelectedListData.add(UsersSingleton.getInstance().getGroupsList().get(i));
                }
            }
            rvGroupsList.setVisibility(View.VISIBLE);
            mGAdapter = new SelectedGroupListAdapter(mContext, groupsSelectedListData, new SelectedGroupListAdapter.GroupsAdapterListener() {
                @Override
                public void onGroupSelected(GroupsModel contact) {

                }
            });

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            rvGroupsList.setLayoutManager(mLayoutManager);
            rvGroupsList.setItemAnimator(new DefaultItemAnimator());
            rvGroupsList.addItemDecoration(new SimpleDividerItemDecoration(mContext));
            rvGroupsList.setAdapter(mGAdapter);
        } else {
            rvGroupsList.setVisibility(View.GONE);
        }


   /*     if (mMap == null) {
            URLogs.m("Map empty");
            try {
                mapFragment = (MapFragment) mContext.getFragmentManager()
                        .findFragmentById(R.id.map);
                URLogs.m("Map is being init");
                mapFragment.getMapAsync(this);

                if (mGoogleApiClient == null) {
                    URLogs.m("mGoogleApiClient empty");
                    mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                } else {
                    URLogs.m("mGoogleApiClient not empty");
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        } else {
            URLogs.m("Map not empty");
        }
*/
    }

    @Override
    public void onStart() {
        URLogs.m("Public event onStart");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        super.onStart();
        // mapFragment.onStart();
    }


    @Override
    public void onStop() {
        URLogs.m("Public event onStop");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        // mapFragment.onStop();
    }

    @Override
    public void onDestroy() {
        URLogs.m("Public event onDestroy");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        //   googleApiClient.disconnect();
        super.onDestroy();
        //  mapFragment.onDestroy();
        if (mMap != null)
            mMap.clear();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // mapFragment.onLowMemory();
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // mapFragment.onSaveInstanceState(outState);
    }*/

 /*   @Override
    public void onDestroyView() {
        URLogs.m("Public event onDestroyView");
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        super.onDestroyView();

    }

    @Override
    public void onDetach() {
        URLogs.m("Public event onDetach");
        FragmentManager fm = getFragmentManager();

        Fragment xmlFragment = fm.findFragmentById(R.id.map);
        if (xmlFragment != null) {
            fm.beginTransaction().remove(xmlFragment).commit();
        }
        super.onDetach();

    }*/

    private void setLocation(LatLng latLng) {
        /*MarkerOptions marker = new MarkerOptions().position(latLng);//.title("Marker in Sydney");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin));
        marker.draggable(true);
        mMap.addMarker(marker);
        drawMarker(latLng, etSearch.getText().toString().trim(), null, 0);
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
        if (mMap != null) {
            mMap.clear();
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting the position for the marker
            markerOptions.position(point);
            markerOptions.icon(AppUtils.bitmapDescriptorFromVector(mContext));
            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(Title);
            markerOptions.draggable(true);
            // Placing a marker on the touched position
            mMap.addMarker(markerOptions).setTag(pos);
        }

//        //imgLocMarker.setImageBitmap(getMarkerBitmapFromView(Title, ImageUrl));
//
//        if(mMap!=null)
//            mMap.clear();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if(mMap!=null)
//                mMap.addMarker((new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(Title, ImageUrl)))).draggable(true)).setTag(pos);
//        } else {
//
//            if(mMap!=null)
//                mMap.addMarker((new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin)).title(Title)).draggable(true)).setTag(pos);
//        }
    }



    private Bitmap getMarkerBitmapFromView(String event, String image) {

        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_pin, null);
        TextView markerTextView = (TextView) customMarkerView.findViewById(R.id.txt_pin);
        ImageView EventView = customMarkerView.findViewById(R.id.img_event);

        markerTextView.setText(event);

        if (AppUtils.isSet(image)) {
            Picasso.with(getActivity())
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


    private boolean checkPermission() {
        PermissionEnum permissionEnum = PermissionEnum.ACCESS_FINE_LOCATION;
        boolean granted = PermissionUtils.isGranted(mContext, PermissionEnum.WRITE_EXTERNAL_STORAGE,
                PermissionEnum.READ_EXTERNAL_STORAGE, PermissionEnum.CAMERA);
        //Toast.makeText(Splash.this, permissionEnum.toString() + " isGranted [" + granted + "]", Toast.LENGTH_SHORT).show();
        return granted == true;
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

    }

    private void showDialog(final AskagainCallback.UserResponse response) {
        new AlertDialog.Builder(mContext)
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

    int tries = 1;

    class GetLatLngAddressAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        Context context;
        double latitude;
        double longitude;

        public GetLatLngAddressAsync(Context context, double latitude, double longitude) {
            this.context = context;
            this.latitude = latitude;
            this.longitude = longitude;
            // imgLocMarker.setImageBitmap(getMarkerBitmapFromView("", null));
            if (mMap != null)
                mMap.clear();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ////Log.e(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            ////Log.e(TAG + " DoINBackGround", "On doInBackground...");

            String latLng = AppUtils.getAddress(context, latitude, longitude);
            return latLng;
        }

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String latLng) {
            super.onPostExecute(latLng);
            ////Log.e(TAG + " onPostExecute", "" + latLng);
            if (AppUtils.isSet(latLng)) {
                etSearch.setText(latLng);
                drawMarker(new LatLng(latitude, longitude), latLng, "", 0);
                // imgLocMarker.setImageBitmap(getMarkerBitmapFromView(latLng, null));
            } else if (tries != 4) {
                tries++;
                executeAsyncAddress(latitude, longitude, 5);
            }
        }
    }

    class GetAddressAsync extends AsyncTask<Void, Integer, LatLng> {
        String TAG = getClass().getSimpleName();
        Context context;
        String strAddress;

        public GetAddressAsync(Context context, String strAddress) {
            this.context = context;
            this.strAddress = strAddress;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected LatLng doInBackground(Void... arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");

            LatLng latLng = AppUtils.getLocationFromAddress(context, strAddress);
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
                    etSearch.setText(strAddress);
                    if (mMap != null)
                        mMap.clear();
                    CameraPosition camera_position = new CameraPosition.Builder().target(latLng).zoom(13).build();
                    if (mMap != null)
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position));
                }
            } else {
                DialogFactory.showDropDownNotification(
                        mContext, mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.error_some_thing_went_wrong));
            }

        }
    }
}