package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import id.voela.actrans.AcTrans;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MoreSettingsActivity extends AppCompatActivity {

    ImageView back;
    EditText videoTitle, videoMessage;
    Button addVideo;
    TextView percentage;
    int PICKVIDEO_REQUEST_CODE =130;
    private Uri imageUri1  = null;
    private String myUrl1 = "";
    StorageReference storagePostPictureRef;
    FirebaseUser fUser;
    String message, title;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_settings);
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("Shorts");
        back = findViewById(R.id.back);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        progress = findViewById(R.id.progress);
        percentage = findViewById(R.id.percentage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(MoreSettingsActivity.this).performSlideToRight();
            }
        });

        videoTitle = findViewById(R.id.videoTitle);
        videoMessage = findViewById(R.id.videoMessage);
        addVideo = findViewById(R.id.addVideo);

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              title = videoTitle.getText().toString().trim();
              message  = videoMessage.getText().toString().trim();

                if (TextUtils.isEmpty(title)){
                    Toast.makeText(MoreSettingsActivity.this, "Video should have a title", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(message)){
                    Toast.makeText(MoreSettingsActivity.this, "Video should have a message", Toast.LENGTH_SHORT).show();
                }else {
                    openVideoSelector();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(MoreSettingsActivity.this).performSlideToRight();
    }

    private void openVideoSelector() {
        videoTitle.setEnabled(false);
        videoMessage.setEnabled(false);
        addVideo.setEnabled(false);
        back.setEnabled(false);
        addVideo.setText("Uploading...");

        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICKVIDEO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKVIDEO_REQUEST_CODE && resultCode == RESULT_OK){
            //                If fileType = Docs
            Uri uri = data.getData();
            String src = uri.getPath();

            progress.setVisibility(View.VISIBLE);
            percentage.setVisibility(View.VISIBLE);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(MoreSettingsActivity.this, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMilliSec = Long.parseLong( time );
            long duration = timeInMilliSec / 1000;
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            int seconds = (int) (duration - (hours * 3600 + minutes * 60));

            if (hours == 00 && minutes == 00 && seconds <= 30){
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
                String saveCurrentDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                String saveCurrentTime = currentTime.format(calendar.getTime());
                String ProductRandomKey = fUser.getUid() + saveCurrentDate + saveCurrentTime;

                String filenameArray[] = src.split("\\.");
                String extension = filenameArray[filenameArray.length-1];
                StorageReference filePath = storagePostPictureRef.child(ProductRandomKey + "."+extension);

                filePath.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progressValue = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                progress.setProgress((int) progressValue);
                                percentage.setText(progressValue+"% Uploaded");
                            }
                        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            myUrl1 = task.getResult().toString();
                            Date date = new Date();
                            // Pattern
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

                            sendVideo(fUser.getUid(), myUrl1, sdf.format(date), title, message, seconds);
                        }
                    }
                });

            }   else {

                videoTitle.setEnabled(true);
                videoMessage.setEnabled(true);
                addVideo.setEnabled(true);
                back.setEnabled(true);
                addVideo.setText("Add Video");
                progress.setVisibility(View.GONE);
                percentage.setVisibility(View.GONE);

                Toast.makeText(this, "Video length should be less than 30 seconds", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void sendVideo(String uid, String myUrl1, String time, String title, String message, int seconds) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentTime + saveCurrentDate + fUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("author", uid);
        hashMap.put("time", time);
        hashMap.put("title", title);
        hashMap.put("message", message);
        hashMap.put("videourl", myUrl1);
        hashMap.put("duration", seconds);
        reference.child("Clips").child(RandomKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    videoTitle.setEnabled(true);
                    videoMessage.setEnabled(true);
                    back.setEnabled(true);
                    finish();
                }
            }
        });
    }
}