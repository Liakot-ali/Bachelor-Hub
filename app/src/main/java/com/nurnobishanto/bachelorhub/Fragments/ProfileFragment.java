package com.nurnobishanto.bachelorhub.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.nurnobishanto.bachelorhub.Activity.EditProfileActivity;
import com.nurnobishanto.bachelorhub.Models.User;
import com.nurnobishanto.bachelorhub.Models.UserViewModel;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.PermissionUtility;
import com.nurnobishanto.bachelorhub.utils.Utility;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {



    //Runtime Permissions
    private String[] PERMISSIONS = { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA };
    private PermissionUtility mPermissions;

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;



    private String mImageUrl = null;
    private String mPhone = null;
    private String mAuthId = null;
    //private String mToken = null;


    private ImageView userImageUrl;
    private EditText userFullName, userPhoneNumber, userOccupation, userEmail, userBirthDate, userAddress;
    private TextInputLayout layoutName, layoutPhone;
    private EditText userGroup;
    private RadioButton userRender, userOwner;
    private EditText userRelation;

    private UserViewModel mUserViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgress = new ProgressDialog(getContext());
        mNetworkReceiver = new MyNetworkReceiver(getContext());
        mPermissions = new PermissionUtility(getContext(), PERMISSIONS); //Runtime permissions

        //===============================================| findViewById
        userImageUrl = (ImageView) view.findViewById(R.id.userImageUrl);

        userGroup = (EditText) view.findViewById(R.id.userGroup);
        userRender = (RadioButton) view.findViewById(R.id.userRender);
        userOwner = (RadioButton) view.findViewById(R.id.userOwner);

        userFullName = (EditText) view.findViewById(R.id.userFullName);
        layoutName = (TextInputLayout) view.findViewById(R.id.layoutUserFullName);

        userRelation = (EditText) view.findViewById(R.id.userRelation);


        userPhoneNumber = (EditText) view.findViewById(R.id.userPhoneNumber);
        layoutPhone = (TextInputLayout) view.findViewById(R.id.layoutUserPhoneNumber);

        userOccupation = (EditText) view.findViewById(R.id.userOccupation);
        userEmail = (EditText) view.findViewById(R.id.userEmail);
        userBirthDate = (EditText) view.findViewById(R.id.userBirthDate);
        userAddress = (EditText) view.findViewById(R.id.userAddress);



        //===============================================| Getting SharedPreferences
        mAuthId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Get UUID from FirebaseAuth
        if (SharedPrefManager.getInstance(getContext()).getUserAuthId() != null) {
            mAuthId = SharedPrefManager.getInstance(getContext()).getUserAuthId();
        }
        mPhone = SharedPrefManager.getInstance(getContext()).getPhoneNumber();
        userPhoneNumber.setText(mPhone);

        //===============================================| Receive the data and observe the data from view model
        mUserViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class); //Initialize view model

        if (mAuthId != null) {
            getUserData(mAuthId);
        }

        return view;
    }
    //===============================================| Fetch/Get from Firebase Database
    private void getUserData(String mAuthId) {
        mProgress = Utility.showProgressDialog(getActivity(), getResources().getString( R.string.progress), false);

        mUserViewModel.getUser(mAuthId).observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mImageUrl = user.getUserImageUrl();
                    Picasso.get().load(user.getUserImageUrl()).into((userImageUrl));
                    //Glide.with(ProfileActivity.this).asBitmap().load(user.getUserImageUrl()).into(userImageUrl);
                    userFullName.setText(user.getUserFullName());

                    userRelation.setText(user.getUserRelation());
                    userOccupation.setText(user.getUserOccupation());
                    userEmail.setText(user.getUserEmail());
                    userPhoneNumber.setText(user.getUserPhoneNumber());
                    userBirthDate.setText(user.getUserBirthDate());
                    userAddress.setText(user.getUserAddress());
                    userGroup.setText(user.getIsUserOwner());
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                }
            }
        });
    }
}