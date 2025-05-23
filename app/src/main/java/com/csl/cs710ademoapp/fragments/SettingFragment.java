package com.csl.cs710ademoapp.fragments;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csl.cs710ademoapp.MainActivity;
import com.csl.cs710ademoapp.adapters.SettingAdapter;
import com.csl.cs710ademoapp.R;

public class SettingFragment extends CommonFragment {
    private ActionBar actionBar;
    private ViewPager viewPager;
    FragmentStatePagerAdapter pagerAdapter;
    SettingAdapter mAdapter;
    Fragment fragment0, fragment1;

    private String[] tabs = { "Operation", "Administration" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, true);
        return inflater.inflate(R.layout.custom_tabbed_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_activity_settings);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.OperationsTabLayout);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        pagerAdapter = new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return tabs.length;
            }
            @Override
            public int getItemPosition(Object object) {
                return PagerAdapter.POSITION_NONE;
            }
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new SettingOperateFragment();   //AccessSecurityLockFragment();    //InventoryRfidiMultiFragment();
                        fragment0 = fragment;
                        break;
                    case 1:
                        fragment1 = new SettingAdminFragment();
                        break;
                    default:
                        fragment = null;
                        break;
                }
                return fragment;
            }
        };
        mAdapter = new SettingAdapter(getActivity().getSupportFragmentManager(), tabs.length);

        viewPager = (ViewPager) getActivity().findViewById(R.id.OperationsPager);
        viewPager.setAdapter(mAdapter); //pagerAdapter); //mAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        for (String tab_name : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab_name));
        }

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
    }

    @Override
    public void onPause() {
        mAdapter.fragment0.onPause();
        mAdapter.fragment1.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mAdapter.fragment0.onStop();
        mAdapter.fragment1.onStop();
        MainActivity.csLibrary4A.setAntennaSelect(0);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mAdapter.fragment0.onDestroyView();
        mAdapter.fragment1.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mAdapter.fragment0.onDestroy();
        mAdapter.fragment1.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mAdapter.fragment0.onDetach();
        mAdapter.fragment1.onDetach();
        super.onDetach();
    }

    public SettingFragment() {
        super("SettingFragment");
    }
}
