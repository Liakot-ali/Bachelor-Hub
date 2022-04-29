package com.nurnobishanto.bachelorhub.AuthFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nurnobishanto.bachelorhub.R;

import java.util.Objects;


public class SignUpFragment extends Fragment {
    private TextInputLayout nameInputLayout,emailInputLayout,passInputLayout,confirmPassInputLayout,reflInputLayout;
    private TextInputEditText nameInput,emailInput,passInput,confirmPassInput,refInput;
    private Button signup;
    private TextView signin,forget;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        nameInputLayout=view.findViewById(R.id.nameInputLayout);
        emailInputLayout=view.findViewById(R.id.emailInputLayout);
        passInputLayout=view.findViewById(R.id.passlInputLayout);
        confirmPassInputLayout=view.findViewById(R.id.confirmPasslInputLayout);
        reflInputLayout=view.findViewById(R.id.reflInputLayout);
        nameInput=view.findViewById(R.id.name);
        emailInput=view.findViewById(R.id.email);
        passInput=view.findViewById(R.id.password);
        confirmPassInput=view.findViewById(R.id.confirmPassword);
        refInput=view.findViewById(R.id.refInput);
        signin=view.findViewById(R.id.signin);
        signup=view.findViewById(R.id.signup);
        forget=view.findViewById(R.id.forget);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validate()){

                    Register();
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new SigninFragment()).commit();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new ForgotFragment()).commit();
            }
        });
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nameInput.getText().toString().isEmpty()){
                    nameInputLayout.setErrorEnabled(true);
                    nameInputLayout.setError("Name is required!");
                }else {
                    nameInputLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!emailInput.getText().toString().trim().matches(emailPattern)){
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError("Valid email is required!");
                }else {
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
                if (passInput.getText().toString().length()<8){
                    passInputLayout.setErrorEnabled(true);
                    passInputLayout.setError("Password must be at least 8 Character!");
                }else {
                    passInputLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPassInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(passInput.getText()).toString().equals(confirmPassInput.getText().toString())){
                    confirmPassInputLayout.setErrorEnabled(false);
                }else {
                    confirmPassInputLayout.setErrorEnabled(true);
                    confirmPassInputLayout.setError("Confirm Password Not Match Yet!");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        refInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (refInput.getText().toString().length()<6){
                    reflInputLayout.setErrorEnabled(false);
                }else {
                    reflInputLayout.setErrorEnabled(true);
                    reflInputLayout.setError("Invalid Referral Code!");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return  view;
    }

    private void Register() {
    }

    private boolean Validate() {
        if (nameInput.getText().toString().isEmpty()){
            nameInputLayout.setErrorEnabled(true);
            nameInputLayout.setError("Name is required!");
            return false;
        }
        else if (emailInput.getText().toString().isEmpty()){
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Email is required!");
            return false;
        }else if(!emailInput.getText().toString().trim().matches(emailPattern)){
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Valid email is required!");
            return false;
        }
        else if (passInput.getText().toString().isEmpty()){
            emailInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password is required!");
            return false;
        }
        else if (passInput.getText().toString().length()<8){
            emailInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password must be at least 8 Character!");
            return false;
        }
        else if (!Objects.requireNonNull(passInput.getText()).toString().equals(confirmPassInput.getText().toString())){
            passInputLayout.setErrorEnabled(false);
            confirmPassInputLayout.setErrorEnabled(true);
            confirmPassInputLayout.setError("Confirm Password Not Match!");
            return false;
        }
        else if (refInput.getText().toString().length()>5){
            reflInputLayout.setErrorEnabled(true);
            confirmPassInputLayout.setErrorEnabled(false);
            reflInputLayout.setError("Invalid Referral Code!");
            return false;
        }
        else {
            confirmPassInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(false);
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(false);
            reflInputLayout.setErrorEnabled(false);
            return true;
        }

    }
}