package com.faisaljaved.myparking.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.faisaljaved.myparking.WorkFlowActivities.ChatFragments.Buyingfragment;
import com.faisaljaved.myparking.WorkFlowActivities.ChatFragments.SellingFragment;

public class ChatAdapter extends FragmentPagerAdapter {

    public ChatAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new Buyingfragment();
            case 1:
                return new SellingFragment();
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "BUYING";
            case 1:
                return "SELLING";
            default:
                return null;
        }
    }
}
