package com.mobiletouch.sharehub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;

import adapters.GroupAddPeopleListAdapter;
import models.CreateGroupResponseData;
import models.GroupAddResponse;
import models.GroupNotMembersResponse;
import models.User;
import models.UserSingleton;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.DividerItemDecoration;
import utility.SharedPreference;
import utility.URLogs;


/**
 * Activity to get users list from server and then add the users to a group
 */
public class GroupAddPeopleListingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvPeopleList;
    private EditText et_search;
    private ArrayList<User> groupAddPeopleListData = new ArrayList<User>();
    GroupAddPeopleListAdapter adapter;
    private String groupID, groupUsers, groupName = "", form_context = "";
    private ThreadPoolExecutor executor;
    private CreateGroupResponseData groupResponseData;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people_list);

        mContext = (AppCompatActivity) this;
        getActivityData();
        viewInitialize();
        initToolBar();
        setupListener();

        if (form_context.equalsIgnoreCase("people_list")) {
            showProgressBar(true);
            loadUsers();
        } else {
            DialogFactory.showDropDownSuccessNotification(
                    mContext,
                    mContext.getString(R.string.tv_alert_success),
                    mContext.getString(R.string.tv_group_success));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (UserSingleton.getInstance().getUserList() != null ) {
            UserSingleton.getInstance().getUserList().clear();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (UserSingleton.getInstance().getUserList() != null ) {
            UserSingleton.getInstance().getUserList().clear();
        }
    }



    //get intent data
    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupID = bundle.getString("group_id");
            groupName = bundle.getString("group_name");
            form_context = bundle.getString("form_context");

            if (bundle.getString("users") != null) {
                groupUsers = bundle.getString("users");
                Gson gson = new Gson();
                Type type = new TypeToken<CreateGroupResponseData>() {
                }.getType();
                groupResponseData = gson.fromJson(groupUsers, type);
                groupAddPeopleListData = (ArrayList) groupResponseData.getUsers();
            }
        }
    }

    //views initialization
    private void viewInitialize() {
        et_search = (EditText) findViewById(R.id.et_search);
        rvPeopleList = (RecyclerView) findViewById(R.id.rvPeopleList);
        tvNoDataFound = (TextView) findViewById(R.id.tvNoDataFound);
        progressBar = (FrameLayout) findViewById(R.id.progressBar);

        rvPeopleList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvPeopleList.setLayoutManager(mLayoutManager);
        rvPeopleList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
        rvPeopleList.setItemAnimator(new DefaultItemAnimator());

        adapter = new GroupAddPeopleListAdapter(mContext, groupAddPeopleListData);
        rvPeopleList.setAdapter(adapter);
    }

    //toolbar initialization
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

        tvToolbarTitle.setText(mContext.getString(R.string.tv_select_users));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    Boolean isOnTextChanged = false;

    private void setupListener() {

        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                // TODO Auto-generated method stub
                if (isOnTextChanged) {
                    isOnTextChanged = false;
                    if (AppUtils.isSet(et_search.getText().toString())) {
                        String text = et_search.getText().toString().toLowerCase(Locale.getDefault());
                        adapter.filter(text);
                    } else {
                        adapter.filter("");
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                isOnTextChanged = true;
            }
        });
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

        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
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

    //add button to add member to a group
    public void button_add_click(View view) {
        if (UserSingleton.getInstance().getUserList() != null && UserSingleton.getInstance().getUserList().size() != 0) {
            adapter.filter("");
            ArrayList<String> arrayList = new ArrayList<>();
            for (User user : UserSingleton.getInstance().getUserList()) {
                if (user.getChecked()) {
                    arrayList.add(user.getUserId().toString());
                }
            }

            if (!arrayList.isEmpty()) {
                String userIds = android.text.TextUtils.join(",", arrayList);
                JsonObject paramObject = new JsonObject();
                paramObject.addProperty("group_id", groupID);
                paramObject.addProperty("group_people", "[" + userIds + "]");
                showProgressBar(true);
                addUsersToGroup(paramObject);
            } else {
                DialogFactory.showDropDownNotification(
                        mContext,
                        mContext.getString(R.string.alert_information),
                        mContext.getString(R.string.tv_select_atleast));
            }
        } else {
            DialogFactory.showDropDownNotification(
                    mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.tv_select_atleast));
        }
    }

    //api call to add members
    private void addUsersToGroup(final JsonObject members) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.addUserToGroup(members, new Callback<GroupAddResponse>() {
            @Override
            public void onResponse(Call<GroupAddResponse> call, final retrofit2.Response<GroupAddResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownSuccessNotification(
                                    mContext,
                                    mContext.getString(R.string.tv_alert_success),
                                    mContext.getString(R.string.tv_users_added_success));

                            if (form_context.equalsIgnoreCase("group_list")) {
                                Intent myIntent = new Intent(mContext, GroupPeopleListingActivity.class);
                                myIntent.putExtra("group_id", groupID);
                                myIntent.putExtra("group_name", groupName);
                                mContext.startActivity(myIntent);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mContext.finish();
                                }
                            },1000);
                        }
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
            public void onFailure(Call<GroupAddResponse> call, Throwable t) {
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


    //call to get user list API
    private void loadUsers() {
        URLogs.m("loadPage: ");
        groupAddPeopleListData.clear();
        try {
            if (!AppUtils.isOnline(mContext)) {
                DialogFactory.showDropDownNotification(mContext,
                        mContext.getString(R.string.alert_information),
                        mContext.getString(R.string.alert_internet_connection));
                return;
            }

            JsonObject paramObject = new JsonObject();
            paramObject.addProperty("group_id", groupID);
            showProgressBar(true);
            getPeopleList(paramObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get user list
    private void getPeopleList(JsonObject paramObject) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getNotMembersList(paramObject, new Callback<GroupNotMembersResponse>() {
            @Override
            public void onResponse(Call<GroupNotMembersResponse> call, final retrofit2.Response<GroupNotMembersResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        URLogs.m(" Response " + response.body());

                        groupAddPeopleListData.addAll(response.body().getUserList());

                        if (groupAddPeopleListData.size() > 0) {

                            tvNoDataFound.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            adapter.initCopyArrayList();

                        } else {
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GroupNotMembersResponse> call, Throwable t) {
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
