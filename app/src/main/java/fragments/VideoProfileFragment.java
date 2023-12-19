package fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiletouch.sharehub.EventVideoCommentsActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.service.DownloadService;
import com.mobiletouch.sharehub.service.Download_JobService;
import com.yovenny.videocompress.MediaController;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import adapters.ProfileVideoAdapter;
import imageParsing.ImageUtility;
import models.EventMediaResponse;
import models.EventMediaResponseDataDetail;
import models.VideoUploadResponse;
import network.ApiClient;
import network.ApiClientMedia;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import utility.AppUtils;
import utility.DialogFactory;
import utility.PaginationAdapterCallback;
import utility.PaginationScrollListener;
import utility.ProgressRequestBody;
import utility.SpacesItemDecoration;
import utility.URLogs;

/***
 * Fragment to populate list of event's videos
 * To upload videos and to download videos
 */
public class VideoProfileFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, PaginationAdapterCallback, ProgressRequestBody.UploadCallbacks {

    ImageButton btnAdd, btnDownload;
    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvGalleryList;
    ProfileVideoAdapter adapter;
    ProgressDialog pbProgress;
    int totalUpload = 0;
    int uploading = 0;

    private ArrayList<EventMediaResponseDataDetail> photosListData = new ArrayList<EventMediaResponseDataDetail>();

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    private String profileImagePath = null;
    private static final int SELECT_VIDEO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private Uri imageUri = null;
    private String imagePath = "";
    private ImageUtility imageUtility;
    private ThreadPoolExecutor executor;


    ArrayList<String> videoUrlToDownload = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1;

