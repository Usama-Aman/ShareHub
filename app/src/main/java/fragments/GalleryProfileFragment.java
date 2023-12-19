package fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
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
import androidx.recyclerview.widget.GridLayoutManager;
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


import com.mobiletouch.sharehub.EventCommentsActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.service.DownloadService;
import com.mobiletouch.sharehub.service.Download_JobService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import adapters.ProfilePhotoAdapter;
import imageParsing.ImageUtility;
import models.EventMediaResponse;
import models.EventMediaResponseDataDetail;
import models.GeneralResponse;
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
import utility.ProgressRequestBodyImage;
import utility.SpacesItemDecoration;
import utility.URLogs;

/***
 * Fragment to populate list of event's images
 * To upload images and to download images
 */
public class GalleryProfileFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, PaginationAdapterCallback, ProgressRequestBodyImage.UploadCallbacks {

    ImageButton btnAdd, btnDownload;
    ProgressDialog pbProgress;
    private TextView tvNoDataFound;
    private FrameLayout progressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridLayoutManager mGridLayoutManager;
    private AppCompatActivity mContext;
    private RecyclerView rvGalleryList;
    ProfilePhotoAdapter adapter;


    private ArrayList<EventMediaResponseDataDetail> photosListData = new ArrayList<EventMediaResponseDataDetail>();

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;

    private String profileImagePath = null;
    private static final int SELECT_PHOTO = 100;
    private static final int CAMERA_REQUEST = 1888;
    private Uri imageUri = null;
    private String imagePath = "";
    private ImageUtility imageUtility;
    private ThreadPoolExecutor executor;

    int event_id = 0;
    Boolean downloadMessage = false;

