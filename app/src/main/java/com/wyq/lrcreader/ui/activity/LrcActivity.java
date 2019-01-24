package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.cache.DiskLruCacheUtil;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LrcInfo;
import com.wyq.lrcreader.model.netmodel.gecimemodel.Song;
import com.wyq.lrcreader.share.WeChatShare;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.LogUtil;

import java.io.FileNotFoundException;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Uni.W on 2016/8/30.
 */
public class LrcActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.activity_lrc_view_text)
    public TextView lrcView;
    @BindView(R.id.activity_lrc_view_scrollview)
    public ScrollView scrollView;
    @BindView(R.id.activity_lrc_view_relativelayout)
    public RelativeLayout relativeLayout;
    @BindView(R.id.activity_lrc_view_setmenu)
    public LinearLayout setMenuLayout;

    @BindView(R.id.menu_lrc_view_back_bt)
    public Button menuBackBt;
    @BindView(R.id.menu_lrc_view_hide_bt)
    public Button menuHideBt;
    @BindView(R.id.menu_lrc_view_like_bt)
    public Button menuLikeBt;
    @BindView(R.id.menu_lrc_view_plain_bt)
    public Button menuPlainBt;
    @BindView(R.id.menu_lrc_view_qzone_bt)
    public Button menuQzoneBt;
    @BindView(R.id.menu_lrc_view_weibo_bt)
    public Button menuWeiboBt;
    @BindView(R.id.menu_lrc_view_wechat_bt)
    public Button menuWechatBt;
    @BindView(R.id.menu_lrc_view_moments_bt)
    public Button menuMomentsBt;

    @BindView(R.id.menu_lrc_view_text_size_seek)
    public SeekBar menuTextSizeSeek;

    private Disposable lrcDisposable;
    private Disposable backgroundDisposable;

    private String lrcText, artist, songName;
    private Bitmap albumCover;
    private Song song;
    private LrcInfo lrcInfo;
    float startTextSize = 0;

    private long firstClickTime = 0;
    private float startX = 0, endX = 0, startY = 0, endY = 0;
    private boolean isMenuVisiblity = false;
    private boolean isLike = false;
    private boolean isPlain = false;
    private Animation showAnimation, hideAnimation;

    private DiskLruCacheUtil diskLruCacheUtil;

    private static final String ARGUMENTS_LRC_URI = "LRC_URI";
    private static final String ARGUMENTS_LRC_COVER_URI = "LRC_COVER_URI";

    public static void newInstance(Context context, String lrcUri, String lrcCoverUri) {
        Intent intent = new Intent();
        intent.putExtra(ARGUMENTS_LRC_URI, lrcUri);
        intent.putExtra(ARGUMENTS_LRC_COVER_URI, lrcCoverUri);
        intent.setClass(context, LrcActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.lrc_activity_view;
    }

    @Override
    public void initView() {

        initMenu();

        String lrcUri = Objects.requireNonNull(getIntent().getExtras()).getString(ARGUMENTS_LRC_URI);
        String albumCoverUri = getIntent().getExtras().getString(ARGUMENTS_LRC_COVER_URI);

        loadBackground(albumCoverUri);
        loadLrc(lrcUri);
        lrcView.setOnTouchListener(this);
    }

    private void initMenu() {
        menuBackBt.setOnClickListener(this);
        menuHideBt.setOnClickListener(this);
        menuLikeBt.setOnClickListener(this);
        menuPlainBt.setOnClickListener(this);
        menuQzoneBt.setOnClickListener(this);
        menuWeiboBt.setOnClickListener(this);
        menuWechatBt.setOnClickListener(this);
        menuMomentsBt.setOnClickListener(this);

        startTextSize = lrcView.getTextSize();//the size (in pixels) of the default text size in this TextView

        menuTextSizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lrcView.setTextSize(TypedValue.COMPLEX_UNIT_PX, startTextSize + (progress / 100f - 0.5f) * 20f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void loadBackground(String albumCoverUri) {
        backgroundDisposable = ((BasicApp) getApplication()).getDataRepository()
                .getLrcViewBackground(albumCoverUri, LrcActivity.this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) {
                        lrcView.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    private void loadLrc(String lrcUri) {
        lrcDisposable = ((BasicApp) getApplication()).getDataRepository().getLrc(lrcUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        lrcView.setText(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lrcDisposable != null) {
            lrcDisposable.dispose();
        }
        if (backgroundDisposable != null) {
            backgroundDisposable.dispose();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long secondClickTime = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Toast.makeText(LrcActivity.this,"ckick"+event.getDownTime(),Toast.LENGTH_SHORT).show();
                if (firstClickTime != 0) {
                    secondClickTime = event.getDownTime();
                    if (secondClickTime - firstClickTime < 300) {
                        // Toast.makeText(this, "双击", Toast.LENGTH_SHORT).show();
                        showMenu();
                    }
                }
                firstClickTime = event.getDownTime();

                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getRawX();
                endY = event.getRawY();
                float distanceX = endX - startX;
                float distanceY = Math.abs(endY - startY);
                if (distanceX > 100 && distanceY < 100) {
                    finish();
                }
                break;
            case MotionEvent.ACTION_UP:
                startX = 0;
                startY = 0;
                endX = 0;
                endY = 0;
                break;
            default:
                break;
        }
        return false;
    }

    private void showMenu() {
        if (!isMenuVisiblity) {
            showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                    Animation.RELATIVE_TO_SELF, 0f);
            showAnimation.setDuration(300);
            setMenuLayout.setAnimation(showAnimation);
            setMenuLayout.setVisibility(View.VISIBLE);
            menuHideBt.setBackground(getResources().getDrawable(R.drawable.show_2_white));
            isMenuVisiblity = true;
        } else {
            hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 1f);
            hideAnimation.setDuration(300);
            setMenuLayout.setAnimation(hideAnimation);
            setMenuLayout.setVisibility(View.GONE);
            menuHideBt.setBackground(getResources().getDrawable(R.drawable.hide_1_white));
            isMenuVisiblity = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_lrc_view_back_bt:
                finish();
                break;
            case R.id.menu_lrc_view_hide_bt:
                menuHideBt.setBackground(getResources().getDrawable(R.drawable.hide_1_white));
                hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, 1f);
                hideAnimation.setDuration(300);
                setMenuLayout.setAnimation(hideAnimation);
                setMenuLayout.setVisibility(View.GONE);
                isMenuVisiblity = false;
                break;

            case R.id.menu_lrc_view_qzone_bt:

                break;
            case R.id.menu_lrc_view_weibo_bt:

                break;
            case R.id.menu_lrc_view_wechat_bt:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.menu_lrc_view_moments_bt:
                shareToWX(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.menu_lrc_view_like_bt:
                String filePath = getExternalCacheDir() + "/albumCover";
                LogUtil.i(filePath);
                if (!isLike) {
                    menuLikeBt.setBackground(getResources().getDrawable(R.drawable.like_1_red));
                    isLike = true;
                    diskLruCacheUtil.addToDiskCache(song);

                    //以文件的MD5值为文件名,将封面图片另外存储在文件中
                    if (albumCover == null) {
                        albumCover = song.getAlbumCover();
                    }
                    try {
                        BitmapUtil.saveBitMapToFile(albumCover, BitmapUtil.getBitmapMD5Hex(albumCover), filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    menuLikeBt.setBackground(getResources().getDrawable(R.drawable.unlike_1_white));
                    isLike = false;
                    diskLruCacheUtil.removeFromDiskCache(song);
                    try {
                        BitmapUtil.deleteBitmapFromFile(filePath, BitmapUtil.getBitmapMD5Hex(albumCover));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.menu_lrc_view_plain_bt:
                if (!isPlain) {
                    lrcView.setGravity(Gravity.START);
//                    handler.obtainMessage(0, lrcText).sendToTarget();

                    menuPlainBt.setTextColor(Color.BLACK);
                    isPlain = true;
                } else {
                    lrcView.setGravity(Gravity.CENTER);
//                    if (lrcParserThread != null) {
//                        lrcParserThread.run();
//                    } else {
//                        lrcParserThread = new LrcParserThread();
//                        lrcParserThread.start();
//                    }
                    menuPlainBt.setTextColor(Color.WHITE);
                    isPlain = false;
                }
                break;
            default:
                break;
        }
    }


    public boolean shareToWX(int req) {
        WeChatShare weChatShare = WeChatShare.getInstance(getApplicationContext());
        boolean regB = weChatShare.regToWX();
        LogUtil.i("regB" + (regB ? "true" : "false"));
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.send_img);
        boolean flag = weChatShare.sendImgToWX(BitmapUtil.convertViewToBitmap(scrollView), req);
        LogUtil.i("flag" + (flag ? "true" : "false"));
        return flag;
    }
}
