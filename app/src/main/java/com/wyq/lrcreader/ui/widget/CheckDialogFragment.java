package com.wyq.lrcreader.ui.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * @author Uni.W
 * @date 2019/2/25 9:55
 */
public class CheckDialogFragment extends DialogFragment {

    private static final String TITLE = "TITLE";
    private OnCheckedListenser onCheckedListenser;

    public static CheckDialogFragment newInstance(String title) {
        CheckDialogFragment checkDialogFragment = new CheckDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        checkDialogFragment.setArguments(bundle);
        return checkDialogFragment;
    }

    public CheckDialogFragment setOnCheckedListenser(OnCheckedListenser onCheckedListenser) {
        this.onCheckedListenser = onCheckedListenser;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString(TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).
                setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCheckedListenser.onPositiveClick(dialog, which);
                    }
                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCheckedListenser.onNegativeClick(dialog, which);
                    }
                });

        return builder.create();
    }

    public interface OnCheckedListenser {
        void onPositiveClick(DialogInterface dialog, int which);

        void onNegativeClick(DialogInterface dialog, int which);
    }
}
