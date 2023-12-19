package com.mobiletouch.sharehub;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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

import adapters.GroupPeopleListAdapter;
import models.GroupListResponse;
import models.UserListResponse;
import models.UserResponseDataDetail;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Confirm_Alert_Dialog;
import utility.Constants;
import utility.DialogFactory;
import utility.DividerItemDecoration;
import utility.PaginationAdapterCallback;
import utility.PaginationScrollListener;
import utility.SharedPreference;
import utility.URLogs;

/**
 * Activity to load list of group's members with delete and block functionality
 */

public class GroupPeopleListingActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, PaginationAdapterCallback {

    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvPeopleList;
    private ArrayList<UserResponseDataDetail> groupPeopleListData = new ArrayList<UserResponseDataDetail>();
    GroupPeopleListAdapter adapter;
    private String groupID, groupName = "", from = "";
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_listing);

        mContext = (AppCompatActivity) this;
        getActivityData();
        viewInitialize();
        initToolBar();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    //get intent data
    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupID = bundle.getString("group_id");
            groupName = bundle.getString("group_name");
            from = bundle.getString("from");
        }
    }


    //toolbar initialization
    public void initToolBar() {
        toolBar = (Toolbar) findViewById(R.id.toolBar);

        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.VISIBLE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_my_groups));

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight.setImageResource(R.drawable.icon_add_members);
        tvToolbarTitle.setText(groupName);

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    //views initialization
    private void viewInitialize() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        rvPeopleList = (RecyclerView) findViewById(R.id.rvPeopleList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                finish();
                break;
            case R.id.btn_toolbar_right://to add more members in a group
                Intent intent = new Intent(this, GroupAddPeopleListingActivity.class);
                intent.putExtra("group_id", groupID);
                intent.putExtra("group_name", groupName);
                intent.putExtra("form_context", "people_list");
                startActivity(intent);
                break;
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
            showProgressBar(true);
            isLoading = false;
            isLastPage = false;
            currentPage = 1;
            loadPeopleListFirstPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //show progress dialog
    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    //swipe to refresh list
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        try {

            isLoading = false;
            isLastPage = false;
            currentPage = 1;
            loadPeopleListFirstPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }


    //load first page
    private void loadPeopleListFirstPage() {
        URLogs.m("loadFirstPage: ");

        groupPeopleListData.clear();
        currentPage = 1;
        try {
            if (!AppUtils.isOnline(mContext)) {
                DialogFactory.showDropDownNotification(mContext,
                        mContext.getString(R.string.alert_information),
                        mContext.getString(R.string.alert_internet_connection));
                return;
            }

            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("group_id", groupID);
            paramObject.addProperty("page", currentPage);
            showProgressBar(true);
            getFirstPeopleList(paramObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //call tp API to load first page of group's members
    private void getFirstPeopleList(JsonObject paramObject) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getUserList(paramObject, new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, final retrofit2.Response<UserListResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        URLogs.m(" Response " + response.body());

                        groupPeopleListData.addAll(response.body().getData().getData());

                        if (groupPeopleListData.size() > 0) {

                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);

                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();
                            /// Set Adapter code here

                            /// Set Adapter code here
                            rvPeopleList.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                            rvPeopleList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), true, false));
                            rvPeopleList.setLayoutManager(mLayoutManager);
                            rvPeopleList.setItemAnimator(new DefaultItemAnimator());
                            adapter = new GroupPeopleListAdapter(mContext, groupPeopleListData, new GroupPeopleListAdapter.OnItemClickListener() {
                                @Override
                                public void onBlockClick(final int position) {


                                    Confirm_Alert_Dialog confirmAlertDialog = new Confirm_Alert_Dialog(mContext, getResources().getString(R.string.tv_block_group_member), new Confirm_Alert_Dialog.OnAddClickListener() {
                                        @Override
                                        public void onOkClick() {
                                            try {
                                                if (!AppUtils.isOnline(mContext)) {
                                                    DialogFactory.showDropDownNotification(mContext,
                                                            mContext.getString(R.string.alert_information),
                                                            mContext.getString(R.string.alert_internet_connection));
                                                }
                                                try {
                                                    showProgressBar(true);
                                                    JsonObject paramObject = new JsonObject();
                                                    paramObject.addProperty("gpeople_id", groupPeopleListData.get(position).getGpeopleId());
                                                    blockMemberCall(paramObject);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    confirmAlertDialog.show();


                                }

                                @Override
                                public void onDeleteClick(final int position) {

                                    Confirm_Alert_Dialog confirmAlertDialog = new Confirm_Alert_Dialog(mContext, getResources().getString(R.string.tv_delete_group_member), new Confirm_Alert_Dialog.OnAddClickListener() {
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
                                                paramObject.addProperty("gpeople_id", groupPeopleListData.get(position).getGpeopleId());
                                                deleteMemberCall(paramObject);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    confirmAlertDialog.show();


                                }
                            });
                            rvPeopleList.setAdapter(adapter);


                            rvPeopleList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    loadPeopleListNextPage();
                                }

                                @Override
                                public int getTotalPageCount() {
                                    return TOTAL_PAGES;
                                }

                                @Override
                                public boolean isLastPage() {
                                    return isLastPage;
                                }

                                @Override
                                public boolean isLoading() {
                                    return isLoading;
                                }
                            });

                            if (currentPage < TOTAL_PAGES) {
                                adapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        } else {
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
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


    //load to next page
    private void loadPeopleListNextPage() {

        URLogs.m("loadNextPage: " + currentPage);

        if (currentPage < TOTAL_PAGES) {
            currentPage++;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!AppUtils.isOnline(mContext)) {
                            DialogFactory.showDropDownNotification(mContext,
                                    mContext.getString(R.string.alert_information),
                                    mContext.getString(R.string.alert_internet_connection));
                            return;
                        }
                        JsonObject paramObject = new JsonObject();
                        paramObject.addProperty("group_id", groupID);
                        paramObject.addProperty("page", currentPage);
                        getNextPeopleList(paramObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {

            rvPeopleList.post(new Runnable() {
                public void run() {
                    // There is no need to use notifyDataSetChanged()
                    adapter.removeLoadingFooter();
                    isLastPage = true;
                }
            });

        }
    }

    //API call to load next pages of members list of a group
    private void getNextPeopleList(JsonObject paramObject) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getUserList(paramObject, new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, final retrofit2.Response<UserListResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        if (response.body().getData().getData().size() > 0) {
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);

                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();


                            adapter.removeLoadingFooter();
                            isLoading = false;

                            // Set ListData to Adapter here
                            groupPeopleListData.addAll(response.body().getData().getData());
                            // Set Adapter to notify here
                            adapter.notifyDataSetChanged();

                            rvPeopleList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    loadPeopleListNextPage();
                                }

                                @Override
                                public int getTotalPageCount() {
                                    return TOTAL_PAGES;
                                }

                                @Override
                                public boolean isLastPage() {
                                    return isLastPage;
                                }

                                @Override
                                public boolean isLoading() {
                                    return isLoading;
                                }
                            });

                            if (currentPage < TOTAL_PAGES) {

                                adapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        } else {

                            adapter.removeLoadingFooter();
                            isLoading = false;
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
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

    //block member of a group
    private void blockMemberCall(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.blockMemberCall(paramObject, new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, final retrofit2.Response<GroupListResponse> response) {
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
                        isLoading = false;
                        isLastPage = false;
                        currentPage = 1;
                        loadPeopleListFirstPage();
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
            public void onFailure(Call<GroupListResponse> call, Throwable t) {
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

    //delete member of a group
    private void deleteMemberCall(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.deleteMember(paramObject, new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, final retrofit2.Response<GroupListResponse> response) {
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
                        isLoading = false;
                        isLastPage = false;
                        currentPage = 1;
                        loadPeopleListFirstPage();
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
            public void onFailure(Call<GroupListResponse> call, Throwable t) {
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

    @Override
    public void retryPageLoad() {
        loadPeopleListNextPage();
    }
}
