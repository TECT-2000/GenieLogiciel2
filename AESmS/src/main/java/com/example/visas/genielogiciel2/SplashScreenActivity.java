package com.example.visas.genielogiciel2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;


public class SplashScreenActivity extends Activity {

    private static  int SPLASH_TIME_OUT=3000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(i);

                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
