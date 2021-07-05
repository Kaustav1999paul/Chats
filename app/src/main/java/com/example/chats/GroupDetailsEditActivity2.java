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
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import id.voela.actrans.AcTrans;

public class GroupDetailsEditActivity2 extends AppCompatActivity {
    Intent intent;
    String groupId, photo, name;
    DatabaseReference groupReference;
    EditText nameGroup;
    ImageView avatar,back;
    FirebaseUser user;
    StorageReference storagePostPictureRef;
    ProgressBar progress;
    String aa, bb, cc;
    private Uri imageUri1  = null;
    private String myUrl1 = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details_edit2);
        intent = getIntent();
        avatar = findViewById(R.id.avatar);
        progress = findViewById(R.id.progress);
        groupId = intent.getStringExtra("id");
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("groups");
        nameGroup = findViewById(R.id.nameGroup);
        back = findViewById(R.id.back);

        groupReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photo = snapshot.child("photo").getValue().toString();
                Glide.with(getApplicationContext()).load(photo).into(avatar);
                name = snapshot.child("name").getValue().toString();
                nameGroup.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(GroupDetailsEditActivity2.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri1 = result.getUri();
            progress.setVisibility(View.VISIBLE);
            avatar.setEnabled(false);
            nameGroup.setEnabled(false);
            avatar.setImageURI(imageUri1);

            StorageReference filePath = storagePostPictureRef.child(groupId + ".jpg");
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
                        progress.setVisibility(View.GONE);
                        avatar.setEnabled(true);
                        nameGroup.setEnabled(true);
                    }
                }
            });
        }
    }

    public void savaChanges(View view) {
        String newName = nameGroup.getText().toString().trim();

        HashMap<String, Object> map = new HashMap<>();
        map.put("photo", myUrl1);
        map.put("name", newName);

        groupReference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }
}