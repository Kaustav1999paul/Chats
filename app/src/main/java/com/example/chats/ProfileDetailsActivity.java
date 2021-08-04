package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.Fragments.PostFragment;
import com.example.chats.Model.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;


public class ProfileDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    private String id, aa, bb, cc, dd, ee;
    ImageView personImage;
    FirebaseUser user;
    Button updateAccount;
    TextView personEmail, totalFriends, friendsSince, postt;
    private EditText personName, bio;
    private RecyclerView postList;
    CollapsingToolbarLayout collapseActionView;
    DatabaseReference userRef, friendsRef;
    private LinearLayout call, chat, email,controller,count;
    StorageReference storagePostPictureRef;
    private Uri imageUri1  = null;
    private String myUrl1 = "";
    ProgressBar progress;
    private DatabaseReference postReff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        toolbar = findViewById(R.id.toolbar);
        progress = findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        postList = findViewById(R.id.postList);
        postList.setHasFixedSize(true);
        postList.setLayoutManager(new GridLayoutManager(this, 3));


        postt  = findViewById(R.id.postt);
        count = findViewById(R.id.count);
        totalFriends = findViewById(R.id.totalFriends);
        friendsSince = findViewById(R.id.friendsSince);
        collapseActionView = findViewById(R.id.collapseActionView);
        Slidr.attach(this);
        updateAccount = findViewById(R.id.updateAccount);
        controller = findViewById(R.id.controller);
        personEmail = findViewById(R.id.personEmail);
        personName = findViewById(R.id.personName);
        bio = findViewById(R.id.personBio);
        personImage = findViewById(R.id.personImage);
        setSupportActionBar(toolbar);
        intent = getIntent();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
         call = findViewById(R.id.call);
         chat = findViewById(R.id.chat);
         email = findViewById(R.id.email);
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("dp");
        id = intent.getStringExtra("id");
        user = FirebaseAuth.getInstance().getCurrentUser();

        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        postReff = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Post");


        userRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aa = String.valueOf(snapshot.child("imageURL").getValue());
                Glide.with(getApplicationContext()).load(aa).into(personImage);
                myUrl1 = aa;
                cc = String.valueOf(snapshot.child("username").getValue());
                dd = String.valueOf(snapshot.child("bio").getValue());
                ee = String.valueOf(snapshot.child("phno").getValue());

                collapseActionView.setTitleEnabled(false);
                getSupportActionBar().setTitle("");
                bb = String.valueOf(snapshot.child("email").getValue());

                personName.setText(cc);
                bio.setText(dd);
                personEmail.setText(bb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if (id.equals(user.getUid())){
//            Self Profile
            postList.setVisibility(View.GONE);
            postt.setVisibility(View.GONE);
            personImage.setClickable(true);
            personName.setEnabled(true);
            bio.setEnabled(true);
            count.setVisibility(View.GONE);
            updateAccount.setVisibility(View.VISIBLE);
            controller.setVisibility(View.GONE);
            personImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity().setAspectRatio(1,1).start(ProfileDetailsActivity.this);
                }
            });
        }else {
//            Other Profile
            showPosts();
            personImage.setClickable(false);
            friendsRef.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    totalFriends.setText(String.valueOf(snapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            friendsRef.child(id).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String date = String.valueOf(snapshot.child("date").getValue());
                    friendsSince.setText(date);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDetailsActivity.this, MessageActivity.class);
                intent.putExtra("userid", id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+bb)));
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(ee);
            }
        });
    }

    private void showPosts() {

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postReff, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostProfileVH> adapter = new FirebaseRecyclerAdapter<Posts, PostProfileVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostProfileVH holder, int position, @NonNull Posts model) {
                Glide.with(getApplicationContext()).load(model.getImageUrl()).into(holder.postImage);
                if (model.getType().equals("image")){
                    holder.videoTemp.setVisibility(View.GONE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileDetailsActivity.this, FullImage.class);
                            intent.putExtra("image", model.getImageUrl());
                            startActivity(intent);
                        }
                    });
                }else {
                    holder.videoTemp.setVisibility(View.VISIBLE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileDetailsActivity.this, FullVideo.class);
                            intent.putExtra("video", model.getImageUrl());
                            startActivity(intent);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public PostProfileVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ProfileDetailsActivity.this).inflate(R.layout.grid_layout, parent, false);
                return new PostProfileVH(v);
            }
        };
        postList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri1 = result.getUri();
            progress.setVisibility(View.VISIBLE);
            updateAccount.setEnabled(false);
            bio.setEnabled(false);
            personImage.setImageURI(imageUri1);

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
                        updateAccount.setEnabled(true);
                        bio.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 347){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall(ee);
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void makePhoneCall(String ee) {
        if (ContextCompat.checkSelfPermission(ProfileDetailsActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(ProfileDetailsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 347);

        }else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+ee));
            startActivity(intent);
        }
    }

    public void savaChanges(View view) {
        view.setEnabled(false);
        String newStatus = bio.getText().toString().trim();
        String newName = personName.getText().toString().trim();

        HashMap<String, Object> map = new HashMap<>();
        map.put("imageURL", myUrl1);
        map.put("bio", newStatus);
        map.put("username", newName);


        userRef.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                new AcTrans.Builder(ProfileDetailsActivity.this).performSlideToRight();
            }
        });
    }

    public class PostProfileVH extends RecyclerView.ViewHolder{

        ImageView postImage, videoTemp;

        public PostProfileVH(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.iamgeView);
            videoTemp = itemView.findViewById(R.id.tempVideo);
        }
    }
}