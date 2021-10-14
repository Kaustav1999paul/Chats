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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.AddPostActivity;
import com.example.chats.FullImage;
import com.example.chats.FullVideo;
import com.example.chats.MessageActivity;
import com.example.chats.Model.Comments;
import com.example.chats.Model.Posts;
import com.example.chats.ProfileDetailsActivity;
import com.example.chats.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    LinearLayoutManager layoutManager, commentLayoutManager;
    FirebaseUser user;
    TextView messageNo;
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
        messageNo = view.findViewById(R.id.messageNo);
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
                    messageNo.setVisibility(View.VISIBLE);
                }else {
                    messageNo.setVisibility(View.GONE);
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
                    holder.time.setText(model.getTime()+ ",  "+ model.getDate());
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
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Post")
                        .child(model.getId()).child("Likes");


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

                DatabaseReference refCom = FirebaseDatabase.getInstance().getReference().child("Post")
                        .child(model.getId()).child("Comments");

                refCom.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            int count = (int) snapshot.getChildrenCount();
                            holder.commentCount.setText(String.valueOf(count));
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

                            holder.time.setText(model.getTime()+ ",  "+ model.getDate());
                            Glide.with(contextT).load(model.getImageUrl()).into(holder.postImage);
                            holder.postText.setText(model.getTitle());
                            holder.personName.setText(model.getPersonName());
                            Glide.with(contextT).load(model.getPersonImage()).into(holder.personImage);

                            holder.personImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(contextT, ProfileDetailsActivity.class);
                                    intent.putExtra("id", model.getOwner());
                                    startActivity(intent);
                                }
                            });

                            holder.commentButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showCommentDialog(model.getId());
                                }
                            });


                            if (model.getType().equals("video")){
//                                Media type -> Video
                                holder.videoIcon.setVisibility(View.VISIBLE);
                                holder.postImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(contextT, FullVideo.class);
                                        intent.putExtra("video", model.getImageUrl());
                                        startActivity(intent);
                                    }
                                });
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
        adapter.updateOptions(options);
        adapter.startListening();
    }

    private void showCommentDialog(String id) {
        try{
            View sheetView = getActivity().getLayoutInflater().inflate(R.layout.comment_bottom_dialogue, null);
            addPostDialog = new BottomSheetDialog(contextT);
            addPostDialog.setContentView(sheetView);
            addPostDialog.show();

            LinearLayout close = sheetView.findViewById(R.id.postComment);
            EditText comment = sheetView.findViewById(R.id.comment);
            RecyclerView commentList = sheetView.findViewById(R.id.commentList);
            commentList.setHasFixedSize(true);
            commentLayoutManager = new LinearLayoutManager(getContext());
            commentLayoutManager.setReverseLayout(true);
            commentLayoutManager.setStackFromEnd(true);
            commentList.setLayoutManager(commentLayoutManager);

            showComments(id, commentList, commentLayoutManager);



            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cmnt = comment.getText().toString().trim();
                    if (!TextUtils.isEmpty(cmnt)){
                        postCommnet(cmnt, id);
                        comment.setText("");
                    }

                }
            });

            // Remove default white color background
            FrameLayout bottomSheet = (FrameLayout) addPostDialog
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
        }
        catch (Exception e){


        }
    }

    private void showComments(String id, RecyclerView commentList, LinearLayoutManager commentLayoutManager) {
        Query query = postRef.child(id).child("Comments");

        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(query, Comments.class).build();

        FirebaseRecyclerAdapter<Comments, CommentVH> adapter = new FirebaseRecyclerAdapter<Comments, CommentVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentVH holder, int position, @NonNull Comments model) {
                Glide.with(contextT).load(model.getPersonImage()).into(holder.personImageC);
                holder.personNameC.setText(model.getPersonName());
                holder.personComment.setText(model.getComment());

            }

            @NonNull
            @Override
            public CommentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.comment_layout, parent, false);
                return new CommentVH(v);
            }
        };
        commentList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
    }

    private void postCommnet(String comment, String postId) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime + user.getUid();
        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");


        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("username").getValue().toString();
                    String photo = snapshot.child("imageURL").getValue().toString();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", RandomKey);
                    map.put("owner", user.getUid());
                    map.put("comment", comment);
                    map.put("personImage", photo);
                    map.put("personName", name);

                    postRef.child(postId).child("Comments").child(RandomKey).setValue(map);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




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

    public class CommentVH extends RecyclerView.ViewHolder{

        TextView personNameC, personComment;
        CircleImageView personImageC;

        public CommentVH(@NonNull View itemView) {
            super(itemView);
            personImageC = itemView.findViewById(R.id.personPhotoC);
            personNameC = itemView.findViewById(R.id.personNAme);
            personComment = itemView.findViewById(R.id.personComment);
        }
    }

    public class PostVH extends RecyclerView.ViewHolder{

        CircleImageView personImage;
        ImageView postImage, likeButton, videoIcon, commentButton;
        TextView personName, time, postText, likeCount, commentCount;
        RelativeLayout rel;

        public PostVH(@NonNull View itemView) {
            super(itemView);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            likeButton = itemView.findViewById(R.id.likeButton);
            personImage = itemView.findViewById(R.id.personPhoto);
            postImage = itemView.findViewById(R.id.imagePost);
            commentButton = itemView.findViewById(R.id.commentButton);
            personName = itemView.findViewById(R.id.personNamePost);
            time = itemView.findViewById(R.id.personTimePost);
            likeCount = itemView.findViewById(R.id.likeCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postText = itemView.findViewById(R.id.postTitle);
            rel = itemView.findViewById(R.id.rel);
        }
    }
}