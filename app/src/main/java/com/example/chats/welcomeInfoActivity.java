package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class welcomeInfoActivity extends AppCompatActivity {
    private IntroPref introPref;
    private Button continuee;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_info);
        continuee = findViewById(R.id.continuee);
        introPref = new IntroPref(this);
        mAuth = FirebaseAuth.getInstance();




        if (CheckConnection()){
            if (!introPref.isFirstTimeLaunch()){
                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() != null){
                            LoadHomeActivity();
                            finish();
                        }else {
                            LoadRegisterActivity();
                            finish();
                        }
                    }
                };

                mAuth.addAuthStateListener(mAuthListener);
            }
        }else {
            if (!introPref.isFirstTimeLaunch()){
                LoadNetworkActivity();
                finish();
            }
        }



        continuee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadRegisterActivity();
            }
        });
    }

    private boolean CheckConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            //we are not connected to a network
            return false;
    }

    private void LoadHomeActivity(){
        introPref.setIsFirstTimeLaunch(false);
        Intent intent = new Intent(welcomeInfoActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void LoadRegisterActivity() {
        introPref.setIsFirstTimeLaunch(false);
        Intent intent = new Intent(welcomeInfoActivity.this, LogRegActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void LoadNetworkActivity() {
        introPref.setIsFirstTimeLaunch(false);
        startActivity(new Intent(welcomeInfoActivity.this, NoInternetActivity.class));
        finish();
    }
}