package com.example.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.example.chats.Adapter.MessageAdapter;
import com.example.chats.Fragments.APIService;
import com.example.chats.Model.Chat;
import com.example.chats.Model.User;
import com.example.chats.Notifications.Client;
import com.example.chats.Notifications.Data;
import com.example.chats.Notifications.MyResponse;
import com.example.chats.Notifications.Sender;
import com.example.chats.Notifications.Token;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import id.voela.actrans.AcTrans;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView userName,statusAccount;
    FirebaseUser fuser;
    LinearLayout more;
    RelativeLayout loading;
    DatabaseReference reference;
    Intent intent;
    EditText text_send;
    FloatingActionButton sendButton, addButton;
    String userId, photo, name;
    MessageAdapter messageAdapter;
    ImageView back;
    List<Chat> mchat;
    RecyclerView recyclerView;
    ValueEventListener seenListener;
    boolean notify = false;
    String a, b,c,d,e, shareID;
    private Uri imageUri1  = null;
    private String myUrl1 = "", checker="";
    StorageReference storagePostPictureRef;
    int PICKFILE_REQUEST_CODE =121;
    int PICKVIDEO_REQUEST_CODE =120;
    BottomSheetDialog mBottomDialogNotificationAction;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    DatabaseReference userRef;
    APIService apiService;
    boolean notifyy = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Slidr.attach(this);
        recyclerView = findViewById(R.id.ChatRecyclerView);
        sendButton = findViewById(R.id.sendButton);
        text_send = findViewById(R.id.text_send);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        statusAccount = findViewById(R.id.statusAccount);
        back = findViewById(R.id.back);
        more = findViewById(R.id.more);
        addButton = findViewById(R.id.addButton);
        loading = findViewById(R.id.loading);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(MessageActivity.this).performSlideToRight();
            }
        });


