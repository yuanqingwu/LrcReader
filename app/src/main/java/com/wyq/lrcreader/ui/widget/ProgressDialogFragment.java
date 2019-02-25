package com.wyq.lrcreader.ui.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * @author Uni.W
 * @date 2019/2/23 18:33
 */
public class ProgressDialogFragment extends DialogFragment {

    private static final String TITLE = "TITLE";

    public static ProgressDialogFragment newInstance(String title) {
        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        progressDialogFragment.setArguments(bundle);
        return progressDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String title = getArguments().getString(TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        SeekBar seekBar = new SeekBar(getActivity());

        ViewGroup.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        seekBar.setLayoutParams(layoutParams);
        builder.setView(seekBar);
        return builder.create();
    }
}
