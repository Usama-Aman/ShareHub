package fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiletouch.sharehub.MediaProfileActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.UpdateEventActivity;
import com.mobiletouch.sharehub.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import adapters.EventsListAdapter;
import adapters.OverviewMediaAdapter;
import models.CheckDataModel;
import models.Event;
import models.EventDetailsResponseData;
import models.LoginResult;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.SpacesItemDecoration;
import utility.URLogs;

import static fragments.EventDetailsFragment.eventID;
import static fragments.EventDetailsFragment.isSelfUser;

/**
 * Mubashir 5/22/2018
 */
public class EventOverviewFragment extends Fragment implements View.OnClickListener, EventDetailsFragment.OnEventOverViewRefreshMediaList {
    Button btShare, btLocation, btEditEvent;
    FrameLayout progressBar;
    public RecyclerView mrecyclerView;
    OverviewMediaAdapter mAdapter;
    public ArrayList<EventDetailsResponseData.Media> MediaData = new ArrayList<EventDetailsResponseData.Media>();
    public ArrayList<Event> eventListData = new ArrayList<Event>();
    public TextView mEventDetails, mEventDiscription, mCreatedby, mVenue, mOpenMedia, mclickOpenMedia;
    ImageView eventStatus;
    static ImageView qrCodeIv;
    public int isLiveEvent;
    public String qrCodeUrl, isCommentAllow, stVenue;
    int userId;
    Boolean isPrivateEvent = false;
    Boolean isAttending = false;
    Bitmap b;
    String eventPinCode;
    EventDetailsFragment eventDetailsFragment;
    Boolean isApproved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventDetailsFragment = new EventDetailsFragment();
        eventDetailsFragment.registerListener(this);
        eventId = "";
        eventTitle = "";
        eventDescription = "";
        eventQR = "";
        eventTypeByTime = "";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_overview_events, container, false);
        eventTypeByTime = "";
        CheckDataModel.isApprovalRequired = false;
        CheckDataModel.isPinRequired = false;
        CastView(view);
        callGetEventDetails();
        btShare.setOnClickListener(this);
        btLocation.setOnClickListener(this);
        mVenue.setOnClickListener(this);
        btEditEvent.setOnClickListener(this);
        mOpenMedia.setOnClickListener(this);
        mclickOpenMedia.setOnClickListener(this);


        return view;
    }

    // Initialized Views
    public void CastView(View view) {
        btShare = view.findViewById(R.id.bt_share);
        progressBar = view.findViewById(R.id.progressBar);
        mEventDetails = view.findViewById(R.id.tv_eventdetail);
        mEventDiscription = view.findViewById(R.id.tv_eventdiscription);
        mCreatedby = view.findViewById(R.id.tv_eventcreater_name);
        mVenue = view.findViewById(R.id.tv_venue);
        mrecyclerView = view.findViewById(R.id.rv_media);
        eventStatus = view.findViewById(R.id.indicator_status);
        btLocation = view.findViewById(R.id.bt_location);
        btEditEvent = view.findViewById(R.id.editEvent);
        qrCodeIv = view.findViewById(R.id.iv_qr);
        mOpenMedia = view.findViewById(R.id.tvOpenMedia);
        mclickOpenMedia = view.findViewById(R.id.clickOpenMedia);
        mclickOpenMedia.setVisibility(View.INVISIBLE);
        eventStatus.setVisibility(View.GONE);

// Set Recycler view Properties
        mrecyclerView.setHasFixedSize(true);
        int numberOfColumns = 3;
        mrecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        mrecyclerView.setItemAnimator(new DefaultItemAnimator());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.font_xsmall);
        mrecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mAdapter = new OverviewMediaAdapter(getActivity(), MediaData);
        mrecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        callGetEventDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_share:
                // Share Deep linking link


              /*  Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");*/


             /*   Uri imageUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                share.putExtra(Intent.EXTRA_TEXT,
                        "http://sharehubapp.com/public/event_detail/" + eventID);
                startActivity(Intent.createChooser(share, getActivity().getResources().getString(R.string.tv_select)));*/
                try {
                    if (!AppUtils.isOnline(getActivity()))
                        return;
                    qrCodeIv.buildDrawingCache();
                    Bitmap bmap = qrCodeIv.getDrawingCache();
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                            bmap, "Title", null);
                    AppUtils.shareLinkToSocial(getActivity(), eventId, eventTitle, eventDescription, path, progressBar);
                } catch (Exception e) {
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.tv_error),
                            getActivity().getString(R.string.tv_errordynamic_share));
                }

                break;
            case R.id.bt_location:
            case R.id.tv_venue:
                openGoogleMap();
                break;
            case R.id.editEvent:
                Intent intent = new Intent(getActivity(), UpdateEventActivity.class);
                intent.putExtra("eventListData", eventListData.get(0));
                startActivity(intent);
                break;
            case R.id.tvOpenMedia:
            case R.id.clickOpenMedia:
                Intent myIntent = new Intent(getActivity(), MediaProfileActivity.class);
                myIntent.putExtra("event_id", eventID);
                myIntent.putExtra("isCreator", isSelfUser);
                getActivity().startActivity(myIntent);
                break;

        }
    }


    public void openGoogleMap() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + stVenue + "&avoid=tf");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public static Bitmap getBitmapFromURL(String src) {

        try {

            //uncomment below line in image name have spaces.
            //src = src.replaceAll(" ", "%20");

            URL url = new URL(src);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            Log.d("vk21", e.toString());
            return null;
        }
    }

    private void showProgressBar(final boolean progressVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void callGetEventDetails() {


        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    getActivity().getString(R.string.alert_information),
                    getActivity().getString(R.string.alert_internet_connection));
        }
        try {

//            showProgressBar(true);
            getEventsDataFirst(eventID);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String eventId = "";
    String eventTitle = "";
    String eventDescription = "";
    String eventQR = "";
    String eventTypeByTime = "";

    //Network Call to get Event Details
    private void getEventsDataFirst(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getDetailsOfEvent(id, new Callback<EventDetailsResponseData>() {
            @Override
            public void onResponse(Call<EventDetailsResponseData> call, Response<EventDetailsResponseData> response) {
                try {
//                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    URLogs.m("Successfully updating data.....");
                    if (response.body() != null && response.body().getStatus()) {

                        String sDate = response.body().getData().getEvent().getEventStartDate() + " " + response.body().getData().getEvent().getEventStartTime();
                        String eDate = response.body().getData().getEvent().getEventEndDate() + " " + response.body().getData().getEvent().getEventEndTime();


                        eventTypeByTime = AppUtils.getEventTypeByDate(AppUtils.converterStringToDateTime(sDate),
                                AppUtils.converterStringToDateTime(AppUtils.getCurrentDateTime()),
                                AppUtils.converterStringToDateTime(eDate));

                        eventId = response.body().getData().getEvent().getEventId().toString();
                        eventTitle = response.body().getData().getEvent().getEventTitle();
                        eventDescription = response.body().getData().getEvent().getEventDescription();
                        eventQR = response.body().getData().getEvent().getQrfullImage();
                        MediaData.clear();
                        mEventDetails.setText(response.body().getData().getEvent().getEventDetails());
                        mEventDiscription.setText(response.body().getData().getEvent().getEventDescription());
                        stVenue = (response.body().getData().getEvent().getEventLocation());

                        SpannableString content = new SpannableString(stVenue);
                        content.setSpan(new UnderlineSpan(), 0, stVenue.length(), 0);
                        mVenue.setText(content);

                        userId = response.body().getData().getEvent().getUserId();
                        mCreatedby.setText(response.body().getData().getEvent().getUserFullname());
                        isLiveEvent = response.body().getData().getEvent().getIsLive();

               /*         if (UserProfileActivity.fromUserProfile) {
                            if (UserProfileActivity.fromUserProfileStatus == 1) {
                                isLiveEvent = 1;
                            } else {
                                isLiveEvent = 0;
                            }
                        }*/


                        qrCodeUrl = response.body().getData().getEvent().getQrfullImage();
                        isCommentAllow = response.body().getData().getEvent().getEventIscommentsAllowed().toString();
                        ////Log.e("qrCodeUrl:  ","-----:  "+qrCodeUrl);

                      /*  if (getActivity() == null)
                            return;
                        if (getActivity().isFinishing() || getActivity().isDestroyed())
                            return;*/

                        if (SharedPreference.isUserLoggedIn(getActivity()) && SharedPreference.getUserDetails(getActivity()) != null) {
                            LoginResult userModel = SharedPreference.getUserDetails(getActivity());
                            URLogs.m("ids data.." + userModel.getUserData().getUserId() + ".." + response.body().getData().getEvent().getUserId());
                            if (userModel.getUserData().getUserId().equals(userId)) {
                                isSelfUser = "1";
                            } else {
                                isSelfUser = "0";
                            }
                        } else {
                            isSelfUser = "0";

                        }
                        if (isSelfUser.equalsIgnoreCase("1")) {
                            btEditEvent.setVisibility(View.VISIBLE);
                            qrCodeIv.setVisibility(View.VISIBLE);
                        } else {
                            btEditEvent.setVisibility(View.INVISIBLE);
                            qrCodeIv.setVisibility(View.VISIBLE);
                        }


                        if (EventsListAdapter.fromEventListAdapter) {
                            if (AppUtils.isSet(EventsListAdapter.fromState)) {
                                eventStatus.setVisibility(View.VISIBLE);
                                if (EventsListAdapter.fromState.contains("future")) {
                                    eventStatus.setImageResource(R.drawable.deactive_live);//grey future
                                } else if (EventsListAdapter.fromState.equalsIgnoreCase("live")) {
                                    eventStatus.setImageResource(R.drawable.ic_live_normal);//live green
                                } else {
                                    eventStatus.setImageResource(R.drawable.active_live);//past red
                                }
                            }
                        } else if (UserProfileActivity.fromUserProfile) {
                            if (UserProfileActivity.fromUserProfileStatus == 1) {
                                eventStatus.setVisibility(View.VISIBLE);
                                eventStatus.setImageResource(R.drawable.ic_live_normal);//live green
                            } else {
                                eventStatus.setVisibility(View.VISIBLE);
                                eventStatus.setImageResource(R.drawable.deactive_live);//past red
                            }
                        } else {
                            if (isLiveEvent == 1) {
                                eventStatus.setVisibility(View.VISIBLE);
                                eventStatus.setImageResource(R.drawable.ic_live_normal);//live green
                            } else {
                                eventStatus.setVisibility(View.VISIBLE);
                                eventStatus.setImageResource(R.drawable.deactive_live);//past red
                            }
                        }


                        //Log.e("EventDetailsFragment.isEventStatus: ", "------:  " + EventDetailsFragment.isEventStatus + "  -------   " + eventTypeByTime);

                        if (AppUtils.isSet(qrCodeUrl)) {
                            Picasso.with(getActivity())
                                    .load(qrCodeUrl)   //
                                    .placeholder(R.drawable.ic_placeholder) // optional
                                    .error(R.drawable.ic_placeholder)         // optional
                                    .into(qrCodeIv);
                        }
                        if (response.body().getData().getMedia().size() > 0) {

                            MediaData.addAll(response.body().getData().getMedia());
                            mAdapter.notifyDataSetChanged();

                            if (isSelfUser.equalsIgnoreCase("0")) {
                                if (response.body().getData().getEvent().getIsAttend() == 1) {
                                    mOpenMedia.setVisibility(View.GONE);
                                    mrecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    mrecyclerView.setVisibility(View.GONE);
                                    mOpenMedia.setVisibility(View.VISIBLE);
                                }
                            } else {
                                mOpenMedia.setVisibility(View.GONE);
                                mrecyclerView.setVisibility(View.VISIBLE);
                            }


                        } else {
                            MediaData.clear();
                            mrecyclerView.setVisibility(View.GONE);
                            mOpenMedia.setVisibility(View.VISIBLE);
                        }
                        if (response.body().getData().getEvent() != null) {
                            eventListData.clear();
                            eventListData.add(response.body().getData().getEvent());
                        }

// Static fields to use Project Level
                        CheckDataModel.isSelf = isSelfUser;
                        CheckDataModel.isAllow = isCommentAllow;
                        CheckDataModel.isPinRequired = false;
                        CheckDataModel.isApprovalRequired = false;
                        isApproved = false;
                        if (response.body().getData().getEvent().getEventIsprivate() == 1) {
                            CheckDataModel.isPinRequired = false;
                            CheckDataModel.isApprovalRequired = false;
                            isPrivateEvent = true;
                            eventPinCode = "";
                            if (response.body().getData().getEvent().getEventIsPincodeRequired() == 1) {
                                CheckDataModel.isPinRequired = true;
                                if (AppUtils.isSet(response.body().getData().getEvent().getEventPincode() + ""))
                                    eventPinCode = String.valueOf(response.body().getData().getEvent().getEventPincode());
                            }

                            if (response.body().getData().getEvent().getEventIsApprovalRequired() == 1) {
                                CheckDataModel.isApprovalRequired = true;

                                if (response.body().getData().getEvent().getIsApproved() == 1)
                                    isApproved = true;
                            }
                        }

                        isAttending = false;
                        if (response.body().getData().getEvent().getIsAttend() == 1) {
                            isAttending = true;
                        }

                        if (isSelfUser.equalsIgnoreCase("0")) {
                            if (isAttending) {
                                if (isPrivateEvent) {

                                    if (CheckDataModel.isApprovalRequired) {
                                        if (isApproved) {
                                            mclickOpenMedia.setVisibility(View.VISIBLE);
                                            if (MediaData != null && MediaData.size() != 0) {
                                                mrecyclerView.setVisibility(View.VISIBLE);
                                                mOpenMedia.setVisibility(View.GONE);
                                            } else {
                                                mOpenMedia.setVisibility(View.VISIBLE);
                                                mrecyclerView.setVisibility(View.GONE);
                                            }
                                        } else {
                                            mOpenMedia.setVisibility(View.GONE);
                                            mrecyclerView.setVisibility(View.GONE);
                                            mclickOpenMedia.setVisibility(View.GONE);
                                        }

                                    } else {
                                        mclickOpenMedia.setVisibility(View.VISIBLE);
                                        if (MediaData != null && MediaData.size() != 0) {
                                            mrecyclerView.setVisibility(View.VISIBLE);
                                            mOpenMedia.setVisibility(View.GONE);
                                        } else {
                                            mOpenMedia.setVisibility(View.VISIBLE);
                                            mrecyclerView.setVisibility(View.GONE);
                                        }
                                    }


                                } else {
                                    if (isAttending) {
                                        mclickOpenMedia.setVisibility(View.VISIBLE);
                                        if (MediaData != null && MediaData.size() != 0) {
                                            mrecyclerView.setVisibility(View.VISIBLE);
                                            mOpenMedia.setVisibility(View.GONE);
                                        } else {
                                            mOpenMedia.setVisibility(View.VISIBLE);
                                            mrecyclerView.setVisibility(View.GONE);
                                        }
                                    } else {
                                        mOpenMedia.setVisibility(View.GONE);
                                        mrecyclerView.setVisibility(View.GONE);
                                        mclickOpenMedia.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                mOpenMedia.setVisibility(View.GONE);
                                mrecyclerView.setVisibility(View.GONE);
                                mclickOpenMedia.setVisibility(View.GONE);
                            }
                        } else {
                            mclickOpenMedia.setVisibility(View.VISIBLE);
                        }
                        showProgressBar(false);
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
            public void onFailure(Call<EventDetailsResponseData> call, Throwable t) {
                try {
//                    showProgressBar(false);
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


    @Override
    public void onRefresh(Boolean isParticipant) {
        //Log.e("isParticipant...", "..." + isParticipant);
     /*   if (isParticipant) {
            if (MediaData != null && MediaData.size() != 0 && mOpenMedia != null && mrecyclerView != null) {
                mOpenMedia.setVisibility(View.GONE);
                mrecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mOpenMedia != null && mrecyclerView != null) {
                mOpenMedia.setVisibility(View.VISIBLE);
                mrecyclerView.setVisibility(View.GONE);
            }
        }
*/
        if (isSelfUser.equalsIgnoreCase("0")) {
            if (mOpenMedia != null && mclickOpenMedia != null && mrecyclerView != null) {
                if (isParticipant) {
                    if (isPrivateEvent) {

                        if (CheckDataModel.isApprovalRequired) {
                            if (isApproved) {
                                mclickOpenMedia.setVisibility(View.VISIBLE);
                                if (MediaData != null && MediaData.size() != 0) {
                                    mrecyclerView.setVisibility(View.VISIBLE);
                                    mOpenMedia.setVisibility(View.GONE);
                                } else {
                                    mOpenMedia.setVisibility(View.VISIBLE);
                                    mrecyclerView.setVisibility(View.GONE);
                                }
                            } else {
                                mOpenMedia.setVisibility(View.GONE);
                                mrecyclerView.setVisibility(View.GONE);
                                mclickOpenMedia.setVisibility(View.GONE);
                            }

                        } else {
                            mclickOpenMedia.setVisibility(View.VISIBLE);
                            if (MediaData != null && MediaData.size() != 0) {
                                mrecyclerView.setVisibility(View.VISIBLE);
                                mOpenMedia.setVisibility(View.GONE);
                            } else {
                                mOpenMedia.setVisibility(View.VISIBLE);
                                mrecyclerView.setVisibility(View.GONE);
                            }
                        }


                    } else {
                        if (isParticipant) {
                            isAttending = true;
                            mclickOpenMedia.setVisibility(View.VISIBLE);
                            if (MediaData != null && MediaData.size() != 0) {
                                mrecyclerView.setVisibility(View.VISIBLE);
                                mOpenMedia.setVisibility(View.GONE);
                            } else {
                                mOpenMedia.setVisibility(View.VISIBLE);
                                mrecyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            isAttending = false;
                            mOpenMedia.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.GONE);
                            mclickOpenMedia.setVisibility(View.GONE);
                        }
                    }
                } else {
                    mOpenMedia.setVisibility(View.GONE);
                    mrecyclerView.setVisibility(View.GONE);
                    mclickOpenMedia.setVisibility(View.GONE);
                }
            }
        }

    }
}