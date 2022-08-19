package com.nurnobishanto.bachelorhub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nurnobishanto.bachelorhub.Adapter.MessageAdapter;
import com.nurnobishanto.bachelorhub.MainActivity;
import com.nurnobishanto.bachelorhub.Models.MessageModels;
import com.nurnobishanto.bachelorhub.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView username, status;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private Intent intent;
    private EditText text_send;
    private ImageButton call;
    private ImageButton btn_send;
    private MessageAdapter messageAdapter;
    private List<MessageModels> mChat;
    private RecyclerView recyclerView;
    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        status = (TextView) findViewById(R.id.status);
        call = (ImageButton) findViewById(R.id.call);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        username = (TextView) findViewById(R.id.username);
        text_send = (EditText) findViewById(R.id.text_send);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        intent = getIntent();
        final String userid = intent.getStringExtra("userAuthId");
        String ai = userid;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(userid);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = text_send.getText().toString();
                if (!message.equals("")) {
                    sendMessage(fuser.getUid(), userid, message);
                    text_send.setText("");

                } else {
                    Toast.makeText(MessageActivity.this, "You Can't Send Message!", Toast.LENGTH_LONG).show();
                }

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


                    if (map.get("userFullName") != null) {
                        username.setText(map.get("userFullName").toString());
                    }
                    if (map.get("status") != null) {
                        status.setText(map.get("status").toString());

                    }
                    if (map.get("userPhoneNumber") != null) {
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int REQUEST_PHONE_CALL = 1;
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                    } else {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + map.get("userPhoneNumber")));
                                        startActivity(callIntent);
                                    }
                                } else {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + map.get("userPhoneNumber")));
                                    startActivity(callIntent);
                                }

                            }
                        });

                    }
                    Picasso.get()
                            .load(map.get("userImageUrl").toString())
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(profile_image);


                    readMessage(fuser.getUid(), userid, map.get("userImageUrl").toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        seenMessage(userid);

    }

    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModels chat = snapshot.getValue(MessageModels.class);
                    if (chat.getReciever().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(String sender, String reciever, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("reciever", reciever);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        reference.child("Chats").push().setValue(hashMap);

        final String userid = intent.getStringExtra("userAuthId");
        final DatabaseReference conRef = FirebaseDatabase.getInstance()
                .getReference("users_connection");
        conRef.child(fuser.getUid()).child("ConnectWith").child(userid).child("userAuthId").setValue(userid);
        conRef.child(userid).child("ConnectWith").child(fuser.getUid()).child("userAuthId").setValue(fuser.getUid());

//        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
//                .getReference("tolet_users").child(fuser.getUid())
//                .child("ConnectWith").child(userid);
//        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance()
//                .getReference("tolet_users").child(userid)
//                .child("ConnectWith").child(fuser.getUid());
//        chatRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.exists())
//                {
//                    chatRef.child("userAuthId").setValue(userid);
//                    chatRef1.child("userAuthId").setValue(fuser.getUid().toString());
//
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


    }

    public void readMessage(final String myid, final String userId, final String imageurl) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    try {
                        MessageModels chat = snapshot.getValue(MessageModels.class);
                        if (chat.getReciever().equals(myid) && chat.getSender().equals(userId) ||
                                chat.getReciever().equals(userId) && chat.getSender().equals(myid)) {
                            mChat.add(chat);

                        }


                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                        recyclerView.setAdapter(messageAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }


    @Override
    protected void onPause() {
        super.onPause();
        DateFormat df = new SimpleDateFormat("h:mm a, EEE, d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        status("Last Seen " + date);
    }

}
