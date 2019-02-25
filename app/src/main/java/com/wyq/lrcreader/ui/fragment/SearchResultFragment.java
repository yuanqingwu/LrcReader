package com.wyq.lrcreader.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.LrcPagedAdapter;
import com.wyq.lrcreader.adapter.TvRecyclerViewAdapter;
import com.wyq.lrcreader.base.GlideApp;
import com.wyq.lrcreader.db.entity.SearchResultEntity;
import com.wyq.lrcreader.model.viewmodel.SearchResultViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.ui.HomeAction;
import com.wyq.lrcreader.ui.fragment.base.BaseFragment;
import com.wyq.lrcreader.ui.widget.CheckDialogFragment;
import com.wyq.lrcreader.utils.LogUtil;

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
    /**
     * null 则显示全部数据
     */
    private static final String FILTER = "FILTER";
    @BindView(R.id.search_fragment_delete_iv)
    public ImageView deleteIv;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView bottomRecyclerView;

    //private SearchResultListAdapter adapter;
    private LrcPagedAdapter pagedAdapter;
    private TvRecyclerViewAdapter bottomRecyclerViewAdapter;
    private SearchResultViewModel searchResultViewModel;
    private List<String> nameList;
    private Disposable disposable;
    private volatile String filter = null;

    public static SearchResultFragment newInstance(String filter) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILTER, filter);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.search_result_fragment;
    }

    @Override
    public void initData() {
        filter = getArguments().getString(FILTER);
    }

    @Override
    public void initView(View view) {

        deleteIv.setOnClickListener(this);
        initRecyclerView();
        initBottomSheetDialog();
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
                    searchResultViewModel.setSearchResults(getRepository().getDbGecimiRepository().getAllSearchResult(null));
                } else if (s.equals(HomeAction.ACTION_SEARCH_TITLE_LIST)) {
                    showBottomSheetDialog();
                }

            }
        });
    }

    private void showBottomSheetDialog() {
        disposable = Flowable.create(new FlowableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(FlowableEmitter<List<String>> emitter) {
                nameList = getRepository().getDbGecimiRepository().getAllSearchResultName();
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
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
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
            String songName = nameList.get(position);
            searchResultViewModel.setSearchResults(getRepository().getDbGecimiRepository().getAllSearchResult(songName));
            bottomSheetDialog.cancel();
        }
    }
}
