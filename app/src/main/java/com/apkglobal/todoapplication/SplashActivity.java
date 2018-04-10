package com.apkglobal.todoapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;
    int time = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

       //         startProgress();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish(); //Prevents running back to splash screen

            }
        }, 200);
    }

    private void startProgress() {
        for (int progress = 1; progress <= 100; progress += 1)
            try {
                Log.e("Progress------>", "" + progress);
                progressBar.setProgress(progress);
                sleep(200);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}

