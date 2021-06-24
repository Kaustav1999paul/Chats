package com.example.chats.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chats.FullImage;
import com.example.chats.Model.GroupChats;
import com.example.chats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_IMAGE_LEFT = 2;
    public static final int MSG_TYPE_IMAGE_RIGHT = 3;
    private ArrayList<GroupChats> mChat;
    FirebaseUser fuser;
    LayoutInflater inflater;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

    public GroupMessageAdapter(Context mContext, ArrayList<GroupChats> mChat, LayoutInflater inflater) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case MSG_TYPE_LEFT:
                View textOnlyLViewHolder = inflater.inflate(R.layout.group_item_left, parent,false);
                viewHolder = new LeftMessageG(textOnlyLViewHolder);
                break;
            case MSG_TYPE_RIGHT:
                View textOnlyRViewHolder = inflater.inflate(R.layout.chat_item_right, parent,false);
                viewHolder = new RightMessageG(textOnlyRViewHolder);
                break;
            case MSG_TYPE_IMAGE_LEFT:
                View imageOnlyLViewHolder = inflater.inflate(R.layout.group_image_left, parent,false);
                viewHolder = new LeftImageG(imageOnlyLViewHolder);
                break;
            case MSG_TYPE_IMAGE_RIGHT:
                View imageOnlyRViewHolder = inflater.inflate(R.layout.image_right, parent,false);
                viewHolder = new RightImageG(imageOnlyRViewHolder);
                break;
            default:
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        GroupChats chat = mChat.get(position);

        try{

            String mess = chat.getMessage();

            userRef.child(chat.getSender()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapsh) {
                    if (snapsh.exists()){
                        final String userImage = snapsh.child("imageURL").getValue().toString();


                        switch (holder.getItemViewType()){
                            case MSG_TYPE_RIGHT:
                                RightMessageG rightM = (RightMessageG) holder;
                                rightM.show_messageR.setText(mess);
                                rightM.show_messageR.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(mContext, mess, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                rightM.text_seen.setVisibility(View.INVISIBLE);
                                break;
                            case MSG_TYPE_LEFT:
                                LeftMessageG leftM = (LeftMessageG) holder;
                                leftM.show_messageL.setText(mess);
                                Glide.with(mContext).load(userImage).into(leftM.photoGMember);
                                break;
                            case MSG_TYPE_IMAGE_RIGHT:
                                RightImageG rightI = (RightImageG) holder;
                                rightI.text_seen.setVisibility(View.INVISIBLE);
                                break;
                            case MSG_TYPE_IMAGE_LEFT:
                                LeftImageG leftI = (LeftImageG) holder;
                                Glide.with(mContext).load(userImage).into(leftI.photoGMember);
                                break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (mChat.get(position).getSender().equals(fuser.getUid())){
//            Me
            if (mChat.get(position).getType().equals("text")){
                return MSG_TYPE_RIGHT;
            }else {
                return MSG_TYPE_IMAGE_RIGHT;
            }

        }
        else {
            if (mChat.get(position).getType().equals("text")){
                return MSG_TYPE_LEFT;
            }else {
                return MSG_TYPE_IMAGE_LEFT;
            }
        }
    }

    public class LeftMessageG extends RecyclerView.ViewHolder{
        public TextView show_messageL;
        public CircleImageView text_seen,photoGMember;

        public LeftMessageG(@NonNull View itemView) {
            super(itemView);
            show_messageL = itemView.findViewById(R.id.show_messageLL);
            text_seen = itemView.findViewById(R.id.text_seen);
            photoGMember = itemView.findViewById(R.id.photoGMember);
        }
    }

    public class RightMessageG extends RecyclerView.ViewHolder{
        public TextView show_messageR;
        public CircleImageView text_seen;

        public RightMessageG(@NonNull View itemView) {
            super(itemView);
            show_messageR = itemView.findViewById(R.id.show_messageR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    public class RightImageG extends RecyclerView.ViewHolder{
        public TextView show_imageR;
        public CircleImageView text_seen;

        public RightImageG(@NonNull View itemView) {
            super(itemView);
            show_imageR = itemView.findViewById(R.id.show_imageR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    public class LeftImageG extends RecyclerView.ViewHolder{
        public TextView show_imageL;
        public CircleImageView text_seen, photoGMember;

        public LeftImageG(@NonNull View itemView) {
            super(itemView);
            show_imageL = itemView.findViewById(R.id.show_imageL);
            text_seen = itemView.findViewById(R.id.text_seen);
            photoGMember = itemView.findViewById(R.id.photoGMember);
        }
    }
}
