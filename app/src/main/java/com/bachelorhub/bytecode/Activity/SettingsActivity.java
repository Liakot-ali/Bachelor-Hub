package com.bachelorhub.bytecode.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.utils.LocaleHelper;

public class SettingsActivity extends AppCompatActivity {

    private MyNetworkReceiver mNetworkReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNetworkReceiver = new MyNetworkReceiver(this);

        //==========================================| Language change button
        Button bnBtn = (Button) findViewById(R.id.bangla_btn);
        bnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LocaleHelper.setLocale(SettingsActivity.this, "bn");
                Resources resources = context.getResources();
                reload();
            }
        });
        Button enBtn = (Button) findViewById(R.id.english_btn);
        enBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LocaleHelper.setLocale(SettingsActivity.this, "en");
                Resources resources = context.getResources();
                reload();
            }
        });
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}