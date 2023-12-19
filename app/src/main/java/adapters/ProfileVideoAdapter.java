package adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiletouch.sharehub.MediaProfileActivity;
import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.EventMediaResponseDataDetail;
import models.UserSingleton;
import utility.AppUtils;
import utility.DialogFactory;
import utility.RoundRectCornerImageView;
import utility.RoundedCornersTransformThumbnail;


/**
 * Adapter to set video list of a user's event
 * includes video deletion and  downloading
 */
public class ProfileVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        public void onView(final int position);
    }


    private Activity mContext;
    private ArrayList<EventMediaResponseDataDetail> mDataSet;
    OnItemClickListener listener;
    private EventMediaResponseDataDetail mItem;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private String errorMsg;
    int selectedCounter = 0;

    public ProfileVideoAdapter(Activity context, ArrayList<EventMediaResponseDataDetail> list, OnItemClickListener listener) {
        mContext = context;
        mDataSet = list;
        this.listener = listener;
        this.mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout llMainContent;
        private final RoundRectCornerImageView ivVideoImage;
        private final CheckBox cbCheckImage;
        private final Button btPlayVideo;


        public ViewHolder(View v) {
            super(v);
            llMainContent = (RelativeLayout) v.findViewById(R.id.llMainContent);
            ivVideoImage = (RoundRectCornerImageView) v.findViewById(R.id.ivPhotoImage);
            cbCheckImage = (CheckBox) v.findViewById(R.id.cbCheckImage);
            btPlayVideo = (Button) v.findViewById(R.id.btPlayvideo);
        }

        public void bind(final int position, final OnItemClickListener listener) {

            //checkbox for check or uncheck item to delete or download
            cbCheckImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataSet.get(position).getSelected()) {//if video is selected then unselected
                        mDataSet.get(position).setSelected(false);
                        selectedCounter = selectedCounter - 1;//decrement the selection counter

                        if (selectedCounter == 0) {//if no video is selected then hide all checked marks
                            ((MediaProfileActivity) mContext).setDeleteVisible(false);
                            for (EventMediaResponseDataDetail dataDetail : mDataSet) {
                                dataDetail.setShowAll(false);
                            }
                        }

                    } else {//if video item is not selected
                        mDataSet.get(position).setSelected(true);
                        selectedCounter = selectedCounter + 1;//increment the selection counter
                    }

                    UserSingleton.getInstance().setMediaList(mDataSet);//singleton instance to record the state of video list
                }
            });

            //to play video item
            btPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetList();
                    listener.onView(position);
                }
            });


            //video thumbnail area click listener
            ivVideoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mDataSet.get(position).getShowAll()) {//if no item is selected then this will move the screen to view/play video with to see comments
                        listener.onView(position);
                    } else {

                        //at least 10 items are allowed to select at a time
                        if (selectedCounter <= 10) {

                            if (mDataSet.get(position).getSelected()) {//if video is selected then unselected

                                mDataSet.get(position).setSelected(false);
                                cbCheckImage.setChecked(false);
                                selectedCounter = selectedCounter - 1;//decrement the selection counter
                                if (selectedCounter == 0) {//if no video is selected then hide all checked marks
                                    ((MediaProfileActivity) mContext).setDeleteVisible(false);

                                    for (EventMediaResponseDataDetail dataDetail : mDataSet) {
                                        dataDetail.setShowAll(false);
                                    }
                                }

                            } else {//if video item is not selected

                                mDataSet.get(position).setSelected(true);
                                cbCheckImage.setChecked(true);
                                selectedCounter = selectedCounter + 1;//increment the selection counter

                            }

                        } else {//if selection counter increase more than 10

                            if (mDataSet.get(position).getSelected()) {//here first check if user is selecting already selected item
                                mDataSet.get(position).setSelected(false);
                                cbCheckImage.setChecked(false);
                                selectedCounter = selectedCounter - 1;
                                if (selectedCounter == 0) {//if no video is selected then hide all checked marks
                                    ((MediaProfileActivity) mContext).setDeleteVisible(false);

                                    for (EventMediaResponseDataDetail dataDetail : mDataSet) {
                                        dataDetail.setShowAll(false);
                                    }
                                }
                            } else {//if user is not selecting already selected item then show appropriate message to the user
                                if (selectedCounter >= 10) {
                                    DialogFactory.showDropDownNotification(
                                            mContext,
                                            mContext.getString(R.string.tv_error),
                                            mContext.getResources().getString(R.string.tv_photo_selection_size));
                                }
                            }
                        }
                        UserSingleton.getInstance().setMediaList(mDataSet);//singleton instance to record the state of video list
                    }
                }
            });

            //long press on item for enabling selection
            ivVideoImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    if (selectedCounter == 0) {//if no item is selected then show unchecked checkbox views
                        for (EventMediaResponseDataDetail dataDetail : mDataSet) {
                            dataDetail.setShowAll(true);
                        }
                        notifyDataSetChanged();
                        ((MediaProfileActivity) mContext).setDeleteVisible(true);
                    }

                    //at least 10 items are allowed to select at a time
                    if (selectedCounter <= 10) {//if video is selected then unselected

                        cbCheckImage.setChecked(true);
                        cbCheckImage.setVisibility(View.VISIBLE);
                        if (mDataSet.get(position).getSelected()) {
                            mDataSet.get(position).setSelected(false);
                            selectedCounter = selectedCounter - 1;//decrement the selection counter
                            if (selectedCounter == 0) {
                                ((MediaProfileActivity) mContext).setDeleteVisible(false);

                                for (EventMediaResponseDataDetail dataDetail : mDataSet) {
                                    dataDetail.setShowAll(false);
                                }
                            }
                        } else {
                            mDataSet.get(position).setSelected(true);
                            selectedCounter = selectedCounter + 1;//increment the selection counter
                        }

                    } else {//if selection limit exceeds
                        DialogFactory.showDropDownNotification(
                                mContext,
                                mContext.getString(R.string.tv_error),
                                mContext.getResources().getString(R.string.tv_photo_selection_size));
                    }
                    UserSingleton.getInstance().setMediaList(mDataSet);//singleton instance to record the state of video list
                    return true;
                }
            });

        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }


    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_video, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, final int position) {
        mItem = mDataSet.get(position);


        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder holder = (ViewHolder) vHolder;

                if (AppUtils.isSet(mItem.getThumbImage())) {
                    try {
                        getThumbnail(holder.ivVideoImage, mItem.getFullImage());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                    Picasso.with(mContext)
                            .load("")   //
                            .resize(200, 200)
                            .transform(new RoundedCornersTransformThumbnail())
                            .placeholder(R.drawable.ic_placeholder) // optional
                            .error(R.drawable.ic_placeholder)         // optional
                            .into(holder.ivVideoImage);
                }

                holder.cbCheckImage.setChecked(mItem.getSelected());
                if (mItem.getShowAll())
                    holder.cbCheckImage.setVisibility(View.VISIBLE);
                else {
                    holder.cbCheckImage.setVisibility(View.GONE);
                }

                ((ViewHolder) holder).bind(position, listener);
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) vHolder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    mContext.getString(R.string.alert_msg_unknown));
                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }


    }


    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private RelativeLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (RelativeLayout) itemView.findViewById(R.id.loadmore_errorlayout);
            mProgressBar.setVisibility(View.GONE);
            // mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    //mCallback.retryPageLoad();

                    break;
            }
        }
    }


    /*
     * Displays Pagination retry footer view along with appropriate errorMsg
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mDataSet.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mDataSet.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /************************************************************************
     * Helper Function
     ************************************************************************/
    public void add(EventMediaResponseDataDetail r) {
        try {
            mDataSet.add(r);
            notifyItemInserted(mDataSet.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void addAll(List<EventMediaResponseDataDetail> mDataSet) {
        for (EventMediaResponseDataDetail result : mDataSet) {
            add(result);
        }
    }

    public void remove(EventMediaResponseDataDetail r) {
        try {
            int position = mDataSet.indexOf(r);
            if (position > -1) {
                mDataSet.remove(position);
                notifyItemRemoved(position);
            }
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new EventMediaResponseDataDetail());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mDataSet.size() - 1;
        EventMediaResponseDataDetail result = getItem(position);

        if (result != null) {
            mDataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public EventMediaResponseDataDetail getItem(int position) {
        return mDataSet.get(position);
    }

    public void resetList() {

        selectedCounter = 0;
        if (mDataSet.get(0).getShowAll()) {
            for (EventMediaResponseDataDetail dataDetail : mDataSet) {
                dataDetail.setShowAll(false);
                dataDetail.setSelected(false);
            }
            notifyDataSetChanged();
            ((MediaProfileActivity) mContext).setDeleteVisible(false);
        }
    }

    public void getThumbnail(final RoundRectCornerImageView imageView, final String url) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                String videoPath = url;

                MediaMetadataRetriever mediaMetadataRetriever = null;
                try {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    if (Build.VERSION.SDK_INT >= 14) { // no headers included
                        mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                    } else
                        mediaMetadataRetriever.setDataSource(videoPath);
                    bitmap = mediaMetadataRetriever.getFrameAtTime();
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (mediaMetadataRetriever != null)
                        mediaMetadataRetriever.release();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    public ArrayList<EventMediaResponseDataDetail> getList() {
        return mDataSet;
    }

}