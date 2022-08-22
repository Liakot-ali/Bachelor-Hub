package com.bachelorhub.bytecode.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;


import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bachelorhub.bytecode.MainActivity;
import com.bachelorhub.bytecode.Models.PostAd;
import com.bachelorhub.bytecode.Models.PostAdViewModel;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.ConstantKey;
import com.bachelorhub.bytecode.utils.LocaleHelper;
import com.bachelorhub.bytecode.utils.Network;
import com.bachelorhub.bytecode.utils.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class PostAdActivity extends AppCompatActivity {

    private static final String TAG = "PostAdActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 515;
    private static final int RESULT_LOAD_IMAGE = 1;
    private final ArrayList<String> postImageUri = new ArrayList<>();
    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private int imageCounter = 0;
    private Button addPostImageBtn;
    private EditText name, email, mobile, price, size, addr, desc;
    private CheckBox isMobile;
    private Spinner property, renter, beds, baths, location;
    private LinearLayout checkboxGroup;
    private TableRow imgGroup;

    private PostAdViewModel mPostAdViewModel;
    private User mUser;
    private LatLng mLatLng;
    private DatabaseReference mDatabaseRef;
    private String myAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);

        getSupportActionBar().hide();
        mNetworkReceiver = new MyNetworkReceiver(this);

        //===============================================| Receive the data and observe the data from view model
        mPostAdViewModel = ViewModelProviders.of(this).get(PostAdViewModel.class); //Initialize view model

        mUser = SharedPrefManager.getInstance(PostAdActivity.this).getUser();
        mLatLng = SharedPrefManager.getInstance(PostAdActivity.this).getCurrentLatLng();

        myAddress = Utility.getAddress(this, mLatLng);
