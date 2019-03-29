package com.wyq.lrcreader.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.LrcPagedAdapter;
import com.wyq.lrcreader.adapter.TvRecyclerViewAdapter;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.model.viewmodel.SearchResultViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.ui.HomeAction;
import com.wyq.lrcreader.ui.fragment.base.BaseFragment;
import com.wyq.lrcreader.ui.widget.CheckDialogFragment;
import com.wyq.lrcreader.ui.widget.SideSelectLayout;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Uni.W
 * @date 2019/1/20 14:40
 */
public class SearchResultFragment extends BaseFragment implements View.OnClickListener, TvRecyclerViewAdapter.OnItemClickListener {

    @BindView(R.id.search_fragment_recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.search_fragment_delete_iv)
    public ImageView deleteIv;
    @BindView(R.id.search_fragment_history_filter_tv)
    public TextView filterTv;
    @BindView(R.id.search_fragment_side_select_layout)
    public SideSelectLayout selectLayout;
    /**
     * null 则显示全部数据
     */
    private static final String ARGUMENT_FILTER = "ARGUMENT_FILTER";

    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView bottomRecyclerView;

    //private SearchResultListAdapter adapter;
    private LrcPagedAdapter pagedAdapter;
    private TvRecyclerViewAdapter bottomRecyclerViewAdapter;
    private SearchResultViewModel searchResultViewModel;
    private List<String> nameList;
    private Disposable disposable;
    private volatile String filter;

    public static SearchResultFragment newInstance(String filter) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_FILTER, filter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.search_result_fragment;
    }

    @Override
    public void initData() {
        filter = getArguments().getString(ARGUMENT_FILTER);
        filterTv.setText(filter);
    }

    @Override
    public void initView(View view) {

        deleteIv.setOnClickListener(this);
        initRecyclerView();
        initBottomSheetDialog();
        initSideSelectLayout();
    }

    public void initSideSelectLayout() {
        ((BasicApp) (getActivity().getApplication())).getExecutors().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                List<String> nameList = getRepository().getDbGecimiRepository().getAllSearchResultName();
                Collections.reverse(nameList);
                selectLayout.setNameList(nameList);
                selectLayout.setOnItemSelectListenser(new SideSelectLayout.OnItemSelectListenser() {
                    @Override
                    public void onSelect(String item) {
                        //todo 待优化
                        LogUtil.i("onSelect:" + item);
                        LogUtil.i("getCurrentList:" + pagedAdapter.getCurrentList().get(2) == null ? "null" : "not null");
                        int pos = findPosition(pagedAdapter.getCurrentList().snapshot(), item);
                        LogUtil.i("pos" + pos);
                        if (pos >= 0) {
                            recyclerView.scrollToPosition(pos);
                            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(pos, 0);
                        }
                    }
                });
            }
        });

    }

    private int findPosition(List<SearchResultEntity> resultEntities, String name) {
        if (resultEntities == null || resultEntities.size() == 0) {
            return -1;
        }
        LogUtil.i("Size:" + resultEntities.size());
        for (int i = 0; i < resultEntities.size(); i++) {
            if (resultEntities.get(i) == null) {
                LogUtil.i(i + " null");
                continue;
            }
            LogUtil.i(i + " " + resultEntities.get(i).getSongName());
            if (resultEntities.get(i).getSongName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void initRecyclerView() {
//        adapter = new SearchResultListAdapter(getContext(), null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);
//        adapter.setOnRecyclerItemClickListener(this);
        pagedAdapter = new LrcPagedAdapter(GlideApp.with(this));
        recyclerView.setAdapter(pagedAdapter);
    }

    private void initBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setContentView(R.layout.ui_dialog_bottom_dialog_list_layout);
        bottomSheetDialog.getDelegate().findViewById(R.id.design_bottom_sheet).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        bottomRecyclerView = bottomSheetDialog.findViewById(R.id.ui_dialog_bottom_dialog_recycler_view);
        bottomRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
//        bottomRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        bottomRecyclerViewAdapter = new TvRecyclerViewAdapter(null);
        bottomRecyclerView.setAdapter(bottomRecyclerViewAdapter);

        bottomRecyclerViewAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        searchResultViewModel = ViewModelProviders.of(this, factory)
                .get(SearchResultViewModel.class);

        searchResultViewModel.getSearchResults(filter).observe(this, new Observer<PagedList<SearchResultEntity>>() {
            @Override
            public void onChanged(PagedList<SearchResultEntity> searchResultEntities) {
                if (searchResultEntities != null) {
                    LogUtil.i("LoadedCount:" + searchResultEntities.getLoadedCount());
                    pagedAdapter.submitList(searchResultEntities);
//                    pagedAdapter.setNetworkState(NetworkState.LOADING);
                } else {
                    LogUtil.i("searchResultEntities is null");
                }
            }
        });


        LiveEventBus.get().with(HomeAction.ACTION_SEARCH, String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                LogUtil.i("ACTION_SEARCH:" + (s == null ? "null" : s));

                if (s.equals(HomeAction.ACTION_SEARCH_EXPAND_LIST)) {
                    searchResultViewModel.setSearchResults(getRepository().getDbGecimiRepository().getAllSearchResult(filter));
                    filterTv.setText(filter);
                } else if (s.equals(HomeAction.ACTION_SEARCH_TITLE_LIST)) {
                    showBottomSheetDialog(s);
                } else if (s.equals(HomeAction.ACTION_SEARCH_SOURCE)) {
                    showBottomSheetDialog(s);
                }

            }
        });
    }

    private void showBottomSheetDialog(String filterType) {
        disposable = Flowable.create(new FlowableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(FlowableEmitter<List<String>> emitter) {
                if (filterType.equals(HomeAction.ACTION_SEARCH_TITLE_LIST)) {
                    nameList = getRepository().getDbGecimiRepository().getAllSearchResultName();
                } else if (filterType.equals(HomeAction.ACTION_SEARCH_SOURCE)) {
                    nameList = getRepository().getDbGecimiRepository().getAllSearchResultSource();
                }
                if (nameList != null) {
                    emitter.onNext(nameList);
                    LogUtil.i("nameList" + nameList.get(0));
                } else {
                    LogUtil.i("nameList is null");
                }
            }
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {

                    @Override
                    public void accept(List<String> nameList) {
//                                FirePopupWindow.list(new TvRecyclerViewAdapter(nameList)).showAsDropDown(getView());
                        bottomRecyclerViewAdapter.refreshData(nameList);
                        bottomSheetDialog.show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtil.e(throwable.getMessage());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_fragment_delete_iv:
                CheckDialogFragment.newInstance("确认要删除搜索历史嘛？").setOnCheckedListenser(new CheckDialogFragment.OnCheckedListenser() {
                    @Override
                    public void onPositiveClick(DialogInterface dialog, int which) {
                        if (searchResultViewModel != null) {
                            searchResultViewModel.clearAllSearchResults();
                        }
                    }

                    @Override
                    public void onNegativeClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show(getChildFragmentManager(), "checkDialog");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (nameList != null) {
            String filterName = nameList.get(position);
            filterTv.setText(filterName);
            searchResultViewModel.setSearchResults(getRepository().getDbGecimiRepository().getAllSearchResult(filterName));
            bottomSheetDialog.cancel();
        }
    }
}
