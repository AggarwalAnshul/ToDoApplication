package com.apkglobal.todoapplication;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.R.attr.transitionName;

public class AddNewActivity extends AppCompatActivity {
    SQLiteDatabase sd;
    String text, title;
    EditText et_title, et_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        et_title = (EditText) findViewById(R.id.et_memo_title);
        et_text = (EditText) findViewById(R.id.et_memo_text);



        final Button btn_save = (Button) findViewById(R.id.btn_add);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  createdatabase();
                 insertDatabase();
                sd.close();

                View sharedView = btn_save;
                String transitionName = getString(R.string.btn_transition);

                ActivityOptions transitionActivityOptions = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AddNewActivity.this, sharedView, transitionName);
                }
                startActivity(new Intent(AddNewActivity.this, MainActivity.class), transitionActivityOptions.toBundle());
             }
        });
    }


    private void insertDatabase() {
        title = et_title.getText().toString();
        text = et_text.getText().toString();
        Log.e("TAG", title);
        Log.e("TAG", text);
        if(!title.isEmpty() && !text.isEmpty())
            {
        sd.execSQL("insert into todo_table(title, text, state) values('"+title+"', '"+text+"',0);");
        Toast.makeText(this, "Added text message: " + text, Toast.LENGTH_SHORT).show();
    }
    else
        {
            Toast.makeText(this, "Edit Texts are empty !", Toast.LENGTH_SHORT).show();
        }
    }

    private void createdatabase() {
        sd = openOrCreateDatabase("todo", Context.MODE_PRIVATE, null);
        sd.execSQL("create table if not exists todo_table(id integer primary key autoincrement not null, title varchar, text varchar, state integer default 0);");
    }
}
