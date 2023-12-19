package adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyViewHolder> implements Filterable {
    private Activity mContext;
    private List<UserDetail> contactList;
    private List<UserDetail> contactListFiltered;
    private UsersAdapterListener listener;
    private UserDetail mItem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbSelected;
        private final ImageView thumbnail;
        public TextView name, phone;
        public RelativeLayout rlMainContent;

        public MyViewHolder(View view) {
            super(view);
            this.setIsRecyclable(false);
            name = view.findViewById(R.id.name);
            cbSelected = view.findViewById(R.id.cbDaysSelected);
            thumbnail = view.findViewById(R.id.thumbnail);
            rlMainContent = view.findViewById(R.id.rlMainContent);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onUserSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public UsersListAdapter(Activity context, List<UserDetail> contactList, UsersAdapterListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        mItem = contactListFiltered.get(position);

        if (AppUtils.isSet(mItem.getUserFullname())) {
            holder.name.setText(mItem.getUserFullname());
        }
        if (AppUtils.isSet(mItem.getUserPhoto())) {
            Picasso.with(mContext)
                    .load(mItem.getFullImage())
                    .transform(new RoundedCornersTransform())
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(holder.thumbnail);
        }
        //in some cases, it will prevent unwanted situations
        holder.cbSelected.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        if (UsersSingleton.getInstance().getUsersList() != null && UsersSingleton.getInstance().getUsersList().size() > 0) {
            if (UsersSingleton.getInstance().getUsersList().get(position).isSelected()) {
                mItem.setSelected(true);
                holder.cbSelected.setChecked(true);
            } else {
                mItem.setSelected(false);
                holder.cbSelected.setChecked(false);
            }
        }

        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (buttonView.isChecked()) {
                    contactListFiltered.get(position).setSelected(true);
                    UsersSingleton.getInstance().setUsersList(contactList);
                } else {
                    contactListFiltered.get(position).setSelected(false);
                    UsersSingleton.getInstance().setUsersList(contactList);
                }
            }
        });

        holder.rlMainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbSelected.isChecked())
                    holder.cbSelected.setChecked(false);
                else
                    holder.cbSelected.setChecked(true);
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
                        if (row.getUserFullname().toLowerCase().contains(charString.toLowerCase())) {
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
