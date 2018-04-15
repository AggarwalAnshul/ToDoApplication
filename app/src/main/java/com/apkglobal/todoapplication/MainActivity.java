package com.apkglobal.todoapplication;

import android.content.ContentValues;
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
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static com.apkglobal.todoapplication.R.drawable.ic_help;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btn_new;
    SQLiteDatabase sd;
    View FlingView;
    int index = 0, should_open_touch;
    ImageView iv_alternate;
    GridView gridView;
    AlphaAnimation blinkanimation;
    ArrayList<String> memo_title = new ArrayList<String>();
    ArrayList<String> memo_text = new ArrayList<String>();
    Vibrator vibrator;
    CustomGrid adapter;
    long FlingItem;
    ListView listView;
    private GestureDetectorCompat detector;
    /*SimpleAdapter sa;
      ListView;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /*TypeCastings and Connections*//*
        gridView = (GridView) findViewById(R.id.gridView);*/
        btn_new = (FloatingActionButton) findViewById(R.id.btn_new);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        TextView alterante = (TextView) findViewById(R.id.tv_alternate);
        listView = (ListView) findViewById(R.id.listView);
        iv_alternate = (ImageView) findViewById(R.id.iv_alternate);
        detector = new GestureDetectorCompat(this, new MyGestureListener());

        btn_new.setImageResource(R.drawable.icon_addnew);
        //RENDERING Part
        fetch();  //populates the arrayLists Containing todo fetched from the Database
        render();
        Log.e("----------->", "Size of memo_text: " + memo_text.size() + "Size of memo_title: " + memo_title.size());

        //Animaitons;
        blinkanimation = new AlphaAnimation(1, 0);
        blinkanimation.setDuration(1700);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(Animation.INFINITE);
        blinkanimation.setRepeatMode(Animation.REVERSE);

