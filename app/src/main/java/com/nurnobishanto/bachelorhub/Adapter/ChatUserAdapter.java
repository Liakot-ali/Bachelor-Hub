package com.nurnobishanto.bachelorhub.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nurnobishanto.bachelorhub.Activity.MessageActivity;
import com.nurnobishanto.bachelorhub.Models.ChatUserModels;
import com.nurnobishanto.bachelorhub.R;
import com.squareup.picasso.Picasso;


import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {
    public Context mContext;
    public List<ChatUserModels> modelsList;
    FirebaseAuth mAuth;

    public ChatUserAdapter(Context mContext, List<ChatUserModels> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_user_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final ChatUserModels models = modelsList.get(position);
        getUsernmaeImage(models.getUserid(), holder.name, holder.image, holder.status, holder.call);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, MessageActivity.class);
                i.putExtra("userAuthId", models.getUserid());
                mContext.startActivity(i);
            }
        });

    }

    private void getUsernmaeImage(String userid, TextView name, CircleImageView image, TextView status, ImageButton call) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tolet_users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.e("MessageFragment", "ChatUserAdapter: " + Objects.requireNonNull(snapshot.child("userAuthId").getValue()).toString());
//                    Log.e("MessageFragment", "UserId: " + userid);

                    if (userid.equals(snapshot.child("userAuthId").getValue())) {
                        if (snapshot.child("userFullName").getValue() != null) {
                            name.setText(snapshot.child("userFullName").getValue().toString());
                        }
                        if (snapshot.child("status").getValue() != null) {
                            status.setText(snapshot.child("status").getValue().toString());
                        }
                        if (snapshot.child("userImageUrl").getValue() != null) {
                            Picasso.get()
                                    .load(snapshot.child("userImageUrl").getValue().toString())
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .into(image);
                        }
                        if (snapshot.child("userPhoneNumber").getValue() != null) {
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final int REQUEST_PHONE_CALL = 1;
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                                        } else {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + snapshot.child("userPhoneNumber").getValue().toString()));
                                            mContext.startActivity(callIntent);
                                        }
                                    } else {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + snapshot.child("userPhoneNumber").getValue().toString()));
                                        mContext.startActivity(callIntent);
                                    }


                                }
                            });

                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView image;
        LinearLayout card;
        ImageButton call;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.img);
            card = itemView.findViewById(R.id.card);
            status = itemView.findViewById(R.id.status);
            call = itemView.findViewById(R.id.call);


        }
    }
}
