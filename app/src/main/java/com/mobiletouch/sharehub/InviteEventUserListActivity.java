package com.mobiletouch.sharehub;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.InvitationPeopleListAdapter;
import models.InvitationResponse;
import models.PeopleDataResponse;
import models.UserListSingleton;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.SharedPreference;
import utility.SpacesItemDecoration;

public class InviteEventUserListActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvPeopleList;
    private EditText et_search;
    private ArrayList<PeopleDataResponse.User> ListData = new ArrayList<PeopleDataResponse.User>();
    InvitationPeopleListAdapter adapter;
    String eventID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.multiLanguageConfiguration(this);
        setContentView(R.layout.activity_invite_people_list);

        mContext = (AppCompatActivity) this;
        Intent intent = getIntent();
        eventID = intent.getStringExtra("event_id");
        CastView();
        initToolBar();
        callGetUser();
        SearchViewListner();

    }


    // Casting Views
    public void CastView() {
        et_search = findViewById(R.id.et_search);
        rvPeopleList = findViewById(R.id.rvPeopleList);
        progressBar = findViewById(R.id.progressBar);

        rvPeopleList = findViewById(R.id.rvPeopleList);

    }

    // implement Search on EditText
    public void SearchViewListner() {

        et_search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString();

                final ArrayList<PeopleDataResponse.User> filteredList = new ArrayList<PeopleDataResponse.User>();

                for (int i = 0; i < ListData.size(); i++) {

                    final String text = ListData.get(i).getUserFullname();
                    if (text.contains(query)) {

                        filteredList.add(ListData.get(i));
                    }
                }
                if (filteredList.size() > 0) {
                    rvPeopleList.setVisibility(View.VISIBLE);
                    rvPeopleList.setLayoutManager(new LinearLayoutManager(InviteEventUserListActivity.this));
                    adapter = new InvitationPeopleListAdapter(InviteEventUserListActivity.this, filteredList);
                    rvPeopleList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();  // data set changed
                } else {
                    rvPeopleList.setVisibility(View.GONE);
                    showProgressBar(false);
                }
            }
        });
    }

    public void initToolBar() {

        toolBar = (Toolbar) findViewById(R.id.toolBar);

        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);
        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(this, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(getResources().getString(R.string.txt_userList));


        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                onBackPressed();

                break;
        }


    }

    // Click on Invite Buttin After Selection
    public void button_add_click(View view) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (PeopleDataResponse.User user : UserListSingleton.getInstance().getUserList()) {
            if (user.getChecked() != null) {
                arrayList.add(user.getUserId().toString());
            }
        }
        if (!arrayList.isEmpty()) {
            String userIds = android.text.TextUtils.join(",", arrayList);
            JsonObject paramObject = new JsonObject();
            //Log.e("UserIds", "...." + userIds);
            paramObject.addProperty("event_id", eventID);
            paramObject.addProperty("invitations", "[" + userIds + "]");
            showProgressBar(true);
            CallSendInvitaion(paramObject);
        }
    }

    private void showProgressBar(final boolean progressVisible) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    //Network Call to get All users
    public void callGetUser() {
        if (!AppUtils.isOnline(this)) {
            Alerter.create(InviteEventUserListActivity.this)
                    .setBackgroundColorInt(getResources().getColor(R.color.colorOrangeDark))
                    .setIcon(R.drawable.icon_uncheck)
                    .setTitle("Connection Failed").show();
        }
        try {
            showProgressBar(true);
            getUserDataFirst(eventID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserDataFirst(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getUserListForInvitaion(id, new Callback<PeopleDataResponse>() {
            @Override
            public void onResponse(Call<PeopleDataResponse> call, Response<PeopleDataResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        ListData.addAll(response.body().getData());

                        rvPeopleList.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                        rvPeopleList.setLayoutManager(mLayoutManager);
                        rvPeopleList.addItemDecoration(new SpacesItemDecoration(2));
                        adapter = new InvitationPeopleListAdapter(InviteEventUserListActivity.this, ListData);
                        rvPeopleList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
            public void onFailure(Call<PeopleDataResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

    private void CallSendInvitaion(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getResponseSucessInvitaion(params, new Callback<InvitationResponse>() {
            @Override
            public void onResponse(Call<InvitationResponse> call, final Response<InvitationResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        DialogFactory.showDropDownSuccessNotification(
                                mContext,
                                mContext.getString(R.string.tv_alert_success),
                                response.body().getMessage());

                        mContext.finish();
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
            public void onFailure(Call<InvitationResponse> call, Throwable t) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_request_timeout));
                else if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.alert_file_not_found));
                else
                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
            }
        });
    }

}

