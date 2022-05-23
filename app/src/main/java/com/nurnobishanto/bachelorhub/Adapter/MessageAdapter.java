package com.nurnobishanto.bachelorhub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nurnobishanto.bachelorhub.Models.MessageModels;
import com.nurnobishanto.bachelorhub.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private List<MessageModels> mChat;
    private String imageUrl;
    FirebaseUser fuser;


    public MessageAdapter(Context mContext, List<MessageModels> mChat, String imageUrl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageUrl=imageUrl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        MessageModels chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());

try {
    Picasso.get()
            .load(imageUrl)
            .into(holder.profile_image);
    if (position==mChat.size()-1)
    {
        if(chat.isIsseen())
        {
            holder.txt_seen.setText("Seen");
        }
        else {
            holder.txt_seen.setText("Delivered");
        }
    }
    else {
        holder.txt_seen.setVisibility(View.GONE);
    }
}catch (Exception e)
{
    e.printStackTrace();
}
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public TextView txt_seen;
        public ImageView profile_image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            txt_seen=itemView.findViewById(R.id.txt_seen);
            profile_image=itemView.findViewById(R.id.profile_image);



        }
    }
    @Override
    public int getItemViewType(int position)
    {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }
}
