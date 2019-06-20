package com.wyq.lrcreader.base;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.wyq.lrcreader.datasource.local.cache.MMKVContracts;
import com.wyq.lrcreader.datasource.local.cache.MMKVManager;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.annotation.Nullable;

public class AppInitIntentService extends IntentService {

    public static String APP_INIT_ACTION = "APP_INIT_ACTION";

    public AppInitIntentService() {
        super("AppInitIntentService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AppInitIntentService.class);
        intent.setAction(APP_INIT_ACTION);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (APP_INIT_ACTION.equals(intent.getAction())) {
            doInitTask();
        }
    }


    private void doInitTask() {
        //todo
        Log.i("Test", "AppInitIntentService is working");
        String rootDir = MMKV.initialize(this);
        LogUtil.i("mmkv root: " + rootDir);
        MMKVManager.setSearchHistoryCacheNumberMax(MMKVContracts.DEFAULT_SEARCH_HISTORY_CACHE_MAX_NUMBER);
        MMKVManager.setAppCacheMaxSize(MMKVContracts.DEFAULT_APP_CACHE_MAX_SIZE);
    }
}
