package com.rohitanabhavane.coachsir.Activities;

import static com.rohitanabhavane.coachsir.utilities.Constants.isCoach;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohitanabhavane.coachsir.Adapter.ProfileSkillsAdapter;
import com.rohitanabhavane.coachsir.Adapter.TrainingTimeAdapter;
import com.rohitanabhavane.coachsir.Model.SkillsModels;
import com.rohitanabhavane.coachsir.Model.TrainingTimeModel;
import com.rohitanabhavane.coachsir.R;
import com.rohitanabhavane.coachsir.utilities.NetworkChangeListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BecomeACoachActivity extends AppCompatActivity
        implements ProfileSkillsAdapter.OnSkillDeleteListener, TrainingTimeAdapter.OnTrainingTimeDeleteListener {

    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    EditText edtFullName, edtEmail, edtMobile, edtAge, edtDOB, edtPinCode, edtShortADD, edtShortDesc, edtSkills, edtExpYears, edtExpMonth,
            edtTrainingGroundDetails, edtAadharCard, edtPanCard, edtGroupTrainingFees, edtPersonalTrainingFees;
    RadioGroup radioGroup3;
    RadioButton radio_male, radio_female, radio_others;
    Spinner spinnerCity, spinnerState, spinnerSports, spinnerFrom, spinnerAMPM1, spinnerTo, spinnerAMPM2;
    String loggedInEmail, strFullName, strEmail, strMobile, selectedState, selectedCity, strPinCode, strAge, strDOB, strShortADD,
            strShortDesc, strSkills, strExpYears, strSports, strExpMonth, strTrainingGroundDetails, strAadharCard, strPanCard, strTrainingTimeFrom,
            strTrainingTimeAMPM1, strTrainingTimeTo, strTrainingTimeAMPM2, strGroupTrainingFees, strPersonalTrainingFees;
    String strGender = "";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ArrayAdapter<CharSequence> stateAdapter, cityAdapter;
    RecyclerView rvSkills, rvTrainingTime;
    Button btnBecomeACoachFinish, btnAddSkills, btnAddTrainingTime;
    int currentYear, selectedYear, statePosition, cityPosition;
    ImageView imgFrontAadharCard, imgBackAadharCard, imgPanCard, imgDLicence, imgBecomeACoachProfile;
    SkillsModels skillsModel;
    ArrayList<SkillsModels> skillsModelArrayList;
    ProfileSkillsAdapter skillsAdapter;
    TrainingTimeAdapter trainingTimeAdapter;
    TrainingTimeModel trainingTimeModel;
    ArrayList<TrainingTimeModel> trainingTimeModelArrayList;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public Uri AadharCardFrontImgUrl, AadharCardBackImgUrl, PanCardImgUrl, ProfileImgUrl, DLicenceImgUrl;
    String AadharCardFrontImgLink = "";
    String AadharCardBackImgLink = "";
    String PanCardImgLink = "";
    String ProfileImgLink = "";
    String DLicenceImgLink = "";
    ProgressBar progressBar;
    String strProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_acoach);
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

        Initialization();

        DataRetrieve();

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
                        BecomeACoachActivity.this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + " / " + month + " / " + year;
                        edtDOB.setText(date);
                        selectedYear = year;

                        int age = currentYear - selectedYear;
                        String strAge = String.valueOf(age);
                        edtAge.setText(strAge);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_indian_states, R.layout.spinner_layout);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                selectedState = spinnerState.getSelectedItem().toString();
                statePosition = spinnerState.getSelectedItemPosition();

                int parentID = parent.getId();
                if (parentID == R.id.SpinnerState) {
                    switch (selectedState) {
                        case "Select Your State":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(), R.array.array_default_districts, R.layout.spinner_layout);
                            break;

                        case "Andhra Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_andhra_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Arunachal Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_arunachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Assam":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_assam_districts, R.layout.spinner_layout);
                            break;
                        case "Bihar":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_bihar_districts, R.layout.spinner_layout);
                            break;
                        case "Chhattisgarh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_chhattisgarh_districts, R.layout.spinner_layout);
                            break;
                        case "Goa":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_goa_districts, R.layout.spinner_layout);
                            break;
                        case "Gujarat":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_gujarat_districts, R.layout.spinner_layout);
                            break;
                        case "Haryana":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_haryana_districts, R.layout.spinner_layout);
                            break;
                        case "Himachal Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_himachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Jharkhand":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_jharkhand_districts, R.layout.spinner_layout);
                            break;
                        case "Karnataka":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_karnataka_districts, R.layout.spinner_layout);
                            break;
                        case "Kerala":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_kerala_districts, R.layout.spinner_layout);
                            break;
                        case "Madhya Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_madhya_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Maharashtra":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_maharashtra_districts, R.layout.spinner_layout);
                            break;
                        case "Manipur":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_manipur_districts, R.layout.spinner_layout);
                            break;
                        case "Meghalaya":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_meghalaya_districts, R.layout.spinner_layout);
                            break;
                        case "Mizoram":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_mizoram_districts, R.layout.spinner_layout);
                            break;
                        case "Nagaland":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_nagaland_districts, R.layout.spinner_layout);
                            break;
                        case "Odisha":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_odisha_districts, R.layout.spinner_layout);
                            break;
                        case "Punjab":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_punjab_districts, R.layout.spinner_layout);
                            break;
                        case "Rajasthan":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_rajasthan_districts, R.layout.spinner_layout);
                            break;
                        case "Sikkim":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_sikkim_districts, R.layout.spinner_layout);
                            break;
                        case "Tamil Nadu":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_tamil_nadu_districts, R.layout.spinner_layout);
                            break;
                        case "Telangana":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_telangana_districts, R.layout.spinner_layout);
                            break;
                        case "Tripura":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_tripura_districts, R.layout.spinner_layout);
                            break;
                        case "Uttar Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_uttar_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Uttarakhand":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_uttarakhand_districts, R.layout.spinner_layout);
                            break;
                        case "West Bengal":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_west_bengal_districts, R.layout.spinner_layout);
                            break;
                        case "Andaman and Nicobar Islands":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_andaman_nicobar_districts, R.layout.spinner_layout);
                            break;
                        case "Chandigarh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_chandigarh_districts, R.layout.spinner_layout);
                            break;
                        case "Dadra and Nagar Haveli":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_dadra_nagar_haveli_districts, R.layout.spinner_layout);
                            break;
                        case "Daman and Diu":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_daman_diu_districts, R.layout.spinner_layout);
                            break;
                        case "Delhi":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_delhi_districts, R.layout.spinner_layout);
                            break;
                        case "Jammu and Kashmir":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_jammu_kashmir_districts, R.layout.spinner_layout);
                            break;
                        case "Lakshadweep":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_lakshadweep_districts, R.layout.spinner_layout);
                            break;
                        case "Ladakh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_ladakh_districts, R.layout.spinner_layout);
                            break;
                        case "Puducherry":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_puducherry_districts, R.layout.spinner_layout);
                            break;

                        default:
                            break;
                    }
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);

                    spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedCity = spinnerCity.getSelectedItem().toString();
                            cityPosition = spinnerCity.getSelectedItemPosition();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAddSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSkills = edtSkills.getText().toString();
                if (!TextUtils.isEmpty(strSkills)) {
                    AddSkill();
                    skillsAdapter.notifyDataSetChanged();
                } else {
                    edtSkills.setError("Field can't be Empty!");
                }
            }
        });
        btnAddTrainingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTrainingTimeFrom = spinnerFrom.getSelectedItem().toString();
                strTrainingTimeAMPM1 = spinnerAMPM1.getSelectedItem().toString();
                strTrainingTimeTo = spinnerTo.getSelectedItem().toString();
                strTrainingTimeAMPM2 = spinnerAMPM2.getSelectedItem().toString();
                if (!strTrainingTimeFrom.equals("From")) {
                    if (!strTrainingTimeAMPM1.equals("To")) {
                        AddTrainingTiming();
                        trainingTimeAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(BecomeACoachActivity.this, "Select To Timing", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(BecomeACoachActivity.this, "Select From Timing", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skillsModel = new SkillsModels();
        rvSkills.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BecomeACoachActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvSkills.setLayoutManager(linearLayoutManager);
        skillsModelArrayList = new ArrayList<SkillsModels>();
        skillsAdapter = new ProfileSkillsAdapter(this, skillsModelArrayList, BecomeACoachActivity.this);
        rvSkills.setAdapter(skillsAdapter);
        EventChangeListener();

        trainingTimeModel = new TrainingTimeModel();
        rvTrainingTime.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(BecomeACoachActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvTrainingTime.setLayoutManager(linearLayoutManager1);
        trainingTimeModelArrayList = new ArrayList<TrainingTimeModel>();
        trainingTimeAdapter = new TrainingTimeAdapter(this, trainingTimeModelArrayList, BecomeACoachActivity.this);
        rvTrainingTime.setAdapter(trainingTimeAdapter);
        TrainingTimeEventChangeListener();

        //Back Aadhar Card Image Upload Button
        imgBackAadharCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);
            }
        });

        //Profile Image Upload Button
        imgBecomeACoachProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 5);
            }
        });

        //Front Aadhar Card Image Upload Button
        imgFrontAadharCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        //Pan Card Image Upload Button
        imgPanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 3);
            }
        });

        //D Licence Image Upload Button
        imgDLicence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 4);
            }
        });

        btnBecomeACoachFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToString();
                if (isValidate()) {
                    UploadData();
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

    private void DataRetrieve() {
        firebaseFirestore.collection("User").document(loggedInEmail).get()
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
                                strProfile = documentSnapshot.getString("ProfileImgLink");

                                String newMobileNumber = strMobile.replace("+91 ", "");

                                edtFullName.setText(strFullName);
                                edtEmail.setText(strEmail);
                                edtMobile.setText(newMobileNumber);
                                edtDOB.setText(strDOB);
                                edtPinCode.setText(strPinCode);

                                Glide.with(BecomeACoachActivity.this).load(strProfile).into(imgBecomeACoachProfile);

                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BecomeACoachActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                            trainingTimeAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private boolean isValidate() {
        if (TextUtils.isEmpty(strFullName)) {
            edtFullName.setError("Full Name Field can't be Empty");
            Toast.makeText(this, "Full Name Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strEmail)) {
            edtEmail.setError("Email Field can't be Empty");
            Toast.makeText(this, "Email Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!strEmail.matches(emailPattern)) {
            edtEmail.setError("Enter a valid Email");
            Toast.makeText(this, "Enter a valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strMobile)) {
            edtMobile.setError("Mobile Field can't be Empty");
            Toast.makeText(this, "Mobile Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strMobile.length() < 10) {
            edtMobile.setError("Enter valid Mobile Number");
            Toast.makeText(this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strMobile.length() > 10) {
            edtMobile.setError("Enter valid Mobile Number");
            Toast.makeText(this, "Enter valid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strDOB)) {
            edtDOB.setError("DOB Field can't be Empty");
            Toast.makeText(this, "DOB Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (radio_male.isChecked()) {
            strGender = "Male";
        } else if (radio_female.isChecked()) {
            strGender = "Female";
        } else if (radio_others.isChecked()) {
            strGender = "Other";
        } else if (strGender.equals("")) {
            Toast.makeText(this, "Select a Gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strShortADD)) {
            edtShortADD.setError("Short Address Field can't be Empty");
            Toast.makeText(this, "Short Address Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedState.equals("Select Your State")) {
            Toast.makeText(this, "Select a State", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedCity.equals("Select Your District")) {
            Toast.makeText(this, "Select a District", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strPinCode)) {
            edtPinCode.setError("Pin Code Field can't be Empty");
            Toast.makeText(this, "Pin Code Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strPinCode.length() < 6) {
            edtPinCode.setError("Enter valid Pin Code");
            Toast.makeText(this, "Enter valid Pin Code", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strPinCode.length() > 6) {
            edtPinCode.setError("Enter valid Pin Code");
            Toast.makeText(this, "Enter valid Pin Code", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strShortDesc)) {
            edtShortDesc.setError("Short Description Field can't be Empty");
            Toast.makeText(this, "Short Description Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strExpYears)) {
            edtExpYears.setError("Experience Years Field can't be Empty");
            Toast.makeText(this, "Experience Years Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        int expYears = Integer.parseInt(strExpYears);
        if (expYears > 50) {
            edtExpYears.setError("Years should be less than 50");
            Toast.makeText(this, "Years should be less than 50", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strExpMonth)) {
            edtExpMonth.setError("Experience Month Field can't be Empty");
            Toast.makeText(this, "Experience Month Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strExpMonth.length() < 2) {
            edtExpMonth.setError("Enter valid Experience Months");
            Toast.makeText(this, "Enter valid Experience Months", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strExpMonth.length() > 2) {
            edtExpMonth.setError("Enter valid Experience Months");
            Toast.makeText(this, "Enter valid Experience Months", Toast.LENGTH_SHORT).show();
            return false;
        }

        int expMonths = Integer.parseInt(strExpMonth);
        if (expMonths > 11) {
            edtExpMonth.setError("Months should be less than 12");
            Toast.makeText(this, "Months should be less than 12", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strSports.equals("Select Sports")) {
            Toast.makeText(this, "Select a Sport", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strTrainingGroundDetails)) {
            edtTrainingGroundDetails.setError("Training Ground Details Field can't be Empty");
            Toast.makeText(this, "Training Ground Details Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strGroupTrainingFees)) {
            if (TextUtils.isEmpty(strPersonalTrainingFees)) {
                Toast.makeText(this, "Atleast fill one ! ", Toast.LENGTH_SHORT).show();
                edtPersonalTrainingFees.setError("Field can't be Empty");
                edtGroupTrainingFees.setError("Field can't be Empty");
                return false;
            }
        }

        if (TextUtils.isEmpty(strAadharCard)) {
            edtAadharCard.setError("Aadhar Card Number Field can't be Empty");
            Toast.makeText(this, "Aadhar Card Number Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strAadharCard.length() < 12) {
            edtAadharCard.setError("Enter valid Aadhar Card Number");
            Toast.makeText(this, "Enter valid Aadhar Card Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strAadharCard.length() > 12) {
            edtAadharCard.setError("Enter valid Aadhar Card Number");
            Toast.makeText(this, "Enter valid Aadhar Card Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(strPanCard)) {
            edtPanCard.setError("Pan Card Number Field can't be Empty");
            Toast.makeText(this, "Pan Card Number Field can't be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strPanCard.length() < 10) {
            edtPanCard.setError("Enter valid Pan Card Number");
            Toast.makeText(this, "Enter valid Pan Card Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strPanCard.length() > 10) {
            edtPanCard.setError("Enter valid Pan Card Number");
            Toast.makeText(this, "Enter valid Pan Card Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Objects.equals(AadharCardFrontImgLink, "")) {
            Toast.makeText(this, "Upload Front Aadhar Card Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Objects.equals(AadharCardBackImgLink, "")) {
            Toast.makeText(this, "Upload Back Aadhar Card Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Objects.equals(PanCardImgLink, "")) {
            Toast.makeText(this, "Upload Pan Card Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Objects.equals(ProfileImgLink, "")) {
            Toast.makeText(this, "Upload Profile Image", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void UploadData() {

        if (isValidate()) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("FullName", strFullName);
            userMap.put("DOB", strDOB);
            userMap.put("Age", strAge);
            userMap.put("Gender", strGender);
            userMap.put("Address", strShortADD);
            userMap.put("State", selectedState);
            userMap.put("StatePosition", statePosition);
            userMap.put("City", selectedCity);
            userMap.put("CityPosition", cityPosition);
            userMap.put("PinCode", strPinCode);
            userMap.put("Desc", strShortDesc);
            userMap.put("ExpYears", strExpYears);
            userMap.put("ExpMonths", strExpMonth);
            userMap.put("SelectSports", strSports);
            userMap.put("PersonalTrainingFees", strPersonalTrainingFees);
            userMap.put("GroupTrainingFees", strGroupTrainingFees);
            userMap.put("AadharCardNo", strAadharCard);
            userMap.put("PanCardNo", strPanCard);
            userMap.put("TrainingGroundDetails", strTrainingGroundDetails);
            userMap.put("ProfileImgLink", ProfileImgLink);
            userMap.put("AadharCardFrontImgLink", AadharCardFrontImgLink);
            userMap.put("AadharCardBackImgLink", AadharCardBackImgLink);
            userMap.put("PanCardImgLink", PanCardImgLink);
            userMap.put("DLicenceImgLink", DLicenceImgLink);
            userMap.put("Star1", "0");
            userMap.put("Star2", "0");
            userMap.put("Star3", "0");
            userMap.put("Star4", "0");
            userMap.put("Star5", "0");

            firebaseFirestore.collection("User")
                    .document(loggedInEmail).update(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            isCoach = true;
                            Intent intent = new Intent(BecomeACoachActivity.this, CoachHomePageActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnBecomeACoachFinish.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(BecomeACoachActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void AddSkill() {
        String SkillID = UUID.randomUUID().toString();
        Map<String, String> skillMap = new HashMap<>();
        skillMap.put("SkillID", SkillID);
        skillMap.put("Skill", strSkills);
        firebaseFirestore.collection("User")
                .document(loggedInEmail)
                .collection("Skills")
                .document(SkillID)
                .set(skillMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        edtSkills.setText("");
                        skillsAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BecomeACoachActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddTrainingTiming() {
        String TrainingTimeID = UUID.randomUUID().toString();
        Map<String, String> trainingTimeMap = new HashMap<>();
        trainingTimeMap.put("TrainingTimeID", TrainingTimeID);
        trainingTimeMap.put("TrainingTimeFrom", strTrainingTimeFrom);
        trainingTimeMap.put("TrainingTimeAMPM1", strTrainingTimeAMPM1);
        trainingTimeMap.put("TrainingTimeTo", strTrainingTimeTo);
        trainingTimeMap.put("TrainingTimeAMPM2", strTrainingTimeAMPM2);
        firebaseFirestore.collection("User")
                .document(loggedInEmail)
                .collection("TrainingTime")
                .document(TrainingTimeID)
                .set(trainingTimeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        spinnerFrom.setSelection(0);
                        spinnerAMPM1.setSelection(0);
                        spinnerTo.setSelection(0);
                        spinnerAMPM2.setSelection(0);
                        trainingTimeAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BecomeACoachActivity.this, "Error :- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onSkillClick(int position) {
        SkillsModels skillsModel = skillsModelArrayList.get(position);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }
        String ID = skillsModel.getSkillID();
        firebaseFirestore.collection("User")
                .document(loggedInEmail)
                .collection("Skills")
                .document(ID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        skillsModelArrayList.remove(position);
                        skillsAdapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BecomeACoachActivity.this, "Error -: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Initialization() {
        edtFullName = findViewById(R.id.edtBecomeACoachFullName);
        edtEmail = findViewById(R.id.edtBecomeACoachEmail);
        edtMobile = findViewById(R.id.edtBecomeACoachMobile);
        spinnerCity = findViewById(R.id.SpinnerCity);
        spinnerState = findViewById(R.id.SpinnerState);
        edtAge = findViewById(R.id.edtBecomeACoachAge);
        edtPinCode = findViewById(R.id.edtBecomeACoachPinCode);
        edtDOB = findViewById(R.id.edtBecomeACoachCalender);
        radioGroup3 = findViewById(R.id.radioGroup3);
        radio_male = findViewById(R.id.radioBecomeACoachMale);
        radio_female = findViewById(R.id.radioBecomeACoachFemale);
        radio_others = findViewById(R.id.radioBecomeACoachOthers);
        edtShortADD = findViewById(R.id.edtBecomeACoachAddress);
        edtShortDesc = findViewById(R.id.edtBecomeACoachShortDescription);
        edtSkills = findViewById(R.id.edtBecomeACoachSkills);
        edtExpYears = findViewById(R.id.edtBecomeACoachExpYrs);
        edtExpMonth = findViewById(R.id.edtBecomeACoachExpMonth);
        spinnerFrom = findViewById(R.id.spinnerBecomeACoachFrom);
        spinnerAMPM1 = findViewById(R.id.spinnerBecomeACoachAmPm1);
        spinnerTo = findViewById(R.id.spinnerBecomeACoachTo);
        spinnerAMPM2 = findViewById(R.id.spinnerBecomeACoachAmPm2);
        edtTrainingGroundDetails = findViewById(R.id.edtBecomeACoachGroundDetails);
        edtAadharCard = findViewById(R.id.edtBecomeACoachAadharCardNumber);
        edtPanCard = findViewById(R.id.edtBecomeACoachPanCardNumber);
        spinnerSports = findViewById(R.id.SpinnerBecomeACoachSportType);

        imgFrontAadharCard = findViewById(R.id.imgBecomeACoachFrontAadhar);
        imgBackAadharCard = findViewById(R.id.imgBecomeACoachBackAadhar);
        imgBecomeACoachProfile = findViewById(R.id.imgBecomeACoachProfile);
        imgPanCard = findViewById(R.id.imgBecomeACoachPanCard);
        imgDLicence = findViewById(R.id.imgBecomeACoachDLicence);

        btnAddSkills = findViewById(R.id.btnBecomeACoachAddSkills);
        btnAddTrainingTime = findViewById(R.id.btnAddCoachBecomeACoachTime);
        btnBecomeACoachFinish = findViewById(R.id.btnBecomeACoachFinish);

        rvSkills = findViewById(R.id.rvSkills);
        rvTrainingTime = findViewById(R.id.rvTrainingDuration);
        progressBar = findViewById(R.id.progressBarRegisterCoach);

        edtGroupTrainingFees = findViewById(R.id.edtBecomeACoachGroupFees);
        edtPersonalTrainingFees = findViewById(R.id.edtBecomeACoachPersonalFees);
    }

    private void ToString() {
        strFullName = edtFullName.getText().toString();
        strEmail = edtEmail.getText().toString().trim();
        strMobile = edtMobile.getText().toString();
        strDOB = edtDOB.getText().toString();
        strAge = edtAge.getText().toString();
        strPinCode = edtPinCode.getText().toString();
        strShortADD = edtShortADD.getText().toString();
        strAadharCard = edtAadharCard.getText().toString();
        strExpYears = edtExpYears.getText().toString();
        strExpMonth = edtExpMonth.getText().toString();
        strPanCard = edtPanCard.getText().toString();
        strGroupTrainingFees = edtGroupTrainingFees.getText().toString();
        strPersonalTrainingFees = edtPersonalTrainingFees.getText().toString();
        strShortDesc = edtShortDesc.getText().toString();
        strTrainingGroundDetails = edtTrainingGroundDetails.getText().toString();
        strSkills = edtSkills.getText().toString();
        strSports = spinnerSports.getSelectedItem().toString();
    }

    private void EventChangeListener() {
        firebaseFirestore.collection("User")
                .document(loggedInEmail)
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
                            skillsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onTrainingTimeClick(int position) {
        TrainingTimeModel trainingTimeModel = trainingTimeModelArrayList.get(position);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }
        String TrainingTimeID = trainingTimeModel.getTrainingTimeID();
        firebaseFirestore.collection("User")
                .document(loggedInEmail)
                .collection("TrainingTime")
                .document(TrainingTimeID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        trainingTimeModelArrayList.remove(position);
                        trainingTimeAdapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BecomeACoachActivity.this, "Error -: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 1) {
                AadharCardFrontImgUrl = data.getData();
                imgFrontAadharCard.setImageURI(AadharCardFrontImgUrl);
                AadharCardFrontImg();
            }
        }

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 2) {
                AadharCardBackImgUrl = data.getData();
                imgBackAadharCard.setImageURI(AadharCardBackImgUrl);
                AadharCardBackImg();
            }
        }

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 3) {
                PanCardImgUrl = data.getData();
                imgPanCard.setImageURI(PanCardImgUrl);
                PanCardImg();
            }
        }

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 4) {
                DLicenceImgUrl = data.getData();
                imgDLicence.setImageURI(DLicenceImgUrl);
                DLicenceImg();
            }
        }

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 5) {
                ProfileImgUrl = data.getData();
                imgBecomeACoachProfile.setImageURI(ProfileImgUrl);
                ProfileImg();
            }
        }
    }

    private void AadharCardFrontImg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        String AadharCardFrontID = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("documents/" + AadharCardFrontID);

        ImagesRef.putFile(AadharCardFrontImgUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        ImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    AadharCardFrontImgLink = task.getResult().toString();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(BecomeACoachActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(BecomeACoachActivity.this, "Something Went Wrong ! " + e, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void AadharCardBackImg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        String AadharCardBackID = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("documents/" + AadharCardBackID);

        ImagesRef.putFile(AadharCardBackImgUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        ImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    AadharCardBackImgLink = task.getResult().toString();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(BecomeACoachActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(BecomeACoachActivity.this, "Something Went Wrong ! " + e, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void PanCardImg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        String PanCardID = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("documents/" + PanCardID);

        ImagesRef.putFile(PanCardImgUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        ImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    PanCardImgLink = task.getResult().toString();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(BecomeACoachActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(BecomeACoachActivity.this, "Something Went Wrong ! " + e, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void DLicenceImg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        String DLicenceID = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("documents/" + DLicenceID);

        ImagesRef.putFile(DLicenceImgUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        ImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    DLicenceImgLink = task.getResult().toString();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(BecomeACoachActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(BecomeACoachActivity.this, "Something Went Wrong ! " + e, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void ProfileImg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        String ProfileImgID = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("documents/" + ProfileImgID);

        ImagesRef.putFile(ProfileImgUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        ImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    ProfileImgLink = task.getResult().toString();
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(BecomeACoachActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(BecomeACoachActivity.this, "Something Went Wrong ! " + e, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }


}