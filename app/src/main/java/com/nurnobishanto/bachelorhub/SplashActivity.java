package com.nurnobishanto.bachelorhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nurnobishanto.bachelorhub.Activity.AuthActivity;
import com.nurnobishanto.bachelorhub.Activity.onBoardAtivity;

public class SplashActivity extends AppCompatActivity {

    float i;
    public TextView splashTesxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        splashTesxt =(TextView)findViewById(R.id.SpashtextId);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                SharedPreferences preferences1 = getApplication().getSharedPreferences("Users", Context.MODE_PRIVATE);
                boolean isLogged =preferences1.getBoolean("isLogged",false);
                if (isLogged){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else {
                    SharedPreferences preferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
                    boolean isFirstTime = preferences.getBoolean("isFirstTime",true);
                    if(isFirstTime){
                        startActivity(new Intent(SplashActivity.this, onBoardAtivity.class));

                    }else {
                        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    }
                }
                finish();


            }
        });
        thread.start();
    }
    public void doWork()
    {
        for (i=0;i<1;i+=0.01)
        {
            try {
                Thread.sleep(25);
                splashTesxt.setScaleX(i);
                splashTesxt.setScaleY(i);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}