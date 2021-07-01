package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class TrimActivity extends AppCompatActivity {

    Uri url;
    String imgPath, title, message, oreginalPath, filePrefix;
    ImageView pause;
    VideoView videoView;
    TextView left ,right;
    int duration;
    RangeSeekBar seekbar;
    String[] command;
    File dest;
    boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);

        Intent i = getIntent();
        imgPath = i.getStringExtra("url");
        title = i.getStringExtra("title");
        message = i.getStringExtra("message");
        pause = findViewById(R.id.pause);
        seekbar = findViewById(R.id.seekbar);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse(imgPath));
        isPlaying = true;
        videoView.start();

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    pause.setImageResource(R.drawable.ic_round_play_arrow_24);
                    videoView.pause();
                    isPlaying = false;
                }else {
                    pause.setImageResource(R.drawable.ic_round_pause_24);
                    videoView.start();
                    isPlaying = true;
                }
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration()/1000;
                right.setText(getTime(mp.getDuration()/1000));
                mp.setLooping(true);
                seekbar.setRangeValues(0, 30);
                seekbar.setSelectedMaxValue(30);
                seekbar.setSelectedMinValue(0);
                seekbar.setEnabled(true);

                seekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        videoView.seekTo((int) minValue*1000);
                        left.setText(getTime((int)bar.getSelectedMinValue()));
                        right.setText(getTime((int)bar.getSelectedMaxValue()));
                    }
                });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (videoView.getCurrentPosition() >= seekbar.getSelectedMaxValue().intValue()*1000){
                            videoView.seekTo(seekbar.getSelectedMinValue().intValue()*1000);
                        }
                    }
                }, 1000);
            }
        });
    }

    private String getTime(int seconds) {
        int hr = seconds/3600;
        int rem = seconds % 3600;
        int mn = rem/60;
        int sec = rem%60;
        return String.format("%02d", hr)+":"+String.format("%02d", mn)+":"+String.format("%02d", sec);
    }

//    private void sendVideo(String uid, String myUrl1, String time, String title, String message) {
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
//        String saveCurrentDate = currentDate.format(calendar.getTime());
//        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
//        String saveCurrentTime = currentTime.format(calendar.getTime());
//        String RandomKey = saveCurrentDate + saveCurrentTime+ fUser.getUid();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("id", RandomKey);
//        hashMap.put("author", uid);
//        hashMap.put("time", time);
//        hashMap.put("title", title);
//        hashMap.put("message", message);
//        hashMap.put("videourl", myUrl1);
//        reference.child("Clips").child(RandomKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    videoTitle.setEnabled(true);
//                    videoMessage.setEnabled(true);
//                    back.setEnabled(true);
//                    finish();
//                }
//            }
//        });
//    }
}