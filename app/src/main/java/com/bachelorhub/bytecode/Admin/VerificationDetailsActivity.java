package com.bachelorhub.bytecode.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelorhub.bytecode.R;
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

        Picasso.get().load(userProfilePic).placeholder(R.mipmap.ic_launcher).into(profilePic);
        Picasso.get().load(userNidFrontPic).placeholder(R.mipmap.ic_launcher).into(nidFrontPic);
        Picasso.get().load(userNidBackPic).placeholder(R.mipmap.ic_launcher).into(nidBackPic);
        Picasso.get().load(userVerifyPic).placeholder(R.mipmap.ic_launcher).into(verifyUserPic);

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