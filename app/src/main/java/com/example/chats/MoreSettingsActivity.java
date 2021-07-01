package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MoreSettingsActivity extends AppCompatActivity {

    ImageView back;
    EditText videoTitle, videoMessage;
    Button addVideo;
    int PICKVIDEO_REQUEST_CODE =130;
    private Uri imageUri1  = null;
    private String myUrl1 = "";
    StorageReference storagePostPictureRef;
    FirebaseUser fUser;
    String message, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_settings);
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("Shorts");
        back = findViewById(R.id.back);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

            Intent i = new Intent(MoreSettingsActivity.this, TrimActivity.class);
            i.putExtra("url", uri);
            i.putExtra("title", title);
            i.putExtra("message", message);
            startActivity(i);

//            Calendar calendar = Calendar.getInstance();
//            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
//            String saveCurrentDate = currentDate.format(calendar.getTime());
//            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
//            String saveCurrentTime = currentTime.format(calendar.getTime());
//            String ProductRandomKey = fUser.getUid() + saveCurrentDate + saveCurrentTime;

//            String filenameArray[] = src.split("\\.");
//            String extension = filenameArray[filenameArray.length-1];
//            StorageReference filePath = storagePostPictureRef.child(ProductRandomKey + "."+extension);

//            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    // Continue with the task to get the download URL
//                    return filePath.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        myUrl1 = task.getResult().toString();
////                        progress.setVisibility(View.GONE);
////                        saveChanges.setEnabled(true);
////                        bioEdit.setEnabled(true);
//                        Date date = new Date();
//                        // Pattern
//                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
////                                  Current User||other user||message|| system Time
//                        sendVideo(fUser.getUid(), myUrl1, sdf.format(date), title, message);
//                    }
//                }
//            });
        }
    }
}