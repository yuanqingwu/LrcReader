package com.wyq.lrcreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.ui.fragment.LrcListFragment;
import com.wyq.lrcreader.ui.fragment.SearchFragment;
import com.wyq.lrcreader.utils.LogUtil;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private SearchView searchView;

    public static void newInstance(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.search_activity;
    }

    @Override
    public void initView() {
        initToolBar();
        fragmentAdd(R.id.search_activity_content, SearchFragment.newInstance());
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchView = (SearchView) menu.findItem(R.id.menu_action_search).getActionView();
        searchView.setQueryHint("搜索歌名");
        searchView.onActionViewExpanded();

        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        LogUtil.i("query:" + query);
        fragmentReplace(R.id.search_activity_content, LrcListFragment.newInstance(false, query));
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }
}
