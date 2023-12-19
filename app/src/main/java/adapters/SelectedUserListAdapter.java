package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import models.UserDetail;
import singletons.UsersSingleton;
import utility.AppUtils;
import utility.RoundedCornersTransform;

/**
 * Created by aamir on 5/31/2018.
 */

public class SelectedUserListAdapter extends RecyclerView.Adapter<SelectedUserListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<UserDetail> contactList;
    private List<UserDetail> contactListFiltered;
    private List<UserDetail> userDetailList=new ArrayList<>();
    private UsersAdapterListener listener;
    private UserDetail mItem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivDelete;
        private final ImageView thumbnail;
        public TextView name, phone;
        public RelativeLayout rlMainContent;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            ivDelete = view.findViewById(R.id.ivDelete);
            thumbnail = view.findViewById(R.id.thumbnail);
            rlMainContent = view.findViewById(R.id.rlMainContent);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onUserSelected(contactList.get(getAdapterPosition()));
                }
            });
        }
    }

    public SelectedUserListAdapter(Context context, List<UserDetail> contactList, UsersAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        mItem = contactListFiltered.get(position);

        //if (mItem.isSelected()) {
        holder.name.setText(mItem.getUserFullname());
        //}
        /*
        if (UsersSingleton.getInstance().getUsersList() != null && UsersSingleton.getInstance().getUsersList().size() > 0) {
            if (UsersSingleton.getInstance().getUsersList().get(position).isSelected()) {
                holder.cbSelected.setChecked(true);
            } else {
                holder.cbSelected.setChecked(false);
            }
        }*/


        if (AppUtils.isSet(mItem.getFullImage()) ) {
            Picasso.with(context)
                    .load(mItem.getFullImage())
                    .transform(new RoundedCornersTransform())
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(holder.thumbnail);
        }

        holder.ivDelete.setTag(position);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                for (int i = 0; i < UsersSingleton.getInstance().getUsersList().size(); i++) {
                    if (UsersSingleton.getInstance().getUsersList().get(i).getUserId() == contactListFiltered.get(holder.getAdapterPosition()).getUserId()) {
                        UsersSingleton.getInstance().getUsersList().get(i).setSelected(false);
                    }
                }
                userDetailList.addAll(UsersSingleton.getInstance().getUsersList());
                UsersSingleton.getInstance().setUsersList(userDetailList);

                contactListFiltered.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), contactListFiltered.size());

            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<UserDetail> filteredList = new ArrayList<>();
                    for (UserDetail row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUserFullname().toLowerCase().contains(charString.toLowerCase()) || row.getUserMobileNumber().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<UserDetail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface UsersAdapterListener {
        void onUserSelected(UserDetail contact);
    }
}
