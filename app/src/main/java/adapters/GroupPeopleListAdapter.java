package adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.UserResponseDataDetail;
import utility.AppUtils;
import utility.CircleTransform;
import utility.PaginationAdapterCallback;
import utility.URLs;

/**
 * Adapter to set members list of a group with pagination
 *
 */
public class GroupPeopleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        public void onBlockClick(final int position);
        public void onDeleteClick(final int position);
    }

    private Activity mContext;
    private ArrayList<UserResponseDataDetail> mDataSet;
    OnItemClickListener listener;
    private UserResponseDataDetail mItem;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    private PaginationAdapterCallback mCallback;
    private String errorMsg;

    public GroupPeopleListAdapter(Activity context, ArrayList<UserResponseDataDetail> list, OnItemClickListener listener) {
        mContext = context;
        mDataSet = list;
        this.listener = listener;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout categoryItemRoot;
        private final TextView tv_people_name;
        private final CircleImageView ivPeople;
        ImageButton btn_block, btn_delete;

        public ViewHolder(View v) {
            super(v);
            tv_people_name = (TextView) v.findViewById(R.id.tv_people_name);
            ivPeople = (CircleImageView) v.findViewById(R.id.ivPeople);
            btn_block = (ImageButton) v.findViewById(R.id.btn_block);
            btn_delete = (ImageButton) v.findViewById(R.id.btn_delete);
            categoryItemRoot = (ConstraintLayout) v.findViewById(R.id.llMainContent);
            categoryItemRoot.setTag(this);
        }

        public void bind(final int position, final OnItemClickListener listener) {


            //to block a member in the group
            btn_block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.onBlockClick(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //to delete a member in the group
            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.onDeleteClick(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        View v1 = inflater.inflate(R.layout.item_people_list_edit, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, final int position) {
        mItem = mDataSet.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder holder = (ViewHolder) vHolder;

                if (AppUtils.isSet(mItem.getUserDetail().getUserFullname())) {
                    holder.tv_people_name.setText(AppUtils.capitalize(mItem.getUserDetail().getUserFullname()));
                }

                if (mItem.getGpeopleIsblocked() == 0) {

                } else {

                }

                if (AppUtils.isSet(mItem.getUserDetail().getUserPhoto())) {
                    Picasso.with(mContext)
                            .load(URLs.BaseUrlUserImageUsers + mItem.getUserDetail().getUserPhoto())   //
                            .transform(new CircleTransform())
                            .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
                            .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
                            .into(holder.ivPeople);
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

    //Loading view holder
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
    public void add(UserResponseDataDetail r) {
        try {
            mDataSet.add(r);
            notifyItemInserted(mDataSet.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void addAll(List<UserResponseDataDetail> mDataSet) {
        for (UserResponseDataDetail result : mDataSet) {
            add(result);
        }
    }

    public void remove(UserResponseDataDetail r) {
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
        add(new UserResponseDataDetail());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mDataSet.size() - 1;
        UserResponseDataDetail result = getItem(position);

        if (result != null) {
            mDataSet.remove(position);
            notifyItemRemoved(position);
        }
    }

    public UserResponseDataDetail getItem(int position) {
        return mDataSet.get(position);
    }

}