package com.apkglobal.todoapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anshul Aggarwal on 13-Apr-18.
 */

public class CustomGrid extends BaseAdapter {
    int a = 0;
    private Context context;
    private ArrayList<String> memo_text = new ArrayList<String>();
    private ArrayList<String> memo_title = new ArrayList<String>();

    public CustomGrid(Context c, ArrayList<String> memo_title, ArrayList<String> memo_text) {
        context = c;
        this.memo_title = memo_title;
        this.memo_text = memo_text;
    }

    @Override
    public int getCount() {
        return memo_title.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            grid = new View(context);
            grid = inflater.inflate(R.layout.list_item_revampled, null);
            TextView tv_title = (TextView) grid.findViewById(R.id.tv_list_title);
            TextView tv_text = (TextView) grid.findViewById(R.id.tv_list_text);
            Log.e("---------->", "Loop: " + a);
            Log.e("---------->", "i: " + i + " memo_title: " + memo_title.get(i));
            Log.e("---------->", "i: " + (i) + " memo_text: " + memo_text.get(i));
            a += 1;
            tv_title.setText(memo_title.get(i));
            tv_text.setText(memo_text.get(i));
        } else
            grid = (View) view;
        return grid;

    }
}
