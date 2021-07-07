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
    public static final int MSG_TYPE_DOCS_LEFT = 4;
    public static final int MSG_TYPE_DOCS_RIGHT = 5;
    public static final int MSG_TYPE_VIDEO_LEFT = 6;
    public static final int MSG_TYPE_VIDEO_RIGHT = 7;
    public static final int MSG_TYPE_LOC_LEFT = 8;
    public static final int MSG_TYPE_LOC_RIGHT = 9;

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
            case MSG_TYPE_VIDEO_RIGHT:
                View videoOnlyRViewHolder = inflater.inflate(R.layout.video_right, parent,false);
                viewHolder = new RightVideoGroup(videoOnlyRViewHolder);
                break;
            case MSG_TYPE_VIDEO_LEFT:
                View videoOnlyLViewHolder = inflater.inflate(R.layout.left_video_group, parent,false);
                viewHolder = new LeftVideoGroup(videoOnlyLViewHolder);
                break;
            case MSG_TYPE_DOCS_RIGHT:
                View docsOnlyRViewHolder = inflater.inflate(R.layout.right_docs, parent,false);
                viewHolder = new RightDocsGroup(docsOnlyRViewHolder);
                break;
            case MSG_TYPE_DOCS_LEFT:
                View docsOnlyLViewHolder = inflater.inflate(R.layout.group_doc_left, parent,false);
                viewHolder = new LeftDocsGroup(docsOnlyLViewHolder);
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
                                rightI.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, FullImage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("image", chat.getMessage());
                                    mContext.startActivity(intent);
                                }
                            });
                                break;
                            case MSG_TYPE_IMAGE_LEFT:
                                LeftImageG leftI = (LeftImageG) holder;
                                Glide.with(mContext).load(userImage).into(leftI.photoGMember);
                                leftI.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, FullImage.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("image", chat.getMessage());
                                        mContext.startActivity(intent);
                                    }
                                });
                                break;
                            case MSG_TYPE_VIDEO_LEFT:
                                LeftVideoGroup leftV = (LeftVideoGroup) holder;
                                Glide.with(mContext).load(userImage).into(leftV.photoGMember);
                                leftV.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(browserIntent);
                                    }
                                });
                                break;
                            case MSG_TYPE_VIDEO_RIGHT:
                                RightVideoGroup rightV = (RightVideoGroup) holder;
                                rightV.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(browserIntent);
                                    }
                                });
                                break;
                            case MSG_TYPE_DOCS_LEFT:
                                LeftDocsGroup leftDocsGroup = (LeftDocsGroup) holder;
                                Glide.with(mContext).load(userImage).into(leftDocsGroup.photoGMember);
                                leftDocsGroup.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(browserIntent);
                                    }
                                });
                                break;

                            case MSG_TYPE_DOCS_RIGHT:
                                RightDocsGroup rightDocsGroup = (RightDocsGroup) holder;
                                rightDocsGroup.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(browserIntent);
                                    }
                                });
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
            }else if (mChat.get(position).getType().equals("video")){
                return MSG_TYPE_VIDEO_RIGHT;
            }else if (mChat.get(position).getType().equals("docs")){
                return MSG_TYPE_DOCS_RIGHT;
            }else {
                return MSG_TYPE_IMAGE_RIGHT;
            }

        }
        else {
            if (mChat.get(position).getType().equals("text")){
                return MSG_TYPE_LEFT;
            }else if (mChat.get(position).getType().equals("video")){
                return MSG_TYPE_VIDEO_LEFT;
            }else if (mChat.get(position).getType().equals("docs")){
                return MSG_TYPE_DOCS_LEFT;
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

    public class LeftVideoGroup extends RecyclerView.ViewHolder{
        public TextView show_videoL;
        public CircleImageView text_seen, photoGMember;
        public ImageView isLiked;

        public LeftVideoGroup(@NonNull View itemView) {
            super(itemView);
            show_videoL = itemView.findViewById(R.id.show_videoL);
            text_seen = itemView.findViewById(R.id.text_seen);
            isLiked = itemView.findViewById(R.id.isLiked);
            photoGMember = itemView.findViewById(R.id.photoGMember);
        }
    }

    class RightVideoGroup extends RecyclerView.ViewHolder{
        public TextView show_videoR,liked;
        public CircleImageView text_seen;

        public RightVideoGroup(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_videoR = itemView.findViewById(R.id.show_videoR);
            text_seen = itemView.findViewById(R.id.text_seen);

        }
    }

    public class LeftDocsGroup extends RecyclerView.ViewHolder{
        public TextView show_docsL;
        public CircleImageView text_seen,photoGMember;
        public ImageView isLiked;

        public LeftDocsGroup(@NonNull View itemView) {
            super(itemView);
            show_docsL = itemView.findViewById(R.id.show_docsL);
            text_seen = itemView.findViewById(R.id.text_seen);
            isLiked = itemView.findViewById(R.id.isLiked);
            photoGMember = itemView.findViewById(R.id.photoGMemberQ);
        }
    }

    class RightDocsGroup extends RecyclerView.ViewHolder{
        public TextView show_docsR,liked;
        public CircleImageView text_seen;

        public RightDocsGroup(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_docsR = itemView.findViewById(R.id.show_docsR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }
}
