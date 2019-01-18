package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.tabs.TabLayout;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.HomeViewPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

/**
 * @author Uni.W
 */
public class HomeActivity extends AppCompatActivity {

    private BottomAppBar homeBottomAppBar;
    private TabLayout homeTabLayout;
    private ViewPager homeViewPager;

    private String[] tabName = {"喜欢", "本地", "搜索"};


    public static void newInstance(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initView();
    }


    public void initView() {
        homeBottomAppBar = findViewById(R.id.home_activity_bottom_appbar);
        homeTabLayout = findViewById(R.id.home_activity_appbar_tablayout);
        homeViewPager = findViewById(R.id.home_activity_viewpager);

        homeBottomAppBar.replaceMenu(R.menu.home_appbar);
        for (String name : tabName) {
            homeTabLayout.addTab(homeTabLayout.newTab().setText(name));
        }

        ArrayList tabNameList = new ArrayList();
        Collections.addAll(tabNameList, tabName);
        HomeViewPagerAdapter pagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager(), tabNameList);
        homeViewPager.setAdapter(pagerAdapter);
        homeViewPager.setOffscreenPageLimit(3);
        homeTabLayout.setupWithViewPager(homeViewPager);
    }


}
