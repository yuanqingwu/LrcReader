package com.wyq.lrcreader.adapter;

import com.wyq.lrcreader.ui.EHomePageType;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * @author Uni.W
 * @date 2019/1/14 17:15
 */
public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        for (EHomePageType type : EHomePageType.values()) {
            if (type.getPosition() == i) {
                return type.getFragment();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return EHomePageType.values().length;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return EHomePageType.values()[position].getPageName();
//    }
}
