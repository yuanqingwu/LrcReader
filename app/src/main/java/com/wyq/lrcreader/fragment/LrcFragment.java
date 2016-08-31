package com.wyq.lrcreader.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.model.LrcInfo;
import com.wyq.lrcreader.utils.BitmapUtil;
import com.wyq.lrcreader.utils.LrcParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class LrcFragment extends Fragment{

    private TextView lrcView;
    private RelativeLayout relativeLayout;
    private LinearLayout searchLayout;

    private String lrcText;
    private byte[] albumCover;
    private LrcInfo lrcInfo;
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    lrcView.setText(msg.obj.toString());
//                    break;
//                case 1:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        relativeLayout.setBackground(new BitmapDrawable((Bitmap) msg.obj));
////                        backImage.setAlpha(0.5f);
////                        backImage.setImageBitmap((Bitmap) msg.obj);
//                    }
//                    break;
//            }
//        }
//    };
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_lrc_view, container, false);
//        lrcView = (TextView) view.findViewById(R.id.activity_lrc_view_text);
//        relativeLayout = (RelativeLayout) view.findViewById(R.id.activity_lrc_view_relativelayout);
//
//        searchLayout = (LinearLayout) getActivity().findViewById(R.id.activity_search_layout);
//        searchLayout.setVisibility(View.GONE);
//
//        lrcText = getArguments().getString("lrcText");
//        albumCover = getArguments().getByteArray("albumCover");
//
//
//        new Thread(new Runnable() {
//           @Override
//            public void run() {
//                Display display = getActivity().getWindowManager().getDefaultDisplay();
//                Bitmap backImage = BitmapUtil.getSuitaleBitmap(albumCover, display.getWidth(), display.getHeight());
//                handler.obtainMessage(1, BitmapUtil.blur(BitmapUtil.getTransparentBitmap(backImage, 50), 100)).sendToTarget();
//
//                try {
//                    lrcInfo = new LrcParser().parser(new ByteArrayInputStream(lrcText.getBytes()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (lrcInfo == null || lrcInfo.getInfos().size() == 0) {
//                    lrcText.replaceAll("\n", "\n\n");
//                    handler.obtainMessage(0, lrcText).sendToTarget();
//                    return;
//                }
//
//                String lrcStr = "";
//                if (lrcInfo.getTitle() != null && lrcInfo.getTitle().length() > 0) {
//                    lrcStr = lrcStr + lrcInfo.getTitle();
//                }
//                if (lrcInfo.getArtist() != null && lrcInfo.getArtist().length() > 0) {
//                    lrcStr = lrcStr + "\n\n演唱：" + lrcInfo.getArtist();
//                }
//                if (lrcInfo.getAlbum() != null && lrcInfo.getAlbum().length() > 0) {
//                    lrcStr += ("    专辑：" + lrcInfo.getAlbum());
//                }
//                for (String s : lrcInfo.getInfos().values()) {
//                    lrcStr += ("\n\n" + s);
//                }
//
//                handler.obtainMessage(0, lrcStr).sendToTarget();
//            }
//        }).start();
//
//
//        return view;
//    }
//
//
//    @Override
//    public void onDestroy() {
//        searchLayout.setVisibility(View.VISIBLE);
//        super.onDestroy();
//    }


}
