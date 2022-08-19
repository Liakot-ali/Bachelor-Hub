package com.bachelorhub.bytecode.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bachelorhub.bytecode.Models.VerifyUserModels;
import com.bachelorhub.bytecode.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerifyUserAdapter extends RecyclerView.Adapter<VerifyUserAdapter.ViewHolder> {

    public Context mContext;
    public List<VerifyUserModels> modelsList;

    public VerifyUserAdapter(Context mContext, List<VerifyUserModels> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.verify_user_item,parent,false);


        return new VerifyUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final VerifyUserModels models  = modelsList.get(position);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("tolet_users").child(models.getUserId()).child("userVerify");
        Picasso.get()
                .load(models.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.profileImage);
        holder.fullName.setText(models.getFullName());
        holder.userId.setText(models.getUserId());
        holder.dob.setText("Date of Birth : "+models.getBirthDate());
        holder.method.setText("Verify Method : "+models.getVerifyMethod());
        holder.method_key.setText("Key : "+models.getVerifyKey());
        holder.phone.setText("Phone : "+models.getPhoneNumber());
        holder.address.setText("Address : "+models.getAddress());
        switch (models.getUserVerify()) {
            case "Pending":
                holder.pending.setVisibility(View.GONE);
                holder.approve.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);


                break;
            case "Verified":
                holder.pending.setVisibility(View.VISIBLE);
                holder.approve.setVisibility(View.GONE);
                holder.reject.setVisibility(View.VISIBLE);
                break;
            case "Rejected":
                holder.pending.setVisibility(View.VISIBLE);
                holder.approve.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.GONE);
                break;
        }
        holder.pending.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("Alert !")
                .setMessage("Are you sure you want to change the verification status for this user?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> reference.setValue("Pending"))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
        holder.approve.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("Alert !")
                .setMessage("Are you sure you want to change the verification status for this user?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> reference.setValue("Verified"))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
        holder.reject.setOnClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("Alert !")
                .setMessage("Are you sure you want to change the verification status for this user?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> reference.setValue("Rejected"))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
    }




    @Override
    public int getItemCount() {
        return modelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profileImage;
        private TextView fullName,userId,dob,method,method_key,phone,address;
        private Button pending,approve,reject;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            fullName = itemView.findViewById(R.id.userFullName);
            userId = itemView.findViewById(R.id.userId);
            dob = itemView.findViewById(R.id.dob);
            method = itemView.findViewById(R.id.method);
            method_key = itemView.findViewById(R.id.method_key);
            pending = itemView.findViewById(R.id.pending);
            approve = itemView.findViewById(R.id.approve);
            reject = itemView.findViewById(R.id.reject);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);



        }
    }

}
