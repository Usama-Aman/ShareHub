package com.mobiletouch.sharehub;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.FileProvider;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import utility.photoview.PhotoView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;

import adapters.EventCommentsListAdapter;
import fragments.EventDetailsFragment;
import models.CheckDataModel;
import models.EventDetailsResponseData;
import models.EventsCommentsResponseData;
import models.GeneralResponse;
import models.PostCommentResponseData;
import network.ApiClient;
import network.ApiClientMedia;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utility.AppUtils;
import utility.Constants;
import utility.DialogFactory;
import utility.PaginationScrollListener;
import utility.SharedPreference;
import utility.SpacesItemDecoration;
import utility.URLogs;


import static fragments.EventDetailsFragment.isSelfUser;

/**
 * Mubashir 5/22/2018
 */
public class EventCommentsActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    Dialog alertDialog;
    Button mBtComment, mBtPostComment, mBtCancel;
    EditText metComment;
    private PhotoView imageView;
    private String imageUrl;
    int event_id, media_id;
    Boolean islike, isFromNotifications = false;
    private Toolbar toolBar;
    private EventCommentsListAdapter mAdapter;
    public ArrayList<EventsCommentsResponseData.UserInfoData> EventPeopleCommentsData = new ArrayList<EventsCommentsResponseData.UserInfoData>();
    RecyclerView mrecyclerView;
    TextView tv_noData;
    public static boolean isLoadingForRefresh = false;
    private FrameLayout progressBar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    private LinearLayoutManager mLayoutManager;
    String eventid, mediaId;
    RelativeLayout lyComment;
    private AppCompatActivity mContext;
    String language = "";
    CollapsingToolbarLayout collapsingToolbarLayout;
    Button btShare;
    CheckBox checkBoxLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comments);

        mContext = (AppCompatActivity) this;
        language = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        getActivityData();
        castView();
        isLastPage = false;
        isLoading = false;
        callGetEventDetails();
        mBtPostComment.setOnClickListener(this);

    }

    public void setPreviewImage() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);
        showProgressBarSecond(true);

        try {
            if (AppUtils.isSet(imageUrl)) {
                Picasso.with(mContext)
                        .load(imageUrl)   //
                        .placeholder(R.drawable.ic_placeholder) // optional
                        .error(R.drawable.ic_placeholder)         // optional
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                showProgressBarSecond(false);
                            }

                            @Override
                            public void onError() {
                                showProgressBar(false);
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Casting Views
    public void castView() {
        imageView = findViewById(R.id.ivPreview);
        btShare = findViewById(R.id.btShare);
        checkBoxLike = findViewById(R.id.cbLike);

        progressBar = findViewById(R.id.progressBar);
        progressBar.bringToFront();
        tv_noData = findViewById(R.id.tvDataNotFound);
        mrecyclerView = findViewById(R.id.rv_user);
        lyComment = findViewById(R.id.comment_ly);
        mBtPostComment = findViewById(R.id.btpostComment);
        metComment = findViewById(R.id.comment_et);
        mrecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        toolBar = findViewById(R.id.toolBar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.txt_comments));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparent)); // transperent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.rgb(255, 255, 255)); //Color of your title
        if (AppUtils.isSet(language)) {
            if (language.equals("en")) {
                toolBar.setNavigationIcon(R.drawable.ic_white_arrow);
            } else {
                toolBar.setNavigationIcon(R.drawable.ic_white_arrow_right);
            }
        }

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URLogs.m("11 isFromNotifications" + isFromNotifications);
                if (isFromNotifications) {
                    Intent mediaIntent = new Intent(mContext, MediaProfileActivity.class);

                    mediaIntent.putExtra("event_id", event_id + "");
                    mediaIntent.putExtra("isCreator", isSelfUser + "");
                    mediaIntent.putExtra("fromNotifications", true);
                    startActivity(mediaIntent);
                    finish();
                } else {
                    onBackPressed();
                }

            }
        });
        metComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    AppUtils.hideKeyboard(mContext);
                    return true;
                }
                return false;
            }
        });
        btShare.setOnClickListener(this);
        setPreviewImage();

        checkBoxLike.setChecked(islike);

        checkBoxLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                likeMedia();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                URLogs.m("22 isFromNotifications" + isFromNotifications);
                if (isFromNotifications) {

                    Intent mediaIntent = new Intent(mContext, MediaProfileActivity.class);
                    mediaIntent.putExtra("event_id", event_id + "");
                    mediaIntent.putExtra("isCreator", isSelfUser + "");
                    mediaIntent.putExtra("fromNotifications", true);
                    startActivity(mediaIntent);
                    finish();
                } else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareItem() {


        Uri bmpUri = getLocalBitmapUri(imageView);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            // ...sharing failed, handle error
        }
    }

    public Uri getLocalBitmapUri(PhotoView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "sharehub_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(mContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btpostComment:
                AppUtils.hideKeyboard(mContext);
                if (!AppUtils.isOnline(EventCommentsActivity.this)) {
                    DialogFactory.showDropDownNotification(this,
                            this.getString(R.string.alert_information),
                            this.getString(R.string.alert_internet_connection));
                    return;
                }
                try {
                    JsonObject paramObject = new JsonObject();
                    paramObject.addProperty("event_id", event_id);
                    paramObject.addProperty("media_id", media_id);
//                    paramObject.addProperty("comment", encodeEmoji((metComment.getText().toString())));
                    paramObject.addProperty("comment", metComment.getText().toString());
                    showProgressBarSecond(true);
                    callPostComment(paramObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_toolbar_back:
                URLogs.m("33 isFromNotifications" + isFromNotifications);
                if (isFromNotifications) {

                    Intent mediaIntent = new Intent(mContext, MediaProfileActivity.class);
                    mediaIntent.putExtra("event_id", event_id + "");
                    mediaIntent.putExtra("isCreator", isSelfUser + "");
                    mediaIntent.putExtra("fromNotifications", true);
                    startActivity(mediaIntent);
                    finish();
                } else {
                    onBackPressed();
                }
                break;

            case R.id.btShare:
                shareItem();
                break;
        }
    }

    public static String encodeEmoji(String message) {

//        message = message.replaceAll(" ", " ");
//        message = message.replaceAll(" ", " ");
        try {
            return URLEncoder.encode(message,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }


    private void likeMedia() {
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("event_id", event_id);
        paramObject.addProperty("media_id", media_id);

        showProgressBar(true);
        callLikeMedia(paramObject);
    }

    private void callLikeMedia(JsonObject params) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.likeMedia(params, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final Response<GeneralResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {


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
                                getString(R.string.alert_information),
                                getString(R.string.alert_api_not_found));
                    if (response.code() == 500)
                        DialogFactory.showDropDownNotification(
                                mContext,
                                getString(R.string.alert_information),
                                getString(R.string.alert_internal_server_error));
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
                            getString(R.string.alert_information),
                            getString(R.string.alert_file_not_found));
                else DialogFactory.showDropDownNotification(
                        mContext,
                        getString(R.string.alert_information),
                        t.getLocalizedMessage());
            }
        });
    }


    private void callPostComment(JsonObject params) {
        ApiClientMedia apiClient = ApiClientMedia.getInstance();
        apiClient.getResponseSucessComment(params, new Callback<PostCommentResponseData>() {
            @Override
            public void onResponse(Call<PostCommentResponseData> call, final Response<PostCommentResponseData> response) {
                try {
                    showProgressBarSecond(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus()) {
                        Alerter.create(EventCommentsActivity.this)
                                .setBackgroundColorInt(getResources().getColor(R.color.colorLightGreen))
                                .setIcon(getResources().getDrawable(R.drawable.icon_uncheck))
                                .setTitle(getResources().getString(R.string.tv_alert_success)).show();
                        response.body().getMessage();
                        metComment.setText("");
                        onRefresh();


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
            public void onFailure(Call<PostCommentResponseData> call, Throwable t) {
                try {
                    showProgressBarSecond(false);
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


    // Getting Comments
    private void showProgressBar(final boolean progressVisible) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showProgressBarSecond(final boolean progressVisible) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(progressVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void callGetEventCommentsFirst() {


        EventPeopleCommentsData.clear();
        currentPage = 1;


        if (!AppUtils.isOnline(this)) {
            DialogFactory.showDropDownNotification(this,
                    this.getString(R.string.alert_information),
                    this.getString(R.string.alert_internet_connection));
            return;
        }
        try {

            showProgressBar(true);
            eventid = String.valueOf(event_id);
            mediaId = String.valueOf(media_id);
            getEventsCommentsFirst(currentPage, eventid, mediaId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callEventCommentsListNext() {

        if (currentPage < TOTAL_PAGES) {
            currentPage++;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!AppUtils.isOnline(EventCommentsActivity.this)) {
                            Alerter.create(EventCommentsActivity.this)
                                    .setBackgroundColorInt(getResources().getColor(R.color.colorOrangeDark))
                                    .setTitle("Connection Failed").show();
                            return;
                        }
                        eventid = String.valueOf(event_id);
                        mediaId = String.valueOf(media_id);
                        getEventsCommentsNext(currentPage, eventid, mediaId);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            mrecyclerView.post(new Runnable() {
                public void run() {
                    // There is no need to use notifyDataSetChanged()
                    mAdapter.removeLoadingFooter();
                    isLastPage = true;
                }
            });
        }
    }

    // Network Call to get First 20 Comments
    private void getEventsCommentsFirst(int page, String id, String Media_id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventCommentsListing(page, id, Media_id, new Callback<EventsCommentsResponseData>() {
            @Override
            public void onResponse(Call<EventsCommentsResponseData> call, Response<EventsCommentsResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().getData().size() > 0) {
                            tv_noData.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.VISIBLE);

                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();

                            EventPeopleCommentsData.clear();
                            EventPeopleCommentsData.addAll(response.body().getData().getData());

                            mrecyclerView.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(EventCommentsActivity.this, LinearLayoutManager.VERTICAL, false);
                            mrecyclerView.setLayoutManager(mLayoutManager);
                            mrecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mrecyclerView.setNestedScrollingEnabled(false);
                            mAdapter = new EventCommentsListAdapter(EventCommentsActivity.this, EventPeopleCommentsData);
                            mrecyclerView.setAdapter(mAdapter);

                            mrecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    callEventCommentsListNext();
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
                            }

                        } else {

                            mrecyclerView.setVisibility(View.GONE);
                            tv_noData.setVisibility(View.VISIBLE);
                            showProgressBar(false);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            Alerter.create(EventCommentsActivity.this)
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
            public void onFailure(Call<EventsCommentsResponseData> call, Throwable t) {
                showProgressBar(false);
                mrecyclerView.setVisibility(View.GONE);
                tv_noData.setVisibility(View.VISIBLE);
            }
        });


    }
// Network Call to get every next  20 Comments

    private void getEventsCommentsNext(int page, String id, String Media_id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventCommentsListing(page, id, Media_id, new Callback<EventsCommentsResponseData>() {
            @Override
            public void onResponse(Call<EventsCommentsResponseData> call, Response<EventsCommentsResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getData().getData().size() > 0) {

                            tv_noData.setVisibility(View.GONE);
                            mrecyclerView.setVisibility(View.VISIBLE);

                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();
                            mAdapter.removeLoadingFooter();
                            isLoading = false;

                            EventPeopleCommentsData.addAll(response.body().getData().getData());

                            mAdapter.notifyDataSetChanged();
                            mrecyclerView.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
                                @Override
                                protected void loadMoreItems() {
                                    isLoading = true;
                                    callEventCommentsListNext();

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
                            }

                        } else {
                            mAdapter.removeLoadingFooter();
                            isLoading = false;
                            mrecyclerView.setVisibility(View.GONE);
                            tv_noData.setVisibility(View.VISIBLE);
                            showProgressBar(false);
                        }
                    } else {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            Alerter.create(EventCommentsActivity.this)
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
            public void onFailure(Call<EventsCommentsResponseData> call, Throwable t) {
                showProgressBar(false);
                mrecyclerView.setVisibility(View.GONE);
                tv_noData.setVisibility(View.VISIBLE);
            }
        });
    }

    public void callGetEventDetails() {


        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {

            showProgressBar(true);
            eventid = String.valueOf(event_id);
            getEventsData(eventid);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEventsData(String id) {
        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getDetailsOfEvent(id, new Callback<EventDetailsResponseData>() {
            @Override
            public void onResponse(Call<EventDetailsResponseData> call, Response<EventDetailsResponseData> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {

                        CheckDataModel.isAllow = response.body().getData().getEvent().getEventIscommentsAllowed().toString();

                        if (CheckDataModel.isSelf.equalsIgnoreCase("1")) {
                            lyComment.setVisibility(View.VISIBLE);
                            EventDetailsFragment.isSelfUser = "1";
                        } else if (CheckDataModel.isAllow.equalsIgnoreCase("1")) {
                            lyComment.setVisibility(View.VISIBLE);
                        } else {
                            lyComment.setVisibility(View.GONE);
                        }

                        if (CheckDataModel.isSelf.equalsIgnoreCase("1")) {
                            EventDetailsFragment.isSelfUser = "1";
                        } else {
                            EventDetailsFragment.isSelfUser = "0";
                        }

                        callGetEventCommentsFirst();

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
            public void onFailure(Call<EventDetailsResponseData> call, Throwable t) {
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
    public void onRefresh() {
        isLastPage = false;
        isLoading = false;
        callGetEventDetails();
    }

    @Override
    public void onResume() {
        isLastPage = false;
        isLoading = false;
        super.onResume();
        callGetEventDetails();
    }

    //get intent data
    private void getActivityData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imageUrl = bundle.getString("url");
            event_id = bundle.getInt("event_id");
            media_id = bundle.getInt("media_id");
            islike = bundle.getBoolean("islike");

            if (bundle.containsKey("fromNotifications"))
                isFromNotifications = bundle.getBoolean("fromNotifications");
        }
    }
}
