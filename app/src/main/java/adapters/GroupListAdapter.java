package adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.GroupListResponseData;
import utility.AppUtils;
import utility.Constants;
import utility.RoundedCornersTransform;
import utility.URLs;

/**
 * Adapter to set group list of a user
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    //listeners to handle views in group list item
    public interface OnItemClickListener {
        void onEditClick(final int position);
        void onPeopleViewClick(final int position);
        void onDeleteClick(final int position);
    }


    private Activity mContext;
    private ArrayList<GroupListResponseData> mDataSet;
    OnItemClickListener listener;
    private GroupListResponseData mItem;
    private static final int ITEM = 0;
    private Activity context;

    public GroupListAdapter(Activity context, ArrayList<GroupListResponseData> list, OnItemClickListener listener) {
        mContext = context;
        mDataSet = list;
        this.listener = listener;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout categoryItemRoot;
        private final TextView tv_group_name;
        private final CircleImageView img_group;
        ImageButton btn_edit, btn_people, btn_delete;


        public ViewHolder(View v) {
            super(v);
            tv_group_name = (TextView) v.findViewById(R.id.tv_group_name);
            img_group = (CircleImageView) v.findViewById(R.id.img_group);
            btn_people = (ImageButton) v.findViewById(R.id.btn_people);
            btn_edit = (ImageButton) v.findViewById(R.id.btn_edit);
            btn_delete = (ImageButton) v.findViewById(R.id.btn_delete);
            categoryItemRoot = (ConstraintLayout) v.findViewById(R.id.llMainContent);
            categoryItemRoot.setTag(this);
        }

        //Edit group name
        public void bind(final int position, final OnItemClickListener listener) {
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.onEditClick(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            //viewing and adding members of/in a group
            btn_people.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        listener.onPeopleViewClick(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

//delete a group
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }

    @NonNull
    private ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_group_listing, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, final int position) {
        mItem = mDataSet.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder holder = (ViewHolder) vHolder;


                if (AppUtils.isSet(mItem.getGroupName())) {
                    holder.tv_group_name.setText(AppUtils.capitalize(mItem.getGroupName()));
                }

                holder.img_group.setVisibility(View.INVISIBLE);
                if (AppUtils.isSet(Constants.Image)) {
                    Picasso.with(mContext)
                            .load(URLs.BaseUrlUserImageUsersThumbs + Constants.Image)   //
                            .transform(new RoundedCornersTransform())
                            .placeholder(R.drawable.ic_placeholder) // optional
                            .error(R.drawable.ic_placeholder)         // optional
                            .into(holder.img_group);
                }

                ((ViewHolder) holder).bind(position, listener);

        }


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


}