package com.nurnobishanto.bachelorhub.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nurnobishanto.bachelorhub.Adapter.ChatUserAdapter;
import com.nurnobishanto.bachelorhub.Models.ChatUserModels;
import com.nurnobishanto.bachelorhub.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatUserAdapter adapter;
    private List<ChatUserModels> modelsList;

    private TextView check;
    private FirebaseAuth firebaseAuth;
    String myId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        myId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        check = view.findViewById(R.id.check);

        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setRefreshing(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        readChatUser();
        swipeRefreshLayout.setOnRefreshListener(() -> readChatUser());
        return view;
    }

    private void readChatUser() {
        swipeRefreshLayout.setRefreshing(true);
        modelsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users_connection").child(myId).child("ConnectWith");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = "";
                    Log.e("MessageFragment", "onDataChange: " + snapshot.child("userAuthId").getValue().toString());
                    if (snapshot.child("userAuthId").getValue() != null) {
                        id = snapshot.child("userAuthId").getValue().toString();
                    }
                    ChatUserModels obj = new ChatUserModels(id);
                    modelsList.add(obj);
                }
//                adapter.notifyDataSetChanged();
                adapter = new ChatUserAdapter(getContext(), modelsList);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                if (modelsList.isEmpty()) {
                    check.setVisibility(View.VISIBLE);
                } else {
                    check.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}