package com.example.chats.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClipAdapter extends FirebaseRecyclerAdapter<Clips, ClipAdapter.myviewholder> {

    Context mContext;

    public ClipAdapter(@NonNull FirebaseRecyclerOptions<Clips> options, Context context) {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Clips model) {
        holder.setdata(model);
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clip_layout, parent, false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        VideoView video;
        TextView title, message;
        ProgressBar loading;
        ImageView author;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            video = itemView.findViewById(R.id.videoClips);
            title = itemView.findViewById(R.id.titleClips);
            message = itemView.findViewById(R.id.messageClips);
            loading = itemView.findViewById(R.id.loadingClip);
            author = itemView.findViewById(R.id.author);
        }

        void setdata(Clips obj){
            video.setVideoPath(obj.getVideourl());
            title.setText(obj.getTitle());
            message.setText(obj.getMessage());

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
                    loading.setVisibility(View.GONE);

                    float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                    float screenRatio = video.getWidth() / (float)
                            video.getHeight();
                    float scaleX = videoRatio / screenRatio;
                    if (scaleX >= 1f) {
                        video.setScaleX(scaleX);
                    } else {
                        video.setScaleY(1f / scaleX);
                    }
                }
            });

            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
        }
    }
}