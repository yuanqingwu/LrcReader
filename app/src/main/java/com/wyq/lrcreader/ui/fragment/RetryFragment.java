package com.wyq.lrcreader.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.ui.IRetryLoadCallback;
import com.wyq.lrcreader.ui.fragment.base.BaseFragment;

import androidx.annotation.NonNull;
import butterknife.BindView;

/**
 * @author Uni.W
 * @date 2019/1/24 19:59
 */
public class RetryFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.retry_fragment_click_to_retry_layout)
    public LinearLayout linearLayout;

    private IRetryLoadCallback retryLoadCallback;

    public static RetryFragment newInstance() {
        Bundle bundle = new Bundle();

        return new RetryFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IRetryLoadCallback) {
            retryLoadCallback = (IRetryLoadCallback) context;
        }
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.retry_fragment;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {
        linearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        retryLoadCallback.retry();
    }
}
