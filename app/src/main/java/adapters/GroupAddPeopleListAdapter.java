package adapters;

import android.app.Activity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserSingleton;
import utility.AppUtils;
import utility.CircleTransform;
import utility.OnItemClickListener;
import utility.URLs;


/**
 * Adapter to set users list from server for adding them to a group
 * Will be called after creating group or adding more members in a group
 */

public class GroupAddPeopleListAdapter extends RecyclerView.Adapter<GroupAddPeopleListAdapter.ViewHolder> {

    private Activity mContext;
    private ArrayList<User> mDataSet;
    private ArrayList<User> arraylist;
    OnItemClickListener mItemClickListener;
    private User mItem;

    public GroupAddPeopleListAdapter(Activity context, ArrayList<User> list) {
        mContext = context;
        mDataSet = list;
        this.arraylist = new ArrayList<User>();

        for (User user : mDataSet) {
            this.arraylist.add(user);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnItemClickListener {
        private final ConstraintLayout categoryItemRoot;
        private final TextView tvPeople;
        private final CheckBox cbPeople;
        private final CircleImageView ivPeople;


        public ViewHolder(View v) {
            super(v);
            tvPeople = (TextView) v.findViewById(R.id.tvPeople);
            ivPeople = (CircleImageView) v.findViewById(R.id.ivPeople);
            cbPeople = (CheckBox) v.findViewById(R.id.cbPeople);
            categoryItemRoot = (ConstraintLayout) v.findViewById(R.id.llMainContent);
            categoryItemRoot.setTag(this);
        }

        @Override
        public void onItemClick(View view, int position) {
            mItemClickListener.onItemClick(view, getPosition()); //OnItemClickListener mItemClickListener;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_add_people, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mItem = mDataSet.get(position);


        if (AppUtils.isSet(mItem.getUserFullname())) {
            holder.tvPeople.setText(AppUtils.capitalize(mItem.getUserFullname()));
        }

        if (AppUtils.isSet(mItem.getUserPhoto())) {
            Picasso.with(mContext)
                    .load(URLs.BaseUrlUserImageUsers + mItem.getUserPhoto())   //
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait) // optional
                    .error(R.drawable.com_facebook_profile_picture_blank_portrait)         // optional
                    .into(holder.ivPeople);
        }


        //in some cases, it will prevent unwanted situations
        holder.cbPeople.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.cbPeople.setChecked(mItem.getChecked());

        holder.categoryItemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPeopleChecked(position, holder);
            }
        });

        holder.cbPeople.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                setPeopleChecked(position, holder);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    public void setPeopleChecked(int position, ViewHolder holder) {
        if (mDataSet.get(position).getChecked()) {
            holder.cbPeople.setChecked(false);
            mDataSet.get(position).setChecked(false);
        } else {
            holder.cbPeople.setChecked(true);
            mDataSet.get(position).setChecked(true);
        }
        UserSingleton.getInstance().setUsersList(mDataSet);
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDataSet.clear();
        if (charText.length() == 0) {
            mDataSet.addAll(arraylist);
        } else {
            for (User user : arraylist) {
                if (user.getUserFullname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataSet.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void initCopyArrayList()
    {
        this.arraylist = new ArrayList<User>();

        for (User user : mDataSet) {
            this.arraylist.add(user);
        }

    }

}