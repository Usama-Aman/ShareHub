package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.BlockedPeopleListAdapter;
import models.BlockedPeopleResponse;
import models.BlockedPeopleResponseData;
import models.DeleteBlockedMemberResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Confirm_Alert_Dialog;
import utility.Constants;
import utility.DialogFactory;
import utility.DividerItemDecoration;
import utility.SharedPreference;

/***
 * To show list of blocked members of user's group
 */
public class BlockedPeopleListActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvPeopleList;
    private ArrayList<BlockedPeopleResponseData> blockedListData = new ArrayList<BlockedPeopleResponseData>();
    BlockedPeopleListAdapter adapter;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_blocklist);

        mContext = (AppCompatActivity) this;
        getActivityData();
        viewInitialize();
        initToolBar();
        getDataFromServer();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }




    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // groupID = bundle.getString("groupID");
        }
    }

    public void initToolBar() {
        toolBar = (Toolbar) findViewById(R.id.toolBar);

        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_my_groups));

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        tvToolbarTitle.setText(mContext.getResources().getString(R.string.title_blocked_user_list));
        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    private void viewInitialize() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        rvPeopleList = (RecyclerView) findViewById(R.id.rvPeopleList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        rvPeopleList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvPeopleList.setLayoutManager(mLayoutManager);
        adapter = new BlockedPeopleListAdapter(mContext, blockedListData, new BlockedPeopleListAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(final int position) {


                Confirm_Alert_Dialog confirmAlertDialog = new Confirm_Alert_Dialog(mContext, getResources().getString(R.string.tv_remove_from_block_list), new Confirm_Alert_Dialog.OnAddClickListener() {
                    @Override
                    public void onOkClick() {

                        if (!AppUtils.isOnline(mContext)) {
                            DialogFactory.showDropDownNotification(mContext,
                                    mContext.getString(R.string.alert_information),
                                    mContext.getString(R.string.alert_internet_connection));
                        }
                        try {
                            showProgressBar(true);
                            JsonObject paramObject = new JsonObject();
                            paramObject.addProperty("user_id", blockedListData.get(position).getUserId());
                            paramObject.addProperty("group_id", blockedListData.get(position).getGroupId());
                            deleteBlockMemberCall(paramObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                confirmAlertDialog.show();

            }
        });
        rvPeopleList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
        rvPeopleList.setAdapter(adapter);

    }

    public void getDataFromServer() {
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {
            showProgressBar(true);
            getGroupBlockedUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //btnLogin.setEnabled(!progressVisible);
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        try {
            showProgressBar(true);
            getGroupBlockedUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }


    //get blocked users of a group
    private void getGroupBlockedUsers() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getGroupBlockedList(new Callback<BlockedPeopleResponse>() {
            @Override
            public void onResponse(Call<BlockedPeopleResponse> call, final retrofit2.Response<BlockedPeopleResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().size() > 0) {
                            tvNoDataFound.setVisibility(View.GONE);
                            rvPeopleList.setVisibility(View.VISIBLE);
                            blockedListData.clear();
                            blockedListData.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            rvPeopleList.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                        rvPeopleList.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<BlockedPeopleResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }


    //unblocked the member
    private void deleteBlockMemberCall(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.deleteBlockedMember(paramObject, new Callback<DeleteBlockedMemberResponse>() {
            @Override
            public void onResponse(Call<DeleteBlockedMemberResponse> call, final retrofit2.Response<DeleteBlockedMemberResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        DialogFactory.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.tv_alert_success),
                                response.body().getMessage());
                        getGroupBlockedUsers();
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.alert_information),
                                mContext.getString(R.string.alert_internal_server_error));
                }
            }

            @Override
            public void onFailure(Call<DeleteBlockedMemberResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

}
