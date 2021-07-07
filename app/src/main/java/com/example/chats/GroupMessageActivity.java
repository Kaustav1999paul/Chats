package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.Adapter.GroupMessageAdapter;
import com.example.chats.Model.GroupChats;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class GroupMessageActivity extends AppCompatActivity {

    TextView groupNameChat;
    String groupId,a,b;
    Intent intent;
    CircleImageView groupDP;
    ImageView back;
    EditText message;
    FloatingActionButton sendButton, addButton;
    FirebaseUser user;
    ArrayList<GroupChats> mchat;
    DatabaseReference reference;
    private GroupMessageAdapter messageAdapter;
    RecyclerView groupChatRecyclerView;
    private Uri imageUri1  = null;
    private String myUrl1 = "", checker="";
    StorageReference storagePostPictureRef;
    BottomSheetDialog mBottomDialogNotificationAction;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference groupReference;
    int PICKFILE_REQUEST_CODE =121;
    int PICKVIDEO_REQUEST_CODE =120;
    RelativeLayout loading;
    FirebaseUser fuser;


    @Override
    protected void onStart() {
        super.onStart();
        readMessage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

//        Prevent ScreenShot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        groupNameChat = findViewById(R.id.groupNameChat);
        addButton = findViewById(R.id.addButton);
        groupDP = findViewById(R.id.groupDP);
        groupChatRecyclerView = findViewById(R.id.groupChatRecyclerView);
        groupChatRecyclerView.setHasFixedSize(true);
        intent = getIntent();
        loading = findViewById(R.id.loading);
        user = FirebaseAuth.getInstance().getCurrentUser();
        back = findViewById(R.id.back);
        message = findViewById(R.id.messagee);
        sendButton = findViewById(R.id.sendButton);
        groupId = intent.getStringExtra("groupId");
        groupReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        storagePostPictureRef = FirebaseStorage.getInstance().getReference("Media");
        sendButton.setVisibility(View.GONE);
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                a = snapshot.child("photo").getValue().toString();
                Glide.with(getApplicationContext()).load(a).into(groupDP);
                b = snapshot.child("name").getValue().toString();
                groupNameChat.setText(b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        groupNameChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMessageActivity.this, GroupInfoActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
                new AcTrans.Builder(GroupMessageActivity.this).performSlideToBottom();
            }
        });
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        groupChatRecyclerView.setLayoutManager(linearLayoutManager);

        readMessage();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString().trim();
                if (TextUtils.isEmpty(msg)){
                    Toast.makeText(GroupMessageActivity.this, "No message typed!", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        sendMessage(msg, sdf.format(date));
                    }catch (Exception e){
                        Toast.makeText(GroupMessageActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNotificationAction();
            }
        });


    }

    private void showDialogNotificationAction() {
        try{
                View sheetView = GroupMessageActivity.this.getLayoutInflater().inflate(R.layout.dialog_bottom_notification_action, null);
                mBottomDialogNotificationAction = new BottomSheetDialog(this);
                mBottomDialogNotificationAction.setContentView(sheetView);
                mBottomDialogNotificationAction.show();

                View tempView = sheetView.findViewById(R.id.tempView);
                tempView.setVisibility(View.GONE);
                LinearLayout image = sheetView.findViewById(R.id.photoAdd);
                LinearLayout close = sheetView.findViewById(R.id.close);
                LinearLayout docs = sheetView.findViewById(R.id.docs);
                LinearLayout video = sheetView.findViewById(R.id.videos);
                LinearLayout location = sheetView.findViewById(R.id.location);
                location.setVisibility(View.GONE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomDialogNotificationAction.dismiss();
                    }
                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CropImage.activity().start(GroupMessageActivity.this);
                       // loading.setVisibility(View.VISIBLE);
                        addButton.setEnabled(false);
                        message.setEnabled(false);
                        sendButton.setEnabled(false);
                        back.setEnabled(false);
                        mBottomDialogNotificationAction.dismiss();
                    }
                });

            docs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checker="docs";
                    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
