package com.ituto.android.Fragments.MainFragments;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.ituto.android.Fragments.SessionRequestsFragment;
import com.ituto.android.Fragments.SessionsFragment;
import com.ituto.android.R;

import java.util.ArrayList;
import java.util.List;


public class MainSessionsFragment extends Fragment {

    private View view;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private SessionsFragment sessionsFragment;
    private SessionRequestsFragment sessionRequestsFragment;

    //    private FlightsFragment flightsFragment;
//    private TravelFragment travelFragment;
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_sessions, container, false);
        init();
        return view;
    }

    private void init() {

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        sessionsFragment = new SessionsFragment();
        sessionRequestsFragment = new SessionRequestsFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(sessionsFragment, "Sessions");
        viewPagerAdapter.addFragment(sessionRequestsFragment, "Requests");
        viewPager.setAdapter(viewPagerAdapter);
//
//        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_explore_24);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_flight_24);
//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_card_travel_24);
//
//        BadgeDrawable badgeDrawable = tabLayout.getTabAt(0).getOrCreateBadge();
//        badgeDrawable.setVisible(true);
//        badgeDrawable.setNumber(12);

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}