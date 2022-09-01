package com.bachelorhub.bytecode.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.bachelorhub.bytecode.Activity.AuthActivity;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Session.SharedPrefManager;

public class AdminHomeActivity extends AppCompatActivity {

    ImageView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        logOut = findViewById(R.id.adminHomeLogout);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AdminHomeActivity.this)
                        .setTitle(R.string.about_title)
                        .setMessage(R.string.msg_sign_out)
                        .setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                SharedPrefManager.getInstance(AdminHomeActivity.this).deleteCurrentSession();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(AdminHomeActivity.this, AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //For login to clear this screen for that did not back this screen
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.msg_neg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
//        getSupportActionBar().setTitle("Admin Home");

//        Toolbar toolbar = findViewById(R.id.adminToolbar);
//        setSupportActionBar(toolbar);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.admin_logout:
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.admin_menu, menu);
//        return true;
//    }

    long currentTime = 0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - currentTime < 2000){
            super.onBackPressed();
            finish();
        }
        else {
            currentTime = System.currentTimeMillis();
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        }
    }

    public void Pending(View view) {
        startActivity(new Intent(AdminHomeActivity.this, PendingVerifyActivity.class));

    }

    public void verified(View view) {
        startActivity(new Intent(AdminHomeActivity.this, VerifiedActivity.class));
    }

    public void rejected(View view) {
        startActivity(new Intent(AdminHomeActivity.this, RejectedActivity.class));
    }
}