package fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mobiletouch.sharehub.InviteEventUserListActivity;
import com.mobiletouch.sharehub.R;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import adapters.EventPeopleListAdapter;
import models.EventPeopleResponseData;
import network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.DialogFactory;
import utility.PaginationScrollListener;
import utility.SharedPreference;
import utility.URLogs;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Mubashir 5/23/2018
 */
public class EventPeopleFragment extends EventDetailsFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    private EventPeopleListAdapter mAdapter;
    public ArrayList<EventPeopleResponseData.EventPeopleData> EventPeopleListData = new ArrayList<EventPeopleResponseData.EventPeopleData>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mrecyclerView;
    TextView tv_noData, mparticipantsCount;
    public static boolean isLoadingForRefresh = false;
    private FrameLayout progressBar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    //since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    private LinearLayoutManager mLayoutManager;
    Button btInvite, btInviteUser;
    String mUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_people, container, false);

        mUserId = getArguments().getString("isSelfUser");
        CastView(view);

        return view;
    }


    // Casting Views
    public void CastView(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.bringToFront();
        tv_noData = view.findViewById(R.id.tvNotFound);
        btInvite = view.findViewById(R.id.bt_invite_friends);
        btInviteUser = view.findViewById(R.id.bt_invite_selected_people);
        mrecyclerView = view.findViewById(R.id.rv_user);
        mparticipantsCount = view.findViewById(R.id.count_attend);
        //mrecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        // Hide Event is related to other user
        // Show if Event is Related to LoggedIn User

        if (SharedPreference.isUserLoggedIn(getApplicationContext())) {

            if (mUserId.equalsIgnoreCase("1")) {
                btInviteUser.setVisibility(View.VISIBLE);
            } else {
                btInviteUser.setVisibility(View.GONE);
            }
        }
        callGetEventPeopleFirst();

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        callGetEventPeopleFirst();
        btInviteUser.setOnClickListener(this);
        btInvite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_invite_friends:
             /*   Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        EventDetailsFragment.eventTitle + System.getProperty("line.separator") + "http://sharehubapp.com/public/event_detail/" + eventID);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getActivity().getResources().getString(R.string.tv_select)));*/
                try {
                    if (!AppUtils.isOnline(getActivity()))
                        return;
                    EventOverviewFragment.qrCodeIv.buildDrawingCache();
                    Bitmap bmap = EventOverviewFragment.qrCodeIv.getDrawingCache();
                    String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                            bmap, "Title", null);
                    AppUtils.shareLinkToSocial(getActivity(), eventID, EventDetailsFragment.eventTitle, "", path, progressBar);
                } catch (Exception e) {
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.tv_error),
                            getActivity().getString(R.string.tv_errordynamic_share));
                }
                break;
            case R.id.bt_invite_selected_people:
                Intent myIntent = new Intent(getActivity(), InviteEventUserListActivity.class);
                myIntent.putExtra("event_id", eventID);
                getActivity().startActivity(myIntent);
                break;

        }

    }

    private void showProgressBar(final boolean progressVisible) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    //Network Call to get List of Peoples who join event

    public void callGetEventPeopleFirst() {


        EventPeopleListData.clear();
        currentPage = 1;

        if (!AppUtils.isOnline(getActivity())) {
            DialogFactory.showDropDownNotification(getActivity(),
                    this.getString(R.string.alert_information),
                    this.getString(R.string.alert_internet_connection));
            return;
        }
        try {

            showProgressBar(true);

            getEventsPeopleDataFirst(eventID, currentPage);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callEventPeopleListNext() {

        if (currentPage < TOTAL_PAGES) {
            currentPage++;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!AppUtils.isOnline(getActivity())) {
                            DialogFactory.showDropDownNotification(getActivity(),
                                    getActivity().getString(R.string.alert_information),
                                    getActivity().getString(R.string.alert_internet_connection));
                            return;
                        }
                        getEventsPeopleDataNext(eventID, currentPage);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            mAdapter.removeLoadingFooter();
            isLastPage = true;
        }
    }


    private void getEventsPeopleDataFirst(String id, int page) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsPeople(id, page, new Callback<EventPeopleResponseData>() {
            @Override
            public void onResponse(Call<EventPeopleResponseData> call, Response<EventPeopleResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().size() > 0) {

                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tv_noData.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.VISIBLE);

                            //  currentPage = response.body().getData().getCurrentPage();
                            // TOTAL_PAGES = response.body().getData().getLastPage();

                            EventPeopleListData.clear();
                            EventPeopleListData.addAll(response.body().getData());
                            URLogs.m("People count:" + response.body().getTotal_participants());
                            mparticipantsCount.setText(response.body().getTotal_participants() + "");
                            mrecyclerView.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                            mrecyclerView.setLayoutManager(mLayoutManager);
                            mrecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mAdapter = new EventPeopleListAdapter(getActivity(), EventPeopleListData);
                            mrecyclerView.setAdapter(mAdapter);
                            mrecyclerView.setNestedScrollingEnabled(false);

                           /* mrecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    callEventPeopleListNext();
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
                            });*/


                        } else {
//                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.GONE);
                            tv_noData.setVisibility(View.GONE);
                            showProgressBar(false);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            DialogFactory.showDropDownNotification(
                                    getActivity(),
                                    getActivity().getString(R.string.tv_error),
                                    response.body().getMessage());
                        }
                        mrecyclerView.setVisibility(View.GONE);
                        tv_noData.setVisibility(View.VISIBLE);
                        showProgressBar(false);
                    }
                } else {
                    if (response.code() == 404)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                getActivity(),
                                getActivity().getString(R.string.alert_information),
                                getActivity().getString(R.string.alert_internal_server_error));
                }


            }

            @Override
            public void onFailure(Call<EventPeopleResponseData> call, Throwable t) {
                showProgressBar(false);
                mrecyclerView.setVisibility(View.GONE);
                tv_noData.setVisibility(View.VISIBLE);
            }
        });


    }

    private void getEventsPeopleDataNext(String id, int page) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventsPeople(id, page, new Callback<EventPeopleResponseData>() {
            @Override
            public void onResponse(Call<EventPeopleResponseData> call, Response<EventPeopleResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().size() > 0) {

                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tv_noData.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.VISIBLE);

                            // currentPage = response.body().getData().getCurrentPage();
                            //  TOTAL_PAGES = response.body().getData().getLastPage();
                            mAdapter.removeLoadingFooter();

                            EventPeopleListData.addAll(response.body().getData());
                            mparticipantsCount.setText(response.body().getTotal_participants() + "");
                            mAdapter.notifyDataSetChanged();
                            mrecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    callEventPeopleListNext();

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
                            if (currentPage <= TOTAL_PAGES) {
                                mAdapter.addLoadingFooter();
                            } else {
                                isLastPage = true;
                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                        } else {
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.GONE);
                            tv_noData.setVisibility(View.VISIBLE);
                            showProgressBar(false);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            Alerter.create(getActivity())
                                    .setBackgroundColorInt(getResources().getColor(R.color.colorRed))
                                    .setIcon(R.drawable.icon_uncheck)
                                    .setTitle(response.body().getMessage()).show();
                        }
                        mrecyclerView.setVisibility(View.GONE);
                        tv_noData.setVisibility(View.VISIBLE);
                        showProgressBar(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<EventPeopleResponseData> call, Throwable t) {

                try {
                    showProgressBar(false);
                    mrecyclerView.setVisibility(View.GONE);
                    tv_noData.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (t instanceof IOException)
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_file_not_found));
                else if (t instanceof SocketTimeoutException)
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            getActivity().getString(R.string.alert_request_timeout));
                else
                    DialogFactory.showDropDownNotification(
                            getActivity(),
                            getActivity().getString(R.string.alert_information),
                            t.getLocalizedMessage());


            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        callGetEventPeopleFirst();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}

