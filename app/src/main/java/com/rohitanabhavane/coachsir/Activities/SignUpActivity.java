package com.rohitanabhavane.coachsir.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rohitanabhavane.coachsir.R;

public class SignUpActivity extends AppCompatActivity {

    TextView txtSignIn;
    Button btnNext;
    EditText edtFullName, edtEmail, edtMobile, edtPassword, edtConfirmPassword;
    ProgressBar progressBar;
    String fullName, email, mobile, password, confirmPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtFullName = findViewById(R.id.edtSignUpFullName);
        edtEmail = findViewById(R.id.edtSignUpEmail);
        edtMobile = findViewById(R.id.edtSignUpMobile);
        progressBar = findViewById(R.id.signUpProgressbar);
        edtPassword = findViewById(R.id.edtSignUpPassword);
        edtConfirmPassword = findViewById(R.id.edtSignUpConfirmPassword);
        txtSignIn = findViewById(R.id.txtSignIn);
        btnNext = findViewById(R.id.btnNext);

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = edtFullName.getText().toString();
                email = edtEmail.getText().toString().trim();
                mobile = edtMobile.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                confirmPassword = edtConfirmPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(fullName)) {
                    if (!TextUtils.isEmpty(email)) {
                        if (email.matches(emailPattern)) {
                            if (!TextUtils.isEmpty(mobile)) {
                                if (mobile.length() == 10) {
                                    if (!TextUtils.isEmpty(password)) {
                                        if (!TextUtils.isEmpty(confirmPassword)) {
                                            if (password.equals(confirmPassword)) {
                                                Intent intent = new Intent(SignUpActivity.this, OTPVerificationActivity.class);
                                                intent.putExtra("FullName", fullName);
                                                intent.putExtra("Email", email);
                                                intent.putExtra("Mobile", mobile);
                                                intent.putExtra("Password", password);
                                                startActivity(intent);
                                            } else {
                                                edtConfirmPassword.setError("Password and Confirm should be same");
                                            }
                                        } else {
                                            edtConfirmPassword.setError("Enter a Password");
                                        }
                                    } else {
                                        edtPassword.setError("Enter a Password");
                                    }
                                } else {
                                    edtMobile.setError("Enter a valid Mobile Number");
                                }
                            } else {
                                edtMobile.setError("Enter Mobile Number");
                            }
                        } else {
                            edtEmail.setError("Enter a valid Email");
                        }
                    } else {
                        edtEmail.setError("Enter Email");
                    }
                } else {
                    edtFullName.setError("Enter Full Name");
                }
            }
        });

    }


}