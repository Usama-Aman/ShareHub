package fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.NotificationListAdapter;
import models.GeneralResponse;
import models.NotificationListResponse;
import models.NotificationListResponseDatum;
import network.ApiClient;
import notifications.NotificationConfig;
import notifications.NotificationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.PaginationAdapterCallback;
import utility.PaginationScrollListener;
import utility.SharedPreference;
import utility.SimpleDividerItemDecoration;
import utility.WrapContentLinearLayoutManager;

public class NotificationFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, PaginationAdapterCallback {
    private TextView tvNoDataFound;
    private Button btnMarkAllRead;
    private Toolbar toolBar;
    private ImageView btnToolbarRight, btnToolbarBack;
    private TextView tvToolbarTitle;
    private WrapContentLinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvNotificationList;
    private ArrayList<NotificationListResponseDatum> notificationListData = new ArrayList<NotificationListResponseDatum>();
    private NotificationListAdapter adapter;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isVisibleToUser;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        mContext = (AppCompatActivity) getActivity();
        MainActivity.tabHome.setEnabled(true);

        viewInitialize(v);

        return v;
    }

    private void viewInitialize(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        rvNotificationList = (RecyclerView) v.findViewById(R.id.rvNotificationList);
        tvNoDataFound = (TextView) v.findViewById(R.id.tvNoDataFound);
        btnMarkAllRead = (Button) v.findViewById(R.id.btnMarkAllRead);
        progressBar = (FrameLayout) v.findViewById(R.id.progressBar);

        rvNotificationList.setHasFixedSize(true);
        //mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mLayoutManager = new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvNotificationList.setLayoutManager(mLayoutManager);
        rvNotificationList.setItemAnimator(null);
        adapter = new NotificationListAdapter(mContext, notificationListData, new NotificationListAdapter.OnItemClickListener() {
            @Override
            public void onAcceptClick(NotificationListResponseDatum message) {
                int user_id = 0;
                int event_id = 0;
                try {
                    JSONObject notificationData = new JSONObject(message.getNotificationData());
                    user_id = message.getFromUserId();
                    event_id = notificationData.getInt("event_id");
                } catch (Throwable t) { t.printStackTrace(); }
                try {
                    showProgressBar(true);
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("event_id", event_id);
                    paramObject.addProperty("is_accept", 1);
                    paramObject.addProperty("user_id", user_id);
                    paramObject.addProperty("notification_id", message.getNotiId());
                    requestAcceptOrRecjectCall(paramObject);
                } catch (Exception e) { e.printStackTrace(); }
            }

            @Override
            public void onRejectClick(NotificationListResponseDatum message) {
                int user_id = 0;
                int event_id = 0;
                try {
                    JSONObject notificationData = new JSONObject(message.getNotificationData());
                    user_id = message.getFromUserId();
                    event_id = notificationData.getInt("event_id");
                } catch (Throwable t) { t.printStackTrace(); }
                try {
                    showProgressBar(true);
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("event_id", event_id);
                    paramObject.addProperty("is_accept", 0);
                    paramObject.addProperty("user_id", user_id);
                    paramObject.addProperty("notification_id", message.getNotiId());
                    requestAcceptOrRecjectCall(paramObject);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });

        rvNotificationList.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        rvNotificationList.setAdapter(adapter);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        btnMarkAllRead.setOnClickListener(this);
    }

    public void initToolBar(View v) {
        toolBar = (Toolbar) v.findViewById(R.id.toolBar);
        btnToolbarBack = (ImageView) toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.GONE);

        btnToolbarRight = (ImageView) toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);
        tvToolbarTitle = (TextView) toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_notification));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnMarkAllRead:
                markAllReadNotifications();
                break;

            case R.id.btn_toolbar_back:
                AppUtils.hideKeyboard(mContext);
                mContext.finish();
                break;

            case R.id.btn_toolbar_right:
                AppUtils.hideKeyboard(mContext);
                /*Intent intent = new Intent(mContext, FavoriteDeleteActivity.class);
                startActivity(intent);*/
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotificationListFirstPage();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(NotificationFragment.this).attach(NotificationFragment.this).commit();
        }
    }

    private void showProgressBar(final boolean progressVisible) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        try {
            isLoading = false;
            isLastPage = false;
            currentPage = 1;
            showProgressBar(true);
            loadNotificationListFirstPage();
        } catch (Exception e) { e.printStackTrace(); }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /************************************************************************************/
    //load first page
    private void loadNotificationListFirstPage() {
        notificationListData.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        try {
            if (!AppUtils.isOnline(mContext)) {
                DialogFactory.showDropDownNotification(mContext,
                        mContext.getString(R.string.alert_information),
                        mContext.getString(R.string.alert_internet_connection));
                return;
            }
            showProgressBar(true);
            getNotificationsFirst();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void getNotificationsFirst() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getNotificationList(currentPage, new Callback<NotificationListResponse>() {

            @Override
            public void onResponse(Call<NotificationListResponse> call, final retrofit2.Response<NotificationListResponse> response) {
                try { showProgressBar(false); } catch (Exception e) { e.printStackTrace(); }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData() != null && response.body().getData().getData() != null) {

                            if (response.body().getData().getData().size() > 0) {

                                tvNoDataFound.setVisibility(View.GONE);
                                rvNotificationList.setVisibility(View.VISIBLE);

                                currentPage = response.body().getData().getCurrentPage();
                                TOTAL_PAGES = response.body().getData().getLastPage();

                                if (!notificationListData.isEmpty()) {
                                    notificationListData.clear();
                                    adapter.notifyDataSetChanged();
                                }

                                notificationListData.addAll(0, response.body().getData().getData());
                                //adapter.addAll(response.body().getData().getData());
                                adapter.notifyDataSetChanged();

                                rvNotificationList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                    @Override
                                    protected void loadMoreItems() {
                                        isLoading = true;
                                        loadNotificationListNextPage();
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
                                rvNotificationList.setVisibility(View.GONE);
                                tvNoDataFound.setVisibility(View.VISIBLE);
                            }
                        }else{
                            rvNotificationList.setVisibility(View.GONE);
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
            public void onFailure(Call<NotificationListResponse> call, Throwable t) {
                try { showProgressBar(false); } catch (Exception e) { e.printStackTrace(); }
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

    /*************************************************************/
    //load to next page
    private void loadNotificationListNextPage() {
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
                        getNextNotificationList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            rvNotificationList.post(new Runnable() {
                public void run() {
                    // There is no need to use notifyDataSetChanged()
                    adapter.removeLoadingFooter();
                    isLastPage = true;
                }
            });
        }
    }

    //API call to load next pages of members list of a group
    private void getNextNotificationList() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getNotificationList(currentPage, new Callback<NotificationListResponse>() {
            @Override
            public void onResponse(Call<NotificationListResponse> call, final retrofit2.Response<NotificationListResponse> response) {
                try { showProgressBar(false); } catch (Exception e) { e.printStackTrace(); }
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
                            notificationListData.addAll(notificationListData.size(),response.body().getData().getData());
                            //adapter.addAll(response.body().getData().getData());
                            // Set Adapter to notify here
                            adapter.notifyDataSetChanged();

                            rvNotificationList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    loadNotificationListNextPage();
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
            public void onFailure(Call<NotificationListResponse> call, Throwable t) {
                try { showProgressBar(false); } catch (Exception e) { e.printStackTrace(); }
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
        loadNotificationListNextPage();
    }

    /************************************************************************************************/
    private void requestAcceptOrRecjectCall(JsonObject paramObject) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.acceptOrRejectRequest(paramObject, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final retrofit2.Response<GeneralResponse> response) {
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
                        loadNotificationListFirstPage();
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
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
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

    /*********************************************************************************************/
    private void markAllReadNotifications() {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.markAllReadNotifications(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final retrofit2.Response<GeneralResponse> response) {
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

                        SharedPreference.saveIntegerSharedPrefValue(mContext, Constants.pref_notification_counter, 0);
                        if (!NotificationUtils.isAppIsInBackground(mContext)) {
                            Intent pushNotificationBroadCast = new Intent(NotificationConfig.PUSH_NOTIFICATION);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotificationBroadCast);
                        }
                        loadNotificationListFirstPage();
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
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
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
