package com.nurnobishanto.bachelorhub.AuthFragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nurnobishanto.bachelorhub.Activity.EditProfileActivity;
import com.nurnobishanto.bachelorhub.Activity.PhoneActivity;
import com.nurnobishanto.bachelorhub.Activity.PhoneVerifyActivity;
import com.nurnobishanto.bachelorhub.Activity.onBoardAtivity;
import com.nurnobishanto.bachelorhub.Admin.AdminHomeActivity;
import com.nurnobishanto.bachelorhub.MainActivity;
import com.nurnobishanto.bachelorhub.Models.User;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.ConstantKey;
import com.nurnobishanto.bachelorhub.utils.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SignUpFragment extends Fragment {
    private TextInputLayout nameInputLayout, emailInputLayout, passInputLayout, confirmPassInputLayout;
    private TextInputEditText nameInput, emailInput, passInput, confirmPassInput;
    private Button signup;
    private TextView signin, forget;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth mAuth;
    private Spinner spinner;
    private EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        nameInputLayout = view.findViewById(R.id.nameInputLayout);
        emailInputLayout = view.findViewById(R.id.emailInputLayout);
        passInputLayout = view.findViewById(R.id.passlInputLayout);
        confirmPassInputLayout = view.findViewById(R.id.confirmPasslInputLayout);
        spinner = (Spinner) view.findViewById(R.id.country_code_spinner);
        editText = (EditText) view.findViewById(R.id.user_phone_number);
        nameInput = view.findViewById(R.id.name);
        emailInput = view.findViewById(R.id.email);
        passInput = view.findViewById(R.id.password);
        confirmPassInput = view.findViewById(R.id.confirmPassword);
        signin = view.findViewById(R.id.signin);
        signup = view.findViewById(R.id.signup);
        forget = view.findViewById(R.id.forget);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, ConstantKey.countryAreaCodes);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition("+880")); //set item by default
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {

                    Register();
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SigninFragment()).commit();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new ForgotFragment()).commit();
            }
        });
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nameInput.getText().toString().isEmpty()) {
                    nameInputLayout.setErrorEnabled(true);
                    nameInputLayout.setError("Name is required!");
                } else {
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
        confirmPassInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(passInput.getText()).toString().equals(confirmPassInput.getText().toString())) {
                    confirmPassInputLayout.setErrorEnabled(false);
                } else {
                    confirmPassInputLayout.setErrorEnabled(true);
                    confirmPassInputLayout.setError("Confirm Password Not Match Yet!");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    private void Register() {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Please wait..");
        pd.show();

        mAuth.createUserWithEmailAndPassword(emailInput.getText().toString(), passInput.getText().toString()).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    pd.dismiss();
                    emailInput.setError(task.getException().getMessage().toString());
                } else {
                    String code = spinner.getSelectedItem().toString();
                    String phone = editText.getText().toString().trim();
                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("tolet_users").child(userId);
                    Map<String, Object> userInfo = new HashMap<>();

                    User obj = new User(userId, nameInput.getText().toString(), "", "", emailInput.getText().toString(), code + Utility.removeZero(phone), "", "", "Renter", "", "", "", "", "");
//                    SharedPrefManager.getInstance(EditProfileActivity.this).saveUser(obj);
//                    mUserViewModel.storeUser(obj);
//                    mUserViewModel.storeUser(obj).observe(this, new Observer<String>() {
//                        @Override
//                        public void onChanged(String result) {
//                            if (result.equals("success")) {
//                                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
//                                finish();
//                            }
//                            Utility.dismissProgressDialog(mProgress);
//                        }
//                    });

                    userInfo.put("userFullName", nameInput.getText().toString());
                    userInfo.put("userAuthId", userId);
                    userInfo.put("userEmail", emailInput.getText().toString());
                    userInfo.put("userPhoneNumber", code + Utility.removeZero(phone));
                    userInfo.put("isUserOwner", "Renter");


                    dbRef.setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Registration Completed", Toast.LENGTH_LONG).show();
                                SharedPrefManager.getInstance(getContext()).setUserIsLoggedIn(true);
                                SharedPrefManager.getInstance(getContext()).saveUserAuthId(userId);
                                SharedPrefManager.getInstance(getContext()).saveUser(obj);
                                if (emailInput.getText().toString().equals("bachelorhub.info@gmail.com")) {
                                    startActivity(new Intent(getContext(), AdminHomeActivity.class));
                                } else {
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                }
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean Validate() {
        if (nameInput.getText().toString().isEmpty()) {
            nameInputLayout.setErrorEnabled(true);
            nameInputLayout.setError("Name is required!");
            return false;
        } else if (emailInput.getText().toString().isEmpty()) {
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Email is required!");
            return false;
        } else if (!emailInput.getText().toString().trim().matches(emailPattern)) {
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Valid email is required!");
            return false;
        } else if (passInput.getText().toString().isEmpty()) {
            emailInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password is required!");
            return false;
        } else if (passInput.getText().toString().length() < 8) {
            emailInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(true);
            passInputLayout.setError("Password must be at least 8 Character!");
            return false;
        } else if (!Objects.requireNonNull(passInput.getText()).toString().equals(confirmPassInput.getText().toString())) {
            passInputLayout.setErrorEnabled(false);
            confirmPassInputLayout.setErrorEnabled(true);
            confirmPassInputLayout.setError("Confirm Password Not Match!");
            return false;
        } else {
            confirmPassInputLayout.setErrorEnabled(false);
            passInputLayout.setErrorEnabled(false);
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(false);

            return true;
        }

    }
}