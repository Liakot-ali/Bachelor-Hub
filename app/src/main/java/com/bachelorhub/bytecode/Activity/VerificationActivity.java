package com.bachelorhub.bytecode.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bachelorhub.bytecode.MainActivity;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.utils.ConstantKey;
import com.bachelorhub.bytecode.utils.Utility;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import okio.Timeout;

public class VerificationActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 10;
    public static final int PICTURE_REQUEST_CODE = 15;
    public static final String TAG = "VerificationActivity";
    EditText transIdET;
    ImageView nidFront, nidBack, userPicture, copyNumber;
    Button takeFront, takeBack, takeUserPic, verifyBtn;
    TextView number, transIdHelper;

    Uri frontImageUri, backImageUri, userPicUri;
    private Spinner spinner;
    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress;
    private EditText editText;
    private Button sentButton, goHome;
    private TextView status, method, key;
    private DatabaseReference reference;
    private FirebaseUser fuser;
    private String userid;
    private TextInputLayout method_key_layout;

    String buttonIdc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userid = fuser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(userid);

        mNetworkReceiver = new MyNetworkReceiver(this);
        copyNumber = findViewById(R.id.verifyCopyNumber);
        transIdET = findViewById(R.id.verifyTransId);
        transIdHelper = findViewById(R.id.verifyTransIdHelper);
        nidFront = findViewById(R.id.verifyNIDFrontPic);
        nidBack = findViewById(R.id.verifyNIDBackPic);
        userPicture = findViewById(R.id.verifyUserPic);
        takeFront = findViewById(R.id.verifyTakeNidFrontPic);
        takeBack = findViewById(R.id.verifyTakeNidBackPic);
        verifyBtn = findViewById(R.id.verifyVerifyBtn);
        takeUserPic = findViewById(R.id.verifyTakeUserPic);
        number = findViewById(R.id.verifySendMoneyNumber);


        spinner = (Spinner) findViewById(R.id.method_spinner);
        editText = (EditText) findViewById(R.id.method_key);
        sentButton = (Button) findViewById(R.id.sent_button);
        goHome = (Button) findViewById(R.id.goHome);
        status = (TextView) findViewById(R.id.status);
        method = (TextView) findViewById(R.id.method);
        key = (TextView) findViewById(R.id.key);
        method_key_layout = findViewById(R.id.method_key_layout);

        sentButton.setOnClickListener(new VerificationActivity.ActionHandler());
        goHome.setOnClickListener(new VerificationActivity.ActionHandler());
        takeFront.setOnClickListener(new VerificationActivity.ActionHandler());
        takeBack.setOnClickListener(new VerificationActivity.ActionHandler());
        verifyBtn.setOnClickListener(new VerificationActivity.ActionHandler());
        takeUserPic.setOnClickListener(new VerificationActivity.ActionHandler());
        copyNumber.setOnClickListener(new VerificationActivity.ActionHandler());

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ConstantKey.verifyMethodAre);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition("National ID")); //set item by default

        getUserInformation();

    }

    //===============================================| Click Events
    private class ActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            transIdET.clearFocus();
            switch (view.getId()) {
                case R.id.verifyTakeNidFrontPic:
                    buttonIdc = "frontNid";
                    openCamera();
                    break;
                case R.id.verifyTakeNidBackPic:
                    buttonIdc = "backNid";
                    openCamera();
                    break;
                case R.id.verifyTakeUserPic:
                    buttonIdc = "userPic";
                    openCamera();
                    break;
                case R.id.verifyVerifyBtn:
                    //TODO check validity and submit to admin
                    if(checkValidity()){
                        Toast.makeText(VerificationActivity.this, "Everything is all Ok", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.verifyCopyNumber:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Send money number", number.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(VerificationActivity.this, "Copy to clipboard", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.sent_button:

                    mProgress = Utility.showProgressDialog(VerificationActivity.this, getResources().getString(R.string.progress), false);

                    String method = spinner.getSelectedItem().toString();
                    String key = editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(key)) {
                        Map<String, Object> userInfo = new HashMap<>();

                        userInfo.put("userVerify", "Pending");
                        userInfo.put("verifyMethod", method);
                        userInfo.put("verifyKey", editText.getText().toString());

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


    private void getUserInformation() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


                    if (map.get("userVerify") != null) {
                        status.setVisibility(View.VISIBLE);
                        status.setText(map.get("userVerify").toString());
                        goHome.setVisibility(View.VISIBLE);
                        if (map.get("userVerify").equals("Verified")) {
                            sentButton.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            method_key_layout.setVisibility(View.GONE);
                            spinner.setVisibility(View.GONE);
                        } else {
                            sentButton.setVisibility(View.VISIBLE);
                            editText.setVisibility(View.VISIBLE);
                            method_key_layout.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                        }
                        if (map.get("verifyKey") != null) {
                            key.setVisibility(View.VISIBLE);
                            key.setText(map.get("verifyKey").toString());
                            editText.setText(map.get("verifyKey").toString());
                        }
                        if (map.get("verifyMethod") != null) {
                            method.setVisibility(View.VISIBLE);
                            method.setText(map.get("verifyMethod").toString());
                        }


                    } else {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length <= 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Camera permission is needed", Toast.LENGTH_SHORT).show();
            } else {
                openCamera();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                if(buttonIdc.equals("frontNid")) {
                    frontImageUri = result.getUri();
                    nidFront.setImageURI(frontImageUri);
                }else if(buttonIdc.equals("backNid")){
                    backImageUri = result.getUri();
                    nidBack.setImageURI(backImageUri);
                }else{
                    userPicUri = result.getUri();
                    userPicture.setImageURI(userPicUri);
                }
            } else {
                Log.e(TAG, "onActivityResult: " + result.getError());
            }
        }

    }

    public void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            if(buttonIdc.equals("frontNid") || buttonIdc.equals("backNid")) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3, 2)
                        .setOutputCompressQuality(20)
                        .start(this);
            }else{
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setOutputCompressQuality(20)
                        .start(this);
            }
        }
    }

    public boolean checkValidity(){
        String transId = transIdET.getText().toString().trim();

        if(transId.equals("")){
            transIdET.requestFocus();
            transIdHelper.setText("* write your transaction ID here");
            Utility.alertDialog(this, "Complete the transaction first. Then put the transaction ID");
            return false;
        }
        if(frontImageUri == null){
            transIdHelper.setText("*");
            Utility.alertDialog(this, "Upload your NID front side pic");
            return false;
        }
        if(backImageUri == null){
            transIdHelper.setText("*");
            Utility.alertDialog(this, "Upload your NID back side pic");
            return false;
        }
        if(userPicUri == null){
            transIdHelper.setText("*");
            Utility.alertDialog(this, "Upload your picture");
            return false;
        }
            return true;
    }


}