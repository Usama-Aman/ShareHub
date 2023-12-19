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

import adapters.JoinedEventListAdapter;
import models.JoinedEvent;
import models.LoginResult;
import utility.SharedPreference;
import utility.SpacesItemDecoration;

public class UsersAttendingFragment extends Fragment implements View.OnClickListener {

    private View v;
    private AppCompatActivity mContext;
    private TextView tvNoEventsFound;
    private RecyclerView rvEventsList;
    private String userId;
    private LinearLayoutManager mLayoutManager;
    private JoinedEventListAdapter mAdapter;
    ArrayList<JoinedEvent> joinedEventsListData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_users_attending, container, false);

        mContext = (AppCompatActivity) getActivity();
        initSharedPref();
        getActivityData();
        viewInitialize(v);

        return v;
    }

    private void viewInitialize(View v) {
        tvNoEventsFound = v.findViewById(R.id.tvEventsNotFound);
        rvEventsList = v.findViewById(R.id.rvEventsList);

        if (joinedEventsListData.size() > 0) {

            rvEventsList.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvEventsList.setLayoutManager(mLayoutManager);
            rvEventsList.setItemAnimator(new DefaultItemAnimator());
            rvEventsList.addItemDecoration(new SpacesItemDecoration(10));
            mAdapter = new JoinedEventListAdapter(getActivity(), joinedEventsListData);
            rvEventsList.setAdapter(mAdapter);

        } else {
            rvEventsList.setVisibility(View.GONE);
            tvNoEventsFound.setVisibility(View.VISIBLE);
        }
    }

    private void initSharedPref() {
        LoginResult userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getUserData().getUserId() != null) {
            userId = String.valueOf(userModel.getUserData().getUserId());
        }
    }

    private void getActivityData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            joinedEventsListData = bundle.getParcelableArrayList("Joined_Event");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.btn_toolbar_back:
                break;

            case R.id.btnCreate:
                AppUtils.hideKeyboard(mContext);
                break;*/
        }
    }

}