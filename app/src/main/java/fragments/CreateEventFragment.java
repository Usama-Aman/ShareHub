package fragments;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import androidx.fragment.app.FragmentStatePagerAdapter;

import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;

import utility.AppUtils;

public class CreateEventFragment extends Fragment implements View.OnClickListener {

    private AppCompatActivity mContext;
    private LinearLayout llMainCode;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout rlMainContent;
    private Toolbar toolBar;
    private ImageView btnToolbarBack;
    private ImageView btnToolbarRight;
    private TextView tvToolbarTitle;
    public static int createdEventCurrentTab = -1;
    //public static LatLng locationSend = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        AppUtils.multiLanguageConfiguration(getActivity());
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.fragment_create_event, container, false);

        mContext = (AppCompatActivity) getActivity();
        MainActivity.tabHome.setEnabled(true);

        initSharedPref();
        getActivityData();
        viewInitialize(v);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlMainContent:
                AppUtils.hideKeyboard(mContext);
                break;
        }
    }

    private void initSharedPref() {
        /*UserModel userModel = SharedPreference.getUserDetails(mContext);
        if (userModel != null && userModel.getAuthToken() != null) {
            auth_token = userModel.getAuthToken();
        }*/
    }

    private void getActivityData() {
        Bundle bundle = mContext.getIntent().getExtras();
        if (bundle != null) {
            //phone = bundle.getString("phone");
        }
    }

    private void viewInitialize(View v) {

        rlMainContent = v.findViewById(R.id.rlMainContent);
        tabLayout = v.findViewById(R.id.tabs);
        viewPager = v.findViewById(R.id.viewpager);

        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_public_event)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.tv_private_event)));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        createdEventCurrentTab = 0;
        // set properties of tab
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rlMainContent.setOnClickListener(this);
    }

    // View Pager Adapter
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        //set name of tabs
        private String[] tabTitles = new String[]{getResources().getString(R.string.tv_public_event), getResources().getString(R.string.tv_private_event)};

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // return fragment to container view
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    createdEventCurrentTab = 0;
                    return new PublicEventFragment();
                case 1:
                    createdEventCurrentTab = 1;
                    return new PrivateEventFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    public void onPause() {
        createdEventCurrentTab = -1;
        super.onPause();
    }
}
