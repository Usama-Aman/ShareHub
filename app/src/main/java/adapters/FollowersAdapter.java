package adapters;


import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.FollowersResponse;
import utility.AppUtils;

public class FollowersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity mContext;
    private ArrayList<FollowersResponse.FollowersData> mDataSet;
    private FollowersResponse.FollowersData mItem;

    private static final int ITEM = 0;
    private String errorMsg;
    ViewHolder holder;
    public int isSelfStatus;

    public FollowersAdapter(Activity context, ArrayList<FollowersResponse.FollowersData> list) {
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
        viewHolder = getViewHolder(parent, inflater);
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

        holder = (ViewHolder) vHolder;
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

     /*   holder.llMainContent.setOnClickListener(new View.OnClickListener() {
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
        });*/


    }


    @Override
    public int getItemCount() {
        return mDataSet == null ? 0 : mDataSet.size();
    }



    @Override
    public int getItemViewType(int position) {
        return ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /************************************************************************
     * Helper Function
     ************************************************************************/
    public void add(FollowersResponse.FollowersData r) {
        try {
            mDataSet.add(r);
            notifyItemInserted(mDataSet.size() - 1);
        } catch (IndexOutOfBoundsException e) {
            notifyDataSetChanged();
            e.printStackTrace();
        }
    }

    public void addAll(List<FollowersResponse.FollowersData> mDataSet) {
        for (FollowersResponse.FollowersData result : mDataSet) {
            add(result);
        }
    }

    public void remove(FollowersResponse.FollowersData r) {
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
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }



    public FollowersResponse.FollowersData getItem(int position) {
        return mDataSet.get(position);
    }

}