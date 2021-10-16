package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import id.voela.actrans.AcTrans;

public class AddPostActivity extends AppCompatActivity {

    Intent intent;
    String type, typeFile;
    int PICKVIDEO_REQUEST_CODE = 21100;
    ImageView back, image;
    private Uri imageUri1  = null;
    FirebaseUser fuser;
    TextView postButton;
    private DatabaseReference userReff;
    EditText postTitle;
    RelativeLayout loading;
    private String myUrl1 = "";
    String name, photo;
    StorageReference storagePostPictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        intent = getIntent();
        postTitle = findViewById(R.id.postTitle);
        Slidr.attach(this);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        postButton = findViewById(R.id.postButton);
        type = intent.getStringExtra("type");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("PostMedia");
        userReff = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(AddPostActivity.this).performSlideToRight();
            }
        });


        if (type.equals("photo")){
            back.setEnabled(false);
            CropImage.activity().start(AddPostActivity.this);
        }else if (type.equals("video")) {
            back.setEnabled(false);
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICKVIDEO_REQUEST_CODE);
        }else {
            back.setEnabled(false);
            image.setVisibility(View.GONE);
            postButton.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("username").getValue().toString();
                photo = snapshot.child("imageURL").getValue().toString();

                postButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = postTitle.getText().toString().trim();
                        post(title, myUrl1, name, photo);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void post(String title, String myUrl1, String name, String photo) {
        if (type.equals("text")){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());
            String RandomKey = saveCurrentDate + saveCurrentTime+fuser.getUid();
            Date date = new Date();
            // Pattern
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Post");

            HashMap<String, Object> map = new HashMap<>();
            map.put("id", RandomKey);
            map.put("time", sdf.format(date));
            map.put("owner", fuser.getUid());
            map.put("imageUrl", "");
            map.put("title", title);
            map.put("type", "text");
            map.put("date", saveCurrentDate);
            map.put("personImage", photo);
            map.put("personName", name);

            reference.child(RandomKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    finish();
                }
            });
        }else {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime+fuser.getUid();
        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Post");

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", RandomKey);
        map.put("time", sdf.format(date));
        map.put("owner", fuser.getUid());
        map.put("imageUrl", myUrl1);
        map.put("title", title);
        map.put("type", typeFile);
        map.put("date", saveCurrentDate);
        map.put("personImage", photo);
        map.put("personName", name);

        reference.child(RandomKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
                    String saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    String saveCurrentTime = currentTime.format(calendar.getTime());
                    String RandomKey = saveCurrentDate + saveCurrentTime+fuser.getUid();
                    Date date = new Date();
                    // Pattern
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", RandomKey);
                    map.put("time", sdf.format(date));
                    map.put("owner", fuser.getUid());
                    map.put("imageUrl", myUrl1);
                    map.put("title", title);
                    map.put("type", typeFile);
                    map.put("date", saveCurrentDate);
                    map.put("personImage", photo);
                    map.put("personName", name);

                    userReff.child("Post").child(RandomKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                finish();
                            }
                        }
                    });
                }
            }
        });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri1 = result.getUri();
            image.setImageURI(imageUri1);
            postButton.setVisibility(View.VISIBLE);


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());
            String ProductRandomKey = fuser.getUid() + saveCurrentDate + saveCurrentTime;

            loading.setVisibility(View.VISIBLE);
            postButton.setEnabled(false);
            postTitle.setEnabled(false);
            StorageReference filePath = storagePostPictureRef.child(ProductRandomKey + ".jpg");
            filePath.putFile(imageUri1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

                        loading.setVisibility(View.GONE);
                        postButton.setEnabled(true);
                        postTitle.setEnabled(true);
                        typeFile = "image";
                        back.setEnabled(true);
                    }
                }
            });
        }else if (requestCode == PICKVIDEO_REQUEST_CODE && resultCode == RESULT_OK){
            //                If fileType = Docs
            Uri uri = data.getData();
            String src = uri.getPath();
            image.setVisibility(View.GONE);
            image.setImageURI(uri);
            postButton.setVisibility(View.VISIBLE);

            loading.setVisibility(View.VISIBLE);
            postButton.setEnabled(false);
            postTitle.setEnabled(false);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());
            String ProductRandomKey = fuser.getUid() + saveCurrentDate + saveCurrentTime;

            String filenameArray[] = src.split("\\.");
            String extension = filenameArray[filenameArray.length-1];
            StorageReference filePath = storagePostPictureRef.child(ProductRandomKey + "."+extension);

            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        loading.setVisibility(View.GONE);
                        postButton.setEnabled(true);
                        postTitle.setEnabled(true);
                        typeFile = "video";
                        back.setEnabled(true);
                    }
                }
            });
        }
    }
}