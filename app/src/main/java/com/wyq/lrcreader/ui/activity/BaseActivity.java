package com.wyq.lrcreader.ui.activity;

import android.os.Bundle;

import com.wyq.lrcreader.base.AppExecutors;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.datasource.DataRepository;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

/**
 * @author Uni.W
 * @date 2019/1/20 14:17
 */
public abstract class BaseActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int attachLayoutRes();

    public abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(attachLayoutRes());

//        StatusBarUtils.addStatusViewWithColor(this,android.R.color.transparent);

//        StatusBarUtils.fullScreen(this);

//        StatusBarUtils.setRootViewFitsSystemWindows(this, false);
//        StatusBarUtils.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        // 所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//        if (!StatusBarUtils.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            // 这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtils.setStatusBarColor(this, 0x55000000);
//        }

//        StatusBarUtils.setStatusBarColor(this, android.R.color.transparent);


        ButterKnife.bind(this);

        initView();
    }

    public AppExecutors getExecutors() {
        return ((BasicApp) getApplication()).getExecutors();
    }

    public DataRepository getRepository() {
        return ((BasicApp) getApplication()).getDataRepository();
    }

    public void fragmentAdd(int id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(id, fragment, null)
                .commit();
    }

    public void fragmentReplace(int id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(id, fragment, null)
                .commit();
    }
}
