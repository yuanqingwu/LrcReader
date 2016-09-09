package com.wyq.lrcreader.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.fragment.LrcLikeFragment;
import com.wyq.lrcreader.fragment.LrcListFragment;

/**
 * Created by Uni.W on 2016/8/18.
 */
public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private Button searchBt;
    private EditText editText;
    private String searchText;
    private GridView settingGrid;
    private boolean isSetting;
    private float startX = 0, endX = 0, startY = 0, endY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBt = (Button) findViewById(R.id.activity_search_button);
        searchBt.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.activity_search_edittext);
        settingGrid = (GridView) findViewById(R.id.activity_search_setting_grid);
        searchBt.setOnLongClickListener(this);

        //显示喜欢的歌词列表

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_parent_view, new LrcLikeFragment(), "LrcLikeList").commit();
        } else {
            Log.i("Test", "savedInstanceState not null");
        }
        // fragmentReplace(new LrcLikeFragment(),"LrcLikeList");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_parent_view, lrcListFragment, "LrcList").addToBackStack("LrcList").commit();
            editText.setText("");
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),0);
    }

    private void fragmentReplace(Fragment fragment, String name) {
        FragmentManager manager = getFragmentManager();
        manager.popBackStackImmediate();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_parent_view, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isSetting) {
            settingGrid.setVisibility(View.VISIBLE);
            isSetting=true;
        }else{
            settingGrid.setVisibility(View.GONE);
            isSetting=false;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getRawX();
                endY = event.getRawY();
                float distanceX = endX - startX;
                float distanceY = Math.abs(endY - startY);
                if (distanceX > 100 && distanceY < 100) {
                    getFragmentManager().popBackStack();
                }
                break;
            case MotionEvent.ACTION_UP:
                startX = 0;
                startY = 0;
                endX = 0;
                endY = 0;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

}
