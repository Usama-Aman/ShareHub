package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mobiletouch.sharehub.R;

import java.util.ArrayList;

import utility.AppUtils;

public class PhoneVerifySpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context mContext;
    private ArrayList<String> reportData;

    public PhoneVerifySpinnerAdapter(Context context, ArrayList<String> reportData) {
        this.reportData = reportData;
        mContext = context;
    }

    public int getCount() {
        return reportData.size();
    }

    public Object getItem(int i) {
        return reportData.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        String reportDataList = reportData.get(position);
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.phone_verify_dropdown, null);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        if (AppUtils.isSet(reportDataList))
            tvTitle.setText(reportDataList);

        /*TextView tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
        if (AppUtils.isSet(reportDataList.getDescription()))
        tvDesc.setText(reportDataList.getDescription());*/

        return convertView;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        String reportDataList = reportData.get(i);
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.phone_verify_spinner_row, null);/*
        LayoutInflater inflater = mContext.getLayoutInflater();
        View spView = inflater.inflate(R.layout.report_spinner_row, viewgroup, false);*/

        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        if (AppUtils.isSet(reportDataList))
            tvTitle.setText(reportDataList);

        /*TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        if (AppUtils.isSet(reportDataList.getDescription()))
        tvDesc.setText(reportDataList.getDescription());*/
        return view;
    }


}