package com.rohitanabhavane.coachsir.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginOrSignUpActivity.class));
                }
                finish();
            }
        }, 2000);

    }

}

