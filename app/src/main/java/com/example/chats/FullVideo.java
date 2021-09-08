package com.example.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FullVideo extends AppCompatActivity {

    private ProgressDialog progressDialog;
    String videourl;
    Intent intent;
    VideoView videoView ;
    FloatingActionButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        intent = getIntent();
        videourl = intent.getStringExtra("video");
        videoView = findViewById(R.id.videoView);
        progressDialog = ProgressDialog.show(FullVideo.this, "", "Loading video...",true);
        progressDialog.setCancelable(false);



        playVideo(videourl);
    }

    private void playVideo(String videourl) {
        try {

            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(FullVideo.this);
            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(videourl);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {

                public void onPrepared(MediaPlayer mp)
                {
                    progressDialog.dismiss();
                    videoView.start();
                }
            });

        }catch (Exception e){
            progressDialog.dismiss();
            System.out.println("Video Play Error :"+e.toString());
            finish();
        }
    }
}