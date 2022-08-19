package com.bachelorhub.bytecode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bachelorhub.bytecode.Activity.AuthActivity;
import com.bachelorhub.bytecode.Activity.onBoardAtivity;
import com.bachelorhub.bytecode.Admin.AdminHomeActivity;
import com.bachelorhub.bytecode.Session.SharedPrefManager;

public class SplashActivity extends AppCompatActivity {

    float i;
    public ImageView splashTesxt;
    boolean isLogged, isFirstTime;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        splashTesxt = (ImageView) findViewById(R.id.SpashtextId);
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

                if (isLogged) {
                    email = SharedPrefManager.getInstance(SplashActivity.this).getUser().getUserEmail();
                    Log.i("SplashActivity", "Splash Email: " + email);
                    if (email.equals("bachelorhub.info@gmail.com")) {
                        startActivity(new Intent(SplashActivity.this, AdminHomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                } else {
                    if (isFirstTime) {
                        startActivity(new Intent(SplashActivity.this, onBoardAtivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    }
                }
                finish();
            }
        });
        thread.start();
    }

    public void doWork() {
        for (i = 0; i < 1; i += 0.01) {
            try {
                Thread.sleep(10);
                splashTesxt.setScaleX(i);
                splashTesxt.setScaleY(i);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}