//        Prevent ScreenShot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        storagePostPictureRef = FirebaseStorage.getInstance().getReference("Media");
        intent = getIntent();
        userId = intent.getStringExtra("userid");
        photo = intent.getStringExtra("photo");
        name = intent.getStringExtra("name");
        profile_image = findViewById(R.id.profileDP);
        userName = findViewById(R.id.userNameChat);
        userName.setText(name);
        Glide.with(this).load(photo).into(profile_image);
        fuser = FirebaseAuth.getInstance().getCurrentUser();



        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessage(fuser.getUid(), userId);
                a = snapshot.child("imageURL").getValue().toString();
                Glide.with(getApplicationContext()).load(a).into(profile_image);
                b = snapshot.child("username").getValue().toString();
                c = snapshot.child("email").getValue().toString();
                d = snapshot.child("phno").getValue().toString();
                e = snapshot.child("status").getValue().toString();
                statusAccount.setText(e);
                if (e.equals("online"))
                    statusAccount.setTextColor(Color.parseColor("#00E47A"));
                else
                    statusAccount.setTextColor(Color.parseColor("#858585"));

                userName.setText(b);
                shareID = snapshot.child("id").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProf();
            }
        });


        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAccDetailsActionSheet();
            }
        });
        seenMessage(userId);
        sendButton.setVisibility(View.GONE);

        text_send.addTextChangedListener(new TextWatcher() {
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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                notifyy = true;
                String msg = text_send.getText().toString().trim();
                if (msg.equals("")){

                }else {
                        Date date = new Date();
                        // Pattern
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//                                  Current User||other user||message|| system Time
                        sendMessage(fuser.getUid(), userId, msg, sdf.format(date));
                }
                text_send.setText("");
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Call bottomSheet

                showDialogNotificationAction();


            }
        });

        statu("online", fuser.getUid());


    }

    private void showProf() {
        try{
            View sheetView = MessageActivity.this.getLayoutInflater().inflate(R.layout.person_photo, null);
            mBottomDialogNotificationAction = new BottomSheetDialog(this);
            mBottomDialogNotificationAction.setContentView(sheetView);
            mBottomDialogNotificationAction.show();
            ImageView pho = sheetView.findViewById(R.id.pho);

            Glide.with(getApplicationContext()).load(a).into(pho);
            FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showAccDetailsActionSheet() {
        try {
            View sheetView = MessageActivity.this.getLayoutInflater().inflate(R.layout.activity_other_acc_details, null);
            mBottomDialogNotificationAction = new BottomSheetDialog(this);
            mBottomDialogNotificationAction.setContentView(sheetView);
            mBottomDialogNotificationAction.show();

            ImageView gAvatar = sheetView.findViewById(R.id.gAvatar);
            LinearLayout close = sheetView.findViewById(R.id.close);
            LinearLayout call = sheetView.findViewById(R.id.call);
            LinearLayout chat = sheetView.findViewById(R.id.chat);
            LinearLayout email = sheetView.findViewById(R.id.email);
            TextView gName = sheetView.findViewById(R.id.gName);
            TextView gEmail = sheetView.findViewById(R.id.gDate);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                }
            });


            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                    text_send.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(text_send, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+c)));
                }
            });

            gName.setText(b);
            gEmail.setText(c);
            Glide.with(getApplicationContext()).load(a).into(gAvatar);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();

                   makePhoneCall();

                }
            });


            // Remove default white color background
            FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showDialogNotificationAction() {
        try {
            View sheetView = MessageActivity.this.getLayoutInflater().inflate(R.layout.dialog_bottom_notification_action, null);
            mBottomDialogNotificationAction = new BottomSheetDialog(this);
            mBottomDialogNotificationAction.setContentView(sheetView);
            mBottomDialogNotificationAction.show();

            LinearLayout image = sheetView.findViewById(R.id.photoAdd);
            LinearLayout docs = sheetView.findViewById(R.id.docs);
            LinearLayout video = sheetView.findViewById(R.id.videos);
            LinearLayout location = sheetView.findViewById(R.id.location);
            LinearLayout close = sheetView.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                }
            });

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checker="location";

                    if (ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                MessageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISSION
                        );
                    } else {
                        getCurrentLocation();
                        mBottomDialogNotificationAction.dismiss();
                    }

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
                    text_send.setEnabled(false);
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
                    text_send.setEnabled(false);
                    sendButton.setEnabled(false);
                    back.setEnabled(false);
                    mBottomDialogNotificationAction.dismiss();
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checker = "image";
                CropImage.activity().start(MessageActivity.this);
                loading.setVisibility(View.VISIBLE);
                addButton.setEnabled(false);
                text_send.setEnabled(false);
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

    private void getCurrentLocation() {
        loading.setVisibility(View.VISIBLE);
        addButton.setEnabled(false);
        text_send.setEnabled(false);
        sendButton.setEnabled(false);
        back.setEnabled(false);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.getFusedLocationProviderClient(MessageActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MessageActivity.this)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Date date = new Date();
                            // Pattern
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

                            sendLocation(fuser.getUid(), userId, latitude +" "+longitude, sdf.format(date));

                        }
                        else {
                            loading.setVisibility(View.GONE);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void statu(String status, String fuser){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser);
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 347){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MessageActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 347);

        }else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+d));
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
//            Here the if condition will be checked for images and doc files
            if (checker.equals("image")){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri1 = result.getUri();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
                String saveCurrentDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                String saveCurrentTime = currentTime.format(calendar.getTime());
                String ProductRandomKey = fuser.getUid() + saveCurrentDate + saveCurrentTime;

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
//                                  Current User||other user||message|| system Time
                            sendImg(fuser.getUid(), userId, myUrl1, sdf.format(date));
                            loading.setVisibility(View.GONE);
                            addButton.setEnabled(true);
                            text_send.setEnabled(true);
                            sendButton.setEnabled(true);
                            back.setEnabled(true);
                        }
                    }
                });
            }
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
                        sendDocs(fuser.getUid(), userId, myUrl1, sdf.format(date));
                        loading.setVisibility(View.GONE);
                        addButton.setEnabled(true);
                        text_send.setEnabled(true);
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
                        sendVideo(fuser.getUid(), userId, myUrl1, sdf.format(date));
                        loading.setVisibility(View.GONE);
                        addButton.setEnabled(true);
                        text_send.setEnabled(true);
                        sendButton.setEnabled(true);
                        back.setEnabled(true);
                    }
                }
            });
        }

        else {
            loading.setVisibility(View.GONE);
            addButton.setEnabled(true);
            text_send.setEnabled(true);
            sendButton.setEnabled(true);
            back.setEnabled(true);
        }
    }

    private void sendLocation(String sender, String receiver, String message, String time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime+sender+receiver;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("isLiked", "false");
        hashMap.put("type", "location");
        hashMap.put("isTextOnly", false);
        reference.child("Chats").child(RandomKey).setValue(hashMap);
        loading.setVisibility(View.GONE);
        addButton.setEnabled(true);
        text_send.setEnabled(true);
        sendButton.setEnabled(true);
        back.setEnabled(true);

    }

    private void sendVideo(String sender, String receiver, String message, String time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime+sender+receiver;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("isLiked", "false");
        hashMap.put("type", "video");
        hashMap.put("isTextOnly", false);
        reference.child("Chats").child(RandomKey).setValue(hashMap);

    }

    private void sendDocs(String sender, String receiver, String message, String time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime+sender+receiver;



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("isLiked", "false");
        hashMap.put("type", "docs");
        hashMap.put("isTextOnly", false);
        reference.child("Chats").child(RandomKey).setValue(hashMap);
    }

    private void seenMessage(String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendImg(String sender, String receiver, String message, String time){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime+sender+receiver;



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("isLiked", "false");
        hashMap.put("type", "image");
        hashMap.put("isTextOnly", false);
        reference.child("Chats").child(RandomKey).setValue(hashMap);
    }

    private void sendMessage(String sender, String receiver, String message, String time){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = saveCurrentDate + saveCurrentTime+sender+receiver;


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("type", "text");
        hashMap.put("isLiked", "false");
        hashMap.put("isTextOnly", true);
        reference.child("Chats").child(RandomKey).setValue(hashMap);


        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notifyy){
                    sendNotification(receiver, user.getUsername(), msg);
                }

                notifyy = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, String username, String message) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),
                            R.drawable.ic_launcher_foreground,
                            ""+message,
                            ""+username,
                            userId);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                if(response.body().success != 1){
                                    Toast.makeText(MessageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readMessage(String myid, String userid){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid) ){

                        if (chat.getType().equals("image")){
                            chat.setTextOnly(false);
                            mchat.add(chat);
                        }else {
                            chat.setTextOnly(true);
                            mchat.add(chat);
                        }
                    }
                    messageAdapter = new MessageAdapter(getApplicationContext(), mchat, (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(this).performSlideToRight();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userId);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        currentUser("none");
        status("Last active: "+sdf.format(date));
    }
}