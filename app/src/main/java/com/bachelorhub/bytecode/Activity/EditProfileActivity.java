package com.bachelorhub.bytecode.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.bachelorhub.bytecode.MainActivity;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.Models.UserViewModel;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.ConstantKey;
import com.bachelorhub.bytecode.utils.LocaleHelper;
import com.bachelorhub.bytecode.utils.Network;
import com.bachelorhub.bytecode.utils.PermissionUtility;
import com.bachelorhub.bytecode.utils.Utility;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int RESULT_LOAD_IMAGE = 91;
    private static final int REQUEST_TAKE_PHOTO = 92;
    private static final int CAMERA_REQUEST = 1888;

    //Runtime Permissions
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private PermissionUtility mPermissions;

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;

    private String currentPhotoPath;
    private String userVerify = null;
    private String verifyMethod = null;
    private String verifyKey = null;

    private String mImageUrl = null;
    private String mPhone = null;
    private String mAuthId = null;
    //private String mToken = null;

    private ArrayAdapter adapter;
    private ImageView userImageUrl;
    private EditText userFullName, userPhoneNumber, userOccupation, userEmail, userBirthDate, userAddress, userReligion, userNidNumber;
    private TextInputLayout layoutName, layoutPhone, religionLay, layoutNId;
    private RadioGroup userGroup, genderGroup;
    private RadioButton userRender, userOwner, male, female;
    private Spinner userRelation;

    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mProgress = new ProgressDialog(this);
        mNetworkReceiver = new MyNetworkReceiver(this);
        mPermissions = new PermissionUtility(this, PERMISSIONS); //Runtime permissions

        //===============================================| findViewById
        userImageUrl = (ImageView) findViewById(R.id.userImageUrl);

        userGroup = (RadioGroup) findViewById(R.id.userGroup);
        userRender = (RadioButton) findViewById(R.id.userRender);
        userOwner = (RadioButton) findViewById(R.id.userOwner);
        genderGroup = (RadioGroup) findViewById(R.id.userGender);
        male = (RadioButton) findViewById(R.id.userMale);
        female = (RadioButton) findViewById(R.id.userFemale);

        userFullName = (EditText) findViewById(R.id.userFullName);
        layoutName = (TextInputLayout) findViewById(R.id.layoutUserFullName);

        userReligion = (EditText) findViewById(R.id.userReligion);
        religionLay = (TextInputLayout) findViewById(R.id.userReligionLay);

        userRelation = (Spinner) findViewById(R.id.userRelation);
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.relation_array));
        userRelation.setAdapter(adapter);

        userPhoneNumber = (EditText) findViewById(R.id.userPhoneNumber);
        layoutPhone = (TextInputLayout) findViewById(R.id.layoutUserPhoneNumber);

        userNidNumber = (EditText) findViewById(R.id.userNidNumber);
        layoutNId = (TextInputLayout) findViewById(R.id.layoutUserNidNumber);

        userOccupation = (EditText) findViewById(R.id.userOccupation);
        userEmail = (EditText) findViewById(R.id.userEmail);

        userBirthDate = (EditText) findViewById(R.id.userBirthDate);
        userAddress = (EditText) findViewById(R.id.userAddress);
        Button saveButton = (Button) findViewById(R.id.saveButton);

        userImageUrl.setOnClickListener(new ActionHandler());
        userBirthDate.setOnClickListener(new ActionHandler());
        saveButton.setOnClickListener(new ActionHandler());

        //===============================================| Getting SharedPreferences
        mAuthId = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Get UUID from FirebaseAuth
        if (SharedPrefManager.getInstance(EditProfileActivity.this).getUserAuthId() != null) {
            mAuthId = SharedPrefManager.getInstance(EditProfileActivity.this).getUserAuthId();
        }
        mPhone = SharedPrefManager.getInstance(EditProfileActivity.this).getPhoneNumber();
        userPhoneNumber.setText(mPhone);

        //===============================================| Receive the data and observe the data from view model
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class); //Initialize view model

        if (mAuthId != null) {
            getUserData(mAuthId);
        }

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

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }*/

    //Runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            dispatchTakePictureIntent();
            Log.d(TAG, "Permission granted 2");
        }
    }

    //===============================================| Fetch/Get from Firebase Database
    private void getUserData(String mAuthId) {
        mProgress = Utility.showProgressDialog(EditProfileActivity.this, getResources().getString(R.string.progress), false);

        mUserViewModel.getUser(mAuthId).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    String[] isOwnerSplit = user.getIsUserOwner().split("#");
                    String[] occupationSplit = user.getUserOccupation().split("#");
                    String[] relationSplit = user.getUserRelation().split("#");

                    mImageUrl = user.getUserImageUrl();
                    if (!mImageUrl.equals("")) {
                        Picasso.get().load(user.getUserImageUrl()).into((userImageUrl));
                    }
                    //Glide.with(ProfileActivity.this).asBitmap().load(user.getUserImageUrl()).into(userImageUrl);
                    userFullName.setText(user.getUserFullName());
                    if(relationSplit.length > 0) {
                        userRelation.setSelection(adapter.getPosition(relationSplit[0]));
                    }
                    if(relationSplit.length > 1){
                        userNidNumber.setText(relationSplit[1]);
                    }
                    if (occupationSplit.length > 0) {
                        userOccupation.setText(occupationSplit[0]);
                    }
                    if (isOwnerSplit.length > 1) {
                        userReligion.setText(occupationSplit[1]);
                    }
                    userEmail.setText(user.getUserEmail());
                    userPhoneNumber.setText(user.getUserPhoneNumber());
                    userBirthDate.setText(user.getUserBirthDate());
                    userAddress.setText(user.getUserAddress());
                    userVerify = user.getUserVerify();
                    verifyMethod = user.getVerifyMethod();
                    verifyKey = user.getVerifyKey();
                    if (isOwnerSplit.length > 0) {
                        if (isOwnerSplit[0].equals("Renter")) {
                            userRender.setChecked(true);
                        } else {
                            userOwner.setChecked(true);
                        }
                    }
                    if (isOwnerSplit.length > 1) {
                        if (isOwnerSplit[1].equals("Male")) {
                            male.setChecked(true);
                        } else {
                            female.setChecked(true);
                        }
                    }
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                }
            }
        });
    }

    //===============================================| Insert into Firebase Database
    private void storeToDatabase(String mAuthId, String name, String relation, String occupation, String email, String phone, String birth, String address, String isUserOwner, String mImageUrl, String mToken) {
        User obj = new User(mAuthId, name, relation, occupation, email, phone, birth, address, isUserOwner, mImageUrl, mToken, userVerify, verifyMethod, verifyKey);
        SharedPrefManager.getInstance(EditProfileActivity.this).saveUser(obj);
        mUserViewModel.storeUser(obj);
        mUserViewModel.storeUser(obj).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result.equals("success")) {
                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                    finish();
                }
                Utility.dismissProgressDialog(mProgress);
            }
        });
    }

    //===============================================| Birthday date-picker
    private void birthDate() {
        DatePicker picker = new DatePicker(this);
        int curYear = picker.getYear();
        int curMonth = picker.getMonth() + 1;
        int curDayOfMonth = picker.getDayOfMonth();
        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                userBirthDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, curYear, curMonth, curDayOfMonth);
        pickerDialog.show();
    }

    //====================================================| Camera and Gallery AlertDialog
    public void cameraGalleryAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_photo_option, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();

        final AlertDialog dialog = builder.show();

       /* ((ImageButton) view.findViewById(R.id.camera_id)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //Runtime Permissions
                if(mPermissions.arePermissionsEnabled()){
                    dispatchTakePictureIntent();
                    Log.d(TAG, "Permission granted 1");
                } else {
                    mPermissions.requestMultiplePermissions();

                }

                dialog.dismiss();
            }
        });*/
        ((ImageButton) view.findViewById(R.id.gallery_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE);
                dialog.dismiss();
            }
        });
    }

    //----------------------------------------------| Take a photo with a camera app
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.zaaibo.tolet", photoFile);
                    Log.d(TAG, "Image Uri: " + photoURI);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir/* directory */);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //===============================================| Image gallery with CropImage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            //           uploadImageToStorage(imageUri);
