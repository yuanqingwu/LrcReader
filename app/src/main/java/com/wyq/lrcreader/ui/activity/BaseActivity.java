package com.wyq.lrcreader.ui.activity;

import android.os.Bundle;

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

    protected void fragmentAdd(int id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(id, fragment, null)
                .commit();
    }

    protected void fragmentReplace(int id, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(id, fragment, null)
                .commit();
    }
}
