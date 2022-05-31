package com.nurnobishanto.bachelorhub.Activity;

import androidx.annotation.NonNull;
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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nurnobishanto.bachelorhub.MainActivity;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.ConstantKey;
import com.nurnobishanto.bachelorhub.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {

    private Spinner spinner;
    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress;
    private EditText editText;
    private Button sentButton,goHome;
    private TextView status,method,key;
    private DatabaseReference reference;
    private FirebaseUser fuser;
    private String userid;
    private TextInputLayout method_key_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        userid = fuser.getUid();
        reference= FirebaseDatabase.getInstance().getReference("tolet_users").child(userid);

        mNetworkReceiver = new MyNetworkReceiver(this);
        spinner = (Spinner) findViewById(R.id.method_spinner);
        editText = (EditText) findViewById(R.id.method_key);
        sentButton = (Button) findViewById(R.id.sent_button);
        goHome = (Button) findViewById(R.id.goHome);
        status = (TextView) findViewById(R.id.status);
        method = (TextView) findViewById(R.id.method);
        key = (TextView) findViewById(R.id.key);
        method_key_layout =  findViewById(R.id.method_key_layout);

        sentButton.setOnClickListener(new VerificationActivity.ActionHandler());
        goHome.setOnClickListener(new VerificationActivity.ActionHandler());

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ConstantKey.verifyMethodAre);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition("National ID")); //set item by default

        getUserInformation();

    }

    private void getUserInformation() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {

                    Map<String,Object> map =(Map<String, Object>)dataSnapshot.getValue();


                    if(map.get("userVerify")!=null)
                    {
                        status.setVisibility(View.VISIBLE);
                        status.setText(map.get("userVerify").toString());
                        goHome.setVisibility(View.VISIBLE);
                        if (map.get("userVerify").equals("Verified")){
                            sentButton.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            method_key_layout.setVisibility(View.GONE);
                            spinner.setVisibility(View.GONE);
                        }else {
                            sentButton.setVisibility(View.VISIBLE);
                            editText.setVisibility(View.VISIBLE);
                            method_key_layout.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                        }
                        if(map.get("verifyKey")!=null){
                            key.setVisibility(View.VISIBLE);
                            key.setText(map.get("verifyKey").toString());
                            editText.setText(map.get("verifyKey").toString());
                        }
                        if(map.get("verifyMethod")!=null){
                            method.setVisibility(View.VISIBLE);
                            method.setText(map.get("verifyMethod").toString());
                        }


                    }else {
                        status.setVisibility(View.GONE);
                        method.setVisibility(View.GONE);
                        key.setVisibility(View.GONE);
                        goHome.setVisibility(View.GONE);
                        sentButton.setVisibility(View.VISIBLE);
                        editText.setVisibility(View.VISIBLE);
                        method_key_layout.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.VISIBLE);

                    }





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    //===============================================| Click Events
    private class ActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.sent_button:

                    mProgress = Utility.showProgressDialog(VerificationActivity.this, getResources().getString( R.string.progress), false);

                    String method = spinner.getSelectedItem().toString();
                    String key = editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(key)) {
                        Map<String, Object> userInfo = new HashMap<>();

                        userInfo.put("userVerify","Pending");
                        userInfo.put("verifyMethod",method);
                        userInfo.put("verifyKey",editText.getText().toString());

                        reference.updateChildren(userInfo);
                        Utility.dismissProgressDialog(mProgress);

                    } else {
                        Utility.dismissProgressDialog(mProgress);
                        editText.setError("Enter Verification Number");
                        editText.requestFocus();
                        return;
                    }

                    break;
                case R.id.goHome:
                    startActivity(new Intent(VerificationActivity.this, MainActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}