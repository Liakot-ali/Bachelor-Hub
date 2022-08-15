package com.nurnobishanto.bachelorhub.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.nurnobishanto.bachelorhub.Admin.AdminHomeActivity;
import com.nurnobishanto.bachelorhub.AuthFragment.SigninFragment;
import com.nurnobishanto.bachelorhub.MainActivity;
import com.nurnobishanto.bachelorhub.Models.User;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;

public class AuthActivity extends AppCompatActivity {

    private User mUser = null;
    private boolean mLoggedIn = false;
    private MyNetworkReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mNetworkReceiver = new MyNetworkReceiver(this);
        mUser = SharedPrefManager.getInstance(this).getUser();
        mLoggedIn = SharedPrefManager.getInstance(this).getUserIsLoggedIn();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SigninFragment()).commit();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,
                new SigninFragment()).commit();
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //===============================================| onStart(), onPause(), onResume(), onStop()
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //====================================================| To Check Firebase Authentication
        if (mLoggedIn && mUser != null) {
            Intent intent;//For login to clear this screen for that did not back this screen
            if(mUser.getUserEmail().equals("bachelorhub.info@gmail.com")){
                intent = new Intent(this, AdminHomeActivity.class);
            }else {
                intent = new Intent(this, EditProfileActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //For login to clear this screen for that did not back this screen
            startActivity(intent);
        }

    }

}