//------------------------
        if (memo_text.size() == 0) {
            alterante.setVisibility(View.VISIBLE);
            iv_alternate.setVisibility(View.VISIBLE);
            iv_alternate.setAnimation(blinkanimation);
            listView.setVisibility(View.GONE);
            Log.e("--------->", "Grid view is invisible");
        } else {
            iv_alternate.setVisibility(View.INVISIBLE);
            alterante.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            iv_alternate.clearAnimation();
        }


        //CustomGrid adapter = new CustomGrid(MainActivity.this, memo_title, memo_text);
       /* adapter = new CustomGrid(MainActivity.this, memo_title, memo_text);
        gridView.setAdapter(adapter);*/


        //Chaning the layout according to user preferences:
       /* int layout_preference = 2;
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
        gridView.setNumColumns(layout_preference);*/

        //For left and right swipe
        should_open_touch = 0;
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FlingView = v;
                detector.onTouchEvent(event);
                return false;
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {


                //Obsolete UI Choice,  Used to mark the ToDo as Completed
                //>> New UI Choice: Used to delete the current ToDo

                //Toast.makeText(MainActivity.this, "" + i, Toast.LENGTH_SHORT).show();
                /*  VibrationEffect effect = VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);*/
                /*CheckBox checkbox = view.findViewById(R.id.ch_checkbox);
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
*/
                final int index = i;
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogTheme);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("")
                        .setMessage("Are you sure, You want to Delete it ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TextView textView = view.findViewById(R.id.tv_list_title);
                                String title = textView.getText().toString();
                                textView = view.findViewById(R.id.tv_list_text);
                                String text = textView.getText().toString();

                                delete(title, text);
                                Toast.makeText(MainActivity.this, "Deleting...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {/*
                                Toast.makeText(MainActivity.this, "Marking as Undone...", Toast.LENGTH_SHORT).show();
                                TextView textView = view.findViewById(R.id.tv_list_title);
                                String title = textView.getText().toString();
                                textView = view.findViewById(R.id.tv_list_text);
                                String text = textView.getText().toString();

                                markUndone(title, text);
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                MainActivity.this.finish();*/
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

        //Touch to view the completed task
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (should_open_touch == 0) {
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
           /* case R.id.action_layout:
                Log.d("----------->", "Changing the layout invoked...");
               *//* ShowDialog();*//*
                return true;*/
                /*-----------------------------------------------------------*/

                /*-----------------------------------------------------------*/

            case R.id.action_developer:
                startActivity(new Intent(MainActivity.this, Developer.class));
                return true;

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
                                + "\n\n2. Long Press the ToDo(s) Delete them from the list"
                                + "\n\n3. Swipe the  ToDo left or Right to Mark it Done and Move to Completed list"
                                + "\n\n4. Press the Green Tick button on the top to view completed tasks"
                                + "\n\n5. Press the Red Cross to delete all the tasks"
                                + "\n\n6. Press the Green New button on the bottom right to add a new ToDo"
                                + "\n\n7. Long Press the ToDo(s) in completed Section ( Green Tick) for options"
                                + "\n\n8. Use CheckBoxes to check/uncheck the ToDo Temporarily")
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
                                // Toast.makeText(MainActivity.this, "Deleting...", Toast.LENGTH_SHORT).show();
                                deleteALL();
                                Toast.makeText(MainActivity.this, "All Cleared Up Boss", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                MainActivity.this.finish();
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
        Cursor sc = sd.rawQuery("select * from todo_table where state = 0", null);
        int cursor_count = sc.getCount();
        int state_index = sc.getColumnIndex("state");
        // Log.e("------>", "The index of state column: " + state_index);
        Log.e("-------->", "Cursor Count: " + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            memo_text.clear();
            memo_title.clear();
            do {
                if (sc.getInt(state_index) == 0) {
                    memo_title.add(sc.getString(1)); //The title
                    memo_text.add(sc.getString(2)); //The text
                    Log.e("--------------->", "\tid: " + sc.getInt(0));
                    Log.e("--------------->", "\ttitle: " + sc.getString(1));
                    Log.e("--------------->", "\ttext: " + sc.getString(2));
                    Log.e("--------------->", "\tstate: " + sc.getInt(3));
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
    private void render() {
        if (index != 0) {
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            Log.e("8*********", "Enter to render");
            int for_end = memo_text.size() - 2;
            int size = memo_text.size();
            if (!memo_title.isEmpty()) {
                int i = 0;
                for (i = 0; i <= size; i += 1) {

                    if (i != size) {
                        HashMap<String, String> h = new HashMap<>();
                        h.put("todo_title", memo_title.get(i));
                        h.put("todo_text", memo_text.get(i));
                        arrayList.add(h);
                    }

                }
                /*
if(memo_text.size()%2!=0) {
    h.clear();
    h.put("todo_title", memo_title.get(for_end+1));
    h.put("todo_text", memo_text.get(for_end+1));
    h.put("todo_title2", "EMPTY");
    h.put("todo_text2", "EMPTY");
    arrayList.add(h);
}*/
                String[] from = {"todo_title", "todo_text"};
                int[] to = {R.id.tv_list_title, R.id.tv_list_text};
                {

                    SimpleAdapter sa = new SimpleAdapter(getApplicationContext(), arrayList, R.layout.list_item_revampled, from, to);
                    listView.setAdapter(sa);
                    Log.e("_____________>", "calling from within the hashmap");
                    sa.notifyDataSetChanged();
/*

                    RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
*/

                    //listview.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

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
                /*gridView.setNumColumns(progress);*/
            }
        });
        alertDialog.create();
        alertDialog.show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void onSwipeLeft() {
        //Preventing the null View stalling by chekcing the number of entries in the database --- Workaroudn
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
        Cursor sc = sd.rawQuery("select * from todo_table", null);
        int cursor_count = sc.getCount();
        int count = 0;
        String FlingText = null, FlingTitle = null;
        if (sc != null && cursor_count >= 1) {
            try {
                sc = sd.rawQuery("select * from todo_table where state = 0", null);
                sc.moveToFirst();
                do {
                    if (count == FlingItem) {
                        FlingTitle = sc.getString(1);
                        FlingText = sc.getString(2);
                    }
                    count += 1;
                } while (sc.moveToNext());

                Log.e("---------------->", "Fling motion Text: " + FlingText + "\nFling motion title: " + FlingTitle);
                String query = "update todo_table set state = 1 where text='" + FlingText.replaceAll("'", "''") + "'";
                sd.execSQL(query);
                Toast.makeText(MainActivity.this, "Task Completed !", Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                MainActivity.this.finish();
            } catch (Exception e) {
                Log.e("------------------->", "Stalled  a Null Pointer reference due to fling...");
            }
  /*      Toast.makeText(this, "Right Swipe", Toast.LENGTH_SHORT).show();
  */
        }
    }

    private void onSwipeRight() {
        //Preventing the null View stalling by chekcing the number of entries in the database --- Workaroudn
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
        Cursor sc = sd.rawQuery("select * from todo_table", null);
        int cursor_count = sc.getCount();
        int count = 0;
        String FlingText = null, FlingTitle = null;
        if (sc != null && cursor_count >= 1) {
            try {
                sc = sd.rawQuery("select * from todo_table where state = 0", null);
                sc.moveToFirst();
                do {
                    if (count == FlingItem) {
                        FlingTitle = sc.getString(1);
                        FlingText = sc.getString(2);
                    }
                    count += 1;
                } while (sc.moveToNext());

                Log.e("---------------->", "Fling motion Text: " + FlingText + "\nFling motion title: " + FlingTitle);
                String query = "update todo_table set state = 1 where text='" + FlingText.replaceAll("'", "''") + "'";
                sd.execSQL(query);
                Toast.makeText(MainActivity.this, "Task Completed !", Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                this.finish();
            } catch (Exception e) {
                Log.e("------------------->", "Stalled  a Null Pointer reference due to fling...");
            }
  /*      Toast.makeText(this, "Right Swipe", Toast.LENGTH_SHORT).show();
  */
        }
    }

    private void onSwipeTop() {

    }

    private void onSwipeBottom() {

    }

    //Marks the to do as Undone
    private void markUndone(String title, String text) {
        ContentValues content = new ContentValues();
        content.put("state", 0);

        Log.e("------------>", "markign undone index: " + index);
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.update("todo_table", content, "title=? and text=?", new String[]{title, text});
        sd.close();
    }

    //Deletes the toDo from the database
    private void delete(String title, String text) {
        Log.e("----------->", "Deleting Title: " + title + " Deleteting text: " + text);
        Log.e("------------>", "Deleting  index: " + index);
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.delete("todo_table", "title=? and text=?", new String[]{title, text});
        sd.close();
    }

    //Handles swipe opertion
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();

            Log.e("-------------------->", "Running the recovery diagnostice...");
           /* Log.e("-------------------->", "" + gridView.pointToPosition((int) event1.getX(), (int) event1.getY()));*/
            FlingItem = listView.pointToPosition((int) event1.getX(), (int) event1.getY());

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                        should_open_touch = 1;
                    } else {
                        onSwipeLeft();
                        should_open_touch = 1;
                    }
                }
            }

            return true;
        }
    }
}


