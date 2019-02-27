package com.wyq.lrcreader.ui.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.wyq.lrcreader.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * @author Uni.W
 * @date 2019/2/27 20:09
 */
public class ParamsSetDialogFragment extends DialogFragment {


    private static final String TITLE = "TITLE";
    private static final String HINT = "HINT";
    private OnClickListener onClickListener;

    public static ParamsSetDialogFragment newInstance(String title, String hint) {
        ParamsSetDialogFragment paramsSetDialogFragment = new ParamsSetDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(HINT, hint);
        paramsSetDialogFragment.setArguments(bundle);
        return paramsSetDialogFragment;
    }

    public ParamsSetDialogFragment setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        assert getArguments() != null;
        String title = getArguments().getString(TITLE);
        String hint = getArguments().getString(HINT);
//        builder.setTitle(title);
        builder.setMessage(title);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_params_set_dailog_fragment, null, false);
        EditText editText = view.findViewById(R.id.widget_params_set_dialog_et);
        editText.setHint(hint);
        builder.setView(view);

        builder.setPositiveButton(R.string.ok, (dialog, which) ->
                onClickListener.onPositiveClick(dialog, which, editText.getText().toString()))
                .setNegativeButton(R.string.cancel, (dialog, which) ->
                        onClickListener.onNegativeClick(dialog, which));

        return builder.create();
    }


    public interface OnClickListener {
        void onPositiveClick(DialogInterface dialog, int which, String value);

        void onNegativeClick(DialogInterface dialog, int which);
    }
}
