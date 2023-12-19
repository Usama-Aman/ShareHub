package com.mobiletouch.sharehub;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import adapters.PlacesAutoCompleteAdapter;
import models.ProfileEditResponse;
import models.UserSelfProfileResponse;
import network.ApiClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;

/**
 * Activity to edit profile data of a user
 */
public class UserProfileEditActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity mContext;
    private EditText etName, etPhone, etEmail;
    Button btnSave;
    private FrameLayout progressBar;
    private ThreadPoolExecutor executor;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private AutoCompleteTextView etAddress;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    GoogleApiClient googleApiClient;
    private static double latitude , longitude;
    /* Lahore = 31.5546° N, 74.3572° E*/
    private PlacePicker.IntentBuilder builder;
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).build();
        setContentView(R.layout.activity_profile_edit);

        mContext = (AppCompatActivity) this;
        initToolBar();
        viewInitialize();

    }

    //view initialization
    private void viewInitialize() {
        etName = (EditText) findViewById(R.id.etFullName);
        etName.setClickable(true);
        etName.setFocusable(true);
        etName.setFocusableInTouchMode(true);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etEmail.setClickable(true);
        etEmail.setFocusable(true);
        etEmail.setFocusableInTouchMode(true);

        etPhone = (EditText) findViewById(R.id.etContact);
        etPhone.setClickable(true);
        etPhone.setFocusable(true);
        etPhone.setFocusableInTouchMode(true);

        etAddress = (AutoCompleteTextView) findViewById(R.id.etAddress);
        etAddress.setClickable(true);
        etAddress.setFocusable(false);
        etAddress.setFocusableInTouchMode(false);

        btnSave = (Button) findViewById(R.id.btnSave);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        etAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.places_ic_clear, 0);
        builder = new PlacePicker.IntentBuilder();
        mPlacesAdapter = new PlacesAutoCompleteAdapter(mContext, android.R.layout.simple_list_item_1,
                googleApiClient, BOUNDS_GREATER_SYDNEY, null){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.WHITE);
                tv.setTextSize(12);
                return view;
            }
        };
        //myLocation.setOnItemClickListener(mAutocompleteClickListener);
        etAddress.setAdapter(mPlacesAdapter);

        //et_search_layout.setThreshold(2);
        etAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etAddress.setFocusableInTouchMode(false);
                final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                /*description = new HashMap<String, String>();
                description = (HashMap<String, String>) parent.getItemAtPosition(position);
                et_search_layout.setText(description.get("description").toString().trim());*/

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            /*LatLng loc = getLocationFromAddress(mContext, etAddress.getText().toString().trim());
                            if (loc != null) {
                                mMap.clear();
                                //gMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                                MarkerOptions marker = new MarkerOptions().position(loc);//.title("Marker in Sydney");
                                //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin));
                                //mMap.addMarker(marker);
                                drawMarker(loc, etSearch.getText().toString().trim(),null, 0);
                                CameraPosition camera_position = new CameraPosition.Builder().target(loc).zoom(13).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position));
                            }*/
                            AppUtils.hideKeyboard(mContext);
                            if (etAddress.getText().toString() != null && etAddress.getText().length() > 0)
                                etAddress.setSelection(etAddress.getText().length());
                            etAddress.setFocusableInTouchMode(false);
                            etAddress.setFocusable(false);
                        }catch (Exception e){e.printStackTrace();}
                    }
                });
            }
        });

        etAddress.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = etAddress.getRight()
                            - etAddress.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.place_autocomplete_button_padding);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        etAddress.setText("");
                        return true;
                    }
                }
                return false;
            }
        });


        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            showProgressBar(true);
            getProfileDetail();
        } catch (Exception e) {e.printStackTrace();}

        etName.setOnClickListener(this);
        etPhone.setOnClickListener(this);
        etEmail.setOnClickListener(this);
        etAddress.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    //show progress dialog
    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //btnLogin.setEnabled(!progressVisible);
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
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
        tvToolbarTitle.setText(getResources().getString(R.string.tv_edit_profile));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    //edit profile parameters
    private void requestUpdateProfileApiCall() {
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
            return;
        }
        try {
            final RequestBody UserName = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etName.getText().toString());
            final RequestBody UserEmail = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etEmail.getText().toString());
            final RequestBody UserPhone = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etPhone.getText().toString());
            final RequestBody UserAddress = RequestBody.create(okhttp3.MediaType.parse("text/plain"), etAddress.getText().toString());

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
                        editProfileData(UserName, UserEmail, UserPhone, UserAddress);
                    } catch (Exception e) {e.printStackTrace();}
                }
            });

        } catch (Exception e) {e.printStackTrace();}
    }

    //API call to sending edit profile data
    private void editProfileData(RequestBody userName, RequestBody userEmail, RequestBody userPhone, RequestBody userAddress) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.editUserProfileData(userName, userEmail, userPhone, userAddress, new Callback<ProfileEditResponse>() {
            @Override
            public void onResponse(Call<ProfileEditResponse> call, final retrofit2.Response<ProfileEditResponse> response) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            AppUtils.showToast(mContext, response.body().getMessage());
                        }
                        getProfileDetail();
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
            public void onFailure(Call<ProfileEditResponse> call, Throwable t) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
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

    //get updated profile data
    private void getProfileDetail() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getUserProfileDetail(new Callback<UserSelfProfileResponse>() {
            @Override
            public void onResponse(Call<UserSelfProfileResponse> call, final retrofit2.Response<UserSelfProfileResponse> response) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (AppUtils.isSet(response.body().getData().getUser().getUserFullname())) {
                            etName.setText(AppUtils.capitalize(response.body().getData().getUser().getUserFullname()));
                        }
                        if (AppUtils.isSet(response.body().getData().getUser().getUserMobileNumber())) {
                            etPhone.setText(response.body().getData().getUser().getUserMobileNumber());
                        }
                        if (AppUtils.isSet(response.body().getData().getUser().getEmail())) {
                            etEmail.setText(response.body().getData().getUser().getEmail());
                        }
                        if (AppUtils.isSet(response.body().getData().getUser().getUserAddress())) {
                            etAddress.setText(response.body().getData().getUser().getUserAddress());
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
            public void onFailure(Call<UserSelfProfileResponse> call, Throwable t) {
                try {showProgressBar(false);} catch (Exception e) {e.printStackTrace();}
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.etName:
                etName.setFocusable(true);
                etName.setFocusableInTouchMode(true);
                break;

            case R.id.etPhone:
                etPhone.setFocusable(true);
                etPhone.setFocusableInTouchMode(true);
                break;

            case R.id.etEmail:
                etEmail.setFocusable(true);
                etEmail.setFocusableInTouchMode(true);
                break;

            case R.id.etAddress:
                etAddress.setFocusableInTouchMode(true);
                etAddress.requestFocus();
                if (etAddress.getText().toString() != null && etAddress.getText().length() > 0)
                    etAddress.setSelection(etAddress.getText().length());
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etAddress, InputMethodManager.SHOW_IMPLICIT);
                break;

            case R.id.btn_toolbar_close:
                AppUtils.hideKeyboard(this);
                finish();
                break;

            case R.id.btnSave:
                AppUtils.hideKeyboard(this);
                requestUpdateProfileApiCall();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                //Log.e("place", "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };


    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

}
