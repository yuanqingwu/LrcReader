package com.wyq.lrcreader.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.adapter.ListSelectAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Uni.W on 2016/12/19.
 */

public class LocalLrcFragment extends Fragment implements View.OnClickListener {

    private Button confirmBt;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView progressText;
    private Button expandBt;

    private ListSelectAdapter listAdapter = null;
    private ArrayList<String> lrcList = null;

    private static final int MESSAGE_PROGRESS = 0;
    private static final int MESSAGE_RESULT = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PROGRESS:
                    if (msg.arg1 == 0) {//show
                        progressBar.setVisibility(View.VISIBLE);
                        progressText.setText((String) msg.obj);
                    } else {          //hide
                        progressBar.setVisibility(View.GONE);
                        progressText.setText((String) msg.obj);
                        expandBt.setVisibility(View.VISIBLE);
                    }
                    break;
                case MESSAGE_RESULT:
                    lrcList.add((String) msg.obj);
                    listAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_lrc_search, container, false);
        confirmBt = (Button) view.findViewById(R.id.fragment_local_lrc_search_ok_bt);
        listView = (ListView) view.findViewById(R.id.fragment_local_lrc_search_list);
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_local_lrc_search_progress);
        progressText = (TextView) view.findViewById(R.id.fragment_local_lrc_search_progress_text);
        expandBt = (Button) view.findViewById(R.id.fragment_local_lrc_search_expand_bt);
        progressBar.setVisibility(View.GONE);

        confirmBt.setText("搜寻本地歌词文件夹");
        confirmBt.setOnClickListener(this);
        expandBt.setOnClickListener(this);

        lrcList = new ArrayList<>();
        listAdapter = new ListSelectAdapter(getActivity(), lrcList);
        listView.setAdapter(listAdapter);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_local_lrc_search_ok_bt:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        lrcList.clear();//清空之前的搜索结果
                        handler.obtainMessage(MESSAGE_PROGRESS, 0, -1, getActivity().getText(R.string.local_search_progress_text_ing)).sendToTarget();
                        ArrayList<File> list = sortFilesByType(Environment.getExternalStorageDirectory().getAbsolutePath(), "Lyric");
                        handler.obtainMessage(MESSAGE_PROGRESS, 1, -1, getActivity().getText(R.string.local_search_progress_text_ed)).sendToTarget();
                    }
                }).start();

                break;
            case R.id.fragment_local_lrc_search_expand_bt://将选中文件夹的歌词都显示在列表中
                LrcListFragment lrcListFragment = new LrcListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLocal", true);
                bundle.putStringArrayList("localLyricDir", (ArrayList<String>) listAdapter.getSelectList());
                lrcListFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_parent_view, lrcListFragment, "LrcList").addToBackStack("LrcList").commit();
                break;
            default:
                break;
        }
    }


    public ArrayList<File> sortFilesByType(String rootPath, String typeName) {
        ArrayList<File> filelist = new ArrayList<File>();
        File dir = new File(rootPath);
        File[] files = dir.listFiles();

        if (files == null) {
            return null;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                if (files[i].getName().equalsIgnoreCase(typeName)) {
                    filelist.add(files[i]);
                    handler.obtainMessage(MESSAGE_RESULT, 0, 0, files[i].getAbsolutePath()).sendToTarget();
                    Log.i("Test", files[i].getAbsolutePath());
                }
                sortFilesByType(files[i].getAbsolutePath(), typeName);
            } else {
            }
        }
        return filelist;
    }

}
