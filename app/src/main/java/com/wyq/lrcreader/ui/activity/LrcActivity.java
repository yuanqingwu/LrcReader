package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.BaseRecyclerViewAdapter;
import com.wyq.lrcreader.adapter.RecyclerGridAdapter;
import com.wyq.lrcreader.adapter.item.ImageTextItemModel;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.db.entity.SongEntity;
import com.wyq.lrcreader.share.FireShare;
import com.wyq.lrcreader.share.WeChatShare;
import com.wyq.lrcreader.ui.widget.FireToast;
import com.wyq.lrcreader.ui.widget.ProgressPopupWindow;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.DiskLruCacheUtil;
import com.wyq.lrcreader.utils.LogUtil;
import com.wyq.lrcreader.utils.LrcOperationGenerator;
import com.wyq.lrcreader.utils.ScreenUtils;
import com.wyq.lrcreader.utils.StorageUtil;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.wyq.lrcreader.utils.LrcOperationGenerator.ACTION_LRC_MENU_LIKE;

/**
 * Created by Uni.W on 2016/8/30.
 */
public class LrcActivity extends BaseActivity implements View.OnClickListener,
        BaseRecyclerViewAdapter.OnRecyclerItemClickListener,
        WbShareCallback {

    @BindView(R.id.activity_lrc_view_text)
    public TextView lrcView;
    @BindView(R.id.activity_lrc_view_scrollview)
    public ScrollView scrollView;

    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView recyclerView;

    private static final String ARGUMENTS_LRC_LIST_SONG_ENTITY = "LRC_LIST_SONG_ENTITY";
    private static final String ARGUMENTS_LRC_SONG_ENTITY = "LRC_SONG_ENTITY";

    private Disposable lrcDisposable;
    private Disposable backgroundDisposable;

    private List<ImageTextItemModel> menuItemList;
    private SongEntity songEntity;
    private Bitmap albumCover;
    private String lrcText;
    private int likeGrade;
    private String albumCoverUri;
    private SearchResultEntity songListEntity;

    private RecyclerGridAdapter adapter;

    private WbShareHandler wbShareHandler;

    private long firstClickTime = 0;
    private float startX = 0, endX = 0, startY = 0, endY = 0;
    private boolean isMenuVisiblity = false;
    private boolean isLike = false;
    private boolean isPlain = false;
    private Animation showAnimation, hideAnimation;

    private DiskLruCacheUtil diskLruCacheUtil;
    private float startTextSize = 0;
    private int textSizeProgress = 50;
    private int backgroundBlurRadius = 20;

    private WeakReference<Bitmap> shareBitmap;

    public static void newInstance(Context context, SearchResultEntity entity) {
        Intent intent = new Intent();
        intent.putExtra(ARGUMENTS_LRC_LIST_SONG_ENTITY, entity);
        intent.setClass(context, LrcActivity.class);
        context.startActivity(intent);
    }

    public static void newInstance(Context context, SongEntity songEntity) {
        Intent intent = new Intent();
        intent.putExtra(ARGUMENTS_LRC_SONG_ENTITY, songEntity);
        intent.setClass(context, LrcActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.lrc_activity;
    }

    @Override
    public void initView() {

        songEntity = getIntent().getParcelableExtra(ARGUMENTS_LRC_SONG_ENTITY);
        if (songEntity == null) {
            songListEntity = getIntent().getParcelableExtra(ARGUMENTS_LRC_LIST_SONG_ENTITY);
            if (songListEntity != null) {
                initEntity(songListEntity);
                albumCoverUri = songListEntity.getAlbumCoverUri();
                loadBackground(albumCoverUri);
                loadLrc(songListEntity.getLrcUri());
            }
        } else {
            lrcText = songEntity.getLrc();
            albumCoverUri = songEntity.getAlbumCover();
            lrcView.setText(lrcText);
            likeGrade = songEntity.getLike();
            loadBackground(albumCoverUri);
        }
        wbShareHandler = new WbShareHandler(this);
        wbShareHandler.registerApp();
        wbShareHandler.setProgressColor(0xff33b5e5);

        startTextSize = lrcView.getTextSize();//the size (in pixels) of the default text size in this TextView

        initMenu();
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

        menuItemList = LrcOperationGenerator.getInstance(this).genMenuList(likeGrade);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);
        bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_close_ib).setOnClickListener(this);
        bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_back_ib).setOnClickListener(this);
        recyclerView = bottomSheetDialog.findViewById(R.id.bottom_sheet_dialog_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new RecyclerGridAdapter(this, menuItemList);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerItemClickListener(this);

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
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(backgroundBlurRadius)))
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
                        songEntity.setLrc(lrcText);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        wbShareHandler.doResultIntent(data, this);
    }

    public void shareToWX(int req) {
        getExecutors().networkIO().execute(() -> {
            WeChatShare weChatShare = WeChatShare.getInstance(getApplicationContext());
            boolean regB = weChatShare.regToWX();
            LogUtil.i("regB" + (regB ? "true" : "false"));
            // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.send_img);
            boolean flag = weChatShare.sendImgToWX(BitmapUtil.convertViewToBitmap(scrollView), req);
            LogUtil.i("flag" + (flag ? "true" : "false"));
            if (!flag) {
                FireToast.instance(LrcActivity.this).setText("分享失败！").show();
            }
        });
    }

    public Bitmap getShareBitmap() {
        if (shareBitmap == null || shareBitmap.get() == null) {
            shareBitmap = new WeakReference<Bitmap>(BitmapUtil.convertViewToBitmap(scrollView));
        }
        return shareBitmap.get();
    }

    public void shareToWeibo() {
        WeiboMultiMessage multiMessage = new WeiboMultiMessage();
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(getShareBitmap());
        multiMessage.imageObject = imageObject;

        wbShareHandler.shareMessage(multiMessage, false);
    }

    @Override
    public void onItemClick(View view, int position) {
        LogUtil.i(menuItemList.get(position).getName());
        String action = menuItemList.get(position).getAction();
        if (!action.equals(ACTION_LRC_MENU_LIKE)) {
            bottomSheetDialog.cancel();
        }

        switch (action) {
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_WECHAT:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_MOMENTS:
                shareToWX(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_WEIBO:
                shareToWeibo();
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_SHARE_NORMAL:
                FireShare.shareFileWithSys(this, getString(R.string.app_name), Uri.parse(lrcText));
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_DOWNLOAD:
                //保存到文件
                songEntity.setAlbumCover(StorageUtil.getInstance(getApplicationContext()).saveImageToCacheFile(albumCover));
                getRepository().getDbGecimiRepository().insertToSong(songEntity);
                Toast.makeText(LrcActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_TEXT_RESIZE:
//                ProgressDialogFragment.newInstance("调整字体大小").show(getSupportFragmentManager(),"progressDialog");
                ProgressPopupWindow.getInstance().progress(textSizeProgress).setOnSeekBarChangeListener(new ProgressPopupWindow.ProgressChangeListenser() {
                    @Override
                    public void onProgressChange(int progress) {
                        LogUtil.i("progress:" + progress);
                        textSizeProgress = progress;
                        lrcView.setTextSize(TypedValue.COMPLEX_UNIT_PX, startTextSize + (progress / 100f - 0.5f) * 20f);
                    }
                }).show(lrcView, "调整字体大小");
                break;
            case ACTION_LRC_MENU_LIKE:
                ImageTextItemModel model = LrcOperationGenerator.getInstance(this).addLikeGrade();
                menuItemList.set(6, model);
                adapter.notifyItemChanged(6);

                LogUtil.i("喜欢程度：" + model.getName());
                songEntity.setLike(LrcOperationGenerator.getInstance(this).getLikeGradeWithName(model.getName()).getValue());
                songEntity.setAlbumCover(StorageUtil.getInstance(getApplicationContext()).saveImageToCacheFile(albumCover));
                getRepository().getDbGecimiRepository().insertToSong(songEntity);
                Toast.makeText(LrcActivity.this, model.getName(), Toast.LENGTH_SHORT).show();
                break;
            case LrcOperationGenerator.ACTION_LRC_MENU_BACKGROUND_BLUR:
                ProgressPopupWindow.getInstance().progress(backgroundBlurRadius).setOnSeekBarChangeListener(new ProgressPopupWindow.ProgressChangeListenser() {
                    @Override
                    public void onProgressChange(int progress) {
                        backgroundBlurRadius = progress;
                        loadBackground(albumCoverUri);
                    }
                }).show(lrcView, "调整背景模糊程度");
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

    @Override
    public void onWbShareSuccess() {
//        FireToast.instance(LrcActivity.this).setText("分享成功！").show();
    }

    @Override
    public void onWbShareCancel() {
        FireToast.instance(LrcActivity.this).setText("分享取消！").show();
    }

    @Override
    public void onWbShareFail() {
        FireToast.instance(LrcActivity.this).setText("分享失败！").show();
    }
}
