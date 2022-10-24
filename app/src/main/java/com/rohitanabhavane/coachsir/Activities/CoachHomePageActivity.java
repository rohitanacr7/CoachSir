package com.rohitanabhavane.coachsir.Activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohitanabhavane.coachsir.R;
import com.rohitanabhavane.coachsir.utilities.NetworkChangeListener;

public class CoachHomePageActivity extends AppCompatActivity {
    ImageView imgProfile, btnCoachProfileBack;
    TextView txtFullName, txtEmail, txtMobile, txtDesc, txtAddress, txtPersonalTrainingFees, txtGroupTrainingFees, txtTrainingGroundDetails;
    CardView btnDesc, btnAddress, btnPersonalTrainingFees, btnGroupTrainingFees, btnSkills, btnTrainingTiming, btnLogout, btnTrainingGroundDetails;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String loggedInEmail, strFullName, strEmail, strMobile, strDesc, strAddress, strPersonalTrainingFees, strGroupTrainingFees, strProfileImg, strTrainingGroundDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_home_page);

        Initialization();
        FetchDataFromDatabase();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(CoachHomePageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnCoachProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoachHomePageActivity.this, UpdateDescActivity.class);
                startActivity(intent);
            }
        });
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoachHomePageActivity.this, UpdateAddressActivity.class);
                startActivity(intent);
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

    private void FetchDataFromDatabase() {
        mFirestore.collection("User").document(loggedInEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;
                            if (documentSnapshot.exists()) {
                                strFullName = documentSnapshot.getString("FullName");
                                strEmail = documentSnapshot.getString("Email");
                                strMobile = documentSnapshot.getString("Mobile");
                                strDesc = documentSnapshot.getString("Desc");
                                strPersonalTrainingFees = documentSnapshot.getString("PersonalTrainingFees");
                                strGroupTrainingFees = documentSnapshot.getString("GroupTrainingFees");
                                strTrainingGroundDetails = documentSnapshot.getString("TrainingGroundDetails");
                                String strAdd = documentSnapshot.getString("Address");
                                String strCity = documentSnapshot.getString("City");
                                String strState = documentSnapshot.getString("State");
                                String strPinCode = documentSnapshot.getString("PinCode");
                                strProfileImg = documentSnapshot.getString("ProfileImgLink");

                                strAddress = strAdd + ", " + strCity + " - " + strPinCode + ", " + strState;

                                txtFullName.setText(strFullName);
                                txtEmail.setText(strEmail);
                                txtMobile.setText(strMobile);
                                txtDesc.setText(strDesc);
                                txtAddress.setText(strAddress);
                                txtPersonalTrainingFees.setText(strPersonalTrainingFees);
                                txtGroupTrainingFees.setText(strGroupTrainingFees);
                                txtTrainingGroundDetails.setText(strTrainingGroundDetails);

                                if (strProfileImg == null) {
                                    Toast.makeText(CoachHomePageActivity.this, "Upload a Profile Picture", Toast.LENGTH_SHORT).show();
                                } else {
                                    Glide.with(CoachHomePageActivity.this).load(strProfileImg).into(imgProfile);
                                }

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CoachHomePageActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Initialization() {
        imgProfile = findViewById(R.id.imgCoachHomeProfile);
        btnCoachProfileBack = findViewById(R.id.btnCoachProfileBack);

        txtFullName = findViewById(R.id.txtCoachHomeFullName);
        txtEmail = findViewById(R.id.txtCoachHomeEmail);
        txtMobile = findViewById(R.id.txtCoachHomeMobile);
        txtDesc = findViewById(R.id.txtCoachHomeDesc);
        txtAddress = findViewById(R.id.txtCoachHomeAddress);
        txtPersonalTrainingFees = findViewById(R.id.txtCoachHomePersonalTrainingFees);
        txtGroupTrainingFees = findViewById(R.id.txtCoachHomeGroupTrainingFees);
        txtTrainingGroundDetails = findViewById(R.id.txtCoachHomeTrainingGroundAddress);

        btnDesc = findViewById(R.id.btnCoachHomeDesc);
        btnAddress = findViewById(R.id.btnCoachHomeAddress);
        btnPersonalTrainingFees = findViewById(R.id.btnCoachHomePersonalTrainingFees);
        btnGroupTrainingFees = findViewById(R.id.btnCoachHomeGroupTrainingFees);
        btnSkills = findViewById(R.id.btnCoachHomeSkills);
        btnTrainingTiming = findViewById(R.id.btnCoachHomeTrainingTiming);
        btnLogout = findViewById(R.id.btnCoachHomeLogout);
        btnTrainingGroundDetails = findViewById(R.id.btnCoachHomeTrainingAddress);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }

    }
}