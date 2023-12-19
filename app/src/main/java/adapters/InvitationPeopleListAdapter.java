package adapters;

import android.app.Activity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import models.PeopleDataResponse;
import models.UserListSingleton;
import utility.AppUtils;
import utility.OnItemClickListener;


public class InvitationPeopleListAdapter extends RecyclerView.Adapter<InvitationPeopleListAdapter.ViewHolder> {

    private Activity mContext;
    private ArrayList<PeopleDataResponse.User> mDataSet;
    private ArrayList<PeopleDataResponse.User> arraylist;
    OnItemClickListener mItemClickListener;
    private PeopleDataResponse.User mItem;

    public InvitationPeopleListAdapter(Activity context, ArrayList<PeopleDataResponse.User> list) {
        mContext = context;
        mDataSet = list;
        this.arraylist = new ArrayList<PeopleDataResponse.User>();
        this.arraylist.addAll(mDataSet);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnItemClickListener {
        private final ConstraintLayout categoryItemRoot;
        private final TextView tvPeople;
        private final CheckBox cbPeople;
        private final ImageView ivPeople;


        public ViewHolder(View v) {
            super(v);
            tvPeople = (TextView) v.findViewById(R.id.tvPeople);
            ivPeople = (ImageView) v.findViewById(R.id.ivPeople);
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_invite_people, parent, false);
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
                    .load(mItem.getFullImage())   //
                    .placeholder(R.drawable.user_placeholder) // optional
                    .error(R.drawable.user_placeholder)         // optional
                    .into(holder.ivPeople);
        }


        if (holder.cbPeople.isChecked())
            holder.cbPeople.setChecked(true);
        else
            holder.cbPeople.setChecked(false);




        holder.cbPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavoriteChecked(position, holder);
            }
        });

        holder.cbPeople.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFavoriteChecked(position, holder);
            }
        });
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setFavoriteChecked(int position, ViewHolder holder) {

        if (holder.cbPeople.isChecked()) {
            mDataSet.get(position).setChecked(true);
        } else {
            mDataSet.get(position).setChecked(false);
        }
        if (mDataSet.get(position).getChecked())
         UserListSingleton.getInstance().setUsersList(mDataSet);
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mDataSet.clear();
        if (charText.length() == 0) {
            mDataSet.addAll(arraylist);
        } else {
            for (PeopleDataResponse.User user : arraylist) {
                if (user.getUserFullname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataSet.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

}