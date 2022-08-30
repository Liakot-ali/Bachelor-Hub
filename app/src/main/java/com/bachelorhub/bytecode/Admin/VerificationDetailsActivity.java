package com.bachelorhub.bytecode.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelorhub.bytecode.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerificationDetailsActivity extends AppCompatActivity {

    ImageView nidFrontPic, nidBackPic, verifyUserPic;
    CircleImageView profilePic;

    TextView name, userId, email, phone, address, nidNumber, birthDate;
    Button approveBtn, pendingBtn, rejectBtn;
    String userName, userAuthId, userPhone, userEmail, userAddress, userNidNumber, userBirthFate, userNidFrontPic, userNidBackPic, userVerifyPic, userProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vefication_details);
        InitializeAll();
    }

    private void InitializeAll() {

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

        approveBtn = findViewById(R.id.adminVerifyApproveBtn);
        pendingBtn = findViewById(R.id.adminVerifyPendingBtn);
        rejectBtn = findViewById(R.id.adminVerifyRejectBtn);

    }
}