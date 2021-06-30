package com.example.chats.Adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chats.Model.Clips;
import com.example.chats.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ClipAdapter extends FirebaseRecyclerAdapter<Clips, ClipAdapter.myviewholder> {

    public ClipAdapter(@NonNull FirebaseRecyclerOptions<Clips> options) {
        super(options);
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

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            video = itemView.findViewById(R.id.videoClips);
            title = itemView.findViewById(R.id.titleClips);
            message = itemView.findViewById(R.id.messageClips);
            loading = itemView.findViewById(R.id.loadingClip);

        }

        void setdata(Clips obj){
            video.setVideoPath(obj.getVideourl());
            title.setText(obj.getTitle());
            message.setText(obj.getMessage());

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    loading.setVisibility(View.GONE);
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
