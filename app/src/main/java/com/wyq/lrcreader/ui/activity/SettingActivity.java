package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.tencent.mmkv.MMKV;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.datasource.local.cache.MMKVContracts;
import com.wyq.lrcreader.datasource.local.cache.MMKVManager;
import com.wyq.lrcreader.ui.widget.FireToast;
import com.wyq.lrcreader.ui.widget.ParamsSetDialogFragment;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

/**
 * @author Uni.W
 * @date 2019/2/27 17:24
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.setting_activity_apk_version_tv)
    public TextView apkVersionTv;
    @BindView(R.id.setting_activity_search_history_max_number_tv)
    public TextView searchMaxCacheTv;
    @BindView(R.id.setting_activity_cache_limit_tv)
    public TextView cacheMaxSizeTv;

    private int searchCacheMaxnum;
    private int cacheSize;

    public static void newInstance(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.setting_activity;
    }

    @Override
    public void initView() {
        initToolBar();

        initValue();
    }

    public void initValue() {
        searchCacheMaxnum = MMKVManager.getSearchHistoryCacheNumberMax();
        searchMaxCacheTv.setText(searchCacheMaxnum + "条");
        searchMaxCacheTv.setOnClickListener(this);

        cacheSize = MMKVManager.getAppCacheMaxSize();
        cacheMaxSizeTv.setText(cacheSize + "M");
        cacheMaxSizeTv.setOnClickListener(this);

        try {
            apkVersionTv.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initToolBar() {
        toolbar.setTitle(getString(R.string.setting));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_activity_search_history_max_number_tv:
                ParamsSetDialogFragment.newInstance(getString(R.string.search_history_max_number), searchCacheMaxnum + "")
                        .setOnClickListener(new ParamsSetDialogFragment.OnClickListener() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog, int which, String value) {
                                if (value == null || value.length() < 1) {
                                    return;
                                }
                                int number = Integer.parseInt(value);
                                if (number > 0 && number < 1000) {
                                    LogUtil.i("number:" + number);
                                    boolean res = MMKVManager.setSearchHistoryCacheNumberMax(number);
                                    if (res) {
                                        searchMaxCacheTv.setText(number + "条");
                                    }
                                } else {
                                    FireToast.instance(getApplicationContext()).setText(getString(R.string.param_invalid)).show();
                                }
                            }

                            @Override
                            public void onNegativeClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show(getSupportFragmentManager(), getString(R.string.search_history_max_number));

                break;
            case R.id.setting_activity_cache_limit_tv:
                ParamsSetDialogFragment.newInstance(getString(R.string.set_cache_limit), cacheSize + "")
                        .setOnClickListener(new ParamsSetDialogFragment.OnClickListener() {
                            @Override
                            public void onPositiveClick(DialogInterface dialog, int which, String value) {
                                if (value == null || value.length() < 1) {
                                    return;
                                }
                                int number = Integer.parseInt(value);
                                if (number > 0 && number < 1000) {
                                    boolean res = MMKVManager.setAppCacheMaxSize(number);
                                    if (res) {
                                        cacheMaxSizeTv.setText(number + "M");
                                    }
                                } else {
                                    FireToast.instance(getApplicationContext()).setText(getString(R.string.param_invalid)).show();
                                }
                            }

                            @Override
                            public void onNegativeClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show(getSupportFragmentManager(), getString(R.string.set_cache_limit));
                break;
            default:
                break;
        }
    }
}
