package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.HashMap;

import id.voela.actrans.AcTrans;

public class EditAccountActivity extends AppCompatActivity {
    ImageView avatar,back;
    EditText bioEdit,nameAccountT;
    DatabaseReference userRef;
    FirebaseUser user;
    TextView groupSave;
    StorageReference storagePostPictureRef;
    String aa, bb, cc;
    private Uri imageUri1  = null;
    private String myUrl1 = "";
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        Slidr.attach(this);

        groupSave = findViewById(R.id.groupSave);
        progress = findViewById(R.id.progress);
        avatar = findViewById(R.id.avatar);
        bioEdit = findViewById(R.id.bioAccount);
        nameAccountT = findViewById(R.id.nameAccountT);
        back = findViewById(R.id.back);
        progress.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(EditAccountActivity.this).performSlideToRight();
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("dp");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aa = snapshot.child("imageURL").getValue().toString();
                Glide.with(getApplicationContext()).load(aa).into(avatar);
                myUrl1 = aa;
                cc = snapshot.child("username").getValue().toString();
                nameAccountT.setText(cc);
                bb = snapshot.child("bio").getValue().toString();
                bioEdit.setText(bb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(EditAccountActivity.this);
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
            groupSave.setEnabled(false);
            bioEdit.setEnabled(false);
            avatar.setImageURI(imageUri1);

            StorageReference filePath = storagePostPictureRef.child(user.getUid() + ".jpg");
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
                        groupSave.setEnabled(true);
                        bioEdit.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(this).performSlideToRight();
    }

    public void savaChanges(View view) {
        view.setEnabled(false);
        String newStatus = bioEdit.getText().toString().trim();
        String newName = nameAccountT.getText().toString().trim();

        HashMap<String, Object> map = new HashMap<>();
        map.put("imageURL", myUrl1);
        map.put("bio", newStatus);
        map.put("username", newName);

        userRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                new AcTrans.Builder(EditAccountActivity.this).performSlideToRight();
            }
        });
    }
}