package com.wyq.lrcreader.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.wyq.lrcreader.constants.ShareConstants;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.LogUtil;

/**
 * Created by Uni.W on 2016/11/23.
 */

public class WeChatShare {

    private static WeChatShare weChatShare;
    private Context context;
    private IWXAPI api;

    private WeChatShare(Context context) {
        this.context = context;
    }

    public static WeChatShare getInstance(Context context) {

        if (weChatShare == null) {
            weChatShare = new WeChatShare(context);
        }
        return weChatShare;
    }

    public boolean regToWX() {
        api = WXAPIFactory.createWXAPI(context, ShareConstants.WECHAT_APP_ID, true);
        return api.registerApp(ShareConstants.WECHAT_APP_ID);
    }

    public boolean sendImgToWX(Bitmap bitmap, int scene) {

        if (bitmap == null || bitmap.isRecycled()) {
            LogUtil.i("send img is null");
            return false;
        }
        WXImageObject imageObject = new WXImageObject(bitmap);
        WXMediaMessage mediaMessage = new WXMediaMessage();
        mediaMessage.mediaObject = imageObject;

        //Bitmap sendBitMap = Bitmap.createScaledBitmap(bitmap, 100, 300, true);
        Bitmap sendBitMap = bitmap;
        //bitmap.recycle();
        while (sendBitMap.getByteCount() > (250 * 1024)) {
            Matrix matrix = new Matrix();
            matrix.setScale(0.9f, 0.9f);
            LogUtil.i("send bitmap count:" + sendBitMap.getByteCount());
            sendBitMap = Bitmap.createBitmap(sendBitMap, 0, 0, sendBitMap.getWidth(), sendBitMap.getHeight(), matrix, true);
        }
        //LogUtil.i("bitmap count:" + bitmap.getByteCount());

        bitmap.recycle();
        mediaMessage.thumbData = BitmapUtil.bmpToByteArray(sendBitMap, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "img" + System.currentTimeMillis();
        req.message = mediaMessage;
        req.scene = scene;

        return api.sendReq(req);
    }
}
