package com.wyq.lrcreader.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.cache.DiskLruCacheUtil;
import com.wyq.lrcreader.fragment.LrcLikeFragment;
import com.wyq.lrcreader.fragment.LrcListFragment;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private Button searchBt;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBt = (Button) findViewById(R.id.activity_search_button);
        searchBt.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.activity_search_edittext);
        //显示喜欢的歌词列表
       // fragmentReplace(new LrcLikeFragment());
    }

    @Override
    public void onClick(View v) {
        final String text = editText.getText().toString();
        if (text == null || text.length() == 0) {
            Toast.makeText(MainActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
        } else {
            //    searchForLrc(text);
            LrcListFragment lrcListFragment = new LrcListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchText", text);
            lrcListFragment.setArguments(bundle);
            fragmentReplace(lrcListFragment);
        }
    }

    private void fragmentReplace(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        manager.popBackStackImmediate();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_parent_view, fragment);
        transaction.addToBackStack("LrcList");
        transaction.commit();
    }
}
