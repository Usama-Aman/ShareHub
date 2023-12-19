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

import com.mobiletouch.sharehub.R;

import java.util.ArrayList;

import adapters.ProfileMyEventAdapter;
import models.UserMyEventResponse;
import utility.AppUtils;
import utility.DialogFactory;


/***
 * Fragment to populate list of self user's profile fragment's MyEvents events
 */
public class MyEventFragment extends Fragment implements View.OnClickListener {


    private TextView tvNoDataFound;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvMyEventList;


    private ProfileMyEventAdapter mAdapter;

    public ArrayList<UserMyEventResponse> myEventListData = new ArrayList<UserMyEventResponse>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_event, container, false);


        mContext = (AppCompatActivity) getActivity();
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
    public void updateData(ArrayList<UserMyEventResponse> dataList) {
        myEventListData.clear();
        myEventListData.addAll(dataList);
        getMyEventList();
    }

//API call to get myEvent list
    private void getMyEventList() {


        if (myEventListData.size() > 0) {

            tvNoDataFound.setVisibility(View.GONE);
            rvMyEventList.setVisibility(View.VISIBLE);

            rvMyEventList.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvMyEventList.setLayoutManager(mLayoutManager);
            rvMyEventList.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new ProfileMyEventAdapter(getActivity(), myEventListData);
            rvMyEventList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else {

            rvMyEventList.setVisibility(View.GONE);
            tvNoDataFound.setVisibility(View.VISIBLE);
        }

    }



}