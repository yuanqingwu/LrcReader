package com.wyq.lrcreader.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.utils.ScreenUtils;

/**
 * @author Uni.W
 * @date 2019/2/23 20:04
 */
public class ProgressPopupWindow {

    private static ProgressChangeListenser progressChangeListenser;
    private int progress = 50;

    public static ProgressPopupWindow getInstance() {
        return new ProgressPopupWindow();
    }

    public ProgressPopupWindow setOnSeekBarChangeListener(ProgressChangeListenser listener) {
        progressChangeListenser = listener;
        return this;
    }

    public ProgressPopupWindow progress(int startProgress) {
        progress = startProgress;
        return this;
    }

    public void show(View anchor, String title) {
        Context context = anchor.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.progress_popup_window_layout, null);

        TextView textView = view.findViewById(R.id.progress_popup_window_title_tv);
        textView.setText(title);
        ImageButton imageButton = view.findViewById(R.id.progress_popup_window_close_ibt);

        SeekBar seekBar = view.findViewById(R.id.progress_popup_window_seek_bar);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangeListenser.onProgressChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PopupWindow popupWindow = new PopupWindow(view, ScreenUtils.getWidthPX(context) / 4 * 3, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public interface ProgressChangeListenser {
        void onProgressChange(int progress);
    }
}
