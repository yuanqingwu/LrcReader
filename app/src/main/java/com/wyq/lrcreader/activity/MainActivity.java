package com.wyq.lrcreader.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.constants.LocalConstans;
import com.wyq.lrcreader.fragment.LrcLikeFragment;
import com.wyq.lrcreader.fragment.LrcListFragment;
import com.wyq.lrcreader.fragment.LocalLrcFragment;
import com.wyq.lrcreader.utils.LogUtil;

import java.io.File;

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
    private long exitTime = 0;

    private String[] settingName = {"本地歌词", "语音搜索", "设置"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBt = (Button) findViewById(R.id.activity_search_button);
        searchBt.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.activity_search_edittext);
        settingGrid = (GridView) findViewById(R.id.activity_search_setting_grid);
        searchBt.setOnLongClickListener(this);

        initSettingGrid();

        //显示喜欢的歌词列表

//        if (savedInstanceState == null) {
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                add(R.id.fragment_parent_view, new LrcLikeFragment(), "LrcLikeList").
                commit();
//        } else {
//            Log.i("Test", "savedInstanceState not null");
//        }
        // fragmentReplace(new LrcLikeFragment(),"LrcLikeList");
    }

    public void initSettingGrid() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, settingName);
        settingGrid.setAdapter(arrayAdapter);
        settingGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                                hide(getFragmentManager().findFragmentByTag("LrcLikeList")).
                                add(R.id.fragment_parent_view, new LocalLrcFragment(), "SearchLocal").
                                addToBackStack("SearchLocal").
                                commit();
                        settingGrid.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (getFragmentManager().popBackStackImmediate()) {
                return true;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            bundle.putBoolean("isLocal", false);
            bundle.putString("searchText", searchText);
            lrcListFragment.setArguments(bundle);
            // fragmentReplace(lrcListFragment,"LrcList");
            getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_parent_view, lrcListFragment, "LrcList").addToBackStack("LrcList").commit();
            editText.setText("");
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isSetting) {
            settingGrid.setVisibility(View.VISIBLE);
            isSetting = true;
        } else {
            settingGrid.setVisibility(View.GONE);
            isSetting = false;
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
