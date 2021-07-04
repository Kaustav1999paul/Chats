package com.example.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class welcomeInfoActivity extends AppCompatActivity {
    private IntroPref introPref;
    private Button continuee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_info);
        continuee = findViewById(R.id.continuee);
        introPref = new IntroPref(this);

        if (CheckConnection()){
            if (!introPref.isFirstTimeLaunch()){
                LoadRegisterActivity();
                finish();
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
    private void LoadRegisterActivity() {
        introPref.setIsFirstTimeLaunch(false);
        startActivity(new Intent(welcomeInfoActivity.this, LogRegActivity.class));
        finish();
    }
    private void LoadNetworkActivity() {
        introPref.setIsFirstTimeLaunch(false);
        startActivity(new Intent(welcomeInfoActivity.this, NoInternetActivity.class));
        finish();
    }
}