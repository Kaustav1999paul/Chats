package com.example.chats;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricAuthActivity extends AppCompatActivity {

    ImageView fIcon, circleZoom;
    Animation zoom;

    ConstraintLayout backG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_auth);
        fIcon = findViewById(R.id.fIcon);
        circleZoom = findViewById(R.id.circleZoom);
        backG = findViewById(R.id.backG);
        theme();
        zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        Handler h = new Handler();


        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS:
                fIcon.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.no));
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                shiftUser();
                break;
            case androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                shiftUser();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                shiftUser();
                break;
        }


        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                fIcon.setBackgroundDrawable(ContextCompat.getDrawable(BiometricAuthActivity.this, R.drawable.no));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                fIcon.setBackgroundDrawable(ContextCompat.getDrawable(BiometricAuthActivity.this, R.drawable.yes));
                circleZoom.startAnimation(zoom);


                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backG.setBackgroundColor(getResources().getColor(R.color.backgroundG));
                        shiftUser();
                    }
                }, 501);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                fIcon.setBackgroundDrawable(ContextCompat.getDrawable(BiometricAuthActivity.this, R.drawable.no));
            }
        });

        BiometricPrompt.PromptInfo promptInfo  = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate")
                .setSubtitle("Use your Fingerprint")
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private void shiftUser() {
        Intent inn = new Intent(BiometricAuthActivity.this, welcomeInfoActivity.class);
        inn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(inn);
        finish();
    }

    private void theme() {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}