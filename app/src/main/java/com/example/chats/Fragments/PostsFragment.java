package com.example.chats.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chats.FriendsActivity;
import com.example.chats.FullImage;
import com.example.chats.MessageActivity;
import com.example.chats.Model.Posts;
import com.example.chats.ProfileDetailsActivity;
import com.example.chats.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class PostsFragment extends Fragment {


    public PostsFragment() {
        // Required empty public constructor
    }

    RecyclerView postList;
    LinearLayoutManager layoutManager;
    FirebaseUser user;
    RelativeLayout loading;
    private DatabaseReference friendsRef, userRef, postRef;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
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
        postRef = FirebaseDatabase.getInstance().getReference("Posts");

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
            protected void onBindViewHolder(@NonNull PostsFragment.PostVH holder, int position, @NonNull Posts model) {
                postList.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);

                holder.personName.setText(model.getPersonName());
                Glide.with(getContext()).load(model.getPersonImage()).into(holder.personImage);
                Glide.with(getContext()).load(model.getImageUrl()).into(holder.postImage);
                holder.postText.setText(model.getTitle());
                holder.time.setText(model.getTime());

                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent(getContext(), FullImage.class);
                            intent.putExtra("image", model.getImageUrl());
                            startActivity(intent);

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
        adapter.updateOptions(options);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        displayPosts();
    }

    public class PostVH extends RecyclerView.ViewHolder{

        CircleImageView personImage;
        ImageView postImage;
        TextView personName, time, postText;

        public PostVH(@NonNull View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.personPhoto);
            postImage = itemView.findViewById(R.id.imagePost);
            personName = itemView.findViewById(R.id.personNamePost);
            time = itemView.findViewById(R.id.personTimePost);
            postText = itemView.findViewById(R.id.postTitle);
        }
    }
}