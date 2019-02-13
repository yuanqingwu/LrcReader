package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.db.entity.SearchHistoryEntity;
import com.wyq.lrcreader.model.viewmodel.SearchHistoryViewModel;
import com.wyq.lrcreader.model.viewmodel.ViewModelFactory;
import com.wyq.lrcreader.ui.activity.SearchActivity;
import com.wyq.lrcreader.ui.fragment.base.BaseFragment;
import com.wyq.lrcreader.utils.LogUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;

/**
 * @author Uni.W
 * @date 2019/2/13 16:12
 */
public class SearchHistoryFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.search_history_fragment_flow_layout)
    public TagFlowLayout tagFlowLayout;
    @BindView(R.id.search_history_fragment_delete_all_iv)
    public AppCompatImageView deleteIV;

    public static SearchHistoryFragment newInstance() {
        return new SearchHistoryFragment();
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.search_history_fragment;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {

        deleteIV.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        SearchHistoryViewModel searchHistoryViewModel = ViewModelProviders.of(this, factory).get(SearchHistoryViewModel.class);

        subscribeToModel(searchHistoryViewModel);
    }

    private void subscribeToModel(final SearchHistoryViewModel searchHistoryViewModel) {
        searchHistoryViewModel.getSearchHistory().observe(this, new Observer<List<SearchHistoryEntity>>() {
            @Override
            public void onChanged(List<SearchHistoryEntity> searchHistoryEntities) {
                tagFlowLayout.setAdapter(new TagAdapter<SearchHistoryEntity>(searchHistoryEntities) {

                    @Override
                    public View getView(FlowLayout parent, int position, SearchHistoryEntity entity) {
                        TextView textView = new TextView(getContext());
                        textView.setBackgroundResource(R.drawable.tag_bg);
                        textView.setText(entity.getValue());
                        return textView;
                    }
                });
                tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                    @Override
                    public boolean onTagClick(View view, int position, FlowLayout parent) {
                        String searchText = searchHistoryEntities.get(position).getValue();
                        LogUtil.i(searchText);
                        if (getActivity() instanceof SearchActivity) {
                            ((SearchActivity) getActivity()).onQueryTextSubmit(searchText);
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_history_fragment_delete_all_iv:
                getRepository().deleteAllSearchHistory();
                break;
            default:
                break;
        }
    }
}
