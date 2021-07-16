package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;


public class ProfileDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    private String id, aa, bb, cc, dd, ee;
    ImageView personImage;
    FirebaseUser user;
    Button updateAccount;
    TextView personEmail;
    private EditText personName, bio;
    CollapsingToolbarLayout collapseActionView;
    DatabaseReference userRef;
    private LinearLayout call, chat, email,controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        toolbar = findViewById(R.id.toolbar);
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

        id = intent.getStringExtra("id");
        user = FirebaseAuth.getInstance().getCurrentUser();

        userRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aa = snapshot.child("imageURL").getValue().toString();
                Glide.with(getApplicationContext()).load(aa).into(personImage);
                //myUrl1 = aa;
                cc = snapshot.child("username").getValue().toString();
                dd = snapshot.child("bio").getValue().toString();
                ee = snapshot.child("phno").getValue().toString();

                collapseActionView.setTitleEnabled(false);
                getSupportActionBar().setTitle("");
                bb = snapshot.child("email").getValue().toString();

                personName.setText(cc);
                bio.setText(dd);
                personEmail.setText(bb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        if (id.equals(user.getUid())){
            personImage.setClickable(true);
            personName.setEnabled(true);
            bio.setEnabled(true);
            updateAccount.setVisibility(View.VISIBLE);
            controller.setVisibility(View.GONE);
        }

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}