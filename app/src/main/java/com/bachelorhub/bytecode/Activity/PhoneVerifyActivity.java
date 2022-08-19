package com.bachelorhub.bytecode.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.ConstantKey;
import com.bachelorhub.bytecode.utils.LocaleHelper;
import com.bachelorhub.bytecode.utils.Utility;

import java.util.concurrent.TimeUnit;

public class PhoneVerifyActivity extends AppCompatActivity {

    private static final String TAG = "PhoneVerifyActivity";

    private MyNetworkReceiver mNetworkReceiver;

    private String phoneNumber;
    private EditText editText;
    private TextView phnNumber;
    private Button resend;

    private ProgressDialog mProgress;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        mNetworkReceiver = new MyNetworkReceiver(this);
        //mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        editText = (EditText) findViewById(R.id.user_verify_code);
        phnNumber = (TextView) findViewById(R.id.phone);
        ((Button) findViewById(R.id.verify_button)).setOnClickListener(new ActionHandler());
        resend = ((Button) findViewById(R.id.resend_button));
        resend.setOnClickListener(new ActionHandler());

        if (getIntent().getExtras() != null) {
            phoneNumber = getIntent().getStringExtra(ConstantKey.USER_PHONE_KEY);
            phnNumber.setText(phoneNumber+"\n Change Number");
            phnNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        initFireBaseCallbacks();

    }

    //===============================================| Language Change
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    //===============================================| onStart(), onPause(), onResume(), onStop()
    @Override
    protected void onStart() {
        super.onStart();
        if (phoneNumber != null) {
            mProgress = Utility.showProgressDialog(PhoneVerifyActivity.this, getResources().getString( R.string.progress), true);
            startPhoneNumberVerification(phoneNumber); //Set verified code to firebase phone authentication
        }
    }

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

    //===============================================| Click Events
    private class ActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.verify_button) {
                String code = editText.getText().toString().trim();
                if (TextUtils.isEmpty(code) || code.length() < 6) {
                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }
                verifyPhoneNumberWithCode(code);
            }
            if (view.getId() == R.id.resend_button) {
                if (phoneNumber != null && mResendToken != null) {
                    resendVerificationCode(phoneNumber, mResendToken);
                }
            }
        }
    }

    //====================================================| Auth initialization
    private void initFireBaseCallbacks() {
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                //signInWithPhoneAuthCredential(credential);
                String code = credential.getSmsCode();
                if (code != null) {
                    Utility.dismissProgressDialog(mProgress);
                    editText.setText(code);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Utility.dismissProgressDialog(mProgress);
                Log.e(TAG, e.getMessage());
                //showLoginActivity();
                Utility.alertDialog(PhoneVerifyActivity.this,e.getMessage());

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, e.getMessage());
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e(TAG, e.getMessage()); // The SMS quota for the project has been exceeded
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                //for low level version which doesn't do auto verification save the verification code and the token
                Log.e(TAG, verificationId);
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Utility.dismissProgressDialog(mProgress);
                //showLoginActivity();
                Utility.alertDialog(PhoneVerifyActivity.this,"Your Phone Number Verification is failed.Retry again!");
            }
        };
    }

    //====================================================| Verification Code
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,              // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,       // Activity (for callback binding)
                mCallBack);        // OnVerificationStateChangedCallbacks
        Utility.dismissProgressDialog(mProgress);



    }

    private void verifyPhoneNumberWithCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,            // Phone number to verify
                60,                  // Timeout duration
                TimeUnit.SECONDS,       // Unit of timeout
                this,           // Activity (for callback binding)
                mCallBack,             // OnVerificationStateChangedCallbacks
                token);                // ForceResendingToken from callbacks

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Utility.dismissProgressDialog(mProgress);
                            SharedPrefManager.getInstance(PhoneVerifyActivity.this).savePhoneAndLogInStatus(phoneNumber, true);
                            SharedPrefManager.getInstance(PhoneVerifyActivity.this).saveUserAuthId(user.getUid());
                            Intent intent = new Intent(PhoneVerifyActivity.this, EditProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Utility.dismissProgressDialog(mProgress);
                            Log.e(TAG, task.getException().getMessage());
                            Utility.alertDialog(PhoneVerifyActivity.this, task.getException().getMessage());
                        }
                    }
                });
    }
}
