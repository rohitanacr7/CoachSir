package com.rohitanabhavane.coachsir.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rohitanabhavane.coachsir.R;
import com.rohitanabhavane.coachsir.utilities.NetworkChangeListener;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {

    PinView pinView;
    String strFullName, strEmail, strPassword, strMobile, strMobileNumber, codeBySystem;
    String IN = "+91 ";
    TextView txtMobileNumber;
    FirebaseAuth mAuth;
    Button btnOTPVerify, btnResendOTPEnable, btnResendOTPDisable;
    boolean isBtnEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverfication);

        pinView = findViewById(R.id.PinView);
        txtMobileNumber = findViewById(R.id.txtMobileNumber);
        btnOTPVerify = findViewById(R.id.btnOTPVerify);
        btnResendOTPEnable = findViewById(R.id.btnResentOTPEnable);
        btnResendOTPDisable = findViewById(R.id.btnResentOTPDisable);

        mAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        strFullName = intent.getStringExtra("FullName");
        strEmail = intent.getStringExtra("Email");
        strPassword = intent.getStringExtra("Password");
        strMobile = intent.getStringExtra("Mobile");
        strMobileNumber = IN + strMobile;
        txtMobileNumber.setText(strMobileNumber);

        sendVerificationCodeToUser(strMobileNumber);

        btnOTPVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = pinView.getText().toString();
                if (!code.isEmpty()) {
                    verifyCode(code);
                }
            }
        });

        if (!isBtnEnable) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnResendOTPEnable.setEnabled(true);
                    btnResendOTPEnable.setVisibility(View.VISIBLE);
                    btnResendOTPDisable.setVisibility(View.GONE);

                    btnResendOTPEnable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendVerificationCodeToUser(strMobileNumber);
                            btnResendOTPEnable.setEnabled(false);
                            btnResendOTPEnable.setVisibility(View.GONE);
                            btnResendOTPDisable.setVisibility(View.VISIBLE);

                        }
                    });
                }
            }, 30000);
        }


    }

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void sendVerificationCodeToUser(String strMobileNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(strMobileNumber)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(OTPVerificationActivity.this) // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pinView.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPVerificationActivity.this, "Error - " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OTPVerificationActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OTPVerificationActivity.this, PreferenceActivity.class);
                            intent.putExtra("FullName",strFullName);
                            intent.putExtra("Email",strEmail);
                            intent.putExtra("MobileNumber", strMobileNumber);
                            intent.putExtra("Password", strPassword);
                            startActivity(intent);
                            finish();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(OTPVerificationActivity.this, "Verification Not Completed! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}