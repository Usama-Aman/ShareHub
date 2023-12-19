package adapters;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobiletouch.sharehub.PhoneVerifyActivity;
import com.mobiletouch.sharehub.R;

import java.util.ArrayList;

import models.CountryModel;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.MyViewHolder> {

private ArrayList<CountryModel> countryList;
Context mContext;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title, code;
    RelativeLayout llMainContent;

    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.tvCountry);
        code = (TextView) view.findViewById(R.id.tvCode);
        llMainContent = (RelativeLayout) view.findViewById(R.id.llMainContent);
    }
}


    public CountryListAdapter(Context context , ArrayList<CountryModel> countryList) {
        this.countryList = countryList;
        this.mContext=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_countries, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CountryModel Country = countryList.get(position);
        holder.title.setText(Country.getName());
        holder.code.setText(Country.getCode());

        holder.llMainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhoneVerifyActivity.class);
                intent.putExtra("code", Country.getCode());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }
}