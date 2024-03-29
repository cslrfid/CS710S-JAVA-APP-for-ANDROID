package com.csl.cs710ademoapp.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.csl.cs710ademoapp.fragments.AccessMicronFragment;
import com.csl.cs710ademoapp.fragments.InventoryRfidiMultiFragment;

public class MicronAdapter extends FragmentStatePagerAdapter {
    private final int NO_OF_TABS = 2;
    public Fragment fragment0, fragment1, fragment2;

    @Override
    public Fragment getItem(int index) {
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = InventoryRfidiMultiFragment.newInstance(true, "");
                fragment0 = fragment;
                break;
            case 1:
                fragment = AccessMicronFragment.newInstance(false);
                fragment1 = fragment;
                break;
            default:
                fragment = null;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NO_OF_TABS;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public MicronAdapter(FragmentManager fm) {
        super(fm);
    }
}