    int event_id = 0;
    Boolean downloadMessage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_media, container, false);

        getActivityData();
        mContext = (AppCompatActivity) getActivity();
        imageUtility = new ImageUtility(mContext);
        initViews(view);

        try {
            loadPeopleListFirstPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void getActivityData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            event_id = bundle.getInt("event_id");
        }
    }


    // init Views
    public void initViews(View v) {

        btnAdd = v.findViewById(R.id.btnAdd);
        btnDownload = v.findViewById(R.id.btnDownload);

        pbProgress = new ProgressDialog(mContext);
        pbProgress.setMax(100); // Progress Dialog Max Value
        pbProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
        pbProgress.setProgressDrawable(getResources().getDrawable(R.drawable.progress_states));
        pbProgress.setCancelable(false);


        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        rvGalleryList = (RecyclerView) v.findViewById(R.id.rvPhotosList);
        rvGalleryList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvGalleryList.setLayoutManager(mLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.font_xsmall);
        rvGalleryList.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        rvGalleryList.setItemAnimator(new DefaultItemAnimator());
        tvNoDataFound = (TextView) v.findViewById(R.id.tvNoDataFound);
        progressBar = (FrameLayout) v.findViewById(R.id.progressBar);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        btnAdd.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
    }


    /*****************************************************************************************/
    /*****************************************************************************************/
    private void loadPeopleListFirstPage() {
        URLogs.m("loadFirstPage: ");

        photosListData.clear();
        currentPage = 1;
        try {
            if (!AppUtils.isOnline(mContext)) {
                DialogFactory.showDropDownNotification(mContext,
                        mContext.getString(R.string.alert_information),
                        mContext.getString(R.string.alert_internet_connection));
                return;
            }


            showProgressBar(true);
            getFirstPeopleList(event_id, currentPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getFirstPeopleList(int param1, int param2) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventMedia(param1, param2, new Callback<EventMediaResponse>() {
            @Override
            public void onResponse(Call<EventMediaResponse> call, final retrofit2.Response<EventMediaResponse> response) {
                try {
                    showProgressBar(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        URLogs.m(" Response " + response.body());

                        if (response.body().getCan_upload_media() == 1) {

                            if (response.body().getIsParticipant() == 1) {
                                btnAdd.setVisibility(View.VISIBLE);
                            } else {
                                btnAdd.setVisibility(View.GONE);
                            }
                        } else {
                            btnAdd.setVisibility(View.GONE);

                        }
                        // btnAdd.setVisibility(View.VISIBLE);


                        for (EventMediaResponseDataDetail dataDetail : response.body().getData().getData()) {
                            if (dataDetail.getEmediaType().equals("video")) {
                                photosListData.add(dataDetail);
                            }
                        }

                        if (response.body().getIsParticipant() == 1) {
                            btnDownload.setVisibility(View.VISIBLE);
                        } else
                            btnDownload.setVisibility(View.GONE);


                        if (photosListData.size() > 0) {

                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);

                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();


                            /// Set Adapter code here

                            adapter = new ProfileVideoAdapter(mContext, photosListData, new ProfileVideoAdapter.OnItemClickListener() {
                                @Override
                                public void onView(final int position) {
                                    Intent intent = new Intent(mContext, EventVideoCommentsActivity.class);
                                    intent.putExtra("url", photosListData.get(position).getFullImage());
                                    intent.putExtra("event_id", photosListData.get(position).getEventId());
                                    intent.putExtra("media_id", photosListData.get(position).getEmediaId());
                                    intent.putExtra("islike", photosListData.get(position).getIs_liked());
                                    startActivity(intent);
                                }
                            });
                            rvGalleryList.setAdapter(adapter);

                            rvGalleryList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
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

                            rvGalleryList.setVisibility(View.VISIBLE);
                            if (response.body().getIsParticipant() != 1) {
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                tvNoDataFound.setVisibility(View.VISIBLE);
                                rvGalleryList.setVisibility(View.GONE);
                            }
                        } else {
                            mSwipeRefreshLayout.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EventMediaResponse> call, Throwable t) {
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

                        getNextPeopleList(event_id, currentPage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {

            rvGalleryList.post(new Runnable() {
                public void run() {
                    // There is no need to use notifyDataSetChanged()
                    adapter.removeLoadingFooter();
                    isLastPage = true;
                }
            });

        }
    }


    private void getNextPeopleList(int param1, int param2) {

        ApiClient apiClient = ApiClient.getInstance();
        apiClient.getEventMedia(param1, param2, new Callback<EventMediaResponse>() {
            @Override
            public void onResponse(Call<EventMediaResponse> call, final retrofit2.Response<EventMediaResponse> response) {
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


                            if (response.body().getCan_upload_media() == 1) {

                                if (response.body().getIsParticipant() == 1) {
                                    btnAdd.setVisibility(View.VISIBLE);
                                } else {
                                    btnAdd.setVisibility(View.GONE);
                                }
                            } else {
                                btnAdd.setVisibility(View.GONE);

                            }


                            // Set ListData to Adapter here
                            for (EventMediaResponseDataDetail dataDetail : response.body().getData().getData()) {
                                if (dataDetail.getEmediaType().equals("video")) {
                                    photosListData.add(dataDetail);
                                }
                            }

                            // Set Adapter to notify here
                            adapter.notifyDataSetChanged();

                            rvGalleryList.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
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
            public void onFailure(Call<EventMediaResponse> call, Throwable t) {
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


    /*****************************************************************************************/
    /*****************************************************************************************/


    public void uploadVideosCall() {


        videoUrlToDownload = new ArrayList<>();
        for (EventMediaResponseDataDetail dataDetail : adapter.getList()) {
            if (dataDetail.getSelected()) {
                videoUrlToDownload.add(dataDetail.getEmediaFile());
            }
        }

        if (videoUrlToDownload.size() != 0)
            downloadFile();
        else
            DialogFactory.showDropDownSuccessNotification(
                    mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.msg_select_videos_download));

    }

    public void downloadFile() {

        if (checkPermission()) {
            startDownload();
        } else {
            requestPermission();
        }
    }


    private void startDownload() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent intent = new Intent(mContext, DownloadService.class);
            intent.putExtra("list", android.text.TextUtils.join(",", videoUrlToDownload));
            mContext.startService(intent);
        } else {
            Intent intent = new Intent(mContext, Download_JobService.class);
            intent.putExtra("list", android.text.TextUtils.join(",", videoUrlToDownload));
            Download_JobService.enqueueWork(getContext(), intent);
        }


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;
        }
    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }


    /*****************************************************************************************/
    /*****************************************************************************************/

    @Override
    public void retryPageLoad() {
        loadPeopleListNextPage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.btnAdd:
                AppUtils.hideKeyboard(mContext);


                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(mContext,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
                    return;
                }

                if (!photosListData.isEmpty())
                    adapter.resetList();
                selectImageWithRealQuality();


                break;
            case R.id.btnDownload:
                AppUtils.hideKeyboard(mContext);

                if (!photosListData.isEmpty()) {
                    uploadVideosCall();
                } else
                    DialogFactory.showDropDownNotification(mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getString(R.string.no_Data_found_text));
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void showProgressBar(final boolean progressVisible) {
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
            reloadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void reloadData() {
        isLoading = false;
        isLastPage = false;
        currentPage = 1;
        loadPeopleListFirstPage();
    }

    /*******************************************************************************************/

    /*********************************************************
     * Image selection with original quality also no cropping
     *********************************************************/

    private void selectImageWithRealQuality() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(mContext);
        View sheetView = getLayoutInflater().inflate(R.layout.image_upload_bottomsheet_layout, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        TextView tvCamera = (TextView) sheetView.findViewById(R.id.tvOpenCamera);
        TextView tvGallery = (TextView) sheetView.findViewById(R.id.tvGallery);
        Button btnCancel = (Button) sheetView.findViewById(R.id.btnCancel);
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();

                openCamera();
            }
        });

        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Edit code here;
                mBottomSheetDialog.dismiss();

                openGallery();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete code here;
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Do something
            }
        });
        mBottomSheetDialog.show();
    }

    //Gallery image selection
    public void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("video/*");
        startActivityForResult(photoPickerIntent, SELECT_VIDEO);

    }

    //Camera Capturing
    public void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.TITLE, R.string.app_name);
        values.put(MediaStore.Video.Media.DESCRIPTION, mContext.getPackageName());
        imageUri = mContext.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }


    ArrayList<Uri> imagesURI = new ArrayList<Uri>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        imagesURI.clear();
        totalUpload = 1;
        uploading = 1;
        switch (requestCode) {
            case SELECT_VIDEO:
                if (resultCode == mContext.RESULT_OK) {
                    try {
                        ClipData clipData = imageReturnedIntent.getClipData();

                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                ClipData.Item videoItem = clipData.getItemAt(i);
                                Uri videoURI = videoItem.getUri();
                                imagesURI.add(videoURI);
                            }
                        } else {
                            Uri videoURI = imageReturnedIntent.getData();
                            imagesURI.add(videoURI);
                        }

                        totalUpload = imagesURI.size();
                        uploading = 1;
                        getImageFromURI();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {

                        Uri selectedImage = imageReturnedIntent.getData();
                        profileImagePath = imageUtility.getImagePath(selectedImage, mContext);

                        compress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    private void getImageFromURI() {
        if (imagesURI.size() != 0) {
            Uri imgUri = imagesURI.get(0);
            imagesURI.remove(0);
            uploadMultipleImages(imgUri);
        }
    }

    private void uploadMultipleImages(Uri selectedImage) {

        try {

            imagePath = imageUtility.getPathVideoMedia(mContext, selectedImage);

            profileImagePath = imagePath;
            compress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******************************************
     * Save image -> converting bitmap into file
     ******************************************/


    private void requestUploadVideoApiCall() {
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {

            if (AppUtils.isSet(profileImagePath)) {

                final RequestBody eventId = RequestBody.create(okhttp3.MediaType.parse("text/plain"), Integer.toString(event_id));
                File videoFile = new File(outPathVideo);
                //final RequestBody video = RequestBody.create(okhttp3.MediaType.parse("video/*"), videoFile);
                ProgressRequestBody video = new ProgressRequestBody(videoFile, this);
                final MultipartBody.Part vFile = MultipartBody.Part.createFormData("media", videoFile.getName(), video);

                executor = new ThreadPoolExecutor(
                        5,
                        10,
                        30,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>()
                );
                executor.allowCoreThreadTimeOut(true);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           //showProgressBar(true);

                            uploadVideoCall(eventId, vFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                DialogFactory.showDropDownNotification(
                        mContext,
                        mContext.getString(R.string.tv_error),
                        mContext.getString(R.string.alert_select_video));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadVideoCall(RequestBody eventId, MultipartBody.Part image) {
        ApiClientMedia apiClient = ApiClientMedia.getInstance();
        apiClient.uploadMediaVideo(eventId, image, new Callback<VideoUploadResponse>() {
            @Override
            public void onResponse(Call<VideoUploadResponse> call, final retrofit2.Response<VideoUploadResponse> response) {
                try {
                    if (imagesURI.size() == 0)
                        pbProgress.dismiss(); // Dismiss Progress Dialog
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {

                            if (imagesURI.size() != 0) {
                                uploading++;
                                getImageFromURI();
                            } else {
                                DialogFactory.showDropDownSuccessNotification(
                                        mContext,
                                        mContext.getString(R.string.tv_alert_success),
                                        response.body().getMessage());
                                isLoading = false;
                                isLastPage = false;
                                currentPage = 1;
                                loadPeopleListFirstPage();
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
            public void onFailure(Call<VideoUploadResponse> call, Throwable t) {
                try {
                    //showProgressBar(false);
                    pbProgress.dismiss(); // Dismiss Progress Dialog
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

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getListSize() {
        return photosListData.size();
    }

    public void resetList() {
        adapter.resetList();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageWithRealQuality();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.alert_permission_required_video), Toast.LENGTH_SHORT).show();
                }
                return;

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Snackbar.make(getView().findViewById(R.id.clMainContent), mContext.getString(R.string.alert_permission_denied), Snackbar.LENGTH_LONG).show();
                }
                break;
        }

    }


    @Override
    public void onProgressUpdate(int percentage) {
        // set current progress
        pbProgress.setProgress(percentage);
    }

    @Override
    public void onError() {
        // do something on error
    }

    @Override
    public void onFinish() {
        // do something on upload finished
        // for example start next uploading at queue
        pbProgress.setProgress(100);
        pbProgress.dismiss(); // Dismiss Progress Dialog
    }


    public static final String APP_DIR = "VideoCompressor";
    public static final String COMPRESSED_VIDEOS_DIR = "/Compressed Videos/";
    public static final String TEMP_DIR = "/Temp/";


    public static void try2CreateCompressDir() {
        File f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR);
        f.mkdirs();
        f = new File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR);
        f.mkdirs();
    }

    String outPathVideo = new String();

    public void compress() {
        File videoFile = new File(profileImagePath);
        try2CreateCompressDir();
        outPathVideo = Environment.getExternalStorageDirectory()
                + File.separator
                + APP_DIR
                + COMPRESSED_VIDEOS_DIR
                + videoFile.getName() + ".mp4";
        new VideoCompressor().execute(profileImagePath, outPathVideo);
    }


    class VideoCompressor extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbProgress.setProgress(0);
            pbProgress.setMessage(mContext.getResources().getString(R.string.alert_finish_uploading) + "\n" + (uploading + "/" + totalUpload));
            pbProgress.show();
            // Log.d(TAG,"Start video compression");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return MediaController.getInstance().convertVideo(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            // pbProgress.dismiss();
            if (compressed) {
                try {
                    // pbProgress.setProgress(0);
                    // pbProgress.show(); // Display Progress Dialog
                    requestUploadVideoApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<EventMediaResponseDataDetail> getList() {
        return adapter.getList();
    }

}