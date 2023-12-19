package com.mobiletouch.sharehub;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import adapters.CountryListAdapter;
import models.CountryModel;
import utility.Constants;
import utility.SharedPreference;

public class CountriesList extends AppCompatActivity implements  View.OnClickListener{
    RecyclerView recyclerView;
    EditText etseacrh;
    AppCompatActivity mContext;
    CountryListAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<CountryModel> countryList= new ArrayList<CountryModel>();
    private ImageView btnToolbarRight, btnToolbarBack;
    private TextView tvToolbarTitle;
    private ImageView btnToolbarDrawer;
    private Toolbar toolBar;
    private TextView tvToolbarClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries_list);
        mContext = this;
        initToolBar();
        recyclerView = findViewById(R.id.rvCountries);
        etseacrh = findViewById(R.id.search_et);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new CountryListAdapter(mContext,countryList);
        recyclerView.setAdapter(mAdapter);
        StoreData();
        SearchViewListner();

    }
    public void initToolBar() {
        toolBar = findViewById(R.id.toolBar);

        btnToolbarBack = toolBar.findViewById(R.id.btn_toolbar_back);
        btnToolbarBack.setVisibility(View.VISIBLE);

        btnToolbarRight = toolBar.findViewById(R.id.btn_toolbar_right);
        btnToolbarRight.setVisibility(View.GONE);

        tvToolbarTitle = toolBar.findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mContext.getString(R.string.tv_my_groups));

        if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language)).equalsIgnoreCase("en"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow);
        else if (SharedPreference.unScrambleText(SharedPreference.getSharedPrefValue(mContext, Constants.Pref_Language)).equalsIgnoreCase("ar"))
            btnToolbarBack.setImageResource(R.drawable.ic_white_arrow_right);

        btnToolbarRight.setImageResource(R.drawable.icon_add_members);
        tvToolbarTitle.setText(getResources().getString(R.string.tv_countries));

        btnToolbarBack.setOnClickListener(this);
        btnToolbarRight.setOnClickListener(this);
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    public  void StoreData(){
        countryList.clear();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("countries");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                CountryModel model=new CountryModel();
                String Name = jo_inside.getString("name");
                String Code = jo_inside.getString("dial_code");
                model.setName(Name);
                model.setCode(Code);
                countryList.add(model);


                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void SearchViewListner() {

        etseacrh.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString();

                final ArrayList<CountryModel> filteredList = new ArrayList<CountryModel>();

                for (int i = 0; i < countryList.size(); i++) {

                    final String text = countryList.get(i).getName();
                    if (containsIgnoreCase(text,query.toString())) {

                        filteredList.add(countryList.get(i));
                    }
                }
                if (filteredList.size() > 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mAdapter = new CountryListAdapter(mContext,filteredList);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();  // data set changed
                }
            }
        });
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_toolbar_back:
              onBackPressed();
                break;
        }
    }
}
