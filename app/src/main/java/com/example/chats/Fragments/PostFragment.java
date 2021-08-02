package com.example.chats.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.AddPostActivity;
import com.example.chats.FullImage;
import com.example.chats.Model.Posts;
import com.example.chats.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class PostFragment extends Fragment {

    public PostFragment() {
        // Required empty public constructor
    }

    FloatingActionButton addPost;
    private Context contextT;
    RecyclerView postList;
    LinearLayoutManager layoutManager;
    FirebaseUser user;
    Toolbar toolbar;
    CircleImageView selfAvatar;
    RelativeLayout loading;
    private DatabaseReference friendsRef, userRef, postRef;
    BottomSheetDialog addPostDialog;
    boolean isLiked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        addPost = view.findViewById(R.id.addPost);
        contextT  = getActivity();
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMediaOption();
            }
        });
        selfAvatar = view.findViewById(R.id.selfAvatar);
        toolbar = view.findViewById(R.id.tabanim_toolbar);
        postList = view.findViewById(R.id.postList);
        loading = view.findViewById(R.id.loading);
        postList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        postList.setLayoutManager(layoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(user.getUid());
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        postRef = FirebaseDatabase.getInstance().getReference("Post");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        toolbar.setSubtitle(saveCurrentDate);

        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String ph = snapshot.child("imageURL").getValue().toString();
                    Glide.with(contextT.getApplicationContext()).load(ph).into(selfAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        displayPosts();

        return view;
    }

    private void displayPosts() {
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postRef, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostVH> adapter = new FirebaseRecyclerAdapter<Posts, PostVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostVH holder, int position, @NonNull Posts model) {
                postList.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);

                if (model.getId().equals(user.getUid())){
                    holder.time.setText(model.getTime());
                    Glide.with(contextT).load(model.getImageUrl()).into(holder.postImage);
                    holder.postText.setText(model.getTitle());
                }


                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isLiked){
//                            Add like
                            isLiked = true;
                            addLikeRecord(model.getId());
                        }else {
//                            Remove like
                            isLiked = false;
                            removeLikeRecord(model.getId());
                        }
                    }
                });
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Post").child(model.getId()).child("Likes");


                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = (int) snapshot.getChildrenCount();
                        holder.likeCount.setText(String.valueOf(count));

                        if (snapshot.hasChild(user.getUid())){
                            isLiked = true;
                            holder.likeButton.setBackgroundResource(R.drawable.ic_liked);
                        }else {
                            isLiked = false;
                            holder.likeButton.setBackgroundResource(R.drawable.ic_not_liked);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(model.getOwner())){

                            holder.time.setText(model.getTime());
                            Glide.with(contextT).load(model.getImageUrl()).into(holder.postImage);
                            holder.postText.setText(model.getTitle());
                            holder.personName.setText(model.getPersonName());
                            Glide.with(contextT).load(model.getPersonImage()).into(holder.personImage);


                            if (model.getType().equals("video")){
//                                Media type -> Video
                                holder.videoIcon.setVisibility(View.VISIBLE);
                            }else {
//                                Media type -> Image
                                holder.videoIcon.setVisibility(View.GONE);
                                holder.postImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(contextT, FullImage.class);
                                        intent.putExtra("image", model.getImageUrl());
                                        startActivity(intent);
                                    }
                                });
                            }




                        }else {
                            holder.rel.getLayoutParams().height = 0;
                            holder.rel.setVisibility(View.GONE);
                        }
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public PostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, parent, false);
                return new PostVH(v);
            }
        };
        postList.setAdapter(adapter);
        //adapter.updateOptions(options);
        adapter.startListening();
    }

    private void removeLikeRecord(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("Post")
                .child(id)
                .child("Likes");
        ref.child(user.getUid()).removeValue();
    }

    private void addLikeRecord(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Post").child(id).child("Likes");
        HashMap<String, Object> map = new HashMap<>();
        map.put("userID", user.getUid());
        ref.child(user.getUid()).updateChildren(map);
    }

    private void showDialogMediaOption() {

        try{
            View sheetView = getActivity().getLayoutInflater().inflate(R.layout.post_option_dialogue, null);
            addPostDialog = new BottomSheetDialog(contextT);
            addPostDialog.setContentView(sheetView);
            addPostDialog.show();

            LinearLayout close = sheetView.findViewById(R.id.close);
            LinearLayout photo = sheetView.findViewById(R.id.photo);
            LinearLayout video = sheetView.findViewById(R.id.video);

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(contextT, AddPostActivity.class);
                    intent.putExtra("type", "photo");
                    startActivity(intent);
                    new AcTrans.Builder(contextT).performSlideToLeft();
                    addPostDialog.dismiss();
                }
            });

            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(contextT, AddPostActivity.class);
                    intent.putExtra("type", "video");
                    startActivity(intent);
                    new AcTrans.Builder(contextT).performSlideToLeft();
                    addPostDialog.dismiss();
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPostDialog.dismiss();
                }
            });

            // Remove default white color background
            FrameLayout bottomSheet = (FrameLayout) addPostDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
        }
        catch (Exception e){


        }
    }

    public class PostVH extends RecyclerView.ViewHolder{

        CircleImageView personImage;
        ImageView postImage, likeButton, videoIcon;
        TextView personName, time, postText, likeCount;
        RelativeLayout rel;

        public PostVH(@NonNull View itemView) {
            super(itemView);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            likeButton = itemView.findViewById(R.id.likeButton);
            personImage = itemView.findViewById(R.id.personPhoto);
            postImage = itemView.findViewById(R.id.imagePost);
            personName = itemView.findViewById(R.id.personNamePost);
            time = itemView.findViewById(R.id.personTimePost);
            likeCount = itemView.findViewById(R.id.likeCount);
            postText = itemView.findViewById(R.id.postTitle);
            rel = itemView.findViewById(R.id.rel);
        }
    }
}