package com.wyq.lrcreader.datasource.local.cache;

import com.tencent.mmkv.MMKV;
import com.wyq.lrcreader.base.BasicApp;

/**
 * @author yuanqingwu
 * @date 2019/06/14
 */
public class MMKVManager {

    private static MMKV mmkv;

    static {
        try {
            mmkv = MMKV.defaultMMKV();
        }catch (Throwable throwable){
            throwable.printStackTrace();
            MMKV.initialize(BasicApp.getAppContext());
            try {
                mmkv = MMKV.defaultMMKV();
            }catch (Throwable throwable1){
                throwable1.printStackTrace();
            }
        }
    }

    public static boolean setSearchHistoryCacheNumberMax(int num){
        return mmkv.encode(MMKVContracts.SEARCH_HISTORY_CACHE_MAX_NUMBER,num);
    }

    public static int getSearchHistoryCacheNumberMax(){
       return mmkv.decodeInt(MMKVContracts.SEARCH_HISTORY_CACHE_MAX_NUMBER,0);
    }

    public static boolean setAppCacheMaxSize(int size){
        return mmkv.encode(MMKVContracts.APP_CACHE_MAX_SIZE,size);
    }

    public static int getAppCacheMaxSize(){
        return mmkv.decodeInt(MMKVContracts.APP_CACHE_MAX_SIZE,0);
    }

}
