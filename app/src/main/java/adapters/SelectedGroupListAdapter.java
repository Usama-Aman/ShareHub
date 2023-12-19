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

import models.GroupsModel;
import utility.Constants;
import utility.RoundedCornersTransform;
import utility.URLs;

public class SelectedGroupListAdapter extends RecyclerView.Adapter<SelectedGroupListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<GroupsModel> contactList;
    private List<GroupsModel> contactListFiltered;
    private GroupsAdapterListener listener;
    private GroupsModel mItem;

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
                    listener.onGroupSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public SelectedGroupListAdapter(Context context, List<GroupsModel> contactList, GroupsAdapterListener listener) {
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        mItem = contactListFiltered.get(position);

        //if (mItem.isSelected()) {
        holder.name.setText(mItem.getGroupName());
        //}

        holder.thumbnail.setVisibility(View.GONE);

            Picasso.with(context)
                    .load(URLs.BaseUrlUserImageUsersThumbs + Constants.Image)
                    .transform(new RoundedCornersTransform())
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(holder.thumbnail);


        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactListFiltered.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contactListFiltered.size());
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
                        if (row.getGroupName().toLowerCase().contains(charString.toLowerCase())) {
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
