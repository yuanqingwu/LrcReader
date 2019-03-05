package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.LrcPagedAdapter;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.base.NetworkState;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.model.viewmodel.SearchResultViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.ui.activity.SearchActivity;
import com.wyq.lrcreader.ui.fragment.base.BaseLazyLoadFragment;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcListFragment extends BaseLazyLoadFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.fragment_lrclist_recyclerview)
    public RecyclerView recyclerView;
    @BindView(R.id.fragment_lrclist_swiprefresh_layout)
    public SwipeRefreshLayout swipeRefreshLayout;

    private static final String ARGUMENTS_IS_LOCAL = "IS_LOCAL";

    private boolean isLocal = false;
    private static final String ARGUMENTS_SEARCH_TEXT = "SEARCH_TEXT";

    private String searchText;
    private Disposable loadDisposable;

    private LrcPagedAdapter pagedAdapter;

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
    public void loadData() {
        showRefresh();
        loadData(searchText);
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_lrclist;
    }

    @Override
    public void initData() {

        assert getArguments() != null;
        isLocal = getArguments().getBoolean(ARGUMENTS_IS_LOCAL);

        if (isLocal) {
            //显示本地数据
        } else {
            searchText = getArguments().getString(ARGUMENTS_SEARCH_TEXT);
        }
    }

    @Override
    public void initView(View view) {

        initRecyclerView();

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.forestgreen));
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    public void initRecyclerView() {
//        adapter = new SearchResultListAdapter(getActivity(), searchResultEntities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);
//        adapter.setOnRecyclerItemClickListener(this);

        pagedAdapter = new LrcPagedAdapter(GlideApp.with(this));
        recyclerView.setAdapter(pagedAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        SearchResultViewModel searchResultViewModel = ViewModelProviders.of(this, factory)
                .get(SearchResultViewModel.class);
        LogUtil.i("searchText:" + searchText);
        searchResultViewModel.getSearchResults(searchText).observe(this, new Observer<PagedList<SearchResultEntity>>() {
            @Override
            public void onChanged(PagedList<SearchResultEntity> searchResultEntities) {
                if (searchResultEntities != null && searchResultEntities.size() > 0) {
                    LogUtil.i("LoadedCount:" + searchResultEntities.getLoadedCount() + searchResultEntities.get(0).getAlbumCoverUri());
                    pagedAdapter.submitList(searchResultEntities);
//                    recyclerView.scrollToPosition(0);
//                    pagedAdapter.setNetworkState(NetworkState.LOADING);
                } else {
                    LogUtil.i("searchResultEntities is null");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadDisposable != null) {
            loadDisposable.dispose();
        }
    }


    private void loadData(String searchText) {

        if (searchText == null || searchText.length() == 0) {
            return;
        }
        LogUtil.i("start to load data");
        pagedAdapter.setNetworkState(NetworkState.LOADING);

        loadDisposable = getRepository().getSearchResult(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchResultEntity>>() {
                    @Override
                    public void accept(List<SearchResultEntity> songs) {
//                        searchResultEntities = songs;
//                        adapter.refreshData(songs);
                        LogUtil.i("成功" + songs.size() + songs.get(0).getAlbumCoverUri());
                        hideRefresh();
                        pagedAdapter.setNetworkState(NetworkState.LOADED);
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
            swipeRefreshLayout.setEnabled(false);
        }
    }

    private void showRefresh() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void retry() {
        loadData();
    }
}
