package fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.BlockedPeopleListActivity;
import com.mobiletouch.sharehub.CreateGroupDialog;
import com.mobiletouch.sharehub.GroupAddPeopleListingActivity;
import com.mobiletouch.sharehub.GroupPeopleListingActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.GroupListAdapter;
import models.CreateGroupResponse;
import models.GroupListResponse;
import models.GroupListResponseData;
import models.GroupUpdateResponse;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Confirm_Alert_Dialog;
import utility.Constants;
import utility.DialogFactory;
import utility.DividerItemDecoration;
import utility.SharedPreference;

/****
 * Fragment to create group, share group,  view block users list and to view group list with delete,edit and member list
 */
public class GroupListingFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvGroupList;
    private ArrayList<GroupListResponseData> groupListData = new ArrayList<GroupListResponseData>();
    private GroupListAdapter adapter;

    Boolean isVisibleToUser;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    ImageView appImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_group_listing, container, false);

        mContext = (AppCompatActivity) getActivity();
        initToolBar(v);
        viewInitialize(v);

        return v;
    }


    //view initialization
    private void viewInitialize(View v) {
        appImage = v.findViewById(R.id.appImage);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        rvGroupList = (RecyclerView) v.findViewById(R.id.rvGroupList);
        tvNoDataFound = (TextView) v.findViewById(R.id.tvNoDataFound);
        progressBar = (FrameLayout) v.findViewById(R.id.progressBar);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        Picasso.with(getActivity())
                .load("http://sharehubapp.com/public/appImage.png")   //
                .into(appImage);

        rvGroupList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvGroupList.setLayoutManager(mLayoutManager);
        adapter = new GroupListAdapter(mContext, groupListData, new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onPeopleViewClick(final int position) {
                Intent myIntent = new Intent(mContext, GroupPeopleListingActivity.class);
                myIntent.putExtra("group_id", "" + groupListData.get(position).getGroupId());
                myIntent.putExtra("group_name", "" + groupListData.get(position).getGroupName());
                startActivity(myIntent);
            }

            @Override
            public void onEditClick(final int position) {
                CreateGroupDialog customDialogManager = new CreateGroupDialog(mContext, groupListData.get(position).getGroupName(), new CreateGroupDialog.OnAddClickListener() {
                    @Override
                    public void onAdd(String paramToSend) {
                        try {

                            JsonObject paramObject = new JsonObject();
                            paramObject.addProperty("group_id", "" + groupListData.get(position).getGroupId());
                            paramObject.addProperty("group_name", "" + paramToSend);
                            showProgressBar(true);
                            updateGroup(paramObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                customDialogManager.show();

            }

            @Override
            public void onDeleteClick(final int position) {


                Confirm_Alert_Dialog confirmAlertDialog = new Confirm_Alert_Dialog(mContext, getResources().getString(R.string.tv_delete_group), new Confirm_Alert_Dialog.OnAddClickListener() {
                    @Override
                    public void onOkClick() {
                        try {
                            JsonObject paramObject = new JsonObject();
                            paramObject.addProperty("group_id", "" + groupListData.get(position).getGroupId());
                            showProgressBar(true);
                            deleteGroup(paramObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                confirmAlertDialog.show();

            }
        });
        rvGroupList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), true, false));
        rvGroupList.setAdapter(adapter);
        v.findViewById(R.id.button_manage).setOnClickListener(this);
        v.findViewById(R.id.button_create).setOnClickListener(this);
        v.findViewById(R.id.button_invite).setOnClickListener(this);
    }

    //toolbar initialization
    public void initToolBar(View v) {

        toolBar = (Toolbar) v.findViewById(R.id.toolBar);
        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.GONE);

        if (mContext instanceof UserProfileActivity) {
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
            btnToolbarBack.setVisibility(View.VISIBLE);
        }

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(getActivity(), Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(getActivity(), Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_my_groups));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_toolbar_back:
                if (mContext instanceof UserProfileActivity) {
                    ((UserProfileActivity) mContext).proceedBackPressed();
                }
                break;
            case R.id.button_manage:
                AppUtils.hideKeyboard(mContext);

                Intent myIntent = new Intent(mContext, BlockedPeopleListActivity.class);
                startActivity(myIntent);

                break;
            case R.id.button_create://to create group open dialog of createGroupDialog
                AppUtils.hideKeyboard(mContext);

                CreateGroupDialog customDialogManager = new CreateGroupDialog(mContext, new CreateGroupDialog.OnAddClickListener() {
                    @Override
                    public void onAdd(String paramToSend) {
                        try {

                            JsonObject paramObject = new JsonObject();
                            paramObject.addProperty("group_name", "" + paramToSend);
                            showProgressBar(true);
                            createGroup(paramObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                customDialogManager.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialogManager.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                customDialogManager.show();

                break;
            case R.id.button_invite://invite users
                AppUtils.hideKeyboard(mContext);

                try {

                    if (!AppUtils.isOnline(getActivity()))
                        return;
                    showProgressBar(true);
                    Picasso.with(mContext)
                            .load("http://sharehubapp.com/public/appImage.png")   //
                            .placeholder(R.drawable.ic_placeholder) // optional
                            .error(R.drawable.ic_placeholder)         // optional
                            .into(appImage, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    showProgressBar(false);
                                    appImage.buildDrawingCache();
                                    Bitmap bmap = appImage.getDrawingCache();
                                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                                            bmap, "Title", null);

                                    AppUtils.inviteToApp(getActivity(), path, progressBar);

                                }

                                @Override
                                public void onError() {
                                    showProgressBar(false);
                                }
                            });


                } catch (Exception e) {
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.tv_error),
                            getActivity().getString(R.string.tv_errordynamic_share));
                }

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
            getGroupList();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            //ft.detach(FavoriteFragment.this).attach(FavoriteFragment.this).commit();
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

    //swipe down to refresh
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        try {
            showProgressBar(true);
            getGroupList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }


    //API call to get group list
    private void getGroupList() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getGroupList(new Callback<GroupListResponse>() {
            @Override
            public void onResponse(Call<GroupListResponse> call, final retrofit2.Response<GroupListResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().size() > 0) {
                            tvNoDataFound.setVisibility(View.GONE);
                            rvGroupList.setVisibility(View.VISIBLE);
                            groupListData.clear();
                            groupListData.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            rvGroupList.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
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


    //API call to create group

    private void createGroup(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.createGroup(paramObject, new Callback<CreateGroupResponse>() {
            @Override
            public void onResponse(Call<CreateGroupResponse> call, final retrofit2.Response<CreateGroupResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().getUsers().size() > 0) {
                            Intent myIntent = new Intent(mContext, GroupAddPeopleListingActivity.class);

                            Gson gson = new Gson();
                            myIntent.putExtra("group_id", response.body().getData().getGroup().getGroupId().toString());
                            myIntent.putExtra("group_name", "" + response.body().getData().getGroup().getGroupName());
                            myIntent.putExtra("users", gson.toJson(response.body().getData()));
                            myIntent.putExtra("form_context", "group_list");

                            startActivity(myIntent);
                        } else {
                            try {
                                showProgressBar(true);
                                getGroupList();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
            public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
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


    //API call to edit group name
    private void updateGroup(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.updateGroup(paramObject, new Callback<GroupUpdateResponse>() {
            @Override
            public void onResponse(Call<GroupUpdateResponse> call, final retrofit2.Response<GroupUpdateResponse> response) {
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


                        try {
                            showProgressBar(true);
                            getGroupList();
                        } catch (Exception e) {
                            e.printStackTrace();
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
            public void onFailure(Call<GroupUpdateResponse> call, Throwable t) {
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
                else {

                    DialogFactory.showDropDownNotification(
                            mContext,
                            mContext.getString(R.string.alert_information),
                            t.getLocalizedMessage());
                }
            }
        });
    }


    //API call to delete group
    private void deleteGroup(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.deleteGroup(paramObject, new Callback<GroupListResponse>() {
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
                        try {
                            showProgressBar(true);
                            getGroupList();
                        } catch (Exception e) {
                            e.printStackTrace();
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
}
