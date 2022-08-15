package com.nurnobishanto.bachelorhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nurnobishanto.bachelorhub.Activity.AuthActivity;
import com.nurnobishanto.bachelorhub.Activity.EditProfileActivity;
import com.nurnobishanto.bachelorhub.Activity.PhoneActivity;
import com.nurnobishanto.bachelorhub.Activity.onBoardAtivity;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    float i;
    public ImageView splashTesxt;
    boolean isLogged,isFirstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        splashTesxt =(ImageView) findViewById(R.id.SpashtextId);
        isLogged = SharedPrefManager.getInstance(this).getUserIsLoggedIn();
        isFirstTime = SharedPrefManager.getInstance(this).getUserIsFirstTime();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if (isLogged){
                    startActivity(new Intent(SplashActivity.this, EditProfileActivity.class));
                }else {
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