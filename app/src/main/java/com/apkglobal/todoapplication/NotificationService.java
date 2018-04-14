package com.apkglobal.todoapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by Anshul Aggarwal on 25-01-2018.
 */

public class NotificationService extends BroadcastReceiver {
    SQLiteDatabase sd;
    int id;
    String title, text;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        id = intent.getExtras().getInt("ID");
        Log.e("---------->", "Retrieved ID is..." + id);
        Log.e("----------->", "This is the broadcast receiver for the note memo");
        Log.e("----------->", "Generating the Notification...");
        //Fetching the todo message

        notification(context);
    }

    private void notification(Context context) {

/*-------------------------Database Fetching...*/
        int flag = 0;
        Log.e("--------------->", "Performing the database search");
        sd = context.openOrCreateDatabase("todo", 0, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
        Cursor sc = sd.rawQuery("select * from todo_table", null);
        int cursor_count = sc.getCount();
        int index = sc.getColumnIndex("id");
        Log.e("-------->", "Cursor Count: " + cursor_count);
        if (sc != null && cursor_count != 0) {
            sc.moveToFirst();
            do {
                if (sc.getInt(index) == id) {
                    title = sc.getString(1);
                    text = sc.getString(2);
                    flag = 1;
                    Log.e("--------->", "Retrieved Title: " + title + "\nRetrieved Text: " + text);
                }
            } while (sc.moveToNext() && flag != 1);
        }
        sd.close();
        /*----------------------------------------*/

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).
                setSmallIcon(R.drawable.img_splash)
                .setTicker("You've got to a To-Do")
                .setContentTitle(title)
                .setContentText(text)
                .addAction(R.drawable.icon_app_old, "Show me...", pIntent)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }
}
