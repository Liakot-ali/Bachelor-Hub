package com.nurnobishanto.bachelorhub.AuthFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.nurnobishanto.bachelorhub.R;


public class ForgotFragment extends Fragment {

    private TextInputLayout emailInputLayout;
    private TextInputEditText emailInput;
    private Button forget;
    private TextView signin;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth fauth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        emailInput = view.findViewById(R.id.email);
        signin = view.findViewById(R.id.signin);
        forget = view.findViewById(R.id.forgot);
        fauth = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SigninFragment()).commit();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {
                    ForgetPassword();
                }

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
        return view;
    }

    private void ForgetPassword() {
        fauth.sendPasswordResetEmail(emailInput.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Please Check Email ", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SigninFragment()).commit();
                } else {
                    emailInput.setError(task.getException().getMessage());
                    emailInput.requestFocus();
                }

            }
        });
    }

    private boolean Validate() {
        if (emailInput.getText().toString().isEmpty()) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Email is required!");
            return false;
        } else if (!emailInput.getText().toString().trim().matches(emailPattern)) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Valid email is required!");
            return false;
        } else {
            emailInputLayout.setErrorEnabled(false);
            return true;
        }

    }
}