package com.bachelorhub.bytecode.Activity;

import static com.google.gson.reflect.TypeToken.get;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.Models.UserViewModel;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.PermissionUtility;
import com.bachelorhub.bytecode.utils.Utility;
import com.squareup.picasso.Picasso;

public class ProfileDetailsActivity extends AppCompatActivity {
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private PermissionUtility mPermissions;

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private String mImageUrl = null;
    private String mPhone = null;
    private String mAuthId = null;
    //private String mToken =


    private ImageView userImageUrl;
    private EditText userFullName, userPhoneNumber, userOccupation, userEmail, userBirthDate, userAddress;
    private TextInputLayout layoutName, layoutPhone;
    private EditText userGroup;
    private RadioButton userRender, userOwner;
    private EditText userRelation;

    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        mProgress = new ProgressDialog(ProfileDetailsActivity.this);
        mNetworkReceiver = new MyNetworkReceiver(ProfileDetailsActivity.this);

        mPermissions = new PermissionUtility(this, PERMISSIONS); //Runtime permissions

        //===============================================| findViewById
        userImageUrl = (ImageView) findViewById(R.id.userImageUrl);

        userGroup = (EditText) findViewById(R.id.userGroup);
        userRender = (RadioButton) findViewById(R.id.userRender);
        userOwner = (RadioButton) findViewById(R.id.userOwner);

        userFullName = (EditText) findViewById(R.id.userFullName);
        layoutName = (TextInputLayout) findViewById(R.id.layoutUserFullName);

        userRelation = (EditText) findViewById(R.id.userRelation);


        userPhoneNumber = (EditText) findViewById(R.id.userPhoneNumber);
        layoutPhone = (TextInputLayout) findViewById(R.id.layoutUserPhoneNumber);

        userOccupation = (EditText) findViewById(R.id.userOccupation);
        userEmail = (EditText) findViewById(R.id.userEmail);
        userBirthDate = (EditText) findViewById(R.id.userBirthDate);
        userAddress = (EditText) findViewById(R.id.userAddress);


        //===============================================| Getting SharedPreferences
        mAuthId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Get UUID from FirebaseAuth
        if (SharedPrefManager.getInstance(this).getUserAuthId() != null) {
            mAuthId = SharedPrefManager.getInstance(this).getUserAuthId();
        }
        mPhone = SharedPrefManager.getInstance(this).getPhoneNumber();
        userPhoneNumber.setText(mPhone);

        //===============================================| Receive the data and observe the data from view model
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class); //Initialize view model

        if (mAuthId != null) {
            getUserData(mAuthId);
        }
    }

    //===============================================| Fetch/Get from Firebase Database
    private void getUserData(String mAuthId) {
        mProgress = Utility.showProgressDialog(ProfileDetailsActivity.this, getResources().getString(R.string.progress), false);

        mUserViewModel.getUser(mAuthId).observe(ProfileDetailsActivity.this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mImageUrl = user.getUserImageUrl();
                    if (!mImageUrl.equals("")) {
                        Picasso.get().load(user.getUserImageUrl()).into((userImageUrl));
                    }
                    //Glide.with(ProfileActivity.this).asBitmap().load(user.getUserImageUrl()).into(userImageUrl);
                    userFullName.setText(user.getUserFullName());

                    userRelation.setText(user.getUserRelation());
                    userOccupation.setText(user.getUserOccupation());
                    userEmail.setText(user.getUserEmail());
                    userPhoneNumber.setText(user.getUserPhoneNumber());
                    userBirthDate.setText(user.getUserBirthDate());
                    userAddress.setText(user.getUserAddress());
                    userGroup.setText(user.getIsUserOwner());
                }
                Utility.dismissProgressDialog(mProgress);
            }
        });
    }
}
