package adapters;

import android.content.Context;
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

import models.GroupsModel;
import singletons.UsersSingleton;
import utility.AppUtils;
import utility.Constants;
import utility.RoundedCornersTransform;
import utility.URLs;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<GroupsModel> contactList;
    private List<GroupsModel> contactListFiltered;
    private GroupsAdapterListener listener;
    private GroupsModel mItem;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbSelected;
        private final ImageView thumbnail;
        public TextView name, phone;
        public RelativeLayout rlMainContent;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            cbSelected = view.findViewById(R.id.cbDaysSelected);
            thumbnail = view.findViewById(R.id.thumbnail);
            rlMainContent = view.findViewById(R.id.rlMainContent);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onGroupSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public GroupsListAdapter(Context context, ArrayList<GroupsModel> contactList, GroupsAdapterListener listener) {
        this.context = context;
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

        if (AppUtils.isSet(mItem.getGroupName())) {
            holder.name.setText(mItem.getGroupName());
        }
        holder.thumbnail.setVisibility(View.GONE);
        if (AppUtils.isSet(Constants.Image)) {
            Picasso.with(context)
                    .load(URLs.BaseUrlUserImageUsersThumbs + Constants.Image)   //
                    .transform(new RoundedCornersTransform())
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(holder.thumbnail);
        }

        //in some cases, it will prevent unwanted situations
        holder.cbSelected.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        if (UsersSingleton.getInstance().getGroupsList() != null && UsersSingleton.getInstance().getGroupsList().size() > 0) {
            if (UsersSingleton.getInstance().getGroupsList().get(position).isSelected()) {
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
                    contactList.get(position).setSelected(true);
                    UsersSingleton.getInstance().setGroupsList(contactList);
                }else {
                    contactList.get(position).setSelected(false);
                    UsersSingleton.getInstance().setGroupsList(contactList);
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
                    List<GroupsModel> filteredList = new ArrayList<>();
                    for (GroupsModel row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getGroupName().toLowerCase().contains(charString.toLowerCase()) ) {
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
                contactListFiltered = (ArrayList<GroupsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface GroupsAdapterListener {
        void onGroupSelected(GroupsModel contact);
    }
}
