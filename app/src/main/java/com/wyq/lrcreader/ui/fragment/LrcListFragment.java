package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.RecyclerAdapter;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.ui.activity.LrcActivity;
import com.wyq.lrcreader.ui.activity.SearchActivity;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcListFragment extends BaseLazyLoadFragment implements RecyclerAdapter.OnRecyclerItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.fragment_lrclist_recyclerview)
    public RecyclerView recyclerView;
    @BindView(R.id.fragment_lrclist_swiprefresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerAdapter adapter;
    private static final String ARGUMENTS_IS_LOCAL = "IS_LOCAL";

    private boolean isLocal = false;
    private static final String ARGUMENTS_SEARCH_TEXT = "SEARCH_TEXT";

    private String searchText;
    private List<SearchResultEntity> searchResultEntities;

    public static LrcListFragment newInstance(boolean isLocal, String searchText) {
        LrcListFragment lrcListFragment = new LrcListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARGUMENTS_IS_LOCAL, isLocal);
        bundle.putString(ARGUMENTS_SEARCH_TEXT, searchText);
        lrcListFragment.setArguments(bundle);
        return lrcListFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i("onStart:" + getUserVisibleHint());
        if (getUserVisibleHint()) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i("onCreate");

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.i("setUserVisibleHint" + isVisibleToUser);
    }

    @Override
    void loadData() {
        showRefresh();
        loadData(searchText);
    }

    @Override
    int attachLayoutRes() {
        return R.layout.fragment_lrclist;
    }

    @Override
    void initData() {
        searchResultEntities = new ArrayList<>();

        assert getArguments() != null;
        isLocal = getArguments().getBoolean(ARGUMENTS_IS_LOCAL);

        if (isLocal) {
            //displaySongList(getArguments().<ISong>getParcelableArrayList("songList"));
//            final List<String> songDirList = getArguments().getStringArrayList("localLyricDir");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    expandLocalLyricDir(songDirList);
//                }
//            }).start();
        } else {
            searchText = getArguments().getString(ARGUMENTS_SEARCH_TEXT);
        }
    }

    @Override
    void initView(View view) {

        initRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(this);
    }


    public void initRecyclerView() {
        adapter = new RecyclerAdapter(getActivity(), searchResultEntities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //   outState.putParcelableArrayList("songsList", (ArrayList<? extends Parcelable>) songsList);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void loadData(String searchText) {

        if (searchText == null || searchText.length() == 0) {
            return;
        }
        LogUtil.i("start to load data");
        ((BasicApp) getActivity().getApplication()).getDataRepository().getSearchResult(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchResultEntity>>() {
                    @Override
                    public void accept(List<SearchResultEntity> songs) {
                        searchResultEntities = songs;
                        adapter.refreshData(songs);
                        LogUtil.i("成功" + songs.size());
                        hideRefresh();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        LogUtil.i("出现异常" + throwable.getMessage());
                        hideRefresh();

                        if (getActivity() instanceof SearchActivity) {
                            ((SearchActivity) getActivity()).fragmentReplace(RetryFragment.newInstance());
                        }
                    }
                });
    }


    private void hideRefresh() {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showRefresh() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }
//
//    public void expandLocalLyricDir(List<String> localLyricDir) {
//        for (String dirStr : localLyricDir) {
//            File file = new File(dirStr);
//            if (file.isDirectory()) {
//                String[] files = file.list();
//                for (String fileNme : files) {
//                    Song song = LrcParser.getInstance().parserAll(fileNme, readLocalFile(LocalConstans.NETEASE_CLOUDMUSIC_DOWNLOAD_LYRIC + fileNme));
//                    if (song != null) {
//                        song.setAlbumCover(BitmapFactory.decodeResource(getResources(), R.drawable.album));
////                        songsList.add(song);
////                        Message.obtain(handler, MESSAGE_LRC, -1, MESSAGE_LRC_SONGLIST, songsList).sendToTarget();
//                    }
//                }
//            }
//        }
//    }
//
//    public String readLocalFile(String path) {
//        String lrcText = "";
//        try {
//            FileInputStream fis = new FileInputStream(path);
//            byte[] buffer = new byte[fis.available()];
//            fis.read(buffer);
//            lrcText = new String(buffer);
//            LogUtil.i(lrcText);
//            fis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return lrcText;
//    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (searchResultEntities == null || searchResultEntities.size() <= position) {
            return;
        }
        LrcActivity.newInstance(getActivity(), searchResultEntities.get(position).getLrcUri(), searchResultEntities.get(position).getAlbumCoverUri());
    }

    @Override
    public void retry() {
        loadData();
    }
}
