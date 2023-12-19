package fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;

import java.util.ArrayList;

import adapters.UserEventListAdapter;
import models.CreatedEvent;
import models.GroupsModel;
import models.LoginResult;
import models.UserDetail;
import utility.SharedPreference;
import utility.SpacesItemDecoration;

public class UsersEventFragment extends Fragment implements View.OnClickListener {

    private View v;
    private AppCompatActivity mContext;
    private FrameLayout progressBar;
    ArrayList<UserDetail> usersListData = new ArrayList<UserDetail>();
    ArrayList<GroupsModel> groupsListData = new ArrayList<GroupsModel>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvNoEventsFound;
    private RecyclerView rvEventsList;
    private String userId;
    ArrayList<CreatedEvent> usersEventListData;
    private LinearLayoutManager mLayoutManager;
    private UserEventListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_users_event, container, false);

        mContext = (AppCompatActivity) getActivity();
        initSharedPref();
        getActivityData();
        viewInitialize(v);

        return v;
    }

    private void viewInitialize(View v) {
        tvNoEventsFound = v.findViewById(R.id.tvEventsNotFound);
        rvEventsList = v.findViewById(R.id.rvEventsList);

        if (usersEventListData.size() > 0) {

            rvEventsList.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvEventsList.setLayoutManager(mLayoutManager);
            rvEventsList.setItemAnimator(new DefaultItemAnimator());
            rvEventsList.addItemDecoration(new SpacesItemDecoration(10));
            mAdapter = new UserEventListAdapter(getActivity(), usersEventListData);
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
            usersEventListData = bundle.getParcelableArrayList("Users_Event");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(mContext);

                break;*/

        }
    }



}