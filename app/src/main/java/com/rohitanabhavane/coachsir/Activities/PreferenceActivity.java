package com.rohitanabhavane.coachsir.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohitanabhavane.coachsir.R;
import com.rohitanabhavane.coachsir.utilities.NetworkChangeListener;

import java.util.Calendar;
import java.util.HashMap;

public class PreferenceActivity extends AppCompatActivity {

    Button btnMale, btnFemale, btnAny, btnCricket, btnFootball, btnSportBoth, btnIndividual, btnGroup, btnTrainingBoth, btnFinish;
    String strCoachGender = "Coach Gender";
    String strSports = "Sports";
    String strTraining = "Training Type";
    String date, pinCode, strFullName, strEmail, strMobile, strPassword;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    EditText edtDOB, edtPinCode;
    int currentYear, selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        edtDOB = findViewById(R.id.edtCalender);
        edtPinCode = findViewById(R.id.edtPinCode);
        progressBar = findViewById(R.id.preferenceProgressbar);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        btnAny = findViewById(R.id.btnAny);
        btnCricket = findViewById(R.id.btnCricket);
        btnFootball = findViewById(R.id.btnFootball);
        btnSportBoth = findViewById(R.id.btnSportBoth);
        btnIndividual = findViewById(R.id.btnIndividual);
        btnGroup = findViewById(R.id.btnGroup);
        btnTrainingBoth = findViewById(R.id.btnTrainingBoth);
        btnFinish = findViewById(R.id.btnFinish);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //FireStore
        mFirestore = FirebaseFirestore.getInstance();

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
                        PreferenceActivity.this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + " / " + month + " / " + year;
                        edtDOB.setText(date);
                        selectedYear = year;
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnMale.setOnClickListener(new CoachGender());
        btnFemale.setOnClickListener(new CoachGender());
        btnAny.setOnClickListener(new CoachGender());

        btnCricket.setOnClickListener(new Sports());
        btnFootball.setOnClickListener(new Sports());
        btnSportBoth.setOnClickListener(new Sports());

        btnIndividual.setOnClickListener(new TrainingType());
        btnGroup.setOnClickListener(new TrainingType());
        btnTrainingBoth.setOnClickListener(new TrainingType());

        Intent intent = getIntent();
        strFullName = intent.getStringExtra("FullName");
        strEmail = intent.getStringExtra("Email");
        strMobile = intent.getStringExtra("MobileNumber");
        strPassword = intent.getStringExtra("Password");


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = edtDOB.getText().toString();
                pinCode = edtPinCode.getText().toString();
                if (!strCoachGender.equals("Coach Gender")) {
                    if (!strSports.equals("Sports")) {
                        if (!strTraining.equals("Training Type")) {
                            if (!TextUtils.isEmpty(date)) {
                                if (!TextUtils.isEmpty(pinCode)) {

                                    UploadAuthData();

                                } else {
                                    edtPinCode.setError("Enter Pin Code");
                                }

                            } else {
                                edtDOB.setError("Enter Date of Birth");
                            }

                        } else {
                            Toast.makeText(PreferenceActivity.this, "Select Training Type", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(PreferenceActivity.this, "Select Sports", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PreferenceActivity.this, "Select Coach Gender", Toast.LENGTH_SHORT).show();
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

    public class CoachGender implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnMale:
                    strCoachGender = "Male";
                    if (strCoachGender.equals("Male")) {
                        btnMale.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnMale.setTextColor(Color.parseColor("#FFFFFF"));
                        btnFemale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnFemale.setTextColor(Color.parseColor("#000000"));
                        btnAny.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnAny.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                case R.id.btnFemale:
                    strCoachGender = "Female";
                    if (strCoachGender.equals("Female")) {
                        btnFemale.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnFemale.setTextColor(Color.parseColor("#FFFFFF"));
                        btnMale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnMale.setTextColor(Color.parseColor("#000000"));
                        btnAny.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnAny.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                case R.id.btnAny:
                    strCoachGender = "Any";
                    if (strCoachGender.equals("Any")) {
                        btnAny.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnAny.setTextColor(Color.parseColor("#FFFFFF"));
                        btnMale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnMale.setTextColor(Color.parseColor("#000000"));
                        btnFemale.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnFemale.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                default:
                    Toast.makeText(PreferenceActivity.this, "Select a Coach Gender", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class Sports implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnCricket:
                    strSports = "Cricket";
                    if (strSports.equals("Cricket")) {
                        btnCricket.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnCricket.setTextColor(Color.parseColor("#FFFFFF"));
                        btnFootball.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnFootball.setTextColor(Color.parseColor("#000000"));
                        btnSportBoth.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnSportBoth.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                case R.id.btnFootball:
                    strSports = "Football";
                    if (strSports.equals("Football")) {
                        btnFootball.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnFootball.setTextColor(Color.parseColor("#FFFFFF"));
                        btnCricket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnCricket.setTextColor(Color.parseColor("#000000"));
                        btnSportBoth.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnSportBoth.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                case R.id.btnSportBoth:
                    strSports = "Both";
                    if (strSports.equals("Both")) {
                        btnSportBoth.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnSportBoth.setTextColor(Color.parseColor("#FFFFFF"));
                        btnCricket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnCricket.setTextColor(Color.parseColor("#000000"));
                        btnFootball.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnFootball.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                default:
                    Toast.makeText(PreferenceActivity.this, "Select a Sports", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class TrainingType implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnIndividual:
                    strTraining = "Individual";
                    if (strTraining.equals("Individual")) {
                        btnIndividual.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnIndividual.setTextColor(Color.parseColor("#FFFFFF"));
                        btnGroup.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnGroup.setTextColor(Color.parseColor("#000000"));
                        btnTrainingBoth.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnTrainingBoth.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                case R.id.btnGroup:
                    strTraining = "Group";
                    if (strTraining.equals("Group")) {
                        btnGroup.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnGroup.setTextColor(Color.parseColor("#FFFFFF"));
                        btnIndividual.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnIndividual.setTextColor(Color.parseColor("#000000"));
                        btnTrainingBoth.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnTrainingBoth.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                case R.id.btnTrainingBoth:
                    strTraining = "Both";
                    if (strTraining.equals("Both")) {
                        btnTrainingBoth.setBackgroundColor(Color.parseColor("#1CD6CE"));
                        btnTrainingBoth.setTextColor(Color.parseColor("#FFFFFF"));
                        btnIndividual.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnIndividual.setTextColor(Color.parseColor("#000000"));
                        btnGroup.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        btnGroup.setTextColor(Color.parseColor("#000000"));
                    }
                    break;

                default:
                    Toast.makeText(PreferenceActivity.this, "Select a Training Type", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void UploadAuthData() {
        progressBar.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.INVISIBLE);

        mAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UploadFireStoreData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PreferenceActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                btnFinish.setVisibility(View.VISIBLE);
            }
        });

    }

    private void UploadFireStoreData() {
        HashMap<String, Object> addUser = new HashMap<>();
        addUser.put("FullName", strFullName);
        addUser.put("Email", strEmail);
        addUser.put("Mobile", strMobile);
        addUser.put("Password", strPassword);
        addUser.put("CoachGender", strCoachGender);
        addUser.put("Sports", strSports);
        addUser.put("TrainingType", strTraining);
        addUser.put("PinCode", pinCode);
        addUser.put("DOB", date);


        mFirestore.collection("User").document(strEmail)
                .set(addUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(PreferenceActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnFinish.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(PreferenceActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}