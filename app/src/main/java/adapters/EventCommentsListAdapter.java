package adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.EventsCommentsResponseData;
import utility.AppUtils;
import utility.PaginationAdapterCallback;

public class EventCommentsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {




    private Activity mContext;
    private ArrayList<EventsCommentsResponseData.UserInfoData> mDataSet;
    private EventsCommentsResponseData.UserInfoData mItem;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private String errorMsg;

    public EventCommentsListAdapter(Activity context, ArrayList<EventsCommentsResponseData.UserInfoData> list) {
        mContext = context;
        mDataSet = list;
        this.mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mPeopleImage;
        TextView mPeopleName,mPeopleComment,mCommentDate;

        public ViewHolder(View v) {
            super(v);
            mPeopleImage=v.findViewById(R.id.iv_commentuser);
            mPeopleName=v.findViewById(R.id.tv_commentuser);
            mCommentDate=v.findViewById(R.id.tv_commentdate);
            mPeopleComment=v.findViewById(R.id.tv_comment);

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
                View v2 = inflater.inflate(R.layout.item_progress_bar, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }


    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_event_comments, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, final int position) {
        mItem = mDataSet.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder holder = (ViewHolder) vHolder;

                if (AppUtils.isSet(mItem.getUserFullname())) {
                    holder.mPeopleName.setText(AppUtils.capitalize(mItem.getUserFullname()));
                }
                if (AppUtils.isSet(mItem.getEcommentText())) {
                    String s ="";
                    String comment= mItem.getEcommentText();
                    try {
                        comment = (mItem.getEcommentText()).replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                        comment = comment.replaceAll("\\+", "%2B");
                        s = URLDecoder.decode((comment),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    holder.mPeopleComment.setText(s);
                }
                if (AppUtils.isSet(mItem.getEcommentDateadded())) {
                    holder.mCommentDate.setText(AppUtils.capitalize(mItem.getEcommentDateadded()));
                }

                if (AppUtils.isSet(mItem.getFullImage())) {
                    Picasso.with(mContext)
                            .load(mItem.getFullImage())   //
                            .placeholder(R.drawable.user_placeholder) // optional
                            .error(R.drawable.user_placeholder)         // optional
                            .into(holder.mPeopleImage);
                }


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
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

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
    public void add(EventsCommentsResponseData.UserInfoData r) {
        try {
            mDataSet.add(r);
            notifyItemInserted(mDataSet.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void addAll(List<EventsCommentsResponseData.UserInfoData> mDataSet) {
        for (EventsCommentsResponseData.UserInfoData result : mDataSet) {
            add(result);
        }
    }

    public void remove(EventsCommentsResponseData.UserInfoData r) {
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
        add(new EventsCommentsResponseData.UserInfoData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mDataSet.size() - 1;
        EventsCommentsResponseData.UserInfoData result = getItem(position);

        if (result != null) {
            mDataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public EventsCommentsResponseData.UserInfoData getItem(int position) {
        return mDataSet.get(position);
    }

}