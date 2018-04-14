package com.apkglobal.todoapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CompletedTasks extends AppCompatActivity {
    SQLiteDatabase sd;
    int index = 0;
    ListView listView;
    SimpleAdapter sa;
    ArrayList<String> memo_title = new ArrayList<String>();
    ArrayList<String> memo_text = new ArrayList<String>();
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);
        setTitle("Completed Tasks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = (GridView) findViewById(R.id.gridView);
        final TextView textView = (TextView) findViewById(R.id.tv_alternate_completed);


        //RENDERING Part
        fetch();  //populates the arrayLists Containing todo fetched from the Database
        Log.e("-------------->", "memo_title: " + memo_title.size() + " memo_text: " + memo_text.size());
        if (memo_text.size() == 0 || memo_title.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            Log.e("--------->", "Grid view is invisible");
        } else {
            textView.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
        }


        CustomGrid adapter = new CustomGrid(CompletedTasks.this, memo_title, memo_text);
        gridView.setAdapter(adapter);

        //longPress to delete or mark as unchecked ....
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                //Building a alert to pick the choice

                final int index = i;
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(CompletedTasks.this, R.style.CustomDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(CompletedTasks.this);
                }
                builder.setTitle("")
                        .setMessage("What you want to do ?")
                        .setPositiveButton("Mark as Undone", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(CompletedTasks.this, "Marking as Undone...", Toast.LENGTH_SHORT).show();
                                TextView textView = view.findViewById(R.id.tv_list_title);
                                String title = textView.getText().toString();
                                textView = view.findViewById(R.id.tv_list_text);
                                String text = textView.getText().toString();

                                markUndone(title, text);
                                startActivity(new Intent(CompletedTasks.this, MainActivity.class));
                                CompletedTasks.this.finish();
                            }
                        })
                        .setNegativeButton("Delete it", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TextView textView = view.findViewById(R.id.tv_list_title);
                                String title = textView.getText().toString();
                                textView = view.findViewById(R.id.tv_list_text);
                                String text = textView.getText().toString();

                                delete(title, text);
                                Toast.makeText(CompletedTasks.this, "Deleting...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CompletedTasks.this, CompletedTasks.class));
                                CompletedTasks.this.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

    }

    private void markUndone(String title, String text) {
        ContentValues content = new ContentValues();
        content.put("state", 0);

        Log.e("------------>", "markign undone index: " + index);
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.update("todo_table", content, "title=? and text=?", new String[]{title, text});
        sd.close();
    }

    private void delete(String title, String text) {
        Log.e("----------->", "Deleting Title: " + title + " Deleteting text: " + text);
        Log.e("------------>", "Deleting  index: " + index);
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.delete("todo_table", "title=? and text=?", new String[]{title, text});
        sd.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //The Buttons on the action Bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete_all_completed:
                Log.d("TAG: --------->", "Delete all notes invoked...");

                //asking for a confirmation, Creating a alert Dialogue
                Log.e("------------>", "Working on the ALert");
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(CompletedTasks.this, R.style.CustomDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(CompletedTasks.this);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete all the completed Tasks...?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(CompletedTasks.this, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteAllCompleted();
                                Toast.makeText(CompletedTasks.this, "All Cleared Up Boss", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CompletedTasks.this, CompletedTasks.class));
                                CompletedTasks.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
                /*----------------------------------*/
            //startActivity(new Intent(CompletedTasks.this, CompletedTasks.class));
           /* case R.id.action_addNew:
                Log.e("-------------->", "Creating a new ToDo");
                startActivity(new Intent(CompletedTasks.this, AddNewActivity.class));
                finish();*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Deletes all the Todos, Empties the database
    private void deleteAllCompleted() {
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("delete from todo_table where state = 1");
        Log.e("TAG", "Invoked Delete all records from table");
        Toast.makeText(this, "All Tasks Deleted...", Toast.LENGTH_SHORT).show();
    }

    private void fetch() {
        index = 0;
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
        Cursor sc = sd.rawQuery("select * from todo_table", null);
        int cursor_count = sc.getCount();
        int state_index = sc.getColumnIndex("state");
        Log.e("------>", "The index of state column: " + state_index);
        Log.e("-------->", "Cursor Count: " + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            do {
                if (sc.getInt(state_index) == 1) {
                    memo_title.add(sc.getString(1)); //The title
                    memo_text.add(sc.getString(2)); //The text
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

}
