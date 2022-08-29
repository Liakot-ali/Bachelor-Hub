package com.bachelorhub.bytecode.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bachelorhub.bytecode.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bachelorhub.bytecode.Models.VerifyUserModels;
import com.bachelorhub.bytecode.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VerifyUserAdapter extends RecyclerView.Adapter<VerifyUserAdapter.ViewHolder> {

    public Context mContext;
    public List<User> modelsList;

    public VerifyUserAdapter(Context mContext, List<User> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.verify_user_item, parent, false);


        return new VerifyUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User models = modelsList.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(models.getUserAuthId()).child("userVerify");
        if (models.getUserImageUrl().isEmpty() || models.getUserImageUrl().compareTo("") == 0) {
            Picasso.get()
                    .load(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.profileImage);
        }else{
//            Log.e("ChatUserAdapter", "userImageUrl:" + "." + snapshot.child("userImageUrl").getValue() + ".");
            Picasso.get()
//                                    .load(snapshot.child("userImageUrl").getValue().toString())
                    .load(models.getUserImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.profileImage);
        }

//        if(!models.getImageUrl().equals("")) {
//            Picasso.get()
//                    .load(models.getImageUrl())
//                    .placeholder(R.mipmap.ic_launcher)
//                    .into(holder.profileImage);
//        }

        String[] methodSplit = null;
        String[] relationSplit = null;
        if(models.getUserRelation() != null) {
            relationSplit = models.getUserRelation().split("#");
        }
        if(models.getVerifyMethod() != null) {
             methodSplit = models.getVerifyMethod().split("#");
        }
        String fullName, userId, birthDate, transId, verifyNumber, phone, address, method, nidFront, nidBack, userVerifyPic;

        fullName = models.getUserFullName();
        if(fullName.equals("")){
            fullName = "No Name";
        }
        userId = models.getUserAuthId();

        if(models.getUserBirthDate() != null){
            birthDate = models.getUserBirthDate();
            if(birthDate.equals("")){
                birthDate = "No birth date";
            }
        }else{
            birthDate = "No birth date";
        }

        if(models.getVerifyKey() != null){
            transId = models.getVerifyKey();
            if(transId.equals("")) {
                transId = "No transaction Id";
            }
        }else{
            transId = "No transaction Id";
        }
        method = "National ID";

        int methodLength = 0;
        if(methodSplit != null) {
            methodLength = methodSplit.length;
        }
        if(methodLength > 0){
            nidFront = methodSplit[0];
        }
        if(methodLength > 1){
            nidBack = methodSplit[1];
        }
        if (methodLength > 2){
            userVerifyPic = methodSplit[2];
        }

        int relationLength = 0;
        if(relationSplit != null){
            relationLength = relationSplit.length;
        }
        if(relationLength > 1) {
            verifyNumber = relationSplit[1];
        }else{
            verifyNumber = "No verification number";
            method = "Empty";
        }

        if(models.getUserPhoneNumber() != null){
            phone = models.getUserPhoneNumber();
            if(phone.equals("")) {
                phone = "No phone number";
            }
        }else{
            phone = "No phone number";
        }

        if(models.getUserAddress() != null){
            address = models.getUserAddress();
            if(address.equals("")){
                address = "No address";
            }
        }else{
            address = "No address";
        }

        holder.fullName.setText(fullName);
        holder.userId.setText(userId);
        holder.dob.setText("Date of Birth : " + birthDate);
        holder.method.setText("Transaction ID : " + transId);
        holder.method_key.setText("NID Number : " + verifyNumber);
        holder.phone.setText("Phone : " + phone);
        holder.address.setText("Address : " + address);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImage;
        private TextView fullName, userId, dob, method, method_key, phone, address;
        private Button pending, approve, reject;


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
