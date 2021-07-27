package com.example.chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.chats.Adapter.ClipAdapter;
import com.example.chats.Model.Clips;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;

import id.voela.actrans.AcTrans;

public class ClipVideoActivity extends AppCompatActivity {

    private ViewPager2 videoViewPager;
    private ClipAdapter adapter;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_video);
        theme();
        videoViewPager = findViewById(R.id.videoViewPager);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(ClipVideoActivity.this).performSlideToRight();
            }
        });

        FirebaseRecyclerOptions<Clips> options = new FirebaseRecyclerOptions.Builder<Clips>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Clips"), Clips.class)
                .build();

        adapter = new ClipAdapter(options, getApplicationContext());
        videoViewPager.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(this).performSlideToRight();
    }

    private void theme() {
        Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }
}