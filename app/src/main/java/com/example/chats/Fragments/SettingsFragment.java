package com.example.chats.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.BottomSheetFragment;
import com.example.chats.EditAccountActivity;
import com.example.chats.HomeActivity;
import com.example.chats.LogRegActivity;
import com.example.chats.Login;
import com.example.chats.MoreSettingsActivity;
import com.example.chats.NotificationsActivity;
import com.example.chats.R;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;

import id.voela.actrans.AcTrans;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    FloatingActionButton editAccount;
    FirebaseUser user;
    ImageView avatar;
    Toolbar tabanim_toolbar;
    LinearLayout logout,notifications,settings;
    TextView HolderName, emailHolder, bioAcc;
    DatabaseReference userRef;
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        bioAcc = view.findViewById(R.id.bioAcc);
        logout = view.findViewById(R.id.logout);
        tabanim_toolbar = view.findViewById(R.id.tabanim_toolbar);

        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("a");

        String time = sdf.format(date);
        if (time.equals("am"))
            tabanim_toolbar.setTitle("Good Morning,");
        else
            tabanim_toolbar.setTitle("Good Evening,");



        context = getActivity().getApplicationContext();
        notifications = view.findViewById(R.id.notifications);
        settings = view.findViewById(R.id.settings);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        editAccount = view.findViewById(R.id.editAccount);
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationsActivity.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreSettingsActivity.class);
                startActivity(intent);
            }
        });

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditAccountActivity.class);
                startActivity(intent);
                new AcTrans.Builder(getActivity()).performSlideToLeft();
//                BottomSheetFragment bottomSheetDialog = BottomSheetFragment.newInstance();
//                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
        HolderName = view.findViewById(R.id.holderName);
        emailHolder = view.findViewById(R.id.holderEmail);
        avatar = view.findViewById(R.id.avatar);
        emailHolder.setText(user.getEmail());


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(),LogRegActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheetDialog = BottomSheetFragment.newInstance();
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String na = snapshot.child("username").getValue().toString();
                HolderName.setText(na);
                String bio = snapshot.child("bio").getValue().toString();
                bioAcc.setText("Bio: "+bio);
                String ph = snapshot.child("imageURL").getValue().toString();
                Glide.with(context).load(ph).into(avatar);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

}