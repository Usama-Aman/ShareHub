package fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import adapters.EventsListAdapter;
import adapters.FilterCategoryAdapter;
import models.EventsResponse;
import models.LoginResult;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import singletons.SortingSettingsData;
import utility.AppUtils;
import utility.DialogFactory;
import utility.PaginationAdapterCallback;
import utility.PaginationScrollListener;
import utility.SharedPreference;
import utility.SpacesItemDecoration;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Mubashir 5/16/2018
 */
public class FutureFragment extends Fragment implements View.OnClickListener, LocationListener, SwipeRefreshLayout.OnRefreshListener, PaginationAdapterCallback {
    RelativeLayout Sort_rl, Filter_rl;
    EditText Search_view, location_et, creater_et, date_et, time_et;
    Dialog alertDialog;
    RadioGroup filter_rg;
    LinearLayout sort_rg;
    Button applay, reset;
    DiscreteSeekBar distance_value;
    RecyclerView recyclerViewEvents;
    TextView tv_noEvent;
    private EventsListAdapter mAdapter;
    String strAdd = "";
    public ArrayList<EventsResponse.EventData> EventListData = new ArrayList<EventsResponse.EventData>();
    Button mSortTitle, mSortdate, mSortLocation, mSortPopular;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    private LinearLayoutManager mLayoutManager;
    private FrameLayout progressBar;
    String mSortTitleState = "0", mSortDateState = "0", mSortLocationState = "0", mSortPopularState = "0";
    String SortTitle = "", SortOrder = "";
    String EventType = "future";
    private Button btnBarCode;
    private RadioButton mSortTypeLive, mSortTypePast, mSortTypeFuture;
    private Spinner spinnerCategory;
    ArrayList<EventsResponse.Category> category_list = new ArrayList<EventsResponse.Category>();
    int mCategoryId;

