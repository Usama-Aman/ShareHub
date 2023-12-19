package adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;
import fragments.EventDetailsFragment;
import models.EventsParticipantsResponse;
import models.EventsResponse;
import models.LoginResult;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.PaginationAdapterCallback;
import utility.SharedPreference;

/**
 * Created by Mubashir on 19/05/18.
 */

public class EventsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static boolean fromEventListAdapter = false;
    public static String fromState = "";
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private PaginationAdapterCallback mCallback;
    private ArrayList<EventsResponse.EventData> events;
    ArrayList<String> eventImageList = new ArrayList<String>();
    private Activity context;
    private String mstate;
    private String mMonth, mWeek;
    String errorMsg;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    public Date date;


    public EventsListAdapter(Activity context, ArrayList<EventsResponse.EventData> eventListData, String eventstate) {
        this.context = context;
        events = eventListData;
        this.mstate = eventstate;
      //  fromState = eventstate;
    }

    public List<EventsResponse.EventData> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<EventsResponse.EventData> movies) {
        this.events = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress_bar, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.custom_live_events, parent, false);
        viewHolder = new EventsVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final EventsResponse.EventData event = events.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final EventsVH eventsVH = (EventsVH) holder;
                eventsVH.title.setText(event.getEventTitle());
                eventsVH.mEventDiscription.setText(event.getEventDescription());

                if (!mstate.equalsIgnoreCase("past")) {
                    if (SharedPreference.isUserLoggedIn(context) && SharedPreference.getUserDetails(context) != null) {
                        LoginResult userModel = SharedPreference.getUserDetails(context);
                        if (!userModel.getUserData().getUserId().equals(event.getUserId())) {
                            if (event.getIsAttend() == 0) {
                                eventsVH.mEventAttendingLiveStatus.setVisibility(View.VISIBLE);
                                eventsVH.mEventCancelLiveStatus.setVisibility(View.GONE);
                            } else {
                                eventsVH.mEventAttendingLiveStatus.setVisibility(View.GONE);
                                eventsVH.mEventCancelLiveStatus.setVisibility(View.VISIBLE);
                            }
                        } else {
                            eventsVH.mEventAttendingLiveStatus.setVisibility(View.GONE);
                            eventsVH.mEventCancelLiveStatus.setVisibility(View.GONE);
                        }
                    }
                } else {
                    eventsVH.mEventAttendingLiveStatus.setVisibility(View.GONE);
                    eventsVH.mEventCancelLiveStatus.setVisibility(View.GONE);
                }


                if (mstate.contains("future")) {
                    eventsVH.mEventStatus.setImageResource(R.drawable.deactive_live);//grey future
                } else if (mstate.equalsIgnoreCase("live")) {
                    eventsVH.mEventStatus.setImageResource(R.drawable.ic_live_normal);//live green
                } else {
                    eventsVH.mEventStatus.setImageResource(R.drawable.active_live);//past red
                }

                if (event.getUserAttendEvent().size() > 2) {
                    Picasso.with(context)
                            .load(event.getUserAttendEvent().get(0).getFullImage())   //
                            .placeholder(R.drawable.user_placeholder) // optional
                            .error(R.drawable.user_placeholder)         // optional
                            .into(eventsVH.mFirstUser);
                    Picasso.with(context)
                            .load(event.getUserAttendEvent().get(1).getFullImage())   //
                            .placeholder(R.drawable.user_placeholder) // optional
                            .error(R.drawable.user_placeholder)         // optional
                            .into(eventsVH.mSecondUser);
                    eventsVH.mUserCount.setText(event.getUserAttendEvent().size() + "");
                    eventsVH.mAttendingUers.setVisibility(View.VISIBLE);
                } else {
                    eventsVH.mAttendingUers.setVisibility(View.GONE);
                }

                if (events.get(position).getMediaList() != null && events.get(position).getMediaList().size() > 0) {
                    if (eventImageList != null) {
                        eventImageList.clear();
                    }
                    for (int k = 0; k < event.getMediaList().size(); k++) {
                        eventImageList.add(event.getMediaList().get(k).getFullImage());
                    }

                    if (eventImageList.size() > 1) {
                        eventsVH.mEventCover.setVisibility(View.VISIBLE);
                        eventsVH.mImageCover.setVisibility(View.GONE);
                        eventsVH.mIndicator.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);

                        for (String name : eventImageList) {
                            DefaultSliderView textSliderView = new DefaultSliderView(context);
                            // initialize a SliderLayout
                            textSliderView
                                    .image(name)
                                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            fromEventListAdapter = true;
                                            AppUtils.hideKeyboard(context);
                                            EventDetailsFragment.isSaveEventId = event.getEventId().toString();

                                            EventDetailsFragment optionsFrag = new EventDetailsFragment();
                                            Bundle args = new Bundle();
                                            args.putString("event_id", event.getEventId().toString());
                                            if (SharedPreference.isUserLoggedIn(context) && SharedPreference.getUserDetails(context) != null) {
                                                LoginResult userModel = SharedPreference.getUserDetails(context);
                                                if (userModel.getUserData().getUserId().equals(event.getUserId())) {
                                                    args.putString("isself", "1");
                                                } else {
                                                    args.putString("isself", "0");
                                                }
                                            }
                                            args.putString("isSelfProfile", "eventList");
                                            args.putString("isEventStatus", mstate);
                                            optionsFrag.setArguments(args);
                                            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.container, optionsFrag, "OptionsFragment")
                                                    .addToBackStack(null).commit();
                                        }
                                    });
                            eventsVH.mEventCover.addSlider(textSliderView);


                        }
                        eventsVH.mEventCover.setPresetTransformer(SliderLayout.Transformer.Default);

                        eventsVH.mEventCover.setCustomIndicator(eventsVH.mIndicator);
                        eventsVH.mEventCover.setCurrentPosition(0);
                        eventsVH.mEventCover.stopAutoCycle();
                    } else {
                        eventsVH.mEventCover.setVisibility(View.GONE);
                        eventsVH.mImageCover.setVisibility(View.VISIBLE);
                        eventsVH.mIndicator.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

                        Picasso.with(context)
                                .load(event.getFullImage())   //
                                .placeholder(R.drawable.ic_placeholder) // optional
                                .error(R.drawable.ic_placeholder)         // optional
                                .into(eventsVH.mImageCover);

                        eventsVH.mImageCover.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fromEventListAdapter = true;

                                AppUtils.hideKeyboard(context);
                                EventDetailsFragment.isSaveEventId = event.getEventId().toString();
                                EventDetailsFragment optionsFrag = new EventDetailsFragment();
                                Bundle args = new Bundle();
                                args.putString("event_id", event.getEventId().toString());
                                if (SharedPreference.isUserLoggedIn(context) && SharedPreference.getUserDetails(context) != null) {
                                    LoginResult userModel = SharedPreference.getUserDetails(context);
                                    if (userModel.getUserData().getUserId().equals(event.getUserId())) {
                                        args.putString("isself", "1");
                                    } else {
                                        args.putString("isself", "0");
                                    }
                                }
                                args.putString("isSelfProfile", "eventList");
                                args.putString("isEventStatus", mstate);
                                optionsFrag.setArguments(args);
                                ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, optionsFrag, "OptionsFragment")
                                        .addToBackStack(null).commit();
                            }
                        });


                    }


                }

                eventsVH.mDate.setText(getMonth(event.getEventStartDate()));
                eventsVH.mweekday.setText(getWeekDay(event.getEventStartDate()));
                eventsVH.mEventAttendingLiveStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SharedPreference.isUserLoggedIn(context)) {
                            eventsVH.mEventAttendingLiveStatus.setVisibility(View.GONE);
                            eventsVH.mEventCancelLiveStatus.setVisibility(View.VISIBLE);

                            if (!AppUtils.isOnline(context)) {
                                return;
                            }
                            try {
                                JsonObject paramObject = new JsonObject();
                                paramObject.addProperty("event_id", event.getEventId());
                                callAttendingCancel(paramObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent act = new Intent(context, MainActivity.class);
                            context.startActivity(act);
                            context.finish();
                        }
                    }
                });
                eventsVH.mEventCancelLiveStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (SharedPreference.isUserLoggedIn(context)) {
                            eventsVH.mEventAttendingLiveStatus.setVisibility(View.VISIBLE);
                            eventsVH.mEventCancelLiveStatus.setVisibility(View.GONE);
                            if (!AppUtils.isOnline(context)) {
                                return;
                            }
                            try {
                                JsonObject paramObject = new JsonObject();
                                paramObject.addProperty("event_id", event.getEventId());
                                callAttendingCancel(paramObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent act = new Intent(context, MainActivity.class);
                            context.startActivity(act);
                            context.finish();
                        }
                    }
                });

                eventsVH.mItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fromEventListAdapter = true;
                        AppUtils.hideKeyboard(context);
                        EventDetailsFragment.isSaveEventId = event.getEventId().toString();

                        EventDetailsFragment optionsFrag = new EventDetailsFragment();
                        Bundle args = new Bundle();
                        args.putString("event_id", event.getEventId().toString());
                        if (SharedPreference.isUserLoggedIn(context) && SharedPreference.getUserDetails(context) != null) {
                            LoginResult userModel = SharedPreference.getUserDetails(context);
                            if (userModel.getUserData().getUserId().equals(event.getUserId())) {
                                args.putString("isself", "1");
                            } else {
                                args.putString("isself", "0");
                            }
                        }
                        args.putString("isSelfProfile", "eventList");
                        args.putString("isEventStatus", mstate);
                        optionsFrag.setArguments(args);
                        ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, optionsFrag, "OptionsFragment")
                                .addToBackStack(null).commit();

                    }
                });
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.alert_msg_unknown));
                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == events.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(EventsResponse.EventData mc) {
        events.add(mc);
        notifyItemInserted(events.size() - 1);
    }

    public void addAll(List<EventsResponse.EventData> mcList) {
        for (EventsResponse.EventData mc : mcList) {
            add(mc);
        }
    }

    public void remove(EventsResponse.EventData city) {
        int position = events.indexOf(city);
        if (position > -1) {
            events.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new EventsResponse.EventData());
    }

    public void removeLoadingFooter() {
        try {
            isLoadingAdded = false;

            int position = events.size() - 1;
            if (position < 0)
                return;
            EventsResponse.EventData item = getItem(position);

            if (item != null) {
                events.remove(position);
                notifyItemRemoved(position);
            }
        } catch (Exception e) {

        }
    }

    public EventsResponse.EventData getItem(int position) {
        return events.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class EventsVH extends RecyclerView.ViewHolder {
        private PagerIndicator mIndicator;
        public TextView title, mEventDiscription, mweekday, mDate, mUserCount;
        public ImageView mEventStatus, mImageCover;
        public SliderLayout mEventCover;
        public CircleImageView mFirstUser, mSecondUser;
        public Button mEventAttendingLiveStatus, mEventCancelLiveStatus;
        public RelativeLayout mAttendingUers, mItem;

        public EventsVH(View view) {
            super(view);

            title = view.findViewById(R.id.event_title);
            mItem = view.findViewById(R.id.rlMainContent);
            mweekday = view.findViewById(R.id.week_day);
            mDate = view.findViewById(R.id.date);
            mEventDiscription = view.findViewById(R.id.event_discription);
            mEventStatus = view.findViewById(R.id.status_img);
            mEventAttendingLiveStatus = view.findViewById(R.id.status_bt);
            mEventCancelLiveStatus = view.findViewById(R.id.cancelstatus_bt);
            mEventCover = view.findViewById(R.id.slider);
            mAttendingUers = view.findViewById(R.id.rl_users);
            mFirstUser = view.findViewById(R.id.first_user);
            mSecondUser = view.findViewById(R.id.second_user);
            mIndicator = view.findViewById(R.id.indicator);
            mUserCount = view.findViewById(R.id.all_users);
            mImageCover = view.findViewById(R.id.imgCover);
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(events.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    public String getMonth(String startdate) {

        StringTokenizer st = new StringTokenizer(startdate, "-");
        String styear = st.nextToken();
        String stMonth = st.nextToken();
        String stdate = st.nextToken();
        int Month = Integer.parseInt(stMonth);

        if (Month == 1) {
            mMonth = context.getResources().getString(R.string.jan) + " " + stdate;
        } else if (Month == 2) {
            mMonth = context.getResources().getString(R.string.feb) + " " + stdate;

        } else if (Month == 3) {
            mMonth = context.getResources().getString(R.string.mar) + " " + stdate;

        } else if (Month == 4) {
            mMonth = context.getResources().getString(R.string.apr) + " " + stdate;

        } else if (Month == 5) {
            mMonth = context.getResources().getString(R.string.may) + " " + stdate;

        } else if (Month == 6) {
            mMonth = context.getResources().getString(R.string.jun) + " " + stdate;

        } else if (Month == 7) {
            mMonth = context.getResources().getString(R.string.jul) + " " + stdate;
        } else if (Month == 8) {
            mMonth = context.getResources().getString(R.string.aug) + " " + stdate;

        } else if (Month == 9) {
            mMonth = context.getResources().getString(R.string.sep) + " " + stdate;

        } else if (Month == 10) {
            mMonth = context.getResources().getString(R.string.oct) + " " + stdate;

        } else if (Month == 11) {
            mMonth = context.getResources().getString(R.string.nov) + " " + stdate;

        } else if (Month == 12) {
            mMonth = context.getResources().getString(R.string.dec) + " " + stdate;
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
                mWeek = context.getResources().getString(R.string.sun);

                break;
            case 2:
                mWeek = context.getResources().getString(R.string.mon);
                break;
            case 3:

                mWeek = context.getResources().getString(R.string.tue);
                break;
            case 4:
                mWeek = context.getResources().getString(R.string.wed);
                break;
            case 5:
                mWeek = context.getResources().getString(R.string.thu);
                break;
            case 6:
                mWeek = context.getResources().getString(R.string.fri);
                break;
            case 7:
                mWeek = context.getResources().getString(R.string.sat);
                break;
        }
        return mWeek;

    }

    private void callAttendingCancel(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsParticipants(params, new Callback<EventsParticipantsResponse>() {
            @Override
            public void onResponse(Call<EventsParticipantsResponse> call, final retrofit2.Response<EventsParticipantsResponse> response) {
                try {
                    Constants.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.body().getStatus()) {

                    //response.body().getMessage();
                } else {
                }
            }

            @Override
            public void onFailure(Call<EventsParticipantsResponse> call, Throwable t) {
                try {
                    Constants.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}