package com.bachelorhub.bytecode.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.bachelorhub.bytecode.Activity.AuthActivity;
import com.bachelorhub.bytecode.Activity.FavouritesActivity;
import com.bachelorhub.bytecode.Activity.ProfileDetailsActivity;
import com.bachelorhub.bytecode.Activity.SettingsActivity;
import com.bachelorhub.bytecode.Activity.VerificationActivity;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.Models.UserViewModel;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.Utility;
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
    TextView profileVerify, Faq, help, aboutUs, settings, logout;

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

        profileVerify = view.findViewById(R.id.profileVerify);
        Faq = view.findViewById(R.id.profileFAQ);
        help = view.findViewById(R.id.profileHelp);
        aboutUs = view.findViewById(R.id.profileAboutUs);
        settings = view.findViewById(R.id.profileSettings);
        logout = view.findViewById(R.id.profileLogOut);

        verifiedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Verified", Toast.LENGTH_SHORT).show();
            }
        });
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
        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(getActivity(), "Under Construction", Toast.LENGTH_SHORT).show();
            }
        });
        applied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(getActivity(), "Nothing know about this yet", Toast.LENGTH_SHORT).show();
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
            }
        });
        profileVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), VerificationActivity.class));
            }
        });
        Faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "FAQ Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Help Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "About us Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.about_title)
                        .setMessage(R.string.msg_sign_out)
                        .setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                SharedPrefManager.getInstance(getActivity()).deleteCurrentSession();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getActivity(), AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //For login to clear this screen for that did not back this screen
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.msg_neg, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });


        //===============================================| Receive the data and observe the data from view model
        mUserViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class); //Initialize view model

        mAuthId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(); //Get UUID from FirebaseAuth
        if (SharedPrefManager.getInstance(getContext()).getUserAuthId() != null) {
            mAuthId = SharedPrefManager.getInstance(getContext()).getUserAuthId();
        }
        if (mAuthId != null) {
            getUserData(mAuthId);
        }
        return view;
    }


    //===============================================| Fetch/Get from Firebase Database
    private void getUserData(String mAuthId) {
        mProgress = Utility.showProgressDialog(getActivity(), getResources().getString(R.string.progress), false);

        mUserViewModel.getUser(mAuthId).observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    if (!user.getUserImageUrl().equals("")) {
                        Picasso.get().load(user.getUserImageUrl()).placeholder(R.mipmap.ic_launcher).into(userPicture);
                    }
                    //Glide.with(ProfileActivity.this).asBitmap().load(user.getUserImageUrl()).into(userImageUrl);
                    userName.setText(user.getUserFullName());
                    //userRating.setText(user.getUserRating()); TODO--Work later
                    if (user.getUserVerify().equals("Verified")) {
                        verifiedIcon.setVisibility(View.VISIBLE);
                    } else {
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