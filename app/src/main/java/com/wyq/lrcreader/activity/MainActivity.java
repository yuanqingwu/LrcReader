package com.wyq.lrcreader.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
    private String searchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBt = (Button) findViewById(R.id.activity_search_button);
        searchBt.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.activity_search_edittext);
        //显示喜欢的歌词列表

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_parent_view, new LrcLikeFragment(), "LrcLikeList").commit();
        } else {
            Log.i("Test", "saved not null");
        }
        // fragmentReplace(new LrcLikeFragment(),"LrcLikeList");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // getFragmentManager().popBackStackImmediate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (searchText != null && editText.getText().toString() != null && searchText.equals(editText.getText().toString())) {
            getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentByTag("LrcList")).hide(getFragmentManager().findFragmentByTag("LrcLikeList"));
        }
        searchText = editText.getText().toString();
        if (searchText == null || searchText.length() == 0) {
            Toast.makeText(MainActivity.this, "输入为空", Toast.LENGTH_SHORT).show();
        } else {
            //    searchForLrc(searchText);
            LrcListFragment lrcListFragment = new LrcListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchText", searchText);
            lrcListFragment.setArguments(bundle);
            // fragmentReplace(lrcListFragment,"LrcList");
            getFragmentManager().beginTransaction().add(R.id.fragment_parent_view, lrcListFragment, "LrcList").addToBackStack("LrcList").commit();
        }
    }

    private void fragmentReplace(Fragment fragment, String name) {
        FragmentManager manager = getFragmentManager();
        manager.popBackStackImmediate();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_parent_view, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

}
