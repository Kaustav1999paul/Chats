package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import id.voela.actrans.AcTrans;

public class LogRegActivity extends AppCompatActivity {

    TextView login;
    Button register;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // TODO: Register and Login in same Page with different dialog.........

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_reg);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Intent intent = new Intent(LogRegActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } }
        };
        theme();
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogRegActivity.this, Register.class);
                startActivity(intent);
                new AcTrans.Builder(LogRegActivity.this).performSlideToLeft();
            }
        });



    }

    public void showLogin() {

        final Dialog dialog = new Dialog(LogRegActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        EditText email = (EditText) dialog.findViewById(R.id.emailL);
        EditText password = (EditText) dialog.findViewById(R.id.passwordL);
        CircularProgressButton signin = (CircularProgressButton) dialog.findViewById(R.id.signin);
        TextView fp = (TextView) dialog.findViewById(R.id.fp);

        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogRegActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                new AcTrans.Builder(LogRegActivity.this).performSlideToTop();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String b = email.getText().toString().trim();
                String c = password.getText().toString().trim();

                if (TextUtils.isEmpty(b) || TextUtils.isEmpty(c)){
                    Toast.makeText(LogRegActivity.this, "All the fields should be filled!", Toast.LENGTH_SHORT).show();
                }else if (c.length() < 6){
                    Toast.makeText(LogRegActivity.this, "Password should be larger than 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    signin.startAnimation();
                    dialog.setCancelable(false);
                    login(b,c, dialog);
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void login(String email, String pass, Dialog dialog) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                }else {
                    Toast.makeText(LogRegActivity.this, "Authentication Error", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private void theme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}