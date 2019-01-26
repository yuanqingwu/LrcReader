package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.BaseRecyclerViewAdapter;
import com.wyq.lrcreader.adapter.RecyclerGridAdapter;
import com.wyq.lrcreader.adapter.item.ImageTextItemModel;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.cache.DiskLruCacheUtil;
import com.wyq.lrcreader.datasource.DataRepository;
import com.wyq.lrcreader.model.netmodel.gecimemodel.LrcInfo;
import com.wyq.lrcreader.model.netmodel.gecimemodel.Song;
import com.wyq.lrcreader.share.WeChatShare;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.FireShare;
import com.wyq.lrcreader.utils.LogUtil;
import com.wyq.lrcreader.utils.LrcOperationGenerator;
import com.wyq.lrcreader.utils.ScreenUtils;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Uni.W on 2016/8/30.
 */
public class LrcActivity extends BaseActivity implements View.OnClickListener, BaseRecyclerViewAdapter.OnRecyclerItemClickListener {

    @BindView(R.id.activity_lrc_view_text)
    public TextView lrcView;
    @BindView(R.id.activity_lrc_view_scrollview)
    public ScrollView scrollView;
//    @BindView(R.id.activity_lrc_view_relativelayout)
//    public RelativeLayout relativeLayout;
//    @BindView(R.id.activity_lrc_view_setmenu)
//    public LinearLayout setMenuLayout;

//    @BindView(R.id.menu_lrc_view_like_bt)
//    public Button menuLikeBt;
//    @BindView(R.id.menu_lrc_view_plain_bt)
//    public Button menuPlainBt;
//    @BindView(R.id.menu_lrc_view_qzone_bt)
//    public Button menuQzoneBt;
//    @BindView(R.id.menu_lrc_view_weibo_bt)
//    public Button menuWeiboBt;
//    @BindView(R.id.menu_lrc_view_wechat_bt)
//    public Button menuWechatBt;
//    @BindView(R.id.menu_lrc_view_moments_bt)
//    public Button menuMomentsBt;

//    @BindView(R.id.menu_lrc_view_text_size_seek)
//    public SeekBar menuTextSizeSeek;

    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView recyclerView;

    private Disposable lrcDisposable;
    private Disposable backgroundDisposable;

    private List<ImageTextItemModel> menuItemList;
    private String lrcUri;
    private String albumCoverUri;

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

        lrcUri = Objects.requireNonNull(getIntent().getExtras()).getString(ARGUMENTS_LRC_URI);
        albumCoverUri = getIntent().getExtras().getString(ARGUMENTS_LRC_COVER_URI);

        loadBackground(albumCoverUri);
        loadLrc(lrcUri);
    }

    private void initMenu() {

        menuItemList = LrcOperationGenerator.getInstance(this).genMenuList();

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

        bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_close_ib).setOnClickListener(this);
        bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_back_bt).setOnClickListener(this);
        recyclerView = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        RecyclerGridAdapter adapter = new RecyclerGridAdapter(this, menuItemList);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerItemClickListener(this);

        startTextSize = lrcView.getTextSize();//the size (in pixels) of the default text size in this TextView

//        menuTextSizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                lrcView.setTextSize(TypedValue.COMPLEX_UNIT_PX, startTextSize + (progress / 100f - 0.5f) * 20f);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });

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
                        lrcText = s;
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
    public boolean dispatchTouchEvent(MotionEvent event) {

        long secondClickTime = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                LogUtil.i("dispatchTouchEvent:ACTION_DOWN:" + startY);

                if (firstClickTime != 0) {
                    secondClickTime = event.getDownTime();
                    if (secondClickTime - firstClickTime < 300) {
                        // Toast.makeText(this, "双击", Toast.LENGTH_SHORT).show();
                        showMenu();
                    }
                }
                firstClickTime = event.getDownTime();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getRawX();
                endY = event.getRawY();
                LogUtil.i("dispatchTouchEvent:ACTION_MOVE:" + endY);
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                float distanceYabs = Math.abs(endY - startY);
                if (distanceX > 100 && distanceYabs < 100) {
                    finish();
                    return true;
                }
                if (startY > ScreenUtils.getScreenHeightPX(this) * 5 / 6 && distanceY < -100) {
                    bottomSheetDialog.show();
                    return true;
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

        return super.dispatchTouchEvent(event);
    }

    private void showMenu() {
        if (!isMenuVisiblity) {
            bottomSheetDialog.show();
            isMenuVisiblity = true;
        } else {
            bottomSheetDialog.cancel();
            isMenuVisiblity = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_sheet_dialog_close_ib:
                bottomSheetDialog.cancel();
                break;

            case R.id.bottom_sheet_dialog_back_bt:
                finish();
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
//                    menuLikeBt.setBackground(getResources().getDrawable(R.drawable.like_1_red));
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
//                    menuLikeBt.setBackground(getResources().getDrawable(R.drawable.unlike_1_white));
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

//                    menuPlainBt.setTextColor(Color.BLACK);
                    isPlain = true;
                } else {
                    lrcView.setGravity(Gravity.CENTER);
//                    if (lrcParserThread != null) {
//                        lrcParserThread.run();
//                    } else {
//                        lrcParserThread = new LrcParserThread();
//                        lrcParserThread.start();
//                    }
//                    menuPlainBt.setTextColor(Color.WHITE);
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

    private DataRepository getRepository() {
        return ((BasicApp) getApplication()).getDataRepository();
    }


    @Override
    public void onItemClick(View view, int position) {
        LogUtil.i(menuItemList.get(position).getName());
        switch (menuItemList.get(position).getAction()) {
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_WECHAT:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_MOMENTS:
                shareToWX(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_WEIBO:
                Toast.makeText(this, "即将到来~", Toast.LENGTH_SHORT).show();

                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_NORMAL:
                FireShare.shareFileWithSys(this, getString(R.string.app_name), Uri.parse(lrcUri));
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_DOWNLOAD:
//                getRepository().getDbGecimiRepository().
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_TEXT_RESIZE:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_LIKE:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_BACKGROUND_BLUR:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_CHOOSE_LRC_TEXT:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_GEN_BITMAP:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_GEN_VIDEO:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            default:
                break;

        }
    }
}
