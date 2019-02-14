package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.BaseRecyclerViewAdapter;
import com.wyq.lrcreader.adapter.RecyclerGridAdapter;
import com.wyq.lrcreader.adapter.item.ImageTextItemModel;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.share.WeChatShare;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.DiskLruCacheUtil;
import com.wyq.lrcreader.utils.FireShare;
import com.wyq.lrcreader.utils.LogUtil;
import com.wyq.lrcreader.utils.LrcOperationGenerator;
import com.wyq.lrcreader.utils.ScreenUtils;
import com.wyq.lrcreader.utils.StorageUtil;

import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Uni.W on 2016/8/30.
 */
public class LrcActivity extends BaseActivity implements View.OnClickListener, BaseRecyclerViewAdapter.OnRecyclerItemClickListener {

    @BindView(R.id.activity_lrc_view_text)
    public TextView lrcView;
    @BindView(R.id.activity_lrc_view_scrollview)
    public ScrollView scrollView;

    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView recyclerView;

    private static final String ARGUMENTS_LRC_LIST_SONG_ENTITY = "LRC_LIST_SONG_ENTITY";

    private Disposable lrcDisposable;
    private Disposable backgroundDisposable;

    private List<ImageTextItemModel> menuItemList;
    private SongEntity songEntity;
    private Bitmap albumCover;
    private String lrcText;
    private SearchResultEntity songListEntity;

    private long firstClickTime = 0;
    private float startX = 0, endX = 0, startY = 0, endY = 0;
    private boolean isMenuVisiblity = false;
    private boolean isLike = false;
    private boolean isPlain = false;
    private Animation showAnimation, hideAnimation;

    private DiskLruCacheUtil diskLruCacheUtil;
    private float startTextSize = 0;

    public static void newInstance(Context context, SearchResultEntity entity) {
        Intent intent = new Intent();
        intent.putExtra(ARGUMENTS_LRC_LIST_SONG_ENTITY, entity);
        intent.setClass(context, LrcActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.lrc_activity;
    }

    @Override
    public void initView() {
        initMenu();

        songListEntity = getIntent().getParcelableExtra(ARGUMENTS_LRC_LIST_SONG_ENTITY);
        if (songListEntity != null) {
            initEntity(songListEntity);
            loadBackground(songListEntity.getAlbumCoverUri());
            loadLrc(songListEntity.getLrcUri());
        }
    }

    private void initEntity(SearchResultEntity entity) {
        songEntity = new SongEntity();
        songEntity.setAid(entity.getAid());
        songEntity.setSongName(entity.getSongName());
//        songEntity.setLrc(lrcText);
        songEntity.setArtist(entity.getArtist());
//        songEntity.setAlbumCover(BitmapUtil.convertIconToString(albumCover));
        songEntity.setDataSource(entity.getDataSource());
        songEntity.setLike(0);
        songEntity.setSearchAt(new Date(System.currentTimeMillis()));
    }

    private void initMenu() {

        menuItemList = LrcOperationGenerator.getInstance(this).genMenuList();

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);
        bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_close_ib).setOnClickListener(this);
        bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_back_ib).setOnClickListener(this);
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
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                        albumCover = bitmap;
                        Bitmap blurBitmap = GlideApp.with(LrcActivity.this)
                                .asBitmap()
                                .load(bitmap)
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation()))
                                .submit().get();
                        return blurBitmap;
                    }
                })
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
                        songEntity.setLrcUri(lrcText);
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
        closeDialog();

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
//                LogUtil.i("dispatchTouchEvent:ACTION_DOWN:" + startY);

                if (firstClickTime != 0) {
                    secondClickTime = event.getDownTime();
                    if (secondClickTime - firstClickTime < 300) {
                        // Toast.makeText(this, "双击", Toast.LENGTH_SHORT).show();
                        showHideMenu();
                    }
                }
                firstClickTime = event.getDownTime();

                if (isInShowDialogRegion(startY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getRawX();
                endY = event.getRawY();
//                LogUtil.i("dispatchTouchEvent:ACTION_MOVE:" + endY);
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                float distanceYabs = Math.abs(endY - startY);
                if (distanceX > 100 && distanceYabs < 100) {
                    finish();
                    return true;
                }
                if (isInShowDialogRegion(startY) && distanceY < -100) {
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

    /**
     * 在此区域拦截时间以显示对话框
     *
     * @param value
     * @return
     */
    private boolean isInShowDialogRegion(float value) {
        return value > (ScreenUtils.getScreenHeightPX(this) - 200);
    }

    private void showHideMenu() {
        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
        } else {
            bottomSheetDialog.show();
        }
    }

    public void closeDialog() {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.cancel();
            bottomSheetDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_sheet_dialog_close_ib:
                bottomSheetDialog.cancel();
                break;

            case R.id.bottom_sheet_dialog_back_ib:
                closeDialog();
                finish();
                break;
            default:
                break;
        }
    }


    public void shareToWX(int req) {
        getExecutors().networkIO().execute(() -> {
            WeChatShare weChatShare = WeChatShare.getInstance(getApplicationContext());
            boolean regB = weChatShare.regToWX();
            LogUtil.i("regB" + (regB ? "true" : "false"));
            // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.send_img);
            boolean flag = weChatShare.sendImgToWX(BitmapUtil.convertViewToBitmap(scrollView), req);
            LogUtil.i("flag" + (flag ? "true" : "false"));
        });
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
//                Toast.makeText(this, "即将到来~", Toast.LENGTH_SHORT).show();

                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_NORMAL:
                FireShare.shareFileWithSys(this, getString(R.string.app_name), Uri.parse(lrcText));
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_DOWNLOAD:
                //保存到文件
                songEntity.setAlbumCoverUri(StorageUtil.getInstance(getApplicationContext()).saveImageToCacheFile(albumCover));
                getRepository().getDbGecimiRepository().insertToSong(songEntity);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_TEXT_RESIZE:

                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_LIKE:
                songEntity.setLike(1);
                songEntity.setAlbumCoverUri(StorageUtil.getInstance(getApplicationContext()).saveImageToCacheFile(albumCover));
                getRepository().getDbGecimiRepository().insertToSong(songEntity);
                Toast.makeText(LrcActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_BACKGROUND_BLUR:

                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_CHOOSE_LRC_TEXT:

                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_GEN_BITMAP:

                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_GEN_VIDEO:

                break;
            default:
                break;

        }
    }
}
