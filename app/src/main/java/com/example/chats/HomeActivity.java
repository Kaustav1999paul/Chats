package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.chats.Fragments.ChatFragment;
import com.example.chats.Fragments.ClipsFragment;
import com.example.chats.Fragments.FriendsFragment;
import com.example.chats.Fragments.GroupFragment;
import com.example.chats.Fragments.SearchFragment;
import com.example.chats.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DatabaseReference reference;
    FirebaseUser user;
    public static final String MY_PREFS_NAME = "Dock";
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = FirebaseAuth.getInstance().getCurrentUser();
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (savedInstanceState == null){

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            int idName = prefs.getInt("idName", 1);

            if (idName == 1){
                bottomNavigationView.setSelectedItemId(R.id.chat);
                handleFrames(new ChatFragment() );
            }
            if (idName == 2){
                bottomNavigationView.setSelectedItemId(R.id.groups);
                handleFrames(new GroupFragment() );
            }
            if (idName == 4){
                bottomNavigationView.setSelectedItemId(R.id.status);
                handleFrames(new ClipsFragment() );
            }
            if (idName == 5){
                bottomNavigationView.setSelectedItemId(R.id.friends);
                handleFrames(new FriendsFragment() );
            }
            if (idName == 6){
                bottomNavigationView.setSelectedItemId(R.id.setting);
                handleFrames(new SettingsFragment() );
            }


        }
        handleOnClickListner();
    }

    private void handleFrames(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.fragment_holder, fragment);
        fragmentTransaction.commit();
    }

    private void handleOnClickListner(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.chat:
                        handleFrames(new ChatFragment());
                        editor.putInt("idName", 1);
                        editor.apply();
                        break;
                    case R.id.groups:
                        handleFrames(new GroupFragment());
                        editor.putInt("idName", 2);
                        editor.apply();
                        break;
                    case R.id.status:
                        handleFrames(new ClipsFragment());
                        editor.putInt("idName", 4);
                        editor.apply();
                        break;
                    case R.id.friends:
                        handleFrames(new FriendsFragment());
                        editor.putInt("idName", 5);
                        editor.apply();
                        break;
                    case R.id.setting:
                        handleFrames(new SettingsFragment());
                        editor.putInt("idName", 6);
                        editor.apply();
                        break;
                }
                return true;
            }
        });
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        status("Last active: "+sdf.format(date));
    }
}