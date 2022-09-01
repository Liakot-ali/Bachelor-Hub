package com.bachelorhub.bytecode.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelorhub.bytecode.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerificationDetailsActivity extends AppCompatActivity {

    ImageView nidFrontPic, nidBackPic, verifyUserPic;
    CircleImageView profilePic;

    TextView name, userId, email, phone, address, nidNumber, birthDate, transId;
    Button approveBtn, pendingBtn, rejectBtn;
    String userName, userAuthId, userPhone, userEmail, userAddress, userNidNumber, userTransId;
    String userBirthDate, userNidFrontPic, userNidBackPic, userVerifyPic, userProfilePic;
    String userStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vefication_details);
        InitializeAll();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(userAuthId).child("userVerify");

        pendingBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(VerificationDetailsActivity.this)
                    .setTitle("Alert !")
                    .setMessage("Are you sure you want to change the verification status for this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        reference.setValue("Pending");
                        Intent intent = new Intent(VerificationDetailsActivity.this, PendingVerifyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        });
        approveBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(VerificationDetailsActivity.this)
                    .setTitle("Alert !")
                    .setMessage("Are you sure you want to change the verification status for this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        reference.setValue("Verified");
                        Intent intent1 = new Intent(VerificationDetailsActivity.this, VerifiedActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        });
        rejectBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(VerificationDetailsActivity.this)
                    .setTitle("Alert !")
                    .setMessage("Are you sure you want to change the verification status for this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        reference.setValue("Rejected");
                        Intent intent2 = new Intent(VerificationDetailsActivity.this, RejectedActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent2);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    private void InitializeAll() {

        userStatus = getIntent().getStringExtra("userStatus");
        userName = getIntent().getStringExtra("userName");
        userAuthId = getIntent().getStringExtra("userId");

        userPhone = getIntent().getStringExtra("userPhone");
        userEmail = getIntent().getStringExtra("userEmail");
        userAddress = getIntent().getStringExtra("userAddress");
        userNidNumber = getIntent().getStringExtra("userNidNumber");
        userBirthDate = getIntent().getStringExtra("userBirthDate");
        userNidFrontPic = getIntent().getStringExtra("userNidFrontPic");
        userNidBackPic = getIntent().getStringExtra("userNidBackPic");
        userVerifyPic = getIntent().getStringExtra("userVerifyPic");
        userProfilePic = getIntent().getStringExtra("userProfilePic");
        userTransId = getIntent().getStringExtra("userTransId");


        nidFrontPic = findViewById(R.id.adminVerifyFrontNidPic);
        nidBackPic = findViewById(R.id.adminVerifyBackNidPic);
        verifyUserPic = findViewById(R.id.adminVerifyUserPic);
        profilePic = findViewById(R.id.adminVerifyUserProfilePicture);

        name = findViewById(R.id.adminVerifyUserName);
        userId = findViewById(R.id.adminVerifyUserId);
        email = findViewById(R.id.adminVerifyEmail);
        phone = findViewById(R.id.adminVerifyPhoneNumber);
        address = findViewById(R.id.adminVerifyAddress);
        birthDate = findViewById(R.id.adminVerifyBirthDate);
        nidNumber = findViewById(R.id.adminVerifyNidCardNumber);
        transId = findViewById(R.id.adminVerifyTransId);

        approveBtn = findViewById(R.id.adminVerifyApproveBtn);
        pendingBtn = findViewById(R.id.adminVerifyPendingBtn);
        rejectBtn = findViewById(R.id.adminVerifyRejectBtn);
        switch (userStatus) {
            case "Pending":
                pendingBtn.setVisibility(View.GONE);
                approveBtn.setVisibility(View.VISIBLE);
                rejectBtn.setVisibility(View.VISIBLE);

                break;
            case "Verified":
                pendingBtn.setVisibility(View.VISIBLE);
                approveBtn.setVisibility(View.GONE);
                rejectBtn.setVisibility(View.VISIBLE);
                break;
            case "Rejected":
                pendingBtn.setVisibility(View.VISIBLE);
                approveBtn.setVisibility(View.VISIBLE);
                rejectBtn.setVisibility(View.GONE);
                break;
        }

        Log.e("VerificationDetailsAct", "InitializeAll: " + userNidFrontPic);
        Picasso.get().load(userProfilePic).placeholder(R.mipmap.ic_launcher).into(profilePic);
        if (userNidFrontPic != null && !userNidFrontPic.equals("National ID")) {
            Picasso.get().load(userNidFrontPic).placeholder(R.drawable.loading_nid).into(nidFrontPic);
        } else {
            nidFrontPic.setImageResource(R.drawable.no_photo);
        }
        if (userNidBackPic != null) {
            Picasso.get().load(userNidBackPic).placeholder(R.drawable.loading_nid).into(nidBackPic);
        } else {
            Picasso.get().load(R.drawable.no_photo).into(nidBackPic);
        }
        if (userVerifyPic != null) {
            Picasso.get().load(userVerifyPic).placeholder(R.drawable.loading).into(verifyUserPic);
        } else {
            verifyUserPic.setImageResource(R.drawable.no_photo);
        }

        name.setText(userName);
        userId.setText(userAuthId);
        email.setText("Email: " + userEmail);
        phone.setText("Phone Number: " + userPhone);
        address.setText("Address: " + userAddress);
        birthDate.setText("Birth Date: " + userBirthDate);
        nidNumber.setText("NID Number: " + userNidNumber);
        transId.setText("Transaction ID: " + userTransId);
    }
}