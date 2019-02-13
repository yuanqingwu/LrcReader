package com.wyq.lrcreader.ui.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wyq.lrcreader.base.BasicApp;
import com.wyq.lrcreader.datasource.DataRepository;

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
    public abstract int attachLayoutRes();

    public abstract void initData();

    public abstract void initView(View view);

    public DataRepository getRepository() {
        return ((BasicApp) getActivity().getApplication()).getDataRepository();
    }
}
