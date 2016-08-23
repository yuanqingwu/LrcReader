package com.wyq.lrcreader.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.model.LrcInfo;
import com.wyq.lrcreader.utils.LrcParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcFragment extends Fragment {


    private TextView lrcView;
    private String lrcText;
    private LrcInfo lrcInfo;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    lrcView.setText(msg.obj.toString());
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lrc_view, container, false);
        lrcView = (TextView) view.findViewById(R.id.fragment_lrc_view_text);
        lrcView.setMovementMethod(ScrollingMovementMethod.getInstance());

        lrcText = getArguments().getString("lrcText");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lrcInfo = new LrcParser().parser(new ByteArrayInputStream(lrcText.getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(lrcInfo==null || lrcInfo.getInfos().size()==0){
                    lrcText.replaceAll("\n","\n\n");
                    handler.obtainMessage(0, lrcText).sendToTarget();
                    return;
                }

                String lrcStr = "";
                for (String s : lrcInfo.getInfos().values()) {
                    lrcStr += (s + "\n\n");
                }
                handler.obtainMessage(0, lrcStr).sendToTarget();
            }
        }).start();


        return view;
    }
}
