package com.example.chats.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chats.Model.Clips;
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
import com.white.progressview.HorizontalProgressView;

import java.util.HashMap;

public class ClipAdapter extends FirebaseRecyclerAdapter<Clips, ClipAdapter.myviewholder> {

    Context mContext;
    VideoView video;
    FirebaseUser user;
    boolean isLiked = false;


    public ClipAdapter(@NonNull FirebaseRecyclerOptions<Clips> options, Context context) {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Clips model) {


        holder.setdata(model);
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Clips").child(model.getId()).child("Likes");

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked){
                    isLiked = false;
                    ref.child(user.getUid()).removeValue();
                }else {
                    isLiked = true;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userID", user.getUid());
                    ref.child(user.getUid()).updateChildren(map);
                }
            }
        });

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
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.clip_layout, parent, false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView title, message, likeCount;
        ProgressBar loading;
        ImageView author;
        HorizontalProgressView progr;
        ImageButton likeButton;
        boolean isPlaying = true;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            video = itemView.findViewById(R.id.videoClips);
            likeButton = itemView.findViewById(R.id.likeButton);
            likeCount = itemView.findViewById(R.id.likeCount);
            title = itemView.findViewById(R.id.titleClips);
            message = itemView.findViewById(R.id.messageClips);
            loading = itemView.findViewById(R.id.loadingClip);
            author = itemView.findViewById(R.id.author);
            progr = itemView.findViewById(R.id.progress100);
        }

        void setdata(Clips obj){
            video.setVideoPath(obj.getVideourl());
            title.setText(obj.getTitle());
            message.setText(obj.getMessage());
            int duration = obj.getDuration();
            int value = duration * 1000;

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

            userRef.child(obj.getAuthor()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        final String userImage = snapshot.child("imageURL").getValue().toString();
                        Glide.with(mContext).load(userImage).into(author);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    isPlaying = true;
                    loading.setVisibility(View.GONE);
                    progr.runProgressAnim(value);

                    float videoRate = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = video.getWidth() / (float) video.getHeight();
                    float scale = videoRate / screenRatio;

                    if (scale >= 1f){
                        video.setScaleX(scale);
                    }else {
                        video.setScaleY(1f / scale);
                    }
                }
            });

            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                    isPlaying = true;
                    progr.runProgressAnim(value);
                }
            });
        }
    }
}
