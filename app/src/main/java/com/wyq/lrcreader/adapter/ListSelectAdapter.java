package com.wyq.lrcreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyq.lrcreader.R;
import com.wyq.lrcreader.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Uni.W on 2016/12/20.
 */

public class ListSelectAdapter extends BaseAdapter {

    private List<String> list = null;
    private Context context;

    private Map<Integer, Boolean> selectMap = null;

    public ListSelectAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        selectMap = new HashMap<>();
    }

    public List<String> getSelectList() {
        List<String> selectList = new ArrayList<>();
        for (Integer i : selectMap.keySet()) {
            if (selectMap.get(i)) {
                selectList.add(list.get(i));
            }
        }
        LogUtil.i("select size:" + selectList.size());
        return selectList;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (String s : list) {
            if (list.indexOf(s) >= selectMap.size())
                selectMap.put(list.indexOf(s), true);
        }
        LogUtil.i("seletMap size:" + selectMap.size());
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_local_list_item, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.fragment_local_list_item_text);
            holder.imageView = (ImageView) convertView.findViewById(R.id.fragment_local_list_item_select);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.textView.setText(list.get(position).substring(19));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imageView.getVisibility() == View.VISIBLE) {
                    holder.imageView.setVisibility(View.INVISIBLE);
                    selectMap.put(position, false);
                } else {
                    holder.imageView.setVisibility(View.VISIBLE);
                    selectMap.put(position, true);
                }

            }
        });

        return convertView;
    }
}

class Holder {
    TextView textView;
    ImageView imageView;

}