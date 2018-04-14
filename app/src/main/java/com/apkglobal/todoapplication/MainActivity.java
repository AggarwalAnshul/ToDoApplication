package com.apkglobal.todoapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.apkglobal.todoapplication.R.drawable.ic_help;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btn_new;
    SQLiteDatabase sd;
    int index = 0;
    GridView gridView;
    ArrayList<String> memo_title = new ArrayList<String>();
    ArrayList<String> memo_text = new ArrayList<String>();
    Vibrator vibrator;
    /*SimpleAdapter sa;
      ListView;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /*TypeCastings and Connections*/
        gridView = (GridView) findViewById(R.id.gridView);
        btn_new = (FloatingActionButton) findViewById(R.id.btn_new);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        TextView alterante = (TextView) findViewById(R.id.tv_alternate);
        btn_new.setImageResource(R.drawable.icon_addnew);
        //RENDERING Part
        fetch();  //populates the arrayLists Containing todo fetched from the Database
        Log.e("----------->", "Size of memo_text: " + memo_text.size() + "Size of memo_title: " + memo_title.size());
        if (memo_text.size() == 0) {
            alterante.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
            Log.e("--------->", "Grid view is invisible");
        } else {
            alterante.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
        }
        CustomGrid adapter = new CustomGrid(MainActivity.this, memo_title, memo_text);
        gridView.setAdapter(adapter);


        //Chaning the layout according to user preferences:
        int layout_preference = 2;
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists layout(number integer);");
        Log.e("----------->", "Opening table layout");
        Cursor sc = sd.rawQuery("select * from layout", null);
        int cursor_count = sc.getCount();
        Log.e("--------->", "Cursor count: " + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            {
                layout_preference = sc.getInt(0);
                Log.e("------------->", "Layout_Preference:   " + layout_preference);
                // Toast.makeText(MainActivity.this, "Sednign data: "+findId, Toast.LENGTH_SHORT).show();
            }
        }
        sd.close();
        gridView.setNumColumns(layout_preference);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
              /*  VibrationEffect effect = VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);*/
                CheckBox checkbox = view.findViewById(R.id.ch_checkbox);
                if (checkbox.isChecked())
                    Toast.makeText(MainActivity.this, "Chcekbox is checked...", Toast.LENGTH_SHORT).show();

                checkbox.setChecked(true);
                TextView textView = view.findViewById(R.id.tv_list_text);
                String tv_textView = textView.getText().toString();
                sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);

                sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
                String query = "update todo_table set state = 1 where text='" + tv_textView.replaceAll("'", "''") + "'";
                sd.execSQL(query);
                Toast.makeText(MainActivity.this, "Task Completed !", Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();

                return true;
            }
        });

        //Touch to view the completed task
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int findId = -1;
                sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
                sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
                TextView textView = view.findViewById(R.id.tv_list_text);
                String text = textView.getText().toString();
                Log.e("------------->", "Sending Text: " + text);
                Cursor sc = sd.rawQuery("select * from todo_table where text = '" + text.replaceAll("'", "''") + "' ", null);
                int cursor_count = sc.getCount();
                Log.e("--------->", "Cursor count: " + cursor_count);
                // Toast.makeText(MainActivity.this, "Cursor Size: "+cursor_count, Toast.LENGTH_SHORT).show();
                if (sc != null && cursor_count != 0) {
                    sc.moveToFirst();
                    {
                        findId = sc.getInt(0);
                        Log.e("------------->", "Retrieved ID:  " + findId);
                        // Toast.makeText(MainActivity.this, "Sednign data: "+findId, Toast.LENGTH_SHORT).show();
                    }
                }
                sd.close();

                //Passing this is to the bundle
                Intent intent = new Intent(MainActivity.this, DetailedView.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID", findId);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        //Add new note
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNewActivity.class));
            }
        });

        //Long Press to mark a task complete
        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.ch_checkbox);
                if (checkbox.isChecked())
                    Toast.makeText(MainActivity.this, "Chcekbox is checked...", Toast.LENGTH_SHORT).show();

                checkbox.setChecked(true);
                TextView textView = (TextView) view.findViewById(R.id.tv_list_text);
                String tv_textView = textView.getText().toString();
                sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);

                sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
                String query = "update todo_table set state = 1 where text='" + tv_textView + "'";
                sd.execSQL(query);
                fetch();
                Toast.makeText(MainActivity.this, "Task Completed !", Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                startActivity(new Intent(MainActivity.this, MainActivity.class));

                return true;
            }
        });*//*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.ch_checkbox);
                if (checkbox.isChecked())
                    Toast.makeText(MainActivity.this, "Chcekbox is checked...", Toast.LENGTH_SHORT).show();

                checkbox.setChecked(true);
                TextView textView = (TextView) view.findViewById(R.id.tv_list_text);
                String tv_textView = textView.getText().toString();
                sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);

                sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
                String query = "update todo_table set state = 1 where text='" + tv_textView + "'";
                sd.execSQL(query);
                fetch();
                Toast.makeText(MainActivity.this, "Task Completed !", Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                startActivity(new Intent(MainActivity.this, MainActivity.class));

                return true;
            }
        });*/
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


    //The Buttons on the action Bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_layout:
                Log.d("----------->", "Changing the layout invoked...");
                ShowDialog();
                return true;
                /*-----------------------------------------------------------*/

                /*-----------------------------------------------------------*/

            case R.id.action_display_completed:
                Log.d("TAG------->", "Displayed Completed Tasks");
                Intent intent = new Intent(MainActivity.this, CompletedTasks.class);
                startActivity(intent);
                return true;

            case R.id.action_help:
                Log.d("--------------->", "Popping the alert box");
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("To-Do")
                        .setIcon(getResources().getDrawable(ic_help))
                        .setMessage("1. Press the ToDo(s) to open detailed View and edit them"
                                + "\n2. Long Press the ToDo(s) to mark them Done and remove from current view"
                                + "\n3. Press the Green Tick button on the top to view completed tasks"
                                + "\n4. Press the Red Cross to delete all the tasks"
                                + "\n5. Press the Green New button on the bottom right to add a new ToDo"
                                + "\n6. Long Press the ToDo(s) in completed Section ( Green Tick) for options"
                                + "\n\n7. Use CheckBoxes to check/uncheck the ToDo Temporarily")
                        .setPositiveButton("Got it !", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Glad to help", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Not Sure ?", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                contactUs();
                            }
                        })
                        .show();
                return true;

            case R.id.action_delete_all:
                Log.d("TAG: --------->", "Delete all notes invoked...");

                //asking for a confirmation, Creating a alert Dialogue
                Log.e("------------>", "Working on the ALert");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete all ToDo...?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteALL();
                                Toast.makeText(MainActivity.this, "All Cleared Up Boss", Toast.LENGTH_SHORT).show();
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
                /*----------------------------------*/
            //startActivity(new Intent(MainActivity.this, MainActivity.class));
           /* case R.id.action_addNew:
                Log.e("-------------->", "Creating a new ToDo");
                View sharedView = btn_new;
                String transitionName = getString(R.string.btn_transition);

                ActivityOptions transitionActivityOptions = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
                }
                startActivity(new Intent(MainActivity.this, AddNewActivity.class));
                MainActivity.this.finish();
                return true;
*/
            case R.id.action_share:
                Intent share = new Intent();
                share.putExtra(Intent.EXTRA_TEXT, "Hey Check this out Man ! This Rocks" + "URL:www.google.co.in");
                share.setType("text/plain");
                startActivity(Intent.createChooser(share, "Share App Via"));
                return true;
            case R.id.action_email:
                Intent email = new Intent();
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"anshul.aggarwal.sfd@gmail.com"});
                email.setType("email/rfc822");
                startActivity(Intent.createChooser(email, "send Email Via"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void contactUs() {
        Intent email = new Intent();
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"anshul.aggarwal.sfd@gmail.com"});
        email.setType("email/rfc822");
        startActivity(Intent.createChooser(email, "send Email Via"));
    }

    //Deletes all the Todos, Empties the database
    private void deleteALL() {
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("delete from todo_table");
        Log.e("TAG", "Invoked Delte all records from table");
        Toast.makeText(this, "All Tasks Deleted...", Toast.LENGTH_SHORT).show();
    }

    //Populates the arraylists to render the display
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
                if (sc.getInt(state_index) == 0) {
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

    //Obsolete method used with ListViews, Dropped due to GridView
    /*private void render() {
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

                    sa = new SimpleAdapter(getApplicationContext(), arrayList, R.layout.list_item_revampled, from, to);
                    gridView.setAdapter(sa);
                    Log.e("_____________>", "calling from within the hashmap");
                    sa.notifyDataSetChanged();

                    RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

                    //listview.setVisibility(View.INVISIBLE);
                }
            }
        }
    }*/
    public void ShowDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.seekbar_layout, (ViewGroup) findViewById(R.id.layoutDialog));

        final TextView textView = view.findViewById(R.id.tv_seek);
        alertDialog.setIcon(ic_help);
        alertDialog.setTitle("Select ToDo(s) per Row: ");
        alertDialog.setView(view);

        final SeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        alertDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int progress = seekBar.getProgress();
               /* Toast.makeText(MainActivity.this, "Final Value: " + progress, Toast.LENGTH_SHORT).show();
               */
                SQLiteDatabase sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
                sd.execSQL("create table if not exists layout(number integer);");
                sd.execSQL("delete from layout;");
                sd.execSQL("insert into layout(number) values(" + progress + ");");
                sd.close();
                gridView.setNumColumns(progress);
            }
        });
        alertDialog.create();
        alertDialog.show();

    }


}
