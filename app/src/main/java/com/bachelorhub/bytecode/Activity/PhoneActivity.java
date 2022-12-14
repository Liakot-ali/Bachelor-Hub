package com.bachelorhub.bytecode.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bachelorhub.bytecode.MainActivity;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.ConstantKey;
import com.bachelorhub.bytecode.utils.Utility;

public class PhoneActivity extends AppCompatActivity {

    private static final String TAG = "PhoneActivity";

    private MyNetworkReceiver mNetworkReceiver;

    private Spinner spinner;
    private EditText editText;
    private Button sentButton;
    private TextView email;

    private ProgressDialog mProgress;
    private User mUser = null;
    private boolean mLoggedIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R .layout.activity_phone);
        mNetworkReceiver = new MyNetworkReceiver(this);
        //mProgress = new ProgressDialog(this);
        mUser = SharedPrefManager.getInstance(this).getUser();
        mLoggedIn = SharedPrefManager.getInstance(this).getUserIsLoggedIn();

        //===============================================| findViewById
        spinner = (Spinner) findViewById(R.id.country_code_spinner);
        editText = (EditText) findViewById(R.id.user_phone_number);
        sentButton = (Button) findViewById(R.id.sent_button);
        email = (TextView) findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneActivity.this, AuthActivity.class));
                finish();
            }
        });
        sentButton.setOnClickListener(new ActionHandler());

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ConstantKey.countryAreaCodes);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition("+880")); //set item by default
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
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //For login to clear this screen for that did not back this screen
            startActivity(intent);
        }

    }

    //===============================================| Click Events
    private class ActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sent_button:

                    mProgress = Utility.showProgressDialog(PhoneActivity.this, getResources().getString( R.string.progress), false);

                    String code = spinner.getSelectedItem().toString();
                    String phone = editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(phone)) {
                        Utility.dismissProgressDialog(mProgress);
                        SharedPrefManager.getInstance(PhoneActivity.this).savePhoneAndLogInStatus(code+ Utility.removeZero(phone), false);
                        Intent intent = new Intent(PhoneActivity.this, PhoneVerifyActivity.class);
                        intent.putExtra(ConstantKey.USER_PHONE_KEY, code+Utility.removeZero(phone));
                        startActivity(intent);
                    } else {
                        Utility.dismissProgressDialog(mProgress);
                        editText.setError(getResources().getString( R.string.enter_mobile_number));
                        editText.requestFocus();
                        return;
                    }

                    break;
                default:
                    break;
            }
        }
    }
}
