package fragments;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiletouch.sharehub.MainActivity;
import com.mobiletouch.sharehub.R;

import adapters.EventsListAdapter;
import utility.AppUtils;

public class EventsFragment extends Fragment {
    static ViewPager viewPager;
    static TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        MainActivity.tabHome.setEnabled(true);
        AppUtils.checkGPS(getActivity());
        CastView(view);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.past)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.live)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.future)));
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        // set properties of tab
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.getTabAt(1).select();
        EventsListAdapter.fromState = "live";
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    EventsListAdapter.fromState = "past";
                } else if (tab.getPosition() == 1) {
                    EventsListAdapter.fromState = "live";
                } else {
                    EventsListAdapter.fromState = "future";
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    // Casting Views
    public void CastView(View view) {
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);
    }

    // View Pager Adapter
    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        //set name of tabs
        private String[] tabTitles = new String[]{getResources().getString(R.string.past), getResources().getString(R.string.live), getResources().getString(R.string.future)};

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
                    // EventsListAdapter.fromState = "past";
                    return new PastFragment();
                case 1:
                    //  EventsListAdapter.fromState = "live";
                    return new LiveFragment();
                case 2:
                    // EventsListAdapter.fromState = "future";
                    return new FutureFragment();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    public static void setPageByUser(int pageNumber) {
        tabLayout.getTabAt(pageNumber).select();
        refreshTab(pageNumber);
    }

    private static void refreshTab(int position) {
        switch (position) {
            case 0:
                PastFragment fragment0 = (PastFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                fragment0.onRefresh();
                break;
            case 1:
                LiveFragment fragment1 = (LiveFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                fragment1.onRefresh();
                break;
            case 2:
                FutureFragment fragment2 = (FutureFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                fragment2.onRefresh();
                break;

        }
    }
}