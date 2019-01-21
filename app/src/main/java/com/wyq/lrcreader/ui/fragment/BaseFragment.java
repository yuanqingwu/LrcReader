package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

/**
 * @author Uni.W
 * @date 2019/1/20 11:59
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(attachLayoutRes(), container, false);
        ButterKnife.bind(this, view);

        initData();
        initView(view);

        return view;
    }

    @LayoutRes
    abstract int attachLayoutRes();

    abstract void initData();

    abstract void initView(View view);
}
