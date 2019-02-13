package com.wyq.lrcreader.ui.activity;

import android.os.Bundle;

import com.wyq.lrcreader.base.AppExecutors;
import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.datasource.DataRepository;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

/**
 * @author Uni.W
 * @date 2019/1/20 14:17
 */
public abstract class BaseActivity extends AppCompatActivity {

    @LayoutRes
    protected abstract int attachLayoutRes();

    public abstract void initView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(attachLayoutRes());
        ButterKnife.bind(this);

        initView();
    }

    public AppExecutors getExecutors() {
        return ((BasicApp) getApplication()).getExecutors();
    }

    public DataRepository getRepository() {
        return ((BasicApp) getApplication()).getDataRepository();
    }

    public void fragmentAdd(int id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(id, fragment, null)
                .commit();
    }

    public void fragmentReplace(int id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(id, fragment, null)
                .commit();
    }
}
