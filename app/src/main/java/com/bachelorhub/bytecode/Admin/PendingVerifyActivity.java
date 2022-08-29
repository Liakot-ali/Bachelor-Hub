package com.bachelorhub.bytecode.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;


import com.bachelorhub.bytecode.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bachelorhub.bytecode.Adapter.VerifyUserAdapter;
import com.bachelorhub.bytecode.Models.VerifyUserModels;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class PendingVerifyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference reference;
    private List<User> modelsList;
    private VerifyUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle("Pending Verification");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_pending_verify);
        reference = FirebaseDatabase.getInstance().getReference("tolet_users");
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe);

        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getUserData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserData();
            }
        });


    }

    private void getUserData() {
        swipeRefreshLayout.setRefreshing(true);
        modelsList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User pendingUsr = snapshot.getValue(User.class);
                    VerifyUserModels obj = new VerifyUserModels();
                    /*
                    if (snapshot.child("userAuthId").getValue() != null) {
                        obj.setUserId(snapshot.child("userAuthId").getValue().toString());
                    }else{
                        obj.setUserId("No Authentication ID");
                    }
                    if (snapshot.child("userImageUrl").getValue() != null) {
                        obj.setImageUrl(snapshot.child("userImageUrl").getValue().toString());
                    }
                    if (snapshot.child("userFullName").getValue() != null) {
                        obj.setFullName(snapshot.child("userFullName").getValue().toString());
                    }else {
                        obj.setFullName("No name");
                    }
                    if (snapshot.child("userBirthDate").getValue() != null) {
                        obj.setBirthDate(snapshot.child("userBirthDate").getValue().toString());
                    }else{
                        obj.setBirthDate("No Birth date given");
                    }
                    if (snapshot.child("verifyMethod").getValue() != null) {
//                        obj.setVerifyMethod(snapshot.child("verifyMethod").getValue().toString());
                        obj.setVerifyMethod("National ID");
                    }else{
                        obj.setVerifyMethod("National ID");
                    }
                    if (snapshot.child("verifyKey").getValue() != null) {
                        obj.setVerifyKey(snapshot.child("verifyKey").getValue().toString());
                    }else{
                        obj.setVerifyKey("No verification key");
                    }
                    if (snapshot.child("userPhoneNumber").getValue() != null) {
                        obj.setPhoneNumber(snapshot.child("userPhoneNumber").getValue().toString());
                    }else{
                        obj.setPhoneNumber("No phone number");
                    }
                    if (snapshot.child("userAddress").getValue() != null) {
                        obj.setAddress(snapshot.child("userAddress").getValue().toString());
                    }else{
                        obj.setAddress("No address");
                    }
                     */
//                    if(snapshot.child("userVerify").getValue().toString().equals("Pending")){
//                        modelsList.add(pendingUsr);
//                    }
                    if (snapshot.child("userVerify").getValue() != null) {
                        String userVerify = snapshot.child("userVerify").getValue().toString();
                        obj.setUserVerify(userVerify);
                        if (userVerify.equals("Pending"))
                            modelsList.add(pendingUsr);
                    }
                }

                adapter = new VerifyUserAdapter(PendingVerifyActivity.this, modelsList);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                if (modelsList.isEmpty()) {
                    Utility.alertDialog(PendingVerifyActivity.this, "No Data Found!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}