package com.example.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

public class NoInternetActivity extends AppCompatActivity {

    Button wifiSet, cellSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        wifiSet =findViewById(R.id.wifiSet);
        cellSet = findViewById(R.id.callSet);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startActivity(new Intent(android.provider.Settings.Panel.ACTION_INTERNET_CONNECTIVITY));
        }

        wifiSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
            }
        });

        cellSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
            }
        });
    }
}