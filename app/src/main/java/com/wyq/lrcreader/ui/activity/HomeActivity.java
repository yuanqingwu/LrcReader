package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.HomeViewPagerAdapter;
import com.wyq.lrcreader.ui.EHomePageType;
import com.wyq.lrcreader.ui.HomeAction;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * @author Uni.W
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    //    @BindView(R.id.home_activity_bottom_appbar)
//    public BottomAppBar bottomAppBar;
    @BindView(R.id.home_activity_bottom_navigation_view)
    public BottomNavigationView navigationView;
    @BindView(R.id.home_activity_appbar_tablayout)
    public TabLayout tabLayout;
    @BindView(R.id.home_activity_viewpager)
    public ViewPager viewPager;
    @BindView(R.id.home_activity_float_action_button)
    public FloatingActionButton floatingActionButton;
    @BindView(R.id.home_activity_setting_ibt)
    public ImageButton settingBt;

    private EHomePageType homePageType = EHomePageType.SEARCH_PAGE;

    /**
     * 缓存之前选择页面的哪个菜单
     */
    private int HOME_SEARCH_SELECTED_ITEM_ID;
    private int HOME_LIKE_SELECTED_ITEM_ID;
    private int HOME_LOCAL_SELECTED_ITEM_ID;

    /**
     * 是否是滑动页面之后重新显示
     */
    private boolean reAppear;

    public static void newInstance(Context context) {
        context.startActivity(new Intent(context, HomeActivity.class));
    }


    @Override
    protected int attachLayoutRes() {
        return R.layout.home_activity;
    }

    @Override
    public void initView() {

        floatingActionButton.setOnClickListener(this);
        settingBt.setOnClickListener(this);

        navigationView.setOnNavigationItemSelectedListener(this);


        HomeViewPagerAdapter pagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.recyclerview_item_tv_layout);
            TextView textView = tab.getCustomView().findViewById(R.id.recyclerview_item_tv);
            textView.setTextColor(Color.WHITE);
            textView.setText(EHomePageType.values()[i].getPageName());
            if (i == 0) {
                textView.setTextSize(18);
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.recyclerview_item_tv)).setTextSize(18);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.recyclerview_item_tv)).setTextSize(14);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.i("onPageSelected:" + position);
                for (EHomePageType type : EHomePageType.values()) {
                    if (type.getPosition() == position) {
                        homePageType = type;
                    }
                }

                reAppear = true;
                navigationView.getMenu().clear();
                navigationView.inflateMenu(homePageType.getMenuId());

                if (homePageType == EHomePageType.SEARCH_PAGE && navigationView.getSelectedItemId() != HOME_SEARCH_SELECTED_ITEM_ID) {
                    navigationView.setSelectedItemId(HOME_SEARCH_SELECTED_ITEM_ID);
                } else if (homePageType == EHomePageType.LIKE_PAGE && navigationView.getSelectedItemId() != HOME_LIKE_SELECTED_ITEM_ID) {
                    navigationView.setSelectedItemId(HOME_LIKE_SELECTED_ITEM_ID);
                } else if (homePageType == EHomePageType.LOCAL_PAGE && navigationView.getSelectedItemId() != HOME_LOCAL_SELECTED_ITEM_ID) {
                    navigationView.setSelectedItemId(HOME_LOCAL_SELECTED_ITEM_ID);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_activity_float_action_button:
                SearchActivity.newInstance(HomeActivity.this);
                break;
            case R.id.home_activity_setting_ibt:
                SettingActivity.newInstance(HomeActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.home_appbar_search_list:
                LogUtil.i("search_list");
                if (itemId != HOME_SEARCH_SELECTED_ITEM_ID || !reAppear) {
                    LiveEventBus.get().with(HomeAction.ACTION_SEARCH).postValue(HomeAction.ACTION_SEARCH_EXPAND_LIST);
                    HOME_SEARCH_SELECTED_ITEM_ID = itemId;
                }
                break;
            case R.id.home_appbar_search_name:
                LogUtil.i("search_name");
                if (itemId != HOME_SEARCH_SELECTED_ITEM_ID || !reAppear) {
                    LiveEventBus.get().with(HomeAction.ACTION_SEARCH).postValue(HomeAction.ACTION_SEARCH_TITLE_LIST);
                    HOME_SEARCH_SELECTED_ITEM_ID = itemId;
                }
                break;
            case R.id.home_appbar_search_source:
                LogUtil.i("search_source");
                if (itemId != HOME_SEARCH_SELECTED_ITEM_ID || !reAppear) {
                    LiveEventBus.get().with(HomeAction.ACTION_SEARCH).postValue(HomeAction.ACTION_SEARCH_SOURCE);
                    HOME_SEARCH_SELECTED_ITEM_ID = itemId;
                }
                break;
            case R.id.home_appbar_like_little:
                LogUtil.i("like_little");
                HOME_LIKE_SELECTED_ITEM_ID = itemId;
                LiveEventBus.get().with(HomeAction.ACTION_LIKE, String.class).postValue(HomeAction.ACTION_LIKE_LITTLE);
                break;
            case R.id.home_appbar_like_normal:
                LogUtil.i("like_normal");
                HOME_LIKE_SELECTED_ITEM_ID = itemId;
                LiveEventBus.get().with(HomeAction.ACTION_LIKE, String.class).postValue(HomeAction.ACTION_LIKE_NORMAL);
                break;
            case R.id.home_appbar_like_most:
                LogUtil.i("like_most");
                HOME_LIKE_SELECTED_ITEM_ID = itemId;
                LiveEventBus.get().with(HomeAction.ACTION_LIKE, String.class).postValue(HomeAction.ACTION_LIKE_MOST);
                break;
            case R.id.home_appbar_local_list:
                LogUtil.i("local_list");
                HOME_LOCAL_SELECTED_ITEM_ID = itemId;
                LiveEventBus.get().with(HomeAction.ACTION_LOCAL, String.class).postValue(HomeAction.ACTION_LOCAL_LIST);
                break;
            case R.id.home_appbar_local_folder:
                LogUtil.i("local_folder");
                HOME_LOCAL_SELECTED_ITEM_ID = itemId;
                LiveEventBus.get().with(HomeAction.ACTION_LOCAL, String.class).postValue(HomeAction.ACTION_LOCAL_FOLDER);
                break;
            case R.id.home_appbar_local_refresh:
                LogUtil.i("local_refresh");
                HOME_LOCAL_SELECTED_ITEM_ID = itemId;
                LiveEventBus.get().with(HomeAction.ACTION_LOCAL, String.class).postValue(HomeAction.ACTION_LOCAL_REFRESH);
                break;
            default:
                break;
        }

        if (reAppear) {
            reAppear = false;
        }

        return true;
    }
}
