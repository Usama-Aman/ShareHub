package adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.PublicUserProfileActivity;
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
import models.JoinedEvent;
import models.LoginResult;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.SharedPreference;

/**
 * Created by Mubashir on 19/05/18.
 */

public class JoinedEventListAdapter extends RecyclerView.Adapter<JoinedEventListAdapter.EventsVH> {

    private ArrayList<JoinedEvent> events;
    ArrayList<String> eventImageList = new ArrayList<String>();
    private Context context;
    private String mstate;
    private String mMonth, mWeek;
    Date date;
    public int isSelfStatus;
    private ImageSliderAdapter imageSliderAdapter;

    public JoinedEventListAdapter(Context context, ArrayList<JoinedEvent> eventListData) {
        this.context = context;
        events = eventListData;
    }

    public List<JoinedEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<JoinedEvent> events) {
        this.events = events;
    }

    @Override
    public EventsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        EventsVH viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }

    @NonNull
    private EventsVH getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        EventsVH viewHolder;
        View v1 = inflater.inflate(R.layout.custom_live_events, parent, false);
        viewHolder = new EventsVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final EventsVH holder, int position) {

        final JoinedEvent event = events.get(position);

        EventsVH eventsVH = (EventsVH) holder;
        eventsVH.title.setText(event.getEventTitle());
        eventsVH.mEventDiscription.setText(event.getEventDescription());

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

        int eventType = event.getIsLive();

        if (eventType == 1) {
            holder.mEventStatus.setImageResource(R.drawable.ic_live_normal);
        } else {
            holder.mEventStatus.setImageResource(R.drawable.active_live);
        }

        if (SharedPreference.isUserLoggedIn(context) && SharedPreference.getUserDetails(context) != null) {
            LoginResult userModel = SharedPreference.getUserDetails(context);
            if (userModel.getUserData().getUserId().equals(event.getUserId())) {
                isSelfStatus = 1;
            } else {
                isSelfStatus = 0;

            }
        }

        // Event Attending and Cancel option only will be shown with Live and  Future Events
        // check is event is Past Live  and future by comparing start date time and end date time

        String eventState = eventType(event.getEventStartDate() + " " + event.getEventStartTime(),
                event.getEventEndDate() + " " + event.getEventEndTime());
        if (isSelfStatus == 0 && !eventState.equalsIgnoreCase("past")) {

            if (event.getIsAttend() == 0) {
                holder.mEventApprovedLiveStatus.setVisibility(View.VISIBLE);
                holder.mEventCancelLiveStatus.setVisibility(View.GONE);
            } else {
                holder.mEventApprovedLiveStatus.setVisibility(View.GONE);
                holder.mEventCancelLiveStatus.setVisibility(View.VISIBLE);
            }
        } else {
            holder.mEventApprovedLiveStatus.setVisibility(View.GONE);
            holder.mEventCancelLiveStatus.setVisibility(View.GONE);
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
                                    AppUtils.hideKeyboard((PublicUserProfileActivity) context);
                                    String mstate = eventType(event.getEventStartDate() + " " + event.getEventStartTime(),
                                            event.getEventEndDate() + " " + event.getEventEndTime());
                                    PublicUserProfileActivity.FragmentState = 1;
                                    EventDetailsFragment optionsFrag = new EventDetailsFragment();
                                    Bundle args = new Bundle();
                                    args.putString("event_id", event.getEventId().toString());
                                    args.putString("isSelfProfile", "2");

                                    optionsFrag.setArguments(args);
                                    ((PublicUserProfileActivity) context).getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.containeruserprofile, optionsFrag,
                                            "OptionsFragment").addToBackStack(null).commit();
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
                        AppUtils.hideKeyboard((PublicUserProfileActivity) context);
                        String mstate = eventType(event.getEventStartDate() + " " + event.getEventStartTime(),
                                event.getEventEndDate() + " " + event.getEventEndTime());
                        PublicUserProfileActivity.FragmentState = 1;
                        EventDetailsFragment optionsFrag = new EventDetailsFragment();
                        Bundle args = new Bundle();
                        args.putString("event_id", event.getEventId().toString());
                        args.putString("isSelfProfile", "2");

                        optionsFrag.setArguments(args);
                        ((PublicUserProfileActivity) context).getSupportFragmentManager()
                                .beginTransaction().replace(R.id.containeruserprofile, optionsFrag,
                                "OptionsFragment").addToBackStack(null).commit();
                    }
                });
            }
        }
        eventsVH.mDate.setText(getMonth(event.getEventStartDate()));
        eventsVH.mweekday.setText(getWeekDay(event.getEventStartDate()));

        holder.mEventApprovedLiveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mEventApprovedLiveStatus.setVisibility(View.GONE);
                holder.mEventCancelLiveStatus.setVisibility(View.VISIBLE);

                if (!AppUtils.isOnline(context)) {

                }
                try {
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("event_id", event.getEventId());
                    callAttendingCancel(paramObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.mEventCancelLiveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mEventApprovedLiveStatus.setVisibility(View.VISIBLE);
                holder.mEventCancelLiveStatus.setVisibility(View.GONE);

                if (!AppUtils.isOnline(context)) {

                }
                try {
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("event_id", event.getEventId());
                    callAttendingCancel(paramObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mstate = eventType(event.getEventStartDate() + " " + event.getEventStartTime(),
                        event.getEventEndDate() + " " + event.getEventEndTime());
                PublicUserProfileActivity.FragmentState = 1;
                EventDetailsFragment optionsFrag = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putString("event_id", event.getEventId().toString());
                args.putString("isSelfProfile", "2");


                optionsFrag.setArguments(args);
                ((PublicUserProfileActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.containeruserprofile, optionsFrag, "OptionsFragment").addToBackStack(null).commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return events == null ? 0 : events.size();
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class EventsVH extends RecyclerView.ViewHolder {
        private final PagerIndicator mIndicator;
        public TextView title, mEventDiscription, mweekday, mDate, mUserCount;
        public ImageView mEventStatus, mImageCover;
        public SliderLayout mEventCover;
        public Button mEventApprovedLiveStatus, mEventCancelLiveStatus;
        public CircleImageView mFirstUser, mSecondUser;
        public RelativeLayout mAttendingUers, mItem;

        public EventsVH(View view) {
            super(view);

            title = view.findViewById(R.id.event_title);
            mweekday = view.findViewById(R.id.week_day);
            mDate = view.findViewById(R.id.date);
            mEventDiscription = view.findViewById(R.id.event_discription);
            mEventStatus = view.findViewById(R.id.status_img);
            mEventApprovedLiveStatus = view.findViewById(R.id.status_bt);
            mEventCancelLiveStatus = view.findViewById(R.id.cancelstatus_bt);
            mEventCover = view.findViewById(R.id.slider);
            mIndicator = view.findViewById(R.id.indicator);
            mImageCover = view.findViewById(R.id.imgCover);
            mUserCount = view.findViewById(R.id.all_users);
            mAttendingUers = view.findViewById(R.id.rl_users);
            mFirstUser = view.findViewById(R.id.first_user);
            mSecondUser = view.findViewById(R.id.second_user);
            mItem = view.findViewById(R.id.rlMainContent);
        }
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
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {

                        //response.body().getMessage();
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
//                            DialogFactory.showDropDownNotification(
//                                    mContext,
//                                    mContext.getString(R.string.tv_error),
//                                    response.body().getMessage());
                        }
                    }
                } else {
//                    if (response.code() == 404)
//                        DialogFactory.showDropDownNotification(
//                                mContext,
//                                mContext.getString(R.string.alert_information),
//                                mContext.getString(R.string.alert_api_not_found));
//                    if (response.code() == 500)
//                        DialogFactory.showDropDownNotification(
//                                mContext,
//                                mContext.getString(R.string.alert_information),
//                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<EventsParticipantsResponse> call, Throwable t) {
                try {
                    Constants.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (t instanceof IOException)
//                    DialogFactory.showDropDownNotification(
//                            mContext,
//                            mContext.getString(R.string.alert_information),
//                            mContext.getString(R.string.alert_file_not_found));
//                else if (t instanceof SocketTimeoutException)
//                    DialogFactory.showDropDownNotification(
//                            mContext,
//                            mContext.getString(R.string.alert_information),
//                            mContext.getString(R.string.alert_request_timeout));
//                else
//                    DialogFactory.showDropDownNotification(
//                            mContext,
//                            mContext.getString(R.string.alert_information),
//                            t.getLocalizedMessage());
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
        return AppUtils.getEventTypeByDate(AppUtils.converterStringToDateTime(startDateTime),
                AppUtils.converterStringToDateTime(AppUtils.getCurrentDateTime()),
                AppUtils.converterStringToDateTime(endDateTime));
       /* if (convertStrToDate(startDateTime).compareTo(convertStrToDate(getCurrentDate())) > 0) {
            return "future";//future
        } else if (convertStrToDate(endDateTime).compareTo(convertStrToDate(getCurrentDate())) < 0) {
            return "past";//past
        } else if (convertStrToDate(startDateTime).compareTo(convertStrToDate(getCurrentDate())) * convertStrToDate(endDateTime).compareTo(convertStrToDate(getCurrentDate())) >= 0) {
            return "live";//live
        }
        return "";*/
    }

}