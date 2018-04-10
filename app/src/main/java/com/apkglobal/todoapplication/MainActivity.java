package com.apkglobal.todoapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button btn_new;
    SQLiteDatabase sd;
    int index = 0;
    ListView listView;

    ArrayList<String> memo = new ArrayList<String>();
    SimpleAdapter sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_delete = (Button) findViewById(R.id.btn_delete);
        listView = (ListView) findViewById(R.id.listView);
        btn_new = (Button) findViewById(R.id.btn_new);
        fetch();
        render();


        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteALL();
            }
        });
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNewActivity.class));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.ch_checkbox);
                if (checkbox.isChecked())
                    checkbox.setChecked(false);
                else {
                    checkbox.setChecked(true);
                    TextView textView = (TextView) view.findViewById(R.id.tv_list_text);
                    String tv_textView = textView.getText().toString();
                    sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);

                    sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
                    String query = "update todo_table set state = 1 where text='" + tv_textView + "'";
                    sd.execSQL(query);
                    fetch();
                    render();
                    Toast.makeText(MainActivity.this, "Task Completed !", Toast.LENGTH_SHORT).show();
                    getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
                return true;
            }
        });
/*

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.ch_checkbox);
                checkbox.setChecked(true);
                Toast.makeText(getApplicationContext(), "the postition" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }
*/
    }

    //View all Checked task from the button in taskbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void goBack(View view) {
        Toast.makeText(this, "Gotach !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_display_completed:
                Log.d("TAG------->", "Displayed Completed Tasks");
                startActivity(new Intent(MainActivity.this, CompletedTasks.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteALL() {
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("delete from todo_table");
        Log.e("TAG", "Invoked Delte all records from table");
        Toast.makeText(this, "All Tasks Deleted...", Toast.LENGTH_SHORT).show();
    }

    private void fetch() {
        index = 0;
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
        Cursor sc = sd.rawQuery("select * from todo_table", null);
        int cursor_count = sc.getCount();
        Log.e("-------->", "" + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            do {
                if (sc.getInt(3) == 0) {
                    memo.add(sc.getString(1)); //The title
                    memo.add(sc.getString(2)); //The text
                    Log.e("--------->", "" + sc.getInt(3));
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

                    sa = new SimpleAdapter(getApplicationContext(), arrayList, R.layout.list_item, from, to);
                    listView.setAdapter(sa);
                    Log.e("_____________>", "calling from within the hashmap");
                    sa.notifyDataSetChanged();
                    //listview.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
