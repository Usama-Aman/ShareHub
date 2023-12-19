package fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.R;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.ProfileAttendingEventAdapter;
import models.AddEventParticipantsResponse;
import models.UserAttendingEventResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.DialogFactory;
import utility.FragmentCalls;


/***
 * Fragment to populate list of self user's profile fragment's IamAttending events
 */
public class IAttendingEventFragment extends Fragment implements View.OnClickListener {

    FragmentCalls listener;
    private TextView tvNoDataFound;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvMyEventList;


    private ProfileAttendingEventAdapter mAdapter;

    public ArrayList<UserAttendingEventResponse> myEventListData = new ArrayList<UserAttendingEventResponse>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_event, container, false);


        mContext = (AppCompatActivity) getActivity();
        try {
            listener = (FragmentCalls) mContext;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
        viewInitialize(view);

        return view;
    }


    private void viewInitialize(View v) {

        rvMyEventList = (RecyclerView) v.findViewById(R.id.rvProfileEventList);
        tvNoDataFound = (TextView) v.findViewById(R.id.tvNoDataFound);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {
            // callGetEventsFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //refresh data on swipe down to Refresh
    public void updateData(ArrayList<UserAttendingEventResponse> dataList) {
        myEventListData.clear();
        myEventListData.addAll(dataList);
        getIamAttendingList();
    }


    //API call to get myEvent list
    private void getIamAttendingList() {


        if (myEventListData.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
            rvMyEventList.setVisibility(View.VISIBLE);

            rvMyEventList.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvMyEventList.setLayoutManager(mLayoutManager);
            rvMyEventList.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new ProfileAttendingEventAdapter(getActivity(), myEventListData, new ProfileAttendingEventAdapter.OnItemClickListener() {
                @Override
                public void onCancelEvent(final int position) {
                    if (!AppUtils.isOnline(mContext)) {

                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.tv_error),
                                mContext.getString(R.string.tv_internet));

                    }
                    try {
                        JsonObject paramObject = new JsonObject();
                        paramObject.addProperty("event_id", myEventListData.get(position).getEventId());
                        paramObject.addProperty("isAttending", false);
                        listener.showDialog(true);
                        callAttendingCancel(position, paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onAttendEvent(final int position) {
                    if (!AppUtils.isOnline(mContext)) {
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.tv_error),
                                mContext.getString(R.string.tv_internet));
                    }
                    try {
                        JsonObject paramObject = new JsonObject();
                        paramObject.addProperty("event_id", myEventListData.get(position).getEventId());
                        paramObject.addProperty("isAttending", true);
                        listener.showDialog(true);
                        callAttendingCancel(position, paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            rvMyEventList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else {

            rvMyEventList.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.VISIBLE);
        }

    }

    //API call on pressing cancel button in list item
    private void callAttendingCancel(final int position, JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.addParticipants(params, new Callback<AddEventParticipantsResponse>() {
            @Override
            public void onResponse(Call<AddEventParticipantsResponse> call, final Response<AddEventParticipantsResponse> response) {


                try {
                    listener.showDialog(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        myEventListData.remove(position);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                        getIamAttendingList();
                    }
                } else {

                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
                    getIamAttendingList();
                }
            }

            @Override
            public void onFailure(Call<AddEventParticipantsResponse> call, Throwable t) {
                try {
                    listener.showDialog(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            getString(R.string.alert_information),
                            t.getLocalizedMessage());

                getIamAttendingList();
            }
        });
    }

}