// Ask specifically for something that can be opened:
                    chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                    chooseFile.setType("*/*");
                    startActivityForResult(
                            Intent.createChooser(chooseFile, "Choose a file"),
                            PICKFILE_REQUEST_CODE
                    );

                    loading.setVisibility(View.VISIBLE);
                    addButton.setEnabled(false);
                    message.setEnabled(false);
                    sendButton.setEnabled(false);
                    back.setEnabled(false);
                    mBottomDialogNotificationAction.dismiss();
                }
            });

            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checker= "video";
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICKVIDEO_REQUEST_CODE);

                    loading.setVisibility(View.VISIBLE);
                    addButton.setEnabled(false);
                    message.setEnabled(false);
                    sendButton.setEnabled(false);
                    back.setEnabled(false);
                    mBottomDialogNotificationAction.dismiss();
                }
            });

        // Remove default white color background
        FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheet.setBackground(null);
    } catch (Exception e) {
        e.printStackTrace();
    }
    }


    private void readMessage() {
        mchat = new ArrayList<>();

        reference = groupReference.child("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    GroupChats chat = snapshot.getValue(GroupChats.class);
                    mchat.add(chat);

                    messageAdapter = new GroupMessageAdapter(getApplicationContext(), mchat, (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
                    groupChatRecyclerView.setAdapter(messageAdapter);
                    groupChatRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri1 = result.getUri();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
            String saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            String saveCurrentTime = currentTime.format(calendar.getTime());
            String ProductRandomKey = user.getUid() + saveCurrentDate + saveCurrentTime;

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
//                        progress.setVisibility(View.GONE);
//                        saveChanges.setEnabled(true);
//                        bioEdit.setEnabled(true);

                        try {
                            Date date = new Date();
                            // Pattern
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

//                                message || system Time
                          sendImg(myUrl1, sdf.format(date));
                            //loading.setVisibility(View.GONE);
                            addButton.setEnabled(true);
                            message.setEnabled(true);
                            sendButton.setEnabled(true);
                            back.setEnabled(true);
                        }catch (Exception e){
                            Toast.makeText(GroupMessageActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }else if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK){

//                If fileType = Docs
            Uri uri = data.getData();
            String src = uri.getPath();

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
//                        progress.setVisibility(View.GONE);
//                        saveChanges.setEnabled(true);
//                        bioEdit.setEnabled(true);
                        Date date = new Date();
                        // Pattern
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//                                  Current User||other user||message|| system Time
                        sendDocs(myUrl1, sdf.format(date));
                        loading.setVisibility(View.GONE);
                        addButton.setEnabled(true);
                        message.setEnabled(true);
                        sendButton.setEnabled(true);
                        back.setEnabled(true);
                    }
                }
            });

        }else if (requestCode == PICKVIDEO_REQUEST_CODE && resultCode == RESULT_OK){
            //                If fileType = Docs
            Uri uri = data.getData();
            String src = uri.getPath();

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
//                        progress.setVisibility(View.GONE);
//                        saveChanges.setEnabled(true);
//                        bioEdit.setEnabled(true);
                        Date date = new Date();
                        // Pattern
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//                                  Current User||other user||message|| system Time
                        sendVideo(myUrl1, sdf.format(date));
                        loading.setVisibility(View.GONE);
                        addButton.setEnabled(true);
                        message.setEnabled(true);
                        sendButton.setEnabled(true);
                        back.setEnabled(true);
                    }
                }
            });
        }else {
            loading.setVisibility(View.GONE);
            addButton.setEnabled(true);
            message.setEnabled(true);
            sendButton.setEnabled(true);
            back.setEnabled(true);
        }
    }

    private void sendDocs(String myUrl1, String format) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", user.getUid() );
        hashMap.put("message", myUrl1);
        hashMap.put("time", format);
        hashMap.put("type", "docs");
        groupReference.child("Messages").push().setValue(hashMap);
        message.setText("");
    }

    private void sendVideo(String myUrl1, String format) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", user.getUid() );
        hashMap.put("message", myUrl1);
        hashMap.put("time", format);
        hashMap.put("type", "video");
        groupReference.child("Messages").push().setValue(hashMap);
        message.setText("");
    }

    private void sendImg(String myUrl1, String format) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", user.getUid() );
        hashMap.put("message", myUrl1);
        hashMap.put("time", format);
        hashMap.put("type", "image");
        groupReference.child("Messages").push().setValue(hashMap);
        message.setText("");
    }

    private void sendMessage(String encrypted, String time) {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", user.getUid() );
        hashMap.put("message", encrypted);
        hashMap.put("time", time);
        hashMap.put("type", "text");
        groupReference.child("Messages").push().setValue(hashMap);
        message.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}