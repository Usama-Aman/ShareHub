package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialog;


import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.EventAttendingDialog;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import models.EventDetailsResponseData;
import models.EventsParticipantsResponse;
import models.GeneralResponse;
import models.LoginResult;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.FragmentStackPref;
import utility.SharedPreference;

/**
 * Mubashir 5/16/2018
 */
public class EventDetailsFragment extends Fragment implements View.OnClickListener {
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageView mEventImage;
    public static String eventID, isSelfUser, isAttendEvent, isProfile;
    public static String eventTitle, isEventStatus, startDate, endDate, startTime, endTime, eventQR;
    TextView meventTitle, mWeekDay, mDate, mTime, mEndTime;
    public String mParticipantsCount;
    Button btAttending, btCancel, btPending;
    private ImageView btBack;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    EventOverviewFragment OverviewFragment;
    EventPeopleFragment PeopleFragment;
    public int isAttend;
    public static String isSaveEventId;
    private Integer eventIsPrivate;
    private Integer eventIsApprovalRequired;
    private Integer eventIsPincodeRequired;
    private Integer isApproved;
    private String eventPincode;
    int userId;
    private String mMonth, mWeek;
    private FrameLayout progressBar;
    public static Date date;
    private boolean isAttendingClicked = false;
    FragmentStackPref fragmentStackPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_details, container, false);
        fragmentStackPref = new FragmentStackPref(getActivity());
        SharedPreference.saveSortingSettings(getActivity(), null);
        getActivityData();
        CastView(view);
        callEventDetails();
        return view;
    }


    private void getActivityData() {

        eventID = getArguments().getString("event_id");
        isProfile = getArguments().getString("isSelfProfile");

    }

    //initialized Views
    public void CastView(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.bringToFront();
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpagerprofile);
        mEventImage = view.findViewById(R.id.event_image);
        meventTitle = view.findViewById(R.id.event_title);
        mWeekDay = view.findViewById(R.id.tv_weekday);
        mDate = view.findViewById(R.id.tv_date);
        mTime = view.findViewById(R.id.event_time);
        mEndTime = view.findViewById(R.id.event_end_time);
        btAttending = view.findViewById(R.id.attending_bt);
        btCancel = view.findViewById(R.id.cancel_bt);
        btPending = view.findViewById(R.id.pending_bt);
        btBack = view.findViewById(R.id.btBack);
        btBack.bringToFront();


        btAttending.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btBack.setOnClickListener(this);


    }

    public void initToolBar(View view) {
        toolBar = (Toolbar) view.findViewById(R.id.toolBar);

        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(getActivity(), Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(getActivity(), Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.txt_eventDetail));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attending_bt:

                // Different Conditions Depend on Event state is Private or public
                if (eventIsApprovalRequired == 0) {
                    btCancel.setVisibility(View.VISIBLE);
                    btAttending.setVisibility(View.GONE);
                } else {
                    btAttending.setVisibility(View.VISIBLE);
                    btCancel.setVisibility(View.GONE);
                }
                if (eventIsPrivate != null && eventIsPrivate == 1 && eventIsPincodeRequired == 1) {
                    EventAttendingDialog customDialogManager = new EventAttendingDialog(getActivity(),
                            new EventAttendingDialog.OnAddClickListener() {
                                @Override
                                public void onAdd(String paramToSend) {
                                    showProgressBar(true);
                                    AppUtils.hideKeyboard(getActivity());

                                    if (eventIsPincodeRequired != null && eventIsPincodeRequired == 1) {

                                        if (AppUtils.isSet(eventPincode) && eventPincode.equals(paramToSend)) {

                                            try {
                                                isAttendingClicked = true;
                                                JsonObject paramObject = new JsonObject();
                                                paramObject.addProperty("event_id", eventID);


                                                if (eventIsApprovalRequired == 1) {
                                                    if (isApproved == 1) {
                                                        refreshListener.onRefresh(true);
                                                    } else {
                                                        refreshListener.onRefresh(false);
                                                    }
                                                } else {
                                                    refreshListener.onRefresh(true);
                                                }
                                                callAttendingCancel(paramObject);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            DialogFactory.showDropDownNotification(
                                                    getActivity(),
                                                    getActivity().getString(R.string.tv_error),
                                                    getActivity().getString(R.string.error_msg_pincode));
                                        }
                                    } else {
                                        try {
                                            isAttendingClicked = true;
                                            JsonObject paramObject = new JsonObject();
                                            paramObject.addProperty("event_id", eventID);


                                            if (eventIsApprovalRequired == 1) {
                                                if (isApproved == 1) {
                                                    refreshListener.onRefresh(true);
                                                } else {
                                                    refreshListener.onRefresh(false);
                                                }
                                            } else {
                                                refreshListener.onRefresh(true);
                                            }
                                            callAttendingCancel(paramObject);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                    customDialogManager.show();
                } else if (eventIsPrivate != null && eventIsPrivate == 0) {
                    try {
                        isAttendingClicked = true;
                        JsonObject paramObject = new JsonObject();
                        paramObject.addProperty("event_id", eventID);

                        refreshListener.onRefresh(true);
                        callAttendingCancel(paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (eventIsPrivate != null && eventIsPrivate == 1 && eventIsPincodeRequired == 0) {
                    try {
                        isAttendingClicked = true;
                        JsonObject paramObject = new JsonObject();
                        paramObject.addProperty("event_id", eventID);


                        if (eventIsApprovalRequired == 1) {
                            if (isApproved == 1) {
                                refreshListener.onRefresh(true);
                            } else {
                                refreshListener.onRefresh(false);
                            }
                        } else {
                            refreshListener.onRefresh(true);
                        }
                        callAttendingCancel(paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.cancel_bt:
                if (isSelfUser.equalsIgnoreCase("0")) {
                    btCancel.setVisibility(View.GONE);
                    btAttending.setVisibility(View.VISIBLE);
                    refreshListener.onRefresh(false);
                    try {
                        JsonObject paramObject = new JsonObject();
                        paramObject.addProperty("event_id", eventID);
                        callAttendingCancel(paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    cancelBottomSheet();
                }


                break;
            case R.id.btBack:

                if (isProfile.equalsIgnoreCase("1")) {
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    startActivity(intent);
                } else if (isProfile.equalsIgnoreCase("mapList")) {
                    fragmentStackPref.storeSessionFragment("map");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                } else if (isProfile.equalsIgnoreCase("eventList")) {
                    fragmentStackPref.storeSessionFragment("event");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                } else if (isProfile.equalsIgnoreCase("notification")) {
                    fragmentStackPref.storeSessionFragment("notification");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                } else if (isProfile.equalsIgnoreCase("newEvent")) {
                    fragmentStackPref.storeSessionFragment("event");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                } else {
                    getActivity().onBackPressed();
                }
                break;
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        //set name of tabs
        private String[] tabTitles = new String[]{getResources().getString(R.string.txt_overview), getResources().getString(R.string.txt_people)};

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    OverviewFragment = new EventOverviewFragment();
                    return new EventOverviewFragment();

                case 1:
                    PeopleFragment = new EventPeopleFragment();
                    Bundle args = new Bundle();
                    args.putString("isSelfUser", isSelfUser);
                    PeopleFragment.setArguments(args);
                    return PeopleFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    private void cancelBottomSheet() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.cancel_bottomsheet_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView tvYes = sheetView.findViewById(R.id.tvYes);
        Button btnNo = sheetView.findViewById(R.id.btnNo);
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();
                showProgressBar(true);
                JsonObject paramObject = new JsonObject();
                paramObject.addProperty("event_id", eventID);
                cancelSelfUserEvent(paramObject);

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
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

    private void detailsShowProgressBar(final boolean progressVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    //Network Call to get Event Details
    public void callEventDetails() {
        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    getActivity().getString(R.string.alert_information),
                    getActivity().getString(R.string.alert_internet_connection));
            return;
        }
        try {
            detailsShowProgressBar(true);
            getEventsDetailsDataFirst(eventID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEventsDetailsDataFirst(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getDetailsOfEvent(id, new Callback<EventDetailsResponseData>() {
            @Override
            public void onResponse(Call<EventDetailsResponseData> call, Response<EventDetailsResponseData> response) {
                try {
                    detailsShowProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        eventQR = response.body().getData().getEvent().getQrfullImage();
                        eventTitle = response.body().getData().getEvent().getEventTitle();
                        startDate = response.body().getData().getEvent().getEventStartDate();
                        startTime = response.body().getData().getEvent().getEventStartTime();
                        endDate = response.body().getData().getEvent().getEventEndDate();
                        endTime = response.body().getData().getEvent().getEventEndTime();
                        userId = response.body().getData().getEvent().getUserId();

                        eventIsPrivate = response.body().getData().getEvent().getEventIsprivate();
                        eventIsApprovalRequired = response.body().getData().getEvent().getEventIsApprovalRequired();
                        eventIsPincodeRequired = response.body().getData().getEvent().getEventIsPincodeRequired();
                        eventPincode = String.valueOf(response.body().getData().getEvent().getEventPincode());
                        isAttend = response.body().getData().getEvent().getIsAttend();
                        eventIsApprovalRequired = response.body().getData().getEvent().getEventIsApprovalRequired();
                        isApproved = response.body().getData().getEvent().getIsApproved();
                        if (SharedPreference.isUserLoggedIn(getActivity()) && SharedPreference.getUserDetails(getActivity()) != null) {
                            LoginResult userModel = SharedPreference.getUserDetails(getActivity());
                            if (userModel.getUserData().getUserId().equals(userId)) {
                                isSelfUser = "1";
                            } else {
                                isSelfUser = "0";
                            }
                        }

                        isEventStatus = eventType(startDate + " " + startTime, endDate + " " + endTime);
                        if (SharedPreference.isUserLoggedIn(getActivity())) {
                            if (eventIsPrivate == 1) {
                                if (!isEventStatus.equalsIgnoreCase("past")) {
                                    if (isAttend == 1) {
                                        if (isSelfUser.equalsIgnoreCase("1")) {
                                            btCancel.setVisibility(View.VISIBLE);
                                            btAttending.setVisibility(View.GONE);
                                            btPending.setVisibility(View.GONE);
                                        } else {
                                            if (isApproved == 1) {
                                                btCancel.setVisibility(View.VISIBLE);
                                                btAttending.setVisibility(View.GONE);
                                                btPending.setVisibility(View.GONE);
                                            } else {
                                                btCancel.setVisibility(View.GONE);
                                                btAttending.setVisibility(View.GONE);
                                                btPending.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    } else {
                                        if (isSelfUser.equalsIgnoreCase("1")) {
                                            btCancel.setVisibility(View.VISIBLE);
                                            btAttending.setVisibility(View.GONE);
                                            btPending.setVisibility(View.GONE);
                                        } else {
                                            btCancel.setVisibility(View.GONE);
                                            btAttending.setVisibility(View.VISIBLE);
                                            btPending.setVisibility(View.GONE);
                                        }

                                    }
                                } else {
                                    btCancel.setVisibility(View.GONE);
                                    btAttending.setVisibility(View.GONE);
                                    btPending.setVisibility(View.GONE);
                                }


                            } else {
                                if (!isEventStatus.equalsIgnoreCase("past")) {
                                    if (isSelfUser.equalsIgnoreCase("0")) {
                                        btAttending.setVisibility(View.VISIBLE);
                                        btCancel.setVisibility(View.GONE);
                                        if (isAttend == 1) {
                                            btCancel.setVisibility(View.VISIBLE);
                                        } else {
                                            btAttending.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        btAttending.setVisibility(View.GONE);
                                        btCancel.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    btAttending.setVisibility(View.GONE);
                                    btCancel.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            btAttending.setVisibility(View.GONE);
                            btCancel.setVisibility(View.GONE);
                        }

                        mParticipantsCount = (response.body().getData().getParticipantsCount()).toString();
                        meventTitle.setText(eventTitle);
                        mWeekDay.setText(getWeekDay(startDate));
                        mDate.setText(getMonth(startDate));
                        mTime.setText(getWeekDay(startDate) + " : " + AppUtils.parseTimeReverse(startTime));
                        mEndTime.setText(getWeekDay(endDate) + " : " + AppUtils.parseTimeReverse(endTime));
                        if (AppUtils.isSet(response.body().getData().getEvent().getFullImage())) {
                            Picasso.with(getActivity())
                                    .load(response.body().getData().getEvent().getFullImage())   //
                                    .placeholder(R.drawable.ic_placeholder) // optional
                                    .error(R.drawable.ic_placeholder)         // optional
                                    .into(mEventImage);
                        }


                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.txt_overview)));
                        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.txt_people)));
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
                        // set properties of tab
                        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.setTabMode(TabLayout.MODE_FIXED);
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


                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    getActivity(),
                                    getActivity().getString(R.string.tv_error),
                                    response.body().getMessage());
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_internal_server_error));

                }
            }

            @Override
            public void onFailure(Call<EventDetailsResponseData> call, Throwable t) {
                try {
                    detailsShowProgressBar(false);
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

    private void callAttendingCancel(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsParticipants(params, new Callback<EventsParticipantsResponse>() {
            @Override
            public void onResponse(Call<EventsParticipantsResponse> call, final Response<EventsParticipantsResponse> response) {
                try {
                    showProgressBar(false);
                    Constants.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {

                        if (isAttendingClicked) {

                            if (eventIsApprovalRequired == 0) {
                                btCancel.setVisibility(View.VISIBLE);
                                btAttending.setVisibility(View.GONE);
                                btPending.setVisibility(View.GONE);
                            } else {
                                btAttending.setVisibility(View.GONE);
                                btPending.setVisibility(View.VISIBLE);
                                btCancel.setVisibility(View.GONE);
                                if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                                    DialogFactory.showDropDownSuccessNotification(
                                            getActivity(),
                                            getActivity().getString(R.string.tv_alert_success),
                                            response.body().getMessage());
                                }
                            }
                            isAttendingClicked = false;
                        }

                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    getActivity(),
                                    getActivity().getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<EventsParticipantsResponse> call, Throwable t) {
                try {

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

    private Date convertStrToDate(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateTime);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String eventType(String startDateTime, String endDateTime) {

/*        //Log.e("startTime: ", "---: " + startDateTime);
        //Log.e("nowTime: ", "---: " + convertStrToDate(getCurrentDate()));
        //Log.e("endTime: ", "---: " + endDateTime);*/

        return AppUtils.getEventTypeByDate(AppUtils.converterStringToDateTime(startDateTime),
                AppUtils.converterStringToDateTime(AppUtils.getCurrentDateTime()),
                AppUtils.converterStringToDateTime(endDateTime));

       /* if (convertStrToDate(startDateTime).compareTo(convertStrToDate(getCurrentDate())) > 0) {
            return "future";//future
        } else if (convertStrToDate(endDateTime).compareTo(convertStrToDate(getCurrentDate())) < 0) {
            return "past";//past
        } else if (convertStrToDate(startDateTime).compareTo(convertStrToDate(getCurrentDate())) * convertStrToDate(endDateTime).compareTo(convertStrToDate(getCurrentDate())) >= 0) {
            return "live";//live
        }*/
       // return "";
    }


    public String getMonth(String startdate) {

        StringTokenizer st = new StringTokenizer(startdate, "-");
        String styear = st.nextToken();
        String stMonth = st.nextToken();
        String stdate = st.nextToken();
        int Month = Integer.parseInt(stMonth);

        if (Month == 1) {
            mMonth = getActivity().getResources().getString(R.string.jan) + " " + stdate;
        } else if (Month == 2) {
            mMonth = getActivity().getResources().getString(R.string.feb) + " " + stdate;

        } else if (Month == 3) {
            mMonth = getActivity().getResources().getString(R.string.mar) + " " + stdate;

        } else if (Month == 4) {
            mMonth = getActivity().getResources().getString(R.string.apr) + " " + stdate;

        } else if (Month == 5) {
            mMonth = getActivity().getResources().getString(R.string.may) + " " + stdate;

        } else if (Month == 6) {
            mMonth = getActivity().getResources().getString(R.string.jun) + " " + stdate;

        } else if (Month == 7) {
            mMonth = getActivity().getResources().getString(R.string.jul) + " " + stdate;
        } else if (Month == 8) {
            mMonth = getActivity().getResources().getString(R.string.aug) + " " + stdate;

        } else if (Month == 9) {
            mMonth = getActivity().getResources().getString(R.string.sep) + " " + stdate;

        } else if (Month == 10) {
            mMonth = getActivity().getResources().getString(R.string.oct) + " " + stdate;

        } else if (Month == 11) {
            mMonth = getActivity().getResources().getString(R.string.nov) + " " + stdate;

        } else if (Month == 12) {
            mMonth = getActivity().getResources().getString(R.string.dec) + " " + stdate;
        }


        return mMonth;
    }

    public String getWeekDay(String stringDate) {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(stringDate);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date); // yourdate is an object of type Date

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case 1:
                mWeek = getActivity().getResources().getString(R.string.sun);

                break;
            case 2:
                mWeek = getActivity().getResources().getString(R.string.mon);
                break;
            case 3:

                mWeek = getActivity().getResources().getString(R.string.tue);
                break;
            case 4:
                mWeek = getActivity().getResources().getString(R.string.wed);
                break;
            case 5:
                mWeek = getActivity().getResources().getString(R.string.thu);
                break;
            case 6:
                mWeek = getActivity().getResources().getString(R.string.fri);
                break;
            case 7:
                mWeek = getActivity().getResources().getString(R.string.sat);
                break;
        }
        return mWeek;

    }


    private void showProgressBar(final boolean progressVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void cancelSelfUserEvent(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.cancelSelfEvent(paramObject, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final Response<GeneralResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (isProfile.equalsIgnoreCase("1")) {
                            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                            startActivity(intent);
                        } else if (isProfile.equalsIgnoreCase("0")) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                        } else {
                            getActivity().onBackPressed();
                        }


                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    getActivity(),
                                    getActivity().getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_internal_server_error));
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

    public interface OnEventOverViewRefreshMediaList {
        void onRefresh(Boolean isParticipant);
    }

    private static OnEventOverViewRefreshMediaList refreshListener;

    public void registerListener(OnEventOverViewRefreshMediaList listener) {
        refreshListener = listener;
    }
}