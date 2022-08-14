package com.nurnobishanto.bachelorhub.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nurnobishanto.bachelorhub.Activity.ProfileDetailsActivity;
import com.nurnobishanto.bachelorhub.Models.User;
import com.nurnobishanto.bachelorhub.Models.UserViewModel;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.PermissionUtility;
import com.nurnobishanto.bachelorhub.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {



    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private UserViewModel mUserViewModel;


    String mAuthId;


    TextView userName, userRating;
    ImageView verifiedIcon;
    CircleImageView userPicture;
    CardView archive, favourite, applied;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgress = new ProgressDialog(getContext());
        mNetworkReceiver = new MyNetworkReceiver(requireContext());

        userName = view.findViewById(R.id.proDetailsName);
        userRating = view.findViewById(R.id.proDetailsRating);
        verifiedIcon = view.findViewById(R.id.proDetailsVerified);
        userPicture = view.findViewById(R.id.proDetailsPicture);
        archive = view.findViewById(R.id.proDetailsArchive);
        favourite = view.findViewById(R.id.proDetailsFavourite);
        applied = view.findViewById(R.id.proDetailsApplied);

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));
            }
        });
        userPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileDetailsActivity.class));
            }
        });

        //===============================================| Receive the data and observe the data from view model
        mUserViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class); //Initialize view model

        mAuthId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); //Get UUID from FirebaseAuth
        if (SharedPrefManager.getInstance(getContext()).getUserAuthId() != null) {
            mAuthId = SharedPrefManager.getInstance(getContext()).getUserAuthId();
        }
        if(mAuthId != null){
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
                    if(userPicture != null) {
                        Picasso.get().load(user.getUserImageUrl()).into(userPicture);
                    }else{
                        Picasso.get().load(R.mipmap.ic_launcher).into(userPicture);
                    }
                    //Glide.with(ProfileActivity.this).asBitmap().load(user.getUserImageUrl()).into(userImageUrl);
                    userName.setText(user.getUserFullName());
                    //userRating.setText(user.getUserRating()); TODO--Work later
                    if(user.getUserVerify().equals("Verified")){
                        verifiedIcon.setVisibility(View.VISIBLE);
                    }else{
                        verifiedIcon.setVisibility(View.GONE);
                    }
//                    userRelation.setText(user.getUserRelation());
//                    userOccupation.setText(user.getUserOccupation());
//                    userEmail.setText(user.getUserEmail());
//                    userPhoneNumber.setText(user.getUserPhoneNumber());
//                    userBirthDate.setText(user.getUserBirthDate());
//                    userAddress.setText(user.getUserAddress());
//                    userGroup.setText(user.getIsUserOwner());
                }
                Utility.dismissProgressDialog(mProgress);
            }
        });
    }
}