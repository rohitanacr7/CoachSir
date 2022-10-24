package com.rohitanabhavane.coachsir.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rohitanabhavane.coachsir.R;
import com.rohitanabhavane.coachsir.utilities.NetworkChangeListener;

import java.util.HashMap;
import java.util.UUID;

public class EditProfileImgActivity extends AppCompatActivity {


    Button btnAdd, btnBack;
    ProgressBar progressBar;
    ImageView imgAddBannerImage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    public Uri ProfileImgUrl;
    String loggedInEmail;
    String ProfileImgLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_img);

        btnAdd = findViewById(R.id.btnAddProfile);
        progressBar = findViewById(R.id.progressBarAddProfile);
        imgAddBannerImage = findViewById(R.id.imgUploadProfileImg);
        btnBack = findViewById(R.id.btnEditProfileImgBack);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            loggedInEmail = mAuth.getCurrentUser().getEmail();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Firebase FireStore
        mFireStore = FirebaseFirestore.getInstance();
        //FireBase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Image Upload Button
        imgAddBannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ProfileImgLink.isEmpty()) {
                    AddProfileImg();
                } else {
                    Toast.makeText(EditProfileImgActivity.this, "Image not Selected !", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 1) {
                ProfileImgUrl = data.getData();
                imgAddBannerImage.setImageURI(ProfileImgUrl);
                ProfileImage();
            }
        }

    }

    private void ProfileImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();
        String BannerID = UUID.randomUUID().toString();
        StorageReference ImagesRef = storageReference.child("banner/" + BannerID);

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
                                    Toast.makeText(EditProfileImgActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(EditProfileImgActivity.this, "Something Went Wrong ! " + e, Toast.LENGTH_SHORT).show();
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

    private void AddProfileImg() {
        btnAdd.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> addProfileImg = new HashMap<>();
        addProfileImg.put("ProfileImgLink", ProfileImgLink);

        mFireStore.collection("User").document(loggedInEmail)
                .update(addProfileImg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(EditProfileImgActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btnAdd.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(EditProfileImgActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}