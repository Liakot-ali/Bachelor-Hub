package com.bachelorhub.bytecode.AuthFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.bachelorhub.bytecode.Activity.PhoneActivity;
import com.bachelorhub.bytecode.Admin.AdminHomeActivity;
import com.bachelorhub.bytecode.MainActivity;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SigninFragment extends Fragment {

    private TextInputLayout emailInputLayout, passInputLayout;
    private TextInputEditText emailInput, passInput;
    private Button signin;
    private TextView signup, forget, phone;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);


        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        passInputLayout = view.findViewById(R.id.passlInputLayout);
        emailInput = view.findViewById(R.id.email);
        passInput = view.findViewById(R.id.password);
        signin = view.findViewById(R.id.signin);
        signup = view.findViewById(R.id.signup);
        forget = view.findViewById(R.id.forget);
        phone = view.findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PhoneActivity.class));
                getActivity().finish();
            }
        });


        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignUpFragment()).commit());
        forget.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new ForgotFragment()).commit());
        signin.setOnClickListener(v -> {
            if (Validate()) {
                Login(v);
            }
        });
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!emailInput.getText().toString().trim().matches(emailPattern)) {
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError("Valid email is required!");
                } else {
                    emailInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passInput.getText().toString().length() < 8) {
                    passInputLayout.setErrorEnabled(true);
                    passInputLayout.setError("Password must be at least 8 Character!");
                } else {
                    passInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void Login(View v) {
        //---Hide keyboard---
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        emailInput.clearFocus();
        passInput.clearFocus();
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Login");
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        mAuth.signInWithEmailAndPassword(emailInput.getText().toString(), passInput.getText().toString()).addOnCompleteListener((Activity) getContext(), (OnCompleteListener<AuthResult>) task -> {
            if (!task.isSuccessful()) {
                emailInput.setError(task.getException().getMessage());
                passInput.setError(task.getException().getMessage());
                dialog.dismiss();
            } else {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tolet_users").child(Objects.requireNonNull(mAuth.getUid()));
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dialog.dismiss();
                        User obj = snapshot.getValue(User.class);
//                        User obj = new User(mAuth.getUid(), "", "", "", emailInput.getText().toString(), "", "", "", "Owner", "", "","","","");
                        SharedPrefManager.getInstance(getActivity()).saveUser(obj);
                        SharedPrefManager.getInstance(getContext()).setUserIsLoggedIn(true);

                        if (emailInput.getText().toString().equals("bachelorhub.info@gmail.com")) {
                            startActivity(new Intent(getContext(), AdminHomeActivity.class));
                        } else {
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                        getActivity().finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("SignInFragment ", "onCancelled: " + error.getMessage());

                    }
                });
            }
        });

    }

    private boolean Validate() {
        if (emailInput.getText().toString().isEmpty()) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Email is required!");
            emailInput.requestFocus();
            return false;
        } else if (!emailInput.getText().toString().trim().matches(emailPattern)) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Valid email is required!");
            emailInput.requestFocus();
            return false;
        } else if (passInput.getText().toString().isEmpty()) {
            emailInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password is required!");
            passInput.requestFocus();
            return false;
        } else if (passInput.getText().toString().length() < 8) {
            emailInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password must be at least 8 Character!");
            passInput.requestFocus();
            return false;
        } else {
            passInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(false);
            return true;
        }

    }
}