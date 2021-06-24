package com.example.chats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        if (!introPref.isFirstTimeLaunch()){
            LoadRegisterActivity();
            finish();
        }

        continuee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadRegisterActivity();
            }
        });
    }

    private void LoadRegisterActivity() {
        introPref.setIsFirstTimeLaunch(false);
        startActivity(new Intent(welcomeInfoActivity.this, LogRegActivity.class));
        finish();
    }
}