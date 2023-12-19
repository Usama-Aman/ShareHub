package adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobiletouch.sharehub.PublicUserProfileActivity;
import com.mobiletouch.sharehub.R;
import com.mobiletouch.sharehub.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.EventPeopleResponseData;
import models.LoginResult;
import utility.AppUtils;
import utility.PaginationAdapterCallback;
import utility.SharedPreference;
import utility.URLogs;

public class EventPeopleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity mContext;
    private ArrayList<EventPeopleResponseData.EventPeopleData> mDataSet;
    private EventPeopleResponseData.EventPeopleData mItem;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private String errorMsg;
    ViewHolder holder;
    public int isSelfStatus;

    public EventPeopleListAdapter(Activity context, ArrayList<EventPeopleResponseData.EventPeopleData> list) {
        mContext = context;
        mDataSet = list;
        this.mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mPeopleImage;
        TextView mPeopleName;
        ConstraintLayout llMainContent;

        public ViewHolder(View v) {
            super(v);
            mPeopleImage = v.findViewById(R.id.ivPeople);
            mPeopleName = v.findViewById(R.id.tvPeople);
            llMainContent = v.findViewById(R.id.llMainContent);

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
        View v1 = inflater.inflate(R.layout.item_event_people, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, final int position) {
        mItem = mDataSet.get(position);


        switch (getItemViewType(position)) {
            case ITEM:
                holder = (ViewHolder) vHolder;
                URLogs.m("Event Pe0ple name:"+mItem.getUserFullname());
                if (AppUtils.isSet(mItem.getUserFullname())) {
                    holder.mPeopleName.setText(AppUtils.capitalize(mItem.getUserFullname()));
                }


                if (AppUtils.isSet(mItem.getFullImage())) {
                    Picasso.with(mContext)
                            .load(mItem.getFullImage())   //
                            .placeholder(R.drawable.user_placeholder) // optional
                            .error(R.drawable.user_placeholder)         // optional
                            .into(holder.mPeopleImage);
                }

                holder.llMainContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // open User Profile
                        // if isSelfStatus is 1 open LoggedIn User profile Acyivity
                        // in case of 0 open Public User profile Activity
                        if (SharedPreference.isUserLoggedIn(mContext) && SharedPreference.getUserDetails(mContext) != null) {
                            LoginResult userModel = SharedPreference.getUserDetails(mContext);
                            if (userModel.getUserData().getUserId().equals(mDataSet.get(position).getUserId())) {
                                isSelfStatus = 1;
                            } else {
                                isSelfStatus = 0;

                            }
                        }
                        if (isSelfStatus == 1) {
                            Intent intent = new Intent(mContext, UserProfileActivity.class);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, PublicUserProfileActivity.class);
                            String userId = mDataSet.get(position).getUserId().toString();
                            intent.putExtra("userId", userId);
                            mContext.startActivity(intent);
                        }
                    }
                });

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
    public void add(EventPeopleResponseData.EventPeopleData r) {
        try {
            mDataSet.add(r);
            notifyItemInserted(mDataSet.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void addAll(List<EventPeopleResponseData.EventPeopleData> mDataSet) {
        for (EventPeopleResponseData.EventPeopleData result : mDataSet) {
            add(result);
        }
    }

    public void remove(EventPeopleResponseData.EventPeopleData r) {
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
        add(new EventPeopleResponseData.EventPeopleData());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mDataSet.size() - 1;
        EventPeopleResponseData.EventPeopleData result = getItem(position);

        if (result != null) {
            mDataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public EventPeopleResponseData.EventPeopleData getItem(int position) {
        return mDataSet.get(position);
    }

}