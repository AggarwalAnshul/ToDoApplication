package com.apkglobal.todoapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static java.lang.Math.abs;

public class DetailedView extends AppCompatActivity {
    int id = -1;
    EditText editText_title, editText_text;
    String old_title, old_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Your To-Do");
        setContentView(R.layout.activity_detailed_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editText_title = (EditText) findViewById(R.id.et_detailed_title);
        editText_text = (EditText) findViewById(R.id.et_detailed_text);


        //Menu Option for Timed Reminders
        //Retreiving the Bundle
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int number = 0;
            id = bundle.getInt("ID", -1); //-1 is the default value
            Log.e("----------->", "Retrieved Data: ID " + id);
        }

        //Obtaingin the text and the title
        SQLiteDatabase sd;
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
        Cursor sc = sd.rawQuery("select * from todo_table ", null);
        int cursor_count = sc.getCount();
        Log.e("-------->", "Recovered cursor count" + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            do {
                int temp = sc.getInt(0);
                if (temp == id) {
                    Log.e("-------->", "The title: " + sc.getString(1));
                    Log.e("-------->", "The MEMO: " + sc.getString(2));
                    editText_title.setText(sc.getString(1));
                    old_text = sc.getString(2);
                    editText_text.setText(sc.getString(2));
                    old_title = sc.getString(1);
                }
            }
            while (sc.moveToNext());
        }
        sd.close();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_reminder:
                Log.d("TAG------->", "Setting a reminder for the todo");
                Toast.makeText(this, "I'm Here", Toast.LENGTH_SHORT).show();
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                AlarmManager alarmMgr = (AlarmManager) DetailedView.this.getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(DetailedView.this, NotificationService.class);
                                intent.putExtra("ID", id);
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(DetailedView.this, 0, intent, 0);

                                // Set the alarm to start at 8:30 a.m
                                Log.e("HOUR: ", "" + hourOfDay);
                                Log.e("MINUTE: ", "" + minute);


                                Calendar calendar = Calendar.getInstance();
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minutes = calendar.get(Calendar.MINUTE);

                                int triggerTime = abs(((hourOfDay * 60) + minute) - (hour * 60 + minutes));
                                Log.e("------------>", "Time to wake up..." + triggerTime);
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                                Toast.makeText(DetailedView.this, "Reminder successfully Set ", Toast.LENGTH_SHORT).show();
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                timePickerDialog.setTitle("Set a Reminder for this Task");
                return true;
            case R.id.action_save:
                Log.e("------------------->", "Saving the modified text");
                String title = editText_title.getText().toString();
                String text = editText_text.getText().toString();

                Log.e("-------->","Old title" +old_title+ " new title" +title);
                Log.e("-------->", "Old text: "+old_text+ " New text: "+text);

                //saving into the database
                SQLiteDatabase sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", title.replaceAll("'","''"));
                contentValues.put("text", text.replaceAll("'","''"));
                sd.update("todo_table", contentValues, "title = ? and text = ?", new String[]{old_title, old_text});
                Log.e("------------>", "Database Value Updated");
                startActivity(new Intent(DetailedView.this, MainActivity.class));
                DetailedView.this.finish();
                Toast.makeText(this, "Updated !", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
