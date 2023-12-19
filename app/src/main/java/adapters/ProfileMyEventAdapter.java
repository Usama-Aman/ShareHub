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
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.UserProfileActivity;
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
import models.UserMyEventResponse;
import utility.AppUtils;
import utility.URLs;

/**
 * Adapter to set created event list of a user
 */


public class ProfileMyEventAdapter extends RecyclerView.Adapter<ProfileMyEventAdapter.EventsVH> {

    private ArrayList<UserMyEventResponse> events;
    private Context context;
    private String mMonth, mWeek;
    Date date;
    private ArrayList<String> eventImageList = new ArrayList<String>();
    private ImageSliderAdapter imageSliderAdapter;

    public ProfileMyEventAdapter(Context context, ArrayList<UserMyEventResponse> eventListData) {
        this.context = context;
        events = eventListData;
    }

    public List<UserMyEventResponse> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<UserMyEventResponse> events) {
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
    public void onBindViewHolder(EventsVH holder, int position) {

        final UserMyEventResponse event = events.get(position);
        EventsVH eventsVH = (EventsVH) holder;
        eventsVH.title.setText(event.getEventTitle());
        eventsVH.mEventDiscription.setText(event.getEventDescription());

        if (event.getUser_attend_event().size() > 2) {
            event.getUser_attend_event().get(1).getUserPhoto();
            Picasso.with(context)
                    .load(URLs.BaseUrlUserImageUsersThumbs + event.getUser_attend_event().get(0).getUserPhoto())   //
                    .placeholder(R.drawable.user_placeholder) // optional
                    .error(R.drawable.user_placeholder)         // optional
                    .into(eventsVH.mFirstUser);
            Picasso.with(context)
                    .load(URLs.BaseUrlUserImageUsersThumbs + event.getUser_attend_event().get(1).getUserPhoto())   //
                    .placeholder(R.drawable.user_placeholder) // optional
                    .error(R.drawable.user_placeholder)         // optional
                    .into(eventsVH.mSecondUser);

            eventsVH.mUserCount.setText(event.getUser_attend_event().size() + "");
            eventsVH.mAttendingUers.setVisibility(View.VISIBLE);
        } else {
            eventsVH.mAttendingUers.setVisibility(View.GONE);
        }

        //extract event type for start and ending dates in comparision with current date
        String eventType = eventType(event.getEventStartDate() + " " + event.getEventStartTime(),
                event.getEventEndDate() + " " + event.getEventEndTime());

        event.setEventType(eventType);

        if (event.getIsLive() == 0) {
            eventsVH.mEventStatus.setImageResource(R.drawable.deactive_live);
        } else if (event.getIsLive() == 1) {
            eventType = "live";
            event.setEventType("live");
            eventsVH.mEventStatus.setImageResource(R.drawable.ic_live_normal);

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
                                    AppUtils.hideKeyboard((UserProfileActivity) context);
                                    EventDetailsFragment optionsFrag = new EventDetailsFragment();
                                    Bundle args = new Bundle();
                                    args.putString("event_id", event.getEventId().toString());

                                    args.putString("isSelfProfile", "1");
                                    optionsFrag.setArguments(args);
                                    ((UserProfileActivity) context).getSupportFragmentManager()
                                            .beginTransaction()
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
                        .load(URLs.BaseUrlEvents + event.getEventCoverphoto())   //
                        .placeholder(R.drawable.ic_placeholder) // optional
                        .error(R.drawable.ic_placeholder)         // optional
                        .into(eventsVH.mImageCover);

                eventsVH.mImageCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserProfileActivity.fromUserProfileStatus = event.getIsLive();
                        AppUtils.hideKeyboard((UserProfileActivity) context);
                        EventDetailsFragment optionsFrag = new EventDetailsFragment();
                        Bundle args = new Bundle();
                        args.putString("event_id", event.getEventId().toString());

                        args.putString("isSelfProfile", "1");
                        optionsFrag.setArguments(args);
                        ((UserProfileActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, optionsFrag, "OptionsFragment")
                                .addToBackStack(null).commit();
                    }
                });
            }
        }

        eventsVH.mDate.setText(getMonth(event.getEventStartDate()));
        eventsVH.mweekday.setText(getWeekDay(event.getEventStartDate()));
        eventsVH.mEventApprovedLiveStatus.setVisibility(View.GONE);
        eventsVH.mEventCancelLiveStatus.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.fromUserProfileStatus = event.getIsLive();
                EventDetailsFragment optionsFrag = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putString("event_id", event.getEventId().toString());

                args.putString("isSelfProfile", "1");
                optionsFrag.setArguments(args);
                ((UserProfileActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container, optionsFrag, "OptionsFragment").addToBackStack(null).commit();

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
        public TextView title, mEventDiscription, mweekday, mDate, mUserCount;
        public ImageView mEventStatus, mImageCover;
        private final PagerIndicator mIndicator;
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
            mImageCover = view.findViewById(R.id.imgCover);
            mIndicator = view.findViewById(R.id.indicator);
            mUserCount = view.findViewById(R.id.all_users);
            mAttendingUers = view.findViewById(R.id.rl_users);
            mFirstUser = view.findViewById(R.id.first_user);
            mSecondUser = view.findViewById(R.id.second_user);
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
     /*   if (convertStrToDate(startDateTime).compareTo(convertStrToDate(getCurrentDate())) > 0) {
            return "future";//future
        } else if (convertStrToDate(endDateTime).compareTo(convertStrToDate(getCurrentDate())) < 0) {
            return "past";//past
        } else if (convertStrToDate(startDateTime).compareTo(convertStrToDate(getCurrentDate())) * convertStrToDate(endDateTime).compareTo(convertStrToDate(getCurrentDate())) >= 0) {
            return "live";//live
        }
        return "";*/
    }


}