//            Picasso.get()
//                    .load(imageUri)
//                    .into(userImageUrl);

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setOutputCompressQuality(10)
                    .start(EditProfileActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                uploadImageToStorage(uri);
                userImageUrl.setImageURI(uri);
            }
        }
        //----------------------------------------------| Take a photo with a camera app
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(new File(currentPhotoPath));
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setOutputCompressQuality(10).start(this);
        }
    }

    //===============================================| Save Image to Firebase Storage
    private void uploadImageToStorage(final Uri uri) {
        mProgress = Utility.showProgressDialog(EditProfileActivity.this, getResources().getString(R.string.progress), false);
        mUserViewModel.storeUserImage(uri, ConstantKey.USER_NODE, mAuthId).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                if (result != null) {
                    mImageUrl = result;
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.alertDialog(EditProfileActivity.this, "Did not store your phone");
                    Utility.dismissProgressDialog(mProgress);
                }
            }
        });
    }

    //===============================================| Click Events
    private class ActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.userImageUrl:
                    cameraGalleryAlertDialog();
                    break;
                case R.id.userBirthDate:
                    birthDate();
                    break;
                case R.id.saveButton:
                    String name = userFullName.getText().toString().trim();
                    String relation = userRelation.getSelectedItem().toString();
                    String occupation = userOccupation.getText().toString().trim();
                    String religion = userReligion.getText().toString().trim();
                    String email = userEmail.getText().toString().trim();
                    String phone = userPhoneNumber.getText().toString().trim();
                    String birth = userBirthDate.getText().toString().trim();
                    String nidNumber = userNidNumber.getText().toString().trim();
                    String address = userAddress.getText().toString().trim();

                    int selectedId = userGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) findViewById(selectedId);

                    int genderId = genderGroup.getCheckedRadioButtonId();
                    RadioButton genButton = findViewById(genderId);

                    String gender = genButton.getText().toString();
                    String isUserOwner = radioButton.getText().toString();
                    if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && mPhone != null) {
                        layoutName.setError("required!");
                        layoutPhone.setError("required!");
                    } else if (phone.length() != 11) {
                        layoutPhone.setError("length must be 11 digits");
                    } else if (mImageUrl == null) {
                        Utility.alertDialog(EditProfileActivity.this, getResources().getString(R.string.upload_photo));
                    } else if (mAuthId == null) {
                        Utility.alertDialog(EditProfileActivity.this, "User did not exist");
                    }/*  else if (relation.equals("Marital Status")) {
                        Utility.alertDialog(EditProfileActivity.this, "Please change your marital status");
                    } */
                    else if (nidNumber.equals("")){
                        layoutNId.setError("required");
                    }else {
                        if (Network.haveNetwork(EditProfileActivity.this)) {
                            isUserOwner = isUserOwner + "#" + gender;
                            occupation = occupation + "#" + religion;
                            relation = relation + "#" + nidNumber;
                            mProgress = Utility.showProgressDialog(EditProfileActivity.this, getResources().getString(R.string.progress), false);
                            SharedPrefManager.getInstance(EditProfileActivity.this).savePhoneAndLogInStatus(phone, true);
                            storeToDatabase(mAuthId, name, relation, occupation, email, phone, birth, address, isUserOwner, mImageUrl, "mToken");
                        } else {
                            Utility.alertDialog(EditProfileActivity.this, getString(R.string.network_unavailable));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