    int totalUpload = 0;
    int uploading = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_media, container, false);

        getActivityData();
        mContext = (AppCompatActivity) getActivity();

        pbProgress = new ProgressDialog(mContext);
        pbProgress.setMax(100); // Progress Dialog Max Value
        pbProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
        pbProgress.setProgressDrawable(getResources().getDrawable(R.drawable.progress_states));
        pbProgress.setCancelable(false);

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
        btnAdd.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        rvGalleryList = (RecyclerView) v.findViewById(R.id.rvPhotosList);
        rvGalleryList.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        rvGalleryList.setLayoutManager(mGridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.font_xsmall);
        rvGalleryList.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

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

                        if (response.body().getIsParticipant() == 1) {
                            btnDownload.setVisibility(View.VISIBLE);
                        } else
                            btnDownload.setVisibility(View.GONE);
                        // btnAdd.setVisibility(View.VISIBLE);

                        for (EventMediaResponseDataDetail dataDetail : response.body().getData().getData()) {
                            if (dataDetail.getEmediaType().equals("image")) {
                                photosListData.add(dataDetail);
                            }
                        }

                       /* if (!downloadMessage) {
                            if (photosListData != null && photosListData.size() != 0) {
                                DialogFactory.showDropDownSuccessNotification(
                                        mContext,
                                        mContext.getString(R.string.alert_information),
                                        mContext.getString(R.string.msg_select_photos_download));
                                downloadMessage = true;
                            }
                        }*/

                        if (photosListData.size() > 0) {

                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);


                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();


                            /// Set Adapter code here

                            adapter = new ProfilePhotoAdapter(mContext, photosListData, new ProfilePhotoAdapter.OnItemClickListener() {
                                @Override
                                public void onView(final int position) {
                                    Intent intent = new Intent(mContext, EventCommentsActivity.class);
                                    intent.putExtra("url", photosListData.get(position).getFullImage());
                                    intent.putExtra("event_id", photosListData.get(position).getEventId());
                                    intent.putExtra("media_id", photosListData.get(position).getEmediaId());
                                    intent.putExtra("islike", photosListData.get(position).getIs_liked());
                                    startActivity(intent);
                                }
                            });
                            rvGalleryList.setAdapter(adapter);

                            rvGalleryList.addOnScrollListener(new PaginationScrollListener(mGridLayoutManager) {
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

                        if (response.body().getData().getData().size() > 0) {
                            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);

                            currentPage = response.body().getData().getCurrentPage();
                            TOTAL_PAGES = response.body().getData().getLastPage();


                            adapter.removeLoadingFooter();
                            isLoading = false;

                            // Set ListData to Adapter here
                            for (EventMediaResponseDataDetail dataDetail : response.body().getData().getData()) {
                                if (dataDetail.getEmediaType().equals("image")) {
                                    photosListData.add(dataDetail);
                                }
                            }

                            // Set Adapter to notify here
                            adapter.notifyDataSetChanged();

                            rvGalleryList.addOnScrollListener(new PaginationScrollListener(mGridLayoutManager) {
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

    ArrayList<String> imageUrlToDownload = new ArrayList<>();

    public void uploadImagesCall() {


        imageUrlToDownload = new ArrayList<>();
        for (EventMediaResponseDataDetail dataDetail : adapter.getList()) {
            if (dataDetail.getSelected()) {
                imageUrlToDownload.add(dataDetail.getEmediaFile());
            }
        }

        if (imageUrlToDownload.size() != 0)
            downloadFile();
        else
            DialogFactory.showDropDownSuccessNotification(
                    mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.msg_select_photos_download));
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
            intent.putExtra("list", android.text.TextUtils.join(",", imageUrlToDownload));
            mContext.startService(intent);
        } else {
            Intent intent = new Intent(mContext, Download_JobService.class);
            intent.putExtra("list", android.text.TextUtils.join(",", imageUrlToDownload));
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


    private static final int PERMISSION_REQUEST_CODE = 1;

    private void requestPermission() {

        ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Snackbar.make(getView().findViewById(R.id.clMainContent), mContext.getResources().getString(R.string.alert_permission_denied), Snackbar.LENGTH_LONG).show();
                }
                break;
            case 10:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageWithRealQuality();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_permission_required_image), Toast.LENGTH_SHORT).show();
                }
                return;
        }
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

                if (!photosListData.isEmpty()) {
                    adapter.resetList();
                }
                selectImageWithRealQuality();


                break;
            case R.id.btnDownload:
                AppUtils.hideKeyboard(mContext);
                if (!photosListData.isEmpty())
                    uploadImagesCall();
                else
                    DialogFactory.showDropDownNotification(mContext,
                            mContext.getString(R.string.alert_information),
                            mContext.getResources().getString(R.string.no_Data_found_text));
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
            // adapter.removeLoadingFooter();
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
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    //Camera Capturing
    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, R.string.app_name);
        values.put(MediaStore.Images.Media.DESCRIPTION, mContext.getPackageName());
        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    ArrayList<Uri> imagesURI = new ArrayList<Uri>();


    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;


  @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        imagesURI.clear();
        totalUpload = 1;
        uploading = 1;
        switch (requestCode) {
            case SELECT_PHOTO:

                if (resultCode == mContext.RESULT_OK) {
                    try {


                        ClipData clipData = imageReturnedIntent.getClipData();
                        if (clipData != null) {
                            //Log.e("clipData.getItemCount(): ", "------: " + clipData.getItemCount());
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                imagesURI.add(imageUri);
                                // your code for multiple image selection
                            }
                        } else {
                            //Log.e("clipData.getItemCount(): ", "---single---: ");
                            Uri uri = imageReturnedIntent.getData();
                            imagesURI.add(uri);
                            // your codefor single image selection
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
                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver‌​(), imageUri);
                        InputStream image_stream = mContext.getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
                        imagePath = imageUtility.getImagePath(imageUri, mContext);
                        imagePath = AppUtils.compressImage(imagePath);
                        //bitmap = updateOrientation(imagePath, bitmap);


                        ExifInterface ei = new ExifInterface(imagePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        Bitmap rotatedBitmap = null;
                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }


                        imagePath = saveImage(rotatedBitmap, 100);

                        profileImagePath = imagePath;
                        try {
                            pbProgress.setProgress(0);
                            pbProgress.setMax(100);
                            pbProgress.setMessage(mContext.getResources().getString(R.string.alert_finish_uploading) + "\n" + (uploading + "/" + totalUpload));
                            pbProgress.show();
                            requestUpdateImageApiCall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        imagePath = imageUtility.getPathVideoMedia(mContext,selectedImage);
        profileImagePath = imagePath;

        try {
            pbProgress.setProgress(0);
            pbProgress.setMax(100);
            pbProgress.setMessage(mContext.getResources().getString(R.string.alert_finish_uploading) + "\n" + (uploading + "/" + totalUpload));
            pbProgress.show();
            requestUpdateImageApiCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /******************************************
     * Save image -> converting bitmap into file
     ******************************************/
    public String saveImage(Bitmap bitmap, int quality) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + mContext.getString(R.string.app_name));
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + mContext.getString(R.string.app_name) + File.separator + "IMG_User_GCAM_" + getCurrentDate() + ".jpg";
        File f = new File(imagePath);
        f.createNewFile();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }


    private void requestUpdateImageApiCall() {
        if (!AppUtils.isOnline(mContext)) {
            DialogFactory.showDropDownNotification(mContext,
                    mContext.getString(R.string.alert_information),
                    mContext.getString(R.string.alert_internet_connection));
        }
        try {

            if (AppUtils.isSet(profileImagePath)) {
                profileImagePath = AppUtils.compressImage(profileImagePath);

                final RequestBody eventId = RequestBody.create(okhttp3.MediaType.parse("text/plain"), Integer.toString(event_id));
                File imageFile = new File(profileImagePath);
                ProgressRequestBodyImage image = new ProgressRequestBodyImage(imageFile, this);
                final MultipartBody.Part iFile = MultipartBody.Part.createFormData("media", imageFile.getName(), image);

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

                            uploadImageCall(eventId, iFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                DialogFactory.showDropDownNotification(
                        mContext,
                        mContext.getString(R.string.tv_error),
                        mContext.getResources().getString(R.string.alert_select_image));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageCall(RequestBody eventId, MultipartBody.Part image) {
        ApiClientMedia apiClient = ApiClientMedia.getInstance();
        apiClient.uploadMediaImage(eventId, image, new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, final retrofit2.Response<GeneralResponse> response) {
                try {
                    if (imagesURI.size() == 0)
                        pbProgress.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getStatus()) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {


                            //adapter.removeLoadingFooter();


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
            public void onFailure(Call<GeneralResponse> call, Throwable t) {
                try {
                    pbProgress.dismiss();
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

    public ArrayList<EventMediaResponseDataDetail> getList() {
        return adapter.getList();
    }


}