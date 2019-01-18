package com.wyq.lrcreader.adapter;

import com.wyq.lrcreader.ui.fragment.LrcListFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * @author Uni.W
 * @date 2019/1/14 17:15
 */
public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> tabList;

    public HomeViewPagerAdapter(FragmentManager fm, List<String> tabList) {
        super(fm);
        this.tabList = tabList;
    }

    @Override
    public Fragment getItem(int i) {
        return LrcListFragment.newInstance(false, "海阔天空");
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return tabList.get(position);
    }
}
