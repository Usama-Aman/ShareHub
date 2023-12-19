package adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.BlockedPeopleResponseData;
import utility.AppUtils;
import utility.CircleTransform;
import utility.URLs;

/**
 *
 * Adapter class to get blocked people in a group
 *
 */

public class BlockedPeopleListAdapter extends RecyclerView.Adapter<BlockedPeopleListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        public void onDeleteClick(final int position);
    }


    private Activity mContext;
    private ArrayList<BlockedPeopleResponseData> mDataSet;
    OnItemClickListener listener;
    private BlockedPeopleResponseData mItem;

    public BlockedPeopleListAdapter(Activity context, ArrayList<BlockedPeopleResponseData> list, OnItemClickListener listener) {
        mContext = context;
        mDataSet = list;
        this.listener = listener;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout categoryItemRoot;
        private final TextView tvPeople;
        private final ImageButton ibDelete;
        private final CircleImageView ivPeople;


        public ViewHolder(View v) {
            super(v);
            tvPeople = (TextView) v.findViewById(R.id.tvPeople);
            ivPeople = (CircleImageView) v.findViewById(R.id.ivPeople);
            ibDelete = (ImageButton) v.findViewById(R.id.ibDelete);
            categoryItemRoot = (ConstraintLayout) v.findViewById(R.id.llMainContent);
            categoryItemRoot.setTag(this);
        }


        public void bind(final int position, final OnItemClickListener listener) {
            ibDelete.setOnClickListener(new View.OnClickListener() {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }


    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_people_blocklist, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, final int position) {
        mItem = mDataSet.get(position);


        final ViewHolder holder = (ViewHolder) vHolder;

        if (AppUtils.isSet(mItem.getUserFullname())) {
            holder.tvPeople.setText(AppUtils.capitalize(mItem.getUserFullname()));
        }

        if (AppUtils.isSet(mItem.getUserPhoto())) {
            Picasso.with(mContext)
                    .load(URLs.BaseUrlUserImageUsers + mItem.getUserPhoto())   //image url from server
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
                    .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
                    .into(holder.ivPeople);
        }

        ((ViewHolder) holder).bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();

    }


}