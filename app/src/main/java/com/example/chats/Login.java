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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.r0adkll.slidr.Slidr;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import id.voela.actrans.AcTrans;

public class Login extends AppCompatActivity {

    FloatingActionButton back;
    EditText email, password;
    CircularProgressButton signin;
    TextView fp;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Slidr.attach(this);
        theme();
        fp = findViewById(R.id.fp);
        back = findViewById(R.id.back);
        email = findViewById(R.id.emailL);
        password = findViewById(R.id.passwordL);
        signin = findViewById(R.id.signin);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(Login.this).performSlideToRight();
            }
        });
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPasswordActivity.class);
                startActivity(intent);
                new AcTrans.Builder(Login.this).performSlideToTop();
            }
        });
        auth = FirebaseAuth.getInstance();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String b = email.getText().toString().trim();
                String c = password.getText().toString().trim();

                if (TextUtils.isEmpty(b) || TextUtils.isEmpty(c)){
                    Toast.makeText(Login.this, "All the fields should be filled!", Toast.LENGTH_SHORT).show();
                }else if (c.length() < 6){
                    Toast.makeText(Login.this, "Password should be larger than 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    signin.startAnimation();

                    login(b,c);
                }
            }
        });


    }

    private void login(String email, String pass){
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                }else {
                    Toast.makeText(Login.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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