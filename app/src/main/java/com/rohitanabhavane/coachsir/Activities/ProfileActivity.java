package com.rohitanabhavane.coachsir.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rohitanabhavane.coachsir.R;

public class ProfileActivity extends AppCompatActivity {

    Button btnLogout, btnBecomeCoach, btnEditProfile, btnEditPreference;
    TextView profileFullName, profileEmail, profileMobile, profileDOB, profilePinCode;
    ImageView imgProfileEdit, ProfileImage;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    String loggedInEmail, strFullName, strEmail, strMobile, strDOB, strPinCode, strProfileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }

        profileFullName = findViewById(R.id.profileFullName);
        profileEmail = findViewById(R.id.profileEmail);
        profileMobile = findViewById(R.id.profileMobile);
        profileDOB = findViewById(R.id.profileDOB);
        profilePinCode = findViewById(R.id.profilePinCode);
        ProfileImage = findViewById(R.id.profileImage);
        imgProfileEdit = findViewById(R.id.btnEditProfile);


        btnLogout = findViewById(R.id.btnLogout);
        btnBecomeCoach = findViewById(R.id.btnBecomeCoach);


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
                                strDOB = documentSnapshot.getString("DOB");
                                strPinCode = documentSnapshot.getString("PinCode");
                                strProfileImg = documentSnapshot.getString("ProfileImgLink");

                                profileFullName.setText(strFullName);
                                profileEmail.setText(strEmail);
                                profileMobile.setText(strMobile);
                                profileDOB.setText(strDOB);
                                profilePinCode.setText(strPinCode);

                                if (strProfileImg == null){

                                }else {
                                    Glide.with(ProfileActivity.this).load(strProfileImg).into(ProfileImage);
                                }

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBecomeCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, BecomeACoachActivity.class);
                startActivity(intent);
            }
        });

        imgProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}