package com.rohitanabhavane.coachsir.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rohitanabhavane.coachsir.Adapter.BannerAdapter;
import com.rohitanabhavane.coachsir.Adapter.CoachesAdapter;
import com.rohitanabhavane.coachsir.Model.BannerModel;
import com.rohitanabhavane.coachsir.Model.CoachesModel;
import com.rohitanabhavane.coachsir.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {
    RecyclerView rvBanner, allUsersList;
    BannerAdapter bannerAdapter;
    BannerModel bannerModel;
    ArrayList<BannerModel> bannerModelArrayList;
    FirebaseFirestore mFirestore;
    ImageView btnImgProfile, btnFilter;
    CoachesModel coachesModel;
    CoachesAdapter coachesAdapter;
    ArrayList<CoachesModel> coachesModelArrayList;
    CharSequence search = "";
    EditText Search;
    Boolean isFiltered = true;
    LocationManager locationManager;
    TextView txtLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBanner = findViewById(R.id.rvBanner);
        btnImgProfile = findViewById(R.id.btnImgProfile);
        btnFilter = findViewById(R.id.btnFilter);
        allUsersList = findViewById(R.id.allUsersList);
        Search = findViewById(R.id.homeSearch);
        txtLocation = findViewById(R.id.txtLocation);

        btnImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
                ;
            }
        });

        //Firebase FireStore
        mFirestore = FirebaseFirestore.getInstance();

        //Model
        bannerModel = new BannerModel();
        coachesModel = new CoachesModel();

        //Banner
        bannerModelArrayList = new ArrayList<BannerModel>();
        rvBanner.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvBanner.setLayoutManager(linearLayoutManager);
        bannerAdapter = new BannerAdapter(this, bannerModelArrayList);
        rvBanner.setAdapter(bannerAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvBanner);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < (bannerAdapter.getItemCount() - 1)) {
                    linearLayoutManager.smoothScrollToPosition(rvBanner, new RecyclerView.State(), linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1);
                } else {
                    linearLayoutManager.smoothScrollToPosition(rvBanner, new RecyclerView.State(), 0);
                }
            }
        }, 0, 4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBannerChangeListener();
                bannerAdapter.showShimmer = false;
                bannerAdapter.notifyDataSetChanged();
            }
        }, 3000);

        //Users
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));
        coachesModelArrayList = new ArrayList<CoachesModel>();
        coachesAdapter = new CoachesAdapter(this, coachesModelArrayList);
        allUsersList.setAdapter(coachesAdapter);
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                coachesAdapter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        EventChangeListener();

        grantPermission();
        checkLocationIsEnabledOrNot();
        getLocation();
    }

    private void EventBannerChangeListener() {
        mFirestore.collection("Banner").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore Error", error.getMessage());
                    return;
                }

                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        bannerModelArrayList.add(dc.getDocument().toObject(BannerModel.class));
                    }
                    bannerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void EventChangeListener() {
        mFirestore.collection("User")
                .whereEqualTo("isCoach", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                coachesModelArrayList.add(dc.getDocument().toObject(CoachesModel.class));
                            }
                            coachesAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabledOrNot() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!gpsEnabled && !networkEnabled) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void grantPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            txtLocation.setText(addresses.get(0).getSubLocality());


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

}