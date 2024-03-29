package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import id.voela.actrans.AcTrans;

public class Register extends AppCompatActivity {
    ImageView back;
    EditText username, email, password,phone;
    FirebaseAuth auth;
    DatabaseReference reference, friendsRef;
    FirebaseUser firebaseUser;
    CircularProgressButton regist;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Slidr.attach(this);
        theme();
        back = findViewById(R.id.back);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(Register.this).performSlideToRight();
            }
        });
        auth = FirebaseAuth.getInstance();

        regist = findViewById(R.id.register);




        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = username.getText().toString().trim();
                String b = email.getText().toString().trim();
                String c = password.getText().toString().trim();
                String d = phone.getText().toString().trim();

                if (TextUtils.isEmpty(a) || TextUtils.isEmpty(b) || TextUtils.isEmpty(c)){
                    Toast.makeText(Register.this, "All the fields should be filled!", Toast.LENGTH_SHORT).show();
                }else if (c.length() < 6){
                    Toast.makeText(Register.this, "Password should be larger than 6 characters", Toast.LENGTH_SHORT).show();
                }else if (d.length() < 10){
                    Toast.makeText(Register.this, "Please provide a valid phone Number", Toast.LENGTH_SHORT).show();
                }else {
                    regist.startAnimation();
                    register(a,b,c,d);
                }
            }
        });
    }

    private void register(String uName, String eMail, String passWord, String d){
        auth.createUserWithEmailAndPassword(eMail, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()){
                  firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                  userID = firebaseUser.getUid();
                  reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                  HashMap<String, String> hashMap = new HashMap<>();
                  hashMap.put("id", userID);
                  hashMap.put("username", uName);
                  hashMap.put("email", eMail);
                  hashMap.put("phno", d);
                  hashMap.put("locality", "Durgapur");
                  hashMap.put("bio", "Available!");
                  hashMap.put("status", "offline");
                  hashMap.put("imageURL", "https://firebasestorage.googleapis.com/v0/b/chats-ec34c.appspot.com/o/defaultProfile.jpg?alt=media&token=740df914-84a1-4127-8057-382b9dcbc625");

                  reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){
                              createSelfFriendList();
                          }
                      }
                  });
                  finish();
              }else {
                  Toast.makeText(Register.this, "Server Error!!", Toast.LENGTH_SHORT).show();
              }
            }
        });
    }

    private void createSelfFriendList() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());

        friendsRef = FirebaseDatabase.getInstance().getReference("Friends");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef.child(firebaseUser.getUid())
                .child(firebaseUser.getUid())
                .child("date")
                .setValue(saveCurrentDate);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(this).performSlideToRight();
    }

    private void theme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}