package com.nurnobishanto.bachelorhub.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.nurnobishanto.bachelorhub.R;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Admin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_admin_home);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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