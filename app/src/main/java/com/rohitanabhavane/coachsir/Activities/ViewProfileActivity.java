package com.rohitanabhavane.coachsir.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rohitanabhavane.coachsir.Adapter.RatingAdapter;
import com.rohitanabhavane.coachsir.Adapter.ViewSkillsAdapter;
import com.rohitanabhavane.coachsir.Adapter.ViewTrainingTimeAdapter;
import com.rohitanabhavane.coachsir.Model.RatingModel;
import com.rohitanabhavane.coachsir.Model.SkillsModels;
import com.rohitanabhavane.coachsir.Model.TrainingTimeModel;
import com.rohitanabhavane.coachsir.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String loggedInEmail, strLoggedFullName, strLoggedMobile, strLoggedProfile;
    RecyclerView rvSkills, rvTrainingTime, rvRating;
    SkillsModels skillsModel;
    ArrayList<SkillsModels> skillsModelArrayList;
    ViewSkillsAdapter viewSkillsAdapter;
    TrainingTimeModel trainingTimeModel;
    RatingModel ratingModel;
    RatingAdapter ratingAdapter;
    ArrayList<RatingModel> ratingModelArrayList;
    ArrayList<TrainingTimeModel> trainingTimeModelArrayList;
    ViewTrainingTimeAdapter viewTrainingTimeAdapter;
    String strProfileImg, strEmail, strFullName, strDesc, strExpYear, strExpMonth, strTrainingGroundDetails, strGroupTrainingFees, strPersonalTrainingFees,
            strSportType;
    TextView txtFullName, txtDesc, txtExpYear, txtExpMonth, txtTrainingGroundDetails, txtGroupTrainingFees, txtPersonalTrainingFees, txtSportType, txtCustomerReviewHead;
    ImageView ProfileImg, bthProfileBack;
    Dialog dialog;
    Button btnReview;
    EditText edtRatingComment;
    RatingBar reviewRatingBar;
    String strRatingComment, star1, star2, star3, star4, star5;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Initialization();
        DataRetrieve();
        getDataFromCoachAdapter();
        setDataToField();
        Skills();
        TrainingTime();
        Rating();

        bthProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog = new Dialog(ViewProfileActivity.this);
        dialog.setContentView(R.layout.review_dialog_box);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.review_dialogbox));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animationReviewDialogBox;
        Button btnSubmitReview = dialog.findViewById(R.id.btnReviewSubmit);
        Button btnCancelReview = dialog.findViewById(R.id.btnReviewCancel);

        btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewRatingBar = dialog.findViewById(R.id.reviewRatingBar);
                edtRatingComment = dialog.findViewById(R.id.edtRatingComment);
                strRatingComment = edtRatingComment.getText().toString();
                if (!strRatingComment.isEmpty()) {

                    int ratingCount = (int) reviewRatingBar.getRating();
                    String rating = String.valueOf(ratingCount);

                    Map<String, Object> ratingMap = new HashMap<>();
                    ratingMap.put("FullName", strLoggedFullName);
                    ratingMap.put("Comment", strRatingComment);
                    ratingMap.put("Email", loggedInEmail);
                    ratingMap.put("Mobile", strLoggedMobile);
                    ratingMap.put("ProfileImgLink", strLoggedProfile);
                    ratingMap.put("rating", rating);

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    ratingMap.put("RatingDateTime", formatter.format(date));


                    Map<String, Object> updateRating = new HashMap<>();
                    if (ratingCount == 1) {
                        int star = Integer.parseInt(star1) + 1;
                        String addedStar = String.valueOf(star);
                        updateRating.put("Star1", addedStar);
                    } else if (ratingCount == 2) {
                        int star = Integer.parseInt(star2) + 1;
                        String addedStar = String.valueOf(star);
                        updateRating.put("Star2", addedStar);
                    } else if (ratingCount == 3) {
                        int star = Integer.parseInt(star3) + 1;
                        String addedStar = String.valueOf(star);
                        updateRating.put("Star3", addedStar);
                    } else if (ratingCount == 4) {
                        int star = Integer.parseInt(star4) + 1;
                        String addedStar = String.valueOf(star);
                        updateRating.put("Star4", addedStar);
                    } else if (ratingCount == 5) {
                        int star = Integer.parseInt(star5) + 1;
                        String addedStar = String.valueOf(star);
                        updateRating.put("Star5", addedStar);
                    }

                    firebaseFirestore.collection("User")
                            .document(strEmail).update(updateRating)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firebaseFirestore.collection("User")
                                            .document(strEmail)
                                            .collection("Rating")
                                            .document(loggedInEmail)
                                            .set(ratingMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Intent intent = new Intent(ViewProfileActivity.this, ViewProfileActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ViewProfileActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ViewProfileActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                dialog.dismiss();
            }
        });
        btnCancelReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


    }

    private void Rating() {
        ratingModel = new RatingModel();
        rvRating.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        rvRating.setLayoutManager(linearLayoutManager);
        ratingModelArrayList = new ArrayList<RatingModel>();
        ratingAdapter = new RatingAdapter(this, ratingModelArrayList);
        rvRating.setAdapter(ratingAdapter);
        firebaseFirestore.collection("User")
                .document(strEmail)
                .collection("Rating")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            if (count == 0) {
                                txtCustomerReviewHead.setVisibility(View.GONE);
                                rvRating.setVisibility(View.GONE);
                            } else {
                                txtCustomerReviewHead.setVisibility(View.VISIBLE);
                                rvRating.setVisibility(View.VISIBLE);
                                RatingListener();
                            }
                        } else {
                            Toast.makeText(ViewProfileActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewProfileActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getDataFromCoachAdapter() {
        Intent intent = getIntent();
        strProfileImg = intent.getStringExtra("ProfileImg");
        strFullName = intent.getStringExtra("FullName");
        strEmail = intent.getStringExtra("Email");
        strDesc = intent.getStringExtra("Desc");
        strExpYear = intent.getStringExtra("ExpYear");
        strExpMonth = intent.getStringExtra("ExpMonth");
        strTrainingGroundDetails = intent.getStringExtra("TrainingGroundDetails");
        strGroupTrainingFees = intent.getStringExtra("GroupTrainingFees");
        strPersonalTrainingFees = intent.getStringExtra("PersonalTrainingFees");
        strSportType = intent.getStringExtra("SportType");

    }

    private void setDataToField() {
        Glide.with(this).load(strProfileImg).into(ProfileImg);
        txtFullName.setText(strFullName);
        txtDesc.setText(strDesc);
        txtExpYear.setText(strExpYear);
        txtExpMonth.setText(strExpMonth);
        txtTrainingGroundDetails.setText(strTrainingGroundDetails);
        txtGroupTrainingFees.setText(strGroupTrainingFees);
        txtPersonalTrainingFees.setText(strPersonalTrainingFees);
        txtSportType.setText(strSportType);
    }

    private void TrainingTime() {
        trainingTimeModel = new TrainingTimeModel();
        rvTrainingTime.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(ViewProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvTrainingTime.setLayoutManager(linearLayoutManager1);
        trainingTimeModelArrayList = new ArrayList<TrainingTimeModel>();
        viewTrainingTimeAdapter = new ViewTrainingTimeAdapter(this, trainingTimeModelArrayList);
        rvTrainingTime.setAdapter(viewTrainingTimeAdapter);
        TrainingTimeEventChangeListener();
    }

    private void TrainingTimeEventChangeListener() {
        firebaseFirestore.collection("User").document(loggedInEmail)
                .collection("TrainingTime").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                trainingTimeModelArrayList.add(dc.getDocument().toObject(TrainingTimeModel.class));
                            }
                            viewTrainingTimeAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void Initialization() {
        //FireBase Firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        //FireBase Authentication
        mAuth = FirebaseAuth.getInstance();
        //FireBase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }

        bthProfileBack = findViewById(R.id.bthProfileBack);

        rvSkills = findViewById(R.id.rvViewSkills);
        rvTrainingTime = findViewById(R.id.rvViewTrainingTime);
        rvRating = findViewById(R.id.rvRating);


        ProfileImg = findViewById(R.id.viewProfileImg);
        txtFullName = findViewById(R.id.viewFullName);
        txtDesc = findViewById(R.id.viewDesc);
        txtDesc = findViewById(R.id.viewDesc);
        txtExpYear = findViewById(R.id.viewExpYear);
        txtExpMonth = findViewById(R.id.viewExpMonths);
        txtTrainingGroundDetails = findViewById(R.id.viewTrainingGroundDetails);
        txtGroupTrainingFees = findViewById(R.id.viewGroupCoachFees);
        txtPersonalTrainingFees = findViewById(R.id.viewPersonalCoachFees);
        txtSportType = findViewById(R.id.viewCoachSportType);

        btnReview = findViewById(R.id.btnReview);

        txtCustomerReviewHead = findViewById(R.id.txtCustomerReviewHead);

    }

    private void Skills() {
        skillsModel = new SkillsModels();
        rvSkills.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvSkills.setLayoutManager(linearLayoutManager);
        skillsModelArrayList = new ArrayList<SkillsModels>();
        viewSkillsAdapter = new ViewSkillsAdapter(this, skillsModelArrayList);
        rvSkills.setAdapter(viewSkillsAdapter);
        SkillsListener();
    }

    private void SkillsListener() {
        firebaseFirestore.collection("User")
                .document(strEmail)
                .collection("Skills")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                skillsModelArrayList.add(dc.getDocument().toObject(SkillsModels.class));
                            }
                            viewSkillsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void RatingListener() {
        firebaseFirestore.collection("User")
                .document(strEmail)
                .collection("Rating")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                ratingModelArrayList.add(dc.getDocument().toObject(RatingModel.class));
                            }
                            ratingAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void DataRetrieve() {
        firebaseFirestore.collection("User").document(loggedInEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            assert documentSnapshot != null;

                            if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                strLoggedFullName = documentSnapshot.getString("FullName");
                                strLoggedMobile = documentSnapshot.getString("Mobile");
                                strLoggedProfile = documentSnapshot.getString("ProfileImgLink");
                                star1 = documentSnapshot.getString("Star1");
                                star2 = documentSnapshot.getString("Star2");
                                star3 = documentSnapshot.getString("Star3");
                                star4 = documentSnapshot.getString("Star4");
                                star5 = documentSnapshot.getString("Star5");
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewProfileActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}