    int mDistanceVal = 0;
    String dateString = "";
    String timeString = "";
    String creatorString = "";
    String locationString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);
     //   EventsListAdapter.fromState="future";
        CastView(view);

        Sort_rl.setOnClickListener(this);
        Filter_rl.setOnClickListener(this);
        onRefresh();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = null;
        if (bestProvider != null)
            if (locationManager.getLastKnownLocation(bestProvider) != null)

        if (location == null) {
           /* DialogFactory.showDropDownNotification(getActivity(),
                    getActivity().getString(R.string.alert_information),
                    getActivity().getString(R.string.msg_gps));*/
        }
        if (location != null) {
            //Log.e("locatin", "location--" + location);

            onLocationChanged(location);
        }
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        SearchListner();

        return view;
    }

    @Override
    public void onRefresh() {
        isLastPage = false;
        isLoading = false;
        mSwipeRefreshLayout.setRefreshing(true);
        SortingSettingsData sortingSettingsData = SharedPreference.getSortingSettings(getActivity());
        if (sortingSettingsData != null) {
            if (sortingSettingsData.getType().equals("future")) {
                getSelectedSortingData();
                callGetSortEvents();
            } else {
                SharedPreference.saveSortingSettings(getActivity(), null);
                callGetEventsFirst();
            }
        } else {
            SharedPreference.saveSortingSettings(getActivity(), null);
            callGetEventsFirst();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
      //  EventsListAdapter.fromState="future";
        onRefresh();
    }

    // Casting Views
    public void CastView(View view) {

        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        tv_noEvent = view.findViewById(R.id.tvEventsNotFound);
        recyclerViewEvents = view.findViewById(R.id.rvEventsList);
        progressBar = view.findViewById(R.id.progressBar);
        Search_view = view.findViewById(R.id.search_et);
        btnBarCode = view.findViewById(R.id.bar_code_bt);
        Sort_rl = view.findViewById(R.id.sort_bt);
        Sort_rl.bringToFront();
        Filter_rl = view.findViewById(R.id.filter_bt);
        Filter_rl.bringToFront();
        recyclerViewEvents.addItemDecoration(new SpacesItemDecoration(10));

        btnBarCode.setOnClickListener(this);
    }

    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String date = dateFormatter.format(newDate.getTime());
                date_et.setText(date);
                dateString = date;
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private void TimePicker() {
        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                time_et.setText(selectedHour + ":" + selectedMinute);
                timeString = selectedHour + ":" + selectedMinute;
            }
        }, hour, minute, true);//Yes 24 hour time

        mTimePicker.show();
    }

    // Sort  & Filter OpenDialog()
    private void OpenDialog() {

        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.filter_sort_popup);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        sort_rg = alertDialog.findViewById(R.id.radiogroup_sort);
        filter_rg = alertDialog.findViewById(R.id.radiogroup_filter);

        mSortTitle = alertDialog.findViewById(R.id.title_rb);
        mSortdate = alertDialog.findViewById(R.id.date_rb);
        mSortLocation = alertDialog.findViewById(R.id.location_rb);
        mSortPopular = alertDialog.findViewById(R.id.popular_rb);

        mSortTypeLive = alertDialog.findViewById(R.id.finished_rb);
        mSortTypePast = alertDialog.findViewById(R.id.live_rb);
        mSortTypeFuture = alertDialog.findViewById(R.id.notstarted_rb);

        location_et = alertDialog.findViewById(R.id.location_et);
        creater_et = alertDialog.findViewById(R.id.creater_et);
        date_et = alertDialog.findViewById(R.id.date_et);
        time_et = alertDialog.findViewById(R.id.time_et);
        applay = alertDialog.findViewById(R.id.applay_bt);
        reset = alertDialog.findViewById(R.id.reset_bt);
        spinnerCategory = alertDialog.findViewById(R.id.spinnerCategory);

        distance_value = (DiscreteSeekBar) alertDialog.findViewById(R.id.distance_seek);
        addItemsOnSpinnerCategory();
        getSelectedSortingData();

        location_et.setText(strAdd);
        applay.setOnClickListener(this);
        reset.setOnClickListener(this);
        date_et.setOnClickListener(this);
        time_et.setOnClickListener(this);

        mSortPopular.setOnClickListener(this);
        mSortdate.setOnClickListener(this);
        mSortLocation.setOnClickListener(this);
        mSortTitle.setOnClickListener(this);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCategoryId = category_list.get(i).getEcatId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filter_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.finished_rb:
                        EventType = "live";
                        // do operations specific to this selection
                        break;
                    case R.id.live_rb:
                        EventType = "past";
                        // do operations specific to this selection
                        break;
                    case R.id.notstarted_rb:
                        EventType = "future";
                        // do operations specific to this selection
                        break;
                }
            }
        });
        distance_value.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value;
            }
        });
        alertDialog.show();
    }

    private void getSelectedSortingData() {
        mDistanceVal = 0;
        dateString = "";
        timeString = "";
        creatorString = "";
        locationString = "";

        SortingSettingsData sortingSettingsData = SharedPreference.getSortingSettings(getActivity());
        if (sortingSettingsData != null) {
            if (AppUtils.isSet(sortingSettingsData.getType()))
                if (sortingSettingsData.getType().equalsIgnoreCase("future")) {
                    if (AppUtils.isSet(sortingSettingsData.getSort_field().toString()) &&
                            sortingSettingsData.getSort_field().equals("event_title")) {
                        if (sortingSettingsData.getSort_type().equals("desc")) {
                            int imgResource = R.drawable.assending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortTitleState = "1";
                            SortTitle = "event_title";
                            SortOrder = "desc";
                            if (mSortTitle != null) {
                                mSortTitle.setTextColor(getResources().getColor(R.color.red));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else if (sortingSettingsData.getSort_type().equals("asc")) {
                            int imgResource = R.drawable.decending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortTitleState = "2";
                            SortTitle = "event_title";
                            SortOrder = "asc";
                            if (mSortTitle != null) {
                                mSortTitle.setTextColor(getResources().getColor(R.color.red));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else {
                            int imgResource = R.drawable.sortnormal;
                            if (mSortTitle != null)
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                            mSortTitleState = "0";
                            SortTitle = "";
                            SortOrder = "";
                            if (mSortTitle != null) {
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        }
                        // Date Sorting
                    } else if (AppUtils.isSet(sortingSettingsData.getSort_field().toString()) &&
                            sortingSettingsData.getSort_field().equals("event_start_date")) {
                        if (sortingSettingsData.getSort_type().equals("desc")) {
                            int imgResource = R.drawable.assending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortDateState = "1";
                            SortTitle = "event_start_date";
                            SortOrder = "desc";
                            if (mSortTitle != null) {
                                mSortdate.setTextColor(getResources().getColor(R.color.red));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else if (sortingSettingsData.getSort_type().equals("asc")) {
                            int imgResource = R.drawable.decending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortDateState = "2";
                            SortTitle = "event_start_date";
                            SortOrder = "asc";
                            if (mSortTitle != null) {
                                mSortdate.setTextColor(getResources().getColor(R.color.red));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else {
                            int imgResource = R.drawable.sortnormal;
                            if (mSortTitle != null)
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                            mSortDateState = "0";
                            SortTitle = "";
                            SortOrder = "";
                            if (mSortTitle != null) {
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        }
                        // Location Sorting
                    } else if (AppUtils.isSet(sortingSettingsData.getSort_field().toString()) &&
                            sortingSettingsData.getSort_field().equals("event_location")) {
                        if (sortingSettingsData.getSort_type().equals("desc")) {
                            int imgResource = R.drawable.assending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortLocationState = "1";
                            SortTitle = "event_location";
                            SortOrder = "desc";
                            if (mSortTitle != null) {
                                mSortLocation.setTextColor(getResources().getColor(R.color.red));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else if (sortingSettingsData.getSort_type().equals("asc")) {
                            int imgResource = R.drawable.decending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortLocationState = "2";
                            SortTitle = "event_location";
                            SortOrder = "asc";
                            if (mSortTitle != null) {
                                mSortLocation.setTextColor(getResources().getColor(R.color.red));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else {
                            int imgResource = R.drawable.sortnormal;
                            if (mSortTitle != null)
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                            mSortLocationState = "0";
                            SortTitle = "";
                            SortOrder = "";
                            if (mSortTitle != null) {
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        }
                        // Popular Sorting
                    } else if (AppUtils.isSet(sortingSettingsData.getSort_field().toString()) &&
                            sortingSettingsData.getSort_field().equals("event_count_participants")) {
                        if (sortingSettingsData.getSort_type().equals("desc")) {
                            int imgResource = R.drawable.assending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortPopularState = "1";
                            SortTitle = "event_count_participants";
                            SortOrder = "desc";
                            if (mSortTitle != null) {
                                mSortPopular.setTextColor(getResources().getColor(R.color.red));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else if (sortingSettingsData.getSort_type().equals("asc")) {
                            int imgResource = R.drawable.decending;
                            int imgOtherResource = R.drawable.sortnormal;
                            if (mSortTitle != null) {
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                                mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                                mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                            }
                            mSortPopularState = "2";
                            SortTitle = "event_count_participants";
                            SortOrder = "desc";
                            if (mSortTitle != null) {
                                mSortPopular.setTextColor(getResources().getColor(R.color.red));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        } else {
                            int imgResource = R.drawable.sortnormal;
                            if (mSortTitle != null)
                                mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                            mSortPopularState = "0";
                            SortTitle = "";
                            SortOrder = "";
                            if (mSortTitle != null) {
                                mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                                mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                            }
                        }
                    }
                    //category data
                    if (AppUtils.isSet(sortingSettingsData.getCategoryID() + "")) {
                        int spinnerPos = 0;
                        if (category_list != null && category_list.size() != 0)
                            for (int i = 0; i < category_list.size(); i++)
                                if (category_list.get(i).getEcatId() == sortingSettingsData.getCategoryID())
                                    spinnerPos = i;
                        if (spinnerCategory != null)
                            spinnerCategory.setSelection(spinnerPos);
                        mCategoryId = sortingSettingsData.getCategoryID();
                    }

                    // Distance progress value
                    if (AppUtils.isSet(sortingSettingsData.getDistance_filter().toString())) {
                        if (distance_value != null)
                            distance_value.setProgress(Integer.parseInt(sortingSettingsData.getDistance_filter()));
                        mDistanceVal = Integer.parseInt(sortingSettingsData.getDistance_filter());
                    }
                    // Location text
                    if (AppUtils.isSet(sortingSettingsData.getLocation_filter().toString())) {
                        if (location_et != null)
                            location_et.setText(sortingSettingsData.getLocation_filter());
                        locationString = sortingSettingsData.getLocation_filter();
                    }
                    // Creator text
                    if (AppUtils.isSet(sortingSettingsData.getCreator_filter().toString())) {
                        if (creater_et != null)
                            creater_et.setText(sortingSettingsData.getCreator_filter());
                        creatorString = sortingSettingsData.getCreator_filter();
                    }
                    // Date text
                    if (AppUtils.isSet(sortingSettingsData.getDate_filter().toString())) {
                        if (date_et != null)
                            date_et.setText(sortingSettingsData.getDate_filter());
                        dateString = sortingSettingsData.getDate_filter();
                    }
                    // Time text
                    if (AppUtils.isSet(sortingSettingsData.getTime_filter().toString())) {
                        if (time_et != null)
                            time_et.setText(sortingSettingsData.getTime_filter());
                        timeString = sortingSettingsData.getTime_filter();
                    }
                    // Event type text
                    if (AppUtils.isSet(sortingSettingsData.getType().toString())) {
                        if (sortingSettingsData.getType().equals("live")) {
                            if (mSortTypeLive != null)
                                mSortTypeLive.setChecked(true);
                            EventType = sortingSettingsData.getType();
                        }
                        if (sortingSettingsData.getType().equals("past")) {
                            if (mSortTypePast != null)
                                mSortTypePast.setChecked(true);
                            EventType = sortingSettingsData.getType();
                        }
                        if (sortingSettingsData.getType().equals("future")) {
                            if (mSortTypeFuture != null)
                                mSortTypeFuture.setChecked(true);
                            EventType = sortingSettingsData.getType();
                        }
                    }
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sort_bt:
                OpenDialog();
                break;
            case R.id.bar_code_bt:

                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Place a barcode inside the rectangle.");
                //integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                //integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.forSupportFragment(this).initiateScan();

                break;
            case R.id.filter_bt:
                OpenDialog();
                break;
            case R.id.reset_bt:
                alertDialog.dismiss();
                SharedPreference.saveSortingSettings(getActivity(), null);
                break;
            case R.id.applay_bt:
                alertDialog.dismiss();
                mDistanceVal = distance_value.getProgress();
                dateString = date_et.getText().toString();
                timeString = time_et.getText().toString();
                creatorString = creater_et.getText().toString();
                locationString = location_et.getText().toString();
                SortingSettingsData sortingSettingsData = new SortingSettingsData(
                        EventType, SortOrder, SortTitle, String.valueOf(distance_value.getProgress()),
                        date_et.getText().toString(), creater_et.getText().toString(), time_et.getText().toString(), location_et.getText().toString(), Search_view.getText().toString());
                sortingSettingsData.setCategoryID(mCategoryId);
                SharedPreference.saveSortingSettings(getActivity(), sortingSettingsData);
                if (EventType.equalsIgnoreCase("future")) {
                    callGetSortEvents();
                }
                if (EventType.equalsIgnoreCase("past")) {

                    EventsFragment.setPageByUser(0);
                } else if (EventType.equalsIgnoreCase("live")) {
                    EventsFragment.setPageByUser(1);
                }


                break;
            case R.id.date_et:
                datePicker();
                break;
            case R.id.time_et:
                TimePicker();
                break;
            case R.id.title_rb:
                if (mSortTitleState.equalsIgnoreCase("0")) {
                    int imgResource = R.drawable.assending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitleState = "1";
                    SortTitle = "event_title";
                    SortOrder = "asc";
                    mSortTitle.setTextColor(getResources().getColor(R.color.red));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));

                } else if (mSortTitleState.equalsIgnoreCase("1")) {
                    int imgResource = R.drawable.decending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitleState = "2";
                    SortTitle = "event_title";
                    SortOrder = "desc";
                    mSortTitle.setTextColor(getResources().getColor(R.color.red));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                } else if (mSortTitleState.equalsIgnoreCase("2")) {
                    int imgResource = R.drawable.sortnormal;
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortTitleState = "0";
                    SortTitle = "";
                    SortOrder = "";
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));

                }
                break;
            case R.id.date_rb:

                if (mSortDateState.equalsIgnoreCase("0")) {
                    int imgResource = R.drawable.assending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortDateState = "1";
                    SortTitle = "event_start_date";
                    SortOrder = "asc";
                    mSortdate.setTextColor(getResources().getColor(R.color.red));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                } else if (mSortDateState.equalsIgnoreCase("1")) {
                    int imgResource = R.drawable.decending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortDateState = "2";
                    SortTitle = "event_start_date";
                    SortOrder = "desc";
                    mSortdate.setTextColor(getResources().getColor(R.color.red));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));

                } else if (mSortDateState.equalsIgnoreCase("2")) {
                    int imgResource = R.drawable.sortnormal;
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortDateState = "0";
                    SortTitle = "";
                    SortOrder = "";
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));

                }
                // do operations specific to this selection
                break;
            case R.id.location_rb:
                if (mSortLocationState.equalsIgnoreCase("0")) {
                    int imgResource = R.drawable.assending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortLocationState = "1";
                    SortTitle = "event_location";
                    SortOrder = "asc";
                    mSortLocation.setTextColor(getResources().getColor(R.color.red));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));

                } else if (mSortLocationState.equalsIgnoreCase("1")) {
                    int imgResource = R.drawable.decending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortLocationState = "2";
                    SortTitle = "event_location";
                    SortOrder = "desc";
                    mSortLocation.setTextColor(getResources().getColor(R.color.red));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));

                } else if (mSortLocationState.equalsIgnoreCase("2")) {
                    int imgResource = R.drawable.sortnormal;
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortLocationState = "0";
                    SortTitle = "";
                    SortOrder = "";
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));

                }
                // do operations specific to this selection
                break;
            case R.id.popular_rb:
                if (mSortPopularState.equalsIgnoreCase("0")) {
                    int imgResource = R.drawable.assending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopularState = "1";
                    SortTitle = "event_count_participants";
                    SortOrder = "desc";
                    mSortPopular.setTextColor(getResources().getColor(R.color.red));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));

                } else if (mSortPopularState.equalsIgnoreCase("1")) {
                    int imgResource = R.drawable.decending;
                    int imgOtherResource = R.drawable.sortnormal;
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);
                    mSortdate.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortTitle.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortLocation.setCompoundDrawablesWithIntrinsicBounds(imgOtherResource, 0, 0, 0);
                    mSortPopularState = "2";
                    SortTitle = "event_count_participants";
                    SortOrder = "desc";
                    mSortPopular.setTextColor(getResources().getColor(R.color.red));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));

                } else if (mSortPopularState.equalsIgnoreCase("2")) {
                    int imgResource = R.drawable.sortnormal;
                    mSortPopular.setCompoundDrawablesWithIntrinsicBounds(imgResource, 0, 0, 0);

                    mSortPopularState = "0";
                    SortTitle = "";
                    SortOrder = "";
                    mSortPopular.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortTitle.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortLocation.setTextColor(getResources().getColor(R.color.txtlightgray));
                    mSortdate.setTextColor(getResources().getColor(R.color.txtlightgray));

                }
                // do operations specific to this selection
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                //Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                if (result != null && result.getContents() != null) {
                    //Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                    if (result.getContents() != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(result.getContents());
                            String appName = jsonObj.getString("app_name");
                            if (AppUtils.isSet(appName) && appName.equals("gcam")) {
                                Integer eventId = jsonObj.getInt("event_id");

                                EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("event_id", eventId.toString());

                                if (SharedPreference.isUserLoggedIn(getActivity()) && SharedPreference.getUserDetails(getActivity()) != null) {
                                    LoginResult userModel = SharedPreference.getUserDetails(getActivity());
                                    if (userModel.getUserData().getUserId().equals(eventId))
                                        args.putString("isself", "1");
                                    else
                                        args.putString("isself", "0");
                                }

                                args.putString("isSelfProfile", "0");
                                eventDetailsFragment.setArguments(args);
                                ((MainActivity) getActivity()).launchNewFragment(eventDetailsFragment, R.id.container);
                            }
                        } catch (final JSONException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class GetAddressAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        Context context;
        double LATITUDE;
        double LONGITUDE;

        public GetAddressAsync(Context context, double LATITUDE, double LONGITUDE) {
            this.context = context;
            this.LATITUDE = LATITUDE;
            this.LONGITUDE = LONGITUDE;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");

            String latLng = getCompleteAddressString(LATITUDE, LONGITUDE);
            return latLng;
        }

        protected void onProgressUpdate(Integer... a) {
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String latLng) {
            super.onPostExecute(latLng);
            Log.d(TAG + " onPostExecute", "" + latLng);
            if (AppUtils.isSet(latLng)) {
            }
        }
    }


    // Getting Address from latitude and longitude
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() != 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    private void showProgressBar(final boolean progressVisible) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
                }
            });
    }

    public void callGetSortEvents() {


        EventListData.clear();
        currentPage = 1;

        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    getActivity().getString(R.string.alert_information),
                    getActivity().getString(R.string.alert_internet_connection));
            return;
        }
        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("type", EventType);
            paramObject.addProperty("sort_type", SortOrder);
            paramObject.addProperty("sort_field", SortTitle);
            paramObject.addProperty("distance_filter", mDistanceVal);
            paramObject.addProperty("date_filter", dateString);
            paramObject.addProperty("creator_filter", creatorString);
            paramObject.addProperty("time_filter", timeString);
            paramObject.addProperty("location_filter", locationString);
            paramObject.addProperty("search_keyword", Search_view.getText().toString());
            paramObject.addProperty("event_category", mCategoryId);
            tv_noEvent.setVisibility(View.GONE);
            showProgressBar(true);
            getEventsDataFirst(currentPage, paramObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callSearchEvents(String search) {


        EventListData.clear();
        currentPage = 1;

        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    getActivity().getString(R.string.alert_information),
                    getActivity().getString(R.string.alert_internet_connection));
            return;
        }
        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("type", EventType);
            paramObject.addProperty("search_keyword", search);
            tv_noEvent.setVisibility(View.GONE);
            showProgressBar(true);
            getEventsDataFirst(currentPage, paramObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callGetEventsFirst() {


        EventListData.clear();
        currentPage = 1;


        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    getActivity().getString(R.string.alert_information),
                    getActivity().getString(R.string.alert_internet_connection));
            return;
        }
        try {
            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("type", "future");
            tv_noEvent.setVisibility(View.GONE);
            showProgressBar(true);
            getEventsDataFirst(currentPage, paramObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callEventsListNext() {

        if (currentPage < TOTAL_PAGES) {
            currentPage = currentPage + 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!AppUtils.isOnline(getActivity())) {
                            DialogFactory.showDropDownNotification(getActivity(),
                                    getActivity().getString(R.string.alert_information),
                                    getActivity().getString(R.string.alert_internet_connection));
                            return;
                        }
                        JsonObject paramObject = new JsonObject();

                        paramObject.addProperty("type", "future");

                        tv_noEvent.setVisibility(View.GONE);
                        getEventsDataNext(currentPage, paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            recyclerViewEvents.post(new Runnable() {
                public void run() {
                    // There is no need to use notifyDataSetChanged()
                    isLoading = false;
                    mAdapter.removeLoadingFooter();
                    isLastPage = true;
                }
            });
        }
    }

    private void getEventsDataFirst(int page, JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsData(page, params, new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        category_list.clear();
                        category_list.addAll(response.body().getCategories());
                        if (response.body().getData() != null && response.body().getData().getData() != null) {
                            if (response.body().getData().getData().size() > 0) {

                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                tv_noEvent.setVisibility(View.GONE);
                                recyclerViewEvents.setVisibility(View.VISIBLE);

                                currentPage = response.body().getData().getCurrentPage();
                                TOTAL_PAGES = response.body().getData().getLastPage();


                                EventListData.clear();
                                EventListData.addAll(response.body().getData().getData());
                                recyclerViewEvents.setHasFixedSize(true);
                                mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                recyclerViewEvents.setLayoutManager(mLayoutManager);
                                recyclerViewEvents.setItemAnimator(new DefaultItemAnimator());
                                mAdapter = new EventsListAdapter(getActivity(), EventListData, "future");
                                recyclerViewEvents.setAdapter(mAdapter);
                                recyclerViewEvents.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                    @Override
                                    protected void loadMoreItems() {
                                        isLoading = true;
                                        callEventsListNext();
                                    }

                                    @Override
                                    public int getTotalPageCount() {
                                        return TOTAL_PAGES;
                                    }

                                    @Override
                                    public boolean isLastPage() {
                                        return isLastPage;
                                    }

                                    @Override
                                    public boolean isLoading() {
                                        return isLoading;
                                    }


                                });
                                if (currentPage <= TOTAL_PAGES) {
                                    mAdapter.addLoadingFooter();
                                } else {
                                    isLastPage = true;
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                            } else {
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                recyclerViewEvents.setVisibility(View.GONE);
                                tv_noEvent.setVisibility(View.VISIBLE);
                                showProgressBar(false);
                            }
                        } else {
                            recyclerViewEvents.setVisibility(View.GONE);
                            tv_noEvent.setVisibility(View.VISIBLE);
                            showProgressBar(false);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            Alerter.create(getActivity())
                                    .setBackgroundColorInt(getResources().getColor(R.color.colorRed))
                                    .setIcon(R.drawable.icon_uncheck)
                                    .setTitle(response.body().getMessage()).show();
                        }
                        recyclerViewEvents.setVisibility(View.GONE);
                        tv_noEvent.setVisibility(View.VISIBLE);
                        showProgressBar(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                recyclerViewEvents.setVisibility(View.GONE);
                tv_noEvent.setVisibility(View.VISIBLE);
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });


    }

    private void getEventsDataNext(int page, JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsData(page, params, new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData() != null && response.body().getData().getData() != null) {
                            if (response.body().getData().getData().size() > 0) {
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                tv_noEvent.setVisibility(View.GONE);
                                recyclerViewEvents.setVisibility(View.VISIBLE);

                                currentPage = response.body().getData().getCurrentPage();
                                TOTAL_PAGES = response.body().getData().getLastPage();
                                mAdapter.removeLoadingFooter();
                                isLoading = false;

                                EventListData.addAll(response.body().getData().getData());


                                mAdapter.notifyDataSetChanged();
                                recyclerViewEvents.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                    @Override
                                    protected void loadMoreItems() {
                                        isLoading = true;
                                        callEventsListNext();
                                    }

                                    @Override
                                    public int getTotalPageCount() {
                                        return TOTAL_PAGES;
                                    }

                                    @Override
                                    public boolean isLastPage() {
                                        return isLastPage;
                                    }

                                    @Override
                                    public boolean isLoading() {
                                        return isLoading;
                                    }
                                });
                                if (currentPage < TOTAL_PAGES) {
                                    mAdapter.addLoadingFooter();
                                } else {
                                    isLastPage = true;
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }

                            } else {
                                mAdapter.removeLoadingFooter();
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                recyclerViewEvents.setVisibility(View.GONE);
                                tv_noEvent.setVisibility(View.VISIBLE);
                                showProgressBar(false);
                            }
                        } else {
                            recyclerViewEvents.setVisibility(View.GONE);
                            tv_noEvent.setVisibility(View.VISIBLE);
                            showProgressBar(false);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            Alerter.create(getActivity())
                                    .setBackgroundColorInt(getResources().getColor(R.color.colorRed))
                                    .setIcon(R.drawable.icon_uncheck)
                                    .setTitle(response.body().getMessage()).show();
                        }
                        recyclerViewEvents.setVisibility(View.GONE);
                        tv_noEvent.setVisibility(View.VISIBLE);
                        showProgressBar(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                recyclerViewEvents.setVisibility(View.GONE);
                tv_noEvent.setVisibility(View.VISIBLE);
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    public void SearchListner() {
        Search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (AppUtils.isSet(s.toString()))
                    callSearchEvents(s.toString());


                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        // getCompleteAddressString(location.getLatitude(), location.getLongitude());
        new GetAddressAsync(getApplicationContext(), location.getLatitude(), location.getLongitude()).execute();
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
    public void retryPageLoad() {

    }

    public void addItemsOnSpinnerCategory() {
        FilterCategoryAdapter dataAdapter = new FilterCategoryAdapter(getActivity(),
                R.layout.spinner_cat_item, category_list);

        spinnerCategory.setAdapter(dataAdapter);

    }
}