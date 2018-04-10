package com.apkglobal.todoapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class CompletedTasks extends AppCompatActivity {
    SQLiteDatabase sd;
    int index = 0;
    ListView listView;
    SimpleAdapter sa;
    ArrayList<String> memo = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);
        listView = (ListView) findViewById(R.id.listView_completed);

        //Fetching the completed tasks
        fetch();
        render();

    }

    public void goBack(View view) {
        Toast.makeText(this, "Gotach !", Toast.LENGTH_SHORT).show();
    }

    private void fetch() {
        index = 0;
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        Cursor sc = sd.rawQuery("select * from todo_table", null);
        int cursor_count = sc.getCount();
        Log.e("-------->", "" + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            do {
                if (sc.getInt(3) == 1) {
                    memo.add(sc.getString(1)); //The title
                    memo.add(sc.getString(2)); //The text
                    Log.e("--------->", "" + sc.getInt(3));
                    Toast.makeText(this, "" + sc.getInt(3), Toast.LENGTH_SHORT).show();
                    index += 2;
                }
            } while (sc.moveToNext());
        }
        if (sc == null) {
            Toast.makeText(this, "No TO-DOs Added !", Toast.LENGTH_SHORT).show();
        }
        sd.close();
    }

    private void render() {
        if (index != 0) {
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            Log.e("8*********", "Enter to render");
            if (!memo.isEmpty()) {
                for (int i = 0; i < index; i += 2) {
                    HashMap<String, String> h = new HashMap<>();
                    h.put("todo_title", memo.get(i));
                    h.put("todo_text", memo.get(i + 1));
                    arrayList.add(h);
                }

                String[] from = {"todo_title", "todo_text"};
                int[] to = {R.id.tv_list_title, R.id.tv_list_text};
                {

                    sa = new SimpleAdapter(getApplicationContext(), arrayList, R.layout.list_item_completed, from, to);
                    listView.setAdapter(sa);
                    Log.e("_____________>", "calling from within the hashmap");
                    sa.notifyDataSetChanged();
                    //listview.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
