package com.rohitanabhavane.coachsir.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohitanabhavane.coachsir.R;
import com.rohitanabhavane.coachsir.utilities.NetworkChangeListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText edtFullName, edtEmail, edtMobile, edtDOB, edtPinCode;
    Button btnUpdate;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    int currentYear, selectedYear;
    String loggedInEmail, strFullName, strEmail, strMobile, strPinCode, strDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtFullName = findViewById(R.id.edtEditProfileFullName);
        edtEmail = findViewById(R.id.edtEditProfileUpEmail);
        edtMobile = findViewById(R.id.edtEditProfileMobile);
        edtDOB = findViewById(R.id.edtEditProfileCalender);
        edtPinCode = findViewById(R.id.edtEditProfileEditPinCode);
        btnUpdate = findViewById(R.id.btnEditProfile);
        progressBar = findViewById(R.id.editProfileProgressbar);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }

        mFirestore.collection("User").document(loggedInEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;

                            if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                strFullName = documentSnapshot.getString("FullName");
                                strEmail = documentSnapshot.getString("Email");
                                strMobile = documentSnapshot.getString("Mobile");
                                strDOB = documentSnapshot.getString("DOB");
                                strPinCode = documentSnapshot.getString("PinCode");

                                edtFullName.setText(strFullName);
                                edtEmail.setText(strEmail);
                                edtMobile.setText(strMobile);
                                edtDOB.setText(strDOB);
                                edtPinCode.setText(strPinCode);

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Calendar Code
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditProfileActivity.this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + " / " + month + " / " + year;
                        edtDOB.setText(date);
                        selectedYear = year;

                        int age = currentYear - selectedYear;
                        String strAge = String.valueOf(age);
                        edtDOB.setText(strAge);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strFullName = edtFullName.getText().toString();
                strEmail = edtEmail.getText().toString();
                strMobile = edtMobile.getText().toString();
                strDOB = edtDOB.getText().toString();
                strPinCode = edtPinCode.getText().toString();

                if (!TextUtils.isEmpty(strFullName)) {
                    if (!TextUtils.isEmpty(strEmail)) {
                        if (Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                            if (!TextUtils.isEmpty(strMobile)) {
                                if (strMobile.length() == 10) {
                                    if (!TextUtils.isEmpty(strDOB)) {
                                        if (!TextUtils.isEmpty(strPinCode)) {
                                            if (strPinCode.length() == 6) {
                                                UpdateProfile();
                                            } else {
                                                edtPinCode.setError("Enter a valid Pin Code");
                                            }
                                        } else {
                                            edtPinCode.setError("Pin Code field can't be Empty");
                                        }
                                    } else {
                                        edtDOB.setError("Date of Birth field can't be Empty");
                                    }
                                } else {
                                    edtMobile.setError("Enter a valid Mobile Number");
                                }
                            } else {
                                edtMobile.setError("Mobile field can't be Empty");
                            }
                        } else {
                            edtEmail.setError("Enter a valid Email");
                        }
                    } else {
                        edtEmail.setError("Email field can't be Empty");
                    }
                } else {
                    edtFullName.setError("Full Name field can't be Empty");
                }
            }
        });

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

    private void UpdateProfile() {
        progressBar.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.INVISIBLE);

        Map<String, Object> personalInfoMap = new HashMap<>();
        personalInfoMap.put("FullName", strFullName);
        personalInfoMap.put("Email", strEmail);
        personalInfoMap.put("Mobile", strMobile);
        personalInfoMap.put("DOB", strDOB);
        personalInfoMap.put("PinCode", strPinCode);

        mFirestore.collection("User")
                .document(loggedInEmail)
                .update(personalInfoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnUpdate.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditProfileActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}