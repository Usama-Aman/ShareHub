package adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;

import java.util.ArrayList;

import models.SettingsCheckedCategoryDataModel;
import models.SettingsResponseData;
import utility.AppUtils;
import utility.Constants;
import utility.SharedPreference;

/**
 * Created by M.Mubashir on 5/17/2018.
 */
public class SettingBoubleCategoryAdapter extends RecyclerView.Adapter<SettingBoubleCategoryAdapter.MyViewHolder> {

    private ArrayList<SettingsResponseData.userCategories> List;
    private Context mContext;
    ArrayList<String> data;
    ArrayList<SettingsCheckedCategoryDataModel> checkedList;
    String languageCode = "";
    String item;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mcat;
        public MyViewHolder(View view) {
            super(view);
            mcat = view.findViewById(R.id.tvCat);
        }
    }

    public SettingBoubleCategoryAdapter(Context context, ArrayList<SettingsResponseData.userCategories> eventList, ArrayList<String> dataList, ArrayList<SettingsCheckedCategoryDataModel> checkedList) {
        this.List = eventList;
        this.data = dataList;
        this.checkedList = checkedList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cat_selected, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        SettingsResponseData.userCategories cat = List.get(position);
        languageCode = SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language));
        if (AppUtils.isSet(languageCode)) {
            if (languageCode.equals("en")) {
                holder.mcat.setText(cat.getEcatName());
            } else {
                holder.mcat.setText(cat.getEcat_name_ar());
            }
        }

// Create a Bubble Selected Category in Settings screen
        holder.mcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isSet(languageCode)) {
                    if (languageCode.equals("en")) {
                        item = List.get(position).getEcatName();
                    } else {
                        item = List.get(position).getEcat_name_ar();
                    }
                }

                List.remove(List.get(position));
                data.remove(item);
                for (int i = 0; i < checkedList.size(); i++) {
                    if (checkedList.get(i).getItemText().equalsIgnoreCase(item)) {
                        checkedList.get(i).setChecked(false);
                    }
                }

                SettingBoubleCategoryAdapter.this.notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return List.size();
    }

}

