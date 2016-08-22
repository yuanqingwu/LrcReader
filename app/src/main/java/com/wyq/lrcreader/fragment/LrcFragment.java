package com.wyq.lrcreader.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyq.lrcreader.R;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcFragment extends Fragment {


    private TextView lrcView;
    private String  lrcText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_lrc_view,container,false);
        lrcView=(TextView)view.findViewById(R.id.fragment_lrc_view_text);

        lrcText=getArguments().getString("lrcText");
        lrcView.setText(lrcText);
        lrcView.setMovementMethod(ScrollingMovementMethod.getInstance());
        return view;
    }
}
