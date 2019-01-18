package com.wyq.lrcreader.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by Uni.W on 2016/12/19.
 */

public class LocalLrcFragment extends Fragment implements View.OnClickListener {

    private Button confirmBt;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView progressText;
    private Button expandBt;
    private SearchLocalLyricThread searchLocalLyricThread;

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
                        expandBt.setVisibility(View.GONE);
                    } else {          //hide
                        progressBar.setVisibility(View.GONE);
                        progressText.setText((String) msg.obj);
                        expandBt.setVisibility(View.VISIBLE);
                        if (searchLocalLyricThread != null) {
                            searchLocalLyricThread.interrupt();
                            searchLocalLyricThread = null;
                        }
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
        confirmBt = view.findViewById(R.id.fragment_local_lrc_search_ok_bt);
        listView = view.findViewById(R.id.fragment_local_lrc_search_list);
        progressBar = view.findViewById(R.id.fragment_local_lrc_search_progress);
        progressText = view.findViewById(R.id.fragment_local_lrc_search_progress_text);
        expandBt = view.findViewById(R.id.fragment_local_lrc_search_expand_bt);
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
                if (searchLocalLyricThread == null) {
                    searchLocalLyricThread = new SearchLocalLyricThread();
                    searchLocalLyricThread.start();
                }
                break;
            case R.id.fragment_local_lrc_search_expand_bt://将选中文件夹的歌词都显示在列表中
                LrcListFragment lrcListFragment = new LrcListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLocal", true);
                bundle.putStringArrayList("localLyricDir", (ArrayList<String>) listAdapter.getSelectList());
                lrcListFragment.setArguments(bundle);
//                getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_parent_view, lrcListFragment, "LrcList").addToBackStack("LrcList").commit();
                break;
            default:
                break;
        }
    }

    public class SearchLocalLyricThread extends Thread {
        @Override
        public void run() {
            super.run();
            lrcList.clear();//清空之前的搜索结果
            handler.obtainMessage(MESSAGE_PROGRESS, 0, -1, getActivity().getText(R.string.local_search_progress_text_ing)).sendToTarget();
            ArrayList<File> list = sortFilesByType(Environment.getExternalStorageDirectory().getAbsolutePath(), "Lyric");
            handler.obtainMessage(MESSAGE_PROGRESS, 1, -1, getActivity().getText(R.string.local_search_progress_text_ed)).sendToTarget();
        }
    }

    public ArrayList<File> sortFilesByType(String rootPath, String typeName) {
        ArrayList<File> filelist = new ArrayList<>();
        File dir = new File(rootPath);
        File[] files = dir.listFiles();

        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equalsIgnoreCase(typeName)) {
                    filelist.add(file);
                    handler.obtainMessage(MESSAGE_RESULT, 0, 0, file.getAbsolutePath()).sendToTarget();
                    Log.i("Test", file.getAbsolutePath());
                }
                sortFilesByType(file.getAbsolutePath(), typeName);
            } else {
            }
        }
        return filelist;
    }

}