//        Utility.alertDialog(PostAdActivity.this, myAddress);
        Log.e("UserInfo", "onPostAdActivity: Address " + myAddress + "LatLang " + mLatLng + "Name " + mUser.getUserFullName() + "Email " + mUser.getUserEmail() + "Phone " + mUser.getUserPhoneNumber());


        //====================================================| findViewById Initialing
        this.name = (EditText) findViewById(R.id.owner_name);
        name.setText(mUser.getUserFullName());
        this.email = (EditText) findViewById(R.id.owner_email);
        email.setText(mUser.getUserEmail());
        this.mobile = (EditText) findViewById(R.id.owner_mobile);
        mobile.setText(mUser.getUserPhoneNumber());//user.getUserPhoneNumber()
        this.isMobile = (CheckBox) findViewById(R.id.hide_mobile);
        this.property = (Spinner) findViewById(R.id.property_type);
        this.renter = (Spinner) findViewById(R.id.renter_type);
        this.price = (EditText) findViewById(R.id.rent_price);
        this.beds = (Spinner) findViewById(R.id.bedrooms);
        this.baths = (Spinner) findViewById(R.id.bathrooms);
        this.size = (EditText) findViewById(R.id.square_footage);
        this.checkboxGroup = (LinearLayout) findViewById(R.id.checkbox_group); //https://stackoverflow.com/questions/24322022/getting-multiple-value-of-checked-check-boxes-in-an-array
        this.location = (Spinner) findViewById(R.id.location);
        this.addr = (EditText) findViewById(R.id.address);
        this.desc = (EditText) findViewById(R.id.description);
        this.imgGroup = (TableRow) findViewById(R.id.image_group);

        this.addr.setText(myAddress);
        ((Button) findViewById(R.id.add_post_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //------------------------------------------------------
                String ownerName = name.getText().toString().trim();
                String ownerEmail = email.getText().toString().trim();
                String ownerMobile = mobile.getText().toString().trim();
                String isOwnerMobileHide = "false";
                if (isMobile.isChecked()) {
                    isOwnerMobileHide = "true";
                }
                String propertyType = property.getSelectedItem().toString();
                String renterType = renter.getSelectedItem().toString();
                String rentPrice = price.getText().toString().trim();
                String bedrooms = beds.getSelectedItem().toString();
                String bathrooms = baths.getSelectedItem().toString();
                String squareFootage = size.getText().toString().trim();
                String amenities = "";
                for (int i = 0; i < checkboxGroup.getChildCount(); i++) {
                    CheckBox checkbox = (CheckBox) checkboxGroup.getChildAt(i);
                    if (checkbox.isChecked()) {
                        amenities += checkbox.getText().toString() + ", ";
                    }
                }
                //Log.d(TAG, String.valueOf(getCheckboxText));

                String selectLocation = location.getSelectedItem().toString();
                String address = addr.getText().toString().trim();
                String description = desc.getText().toString().trim();

                Log.d(TAG, "====" + String.valueOf(postImageUri.size()));

                if (Network.haveNetwork(PostAdActivity.this)) {
                    if (postImageUri.size() > 0 && !address.isEmpty() && mUser != null) {
                        if (mUser.getIsUserOwner().equals("Owner")) {
                            mDatabaseRef = FirebaseDatabase.getInstance().getReference(ConstantKey.USER_POST_NODE);
                            String key = mDatabaseRef.push().getKey();
                            LatLng latLng = SharedPrefManager.getInstance(PostAdActivity.this).getCurrentLatLng();
                            String latitude = SharedPrefManager.getInstance(PostAdActivity.this).getCurrentLatitude();
                            String longitude = SharedPrefManager.getInstance(PostAdActivity.this).getCurrentLongitude();
                            PostAd post = new PostAd(mUser.getUserAuthId(), mUser.getUserToken(), ownerName, ownerEmail, ownerMobile, isOwnerMobileHide, propertyType, renterType, rentPrice, bedrooms, bathrooms, squareFootage, amenities, selectLocation, address, latitude, longitude, description, Arrays.toString(postImageUri.toArray()), "", key);
                            mProgress = Utility.showProgressDialog(PostAdActivity.this, getResources().getString(R.string.progress), false);
                            storeToDatabase(post);
                        } else {
                            Utility.alertDialog(PostAdActivity.this, getResources().getString(R.string.msg_register_user));
                        }
                    } else {
                        Utility.alertDialog(PostAdActivity.this, getResources().getString(R.string.msg_photo_add));
                    }
                } else {
                    Utility.alertDialog(PostAdActivity.this, getString(R.string.network_unavailable));
                }

                //------------------------------------------------------
            }
        });

        //====================================================| Add Image
        this.addPostImageBtn = (Button) findViewById(R.id.add_post_image_btn);
        addPostImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser.getIsUserOwner().equals("Owner")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                } else {
                    Utility.alertDialog(PostAdActivity.this, getResources().getString(R.string.msg_register_user));
                }
            }
        });

    }


    //===============================================| Language Change
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
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

    //====================================================| For Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setOutputCompressQuality(10)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                //uploadImageToStorage(uri);
                //userImageUrl.setImageURI(uri);

                if (imageCounter >= 0 && imageCounter < 5) {
                    //Dynamically ImageView set in TableLayout
                    ImageView img = new ImageView(this);
                    img.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    img.setBackground(getResources().getDrawable(R.drawable.shape_image_border));
                    //img.setId(R.id.image+imageCounter);
                    imgGroup.addView(img);
                    img.setImageURI(uri);
                    try {
                        mProgress = Utility.showProgressDialog(PostAdActivity.this, getResources().getString(R.string.progress), false);
                        storeImage(uri, mUser.getUserAuthId());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Utility.alertDialog(PostAdActivity.this, getResources().getString(R.string.msg_range));
                    }
                } else {
                    addPostImageBtn.setVisibility(View.GONE);
                }
                imageCounter++;
            }
        }
    }


    //===============================================| Insert into Firebase Database
    private void storeToDatabase(PostAd post) {

        mPostAdViewModel.storePostAd(post).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result.equals("success")) {
                    startActivity(new Intent(PostAdActivity.this, MainActivity.class));
                    finish();
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                }
            }
        });
    }

    //===============================================| Store Image into Firebase Storage
    private void storeImage(Uri uri, String mAuthId) {
        mPostAdViewModel.storeImage(uri, ConstantKey.USER_POST_NODE, mAuthId + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date())).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    postImageUri.add(result);
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}