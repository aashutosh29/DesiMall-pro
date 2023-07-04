package com.aashutosh.desimall_pro.splash;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.aashutosh.desimall_pro.R;
import com.aashutosh.desimall_pro.models.java.Store;
import com.aashutosh.desimall_pro.ui.SplashOldActivity;
import com.aashutosh.desimall_pro.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    @BindView(R.id.pbMain)
    LinearProgressIndicator pbMain;

    FirebaseFirestore db;
    List<Store> storeList;
    AlertDialog alertDialog;


    boolean onStop = false;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initialization();
        checkAllStuff();
    }


    void checkAllStuff() {
        if (serviceAvailable()) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            if (!checkPermissions()) {
                requestPermissions();
            } else {
                getLocation();
            }
        } else {
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onStop) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            initialization();
            checkAllStuff();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStop = true;
    }


    boolean serviceAvailable() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            alertDialog = new AlertDialog.Builder(this).setMessage("Location is turned off").setPositiveButton("Turn on location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startWebView(Constant.BRANCH_NAME, Constant.BRANCH_CODE);
                }
            }).show();
            return false;
        } else {

            return true;
        }

    }

    private void initialization() {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
        db = FirebaseFirestore.getInstance();
        storeList = new ArrayList<>();

    }

    private void subscribeToTopic(String notificationTopic, String url, String name) {
        FirebaseMessaging.getInstance().subscribeToTopic(notificationTopic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pbMain.setProgress(40, true);
                } else {
                    pbMain.setProgress(40);
                }
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("fetching failed");
                            startWebView(Constant.BRANCH_NAME, Constant.BRANCH_CODE);
                            return;
                        }
                        String token = task.getResult();
                        Log.d("TAG", "onComplete: " + token);
                        startWebView(url, name);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startWebView(Constant.BRANCH_NAME, Constant.BRANCH_CODE);
                    }
                });
                String msg = "Successful";
                if (!task.isSuccessful()) {
                    msg = "Failed";
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startWebView(Constant.BRANCH_NAME, Constant.BRANCH_CODE);
            }
        });

    }

    void startWebView(String gmail, String branchCode) {
        Intent intent = new Intent(SplashActivity.this, SplashOldActivity.class);
        intent.putExtra(Constant.BRANCH_NAME, gmail);
       intent.putExtra(Constant.BRANCH_CODE, branchCode);



        //Gurugram - Hamilton Court
        //6
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    void getDataFromFireBase(Location location) {
        db.collection("stores").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    storeList = new ArrayList<>();
                    Store store;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        store = new Store(document.getId(), document.getData().get("name") + "", document.getData().get("lat") + "", document.getData().get("long") + "", document.getData().get("url") + "", document.getData().get("notificationTopic") + "", document.getData().get("gmail") + "", document.getData().get("branchCode") + "");
                        storeList.add(store);
                        Log.d("proAss", document.getId() + " =>" + document.getData());
                    }
                    fetchUrlAndSubscribeTopic(shortestStoreList(location));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pbMain.setProgress(30, true);
                    } else {
                        pbMain.setProgress(30);
                    }

                } else {
                    Log.w("Aashutosh", "Error getting documents.", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fetchUrlAndSubscribeTopic(new ArrayList<>());
            }
        });
    }

    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("TAG", "Displaying permission rationale to provide additional context.");
            Snackbar.make(findViewById(R.id.clMain), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request permission
                    ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            }).show();
        } else {
            Log.i("TAG", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                getLocation();
            } else {
                Snackbar.make(findViewById(R.id.clMain), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request permission
                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
                    }
                }).show();

            }
        }
    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(R.id.clMain), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions();
                }
            }).show();
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, this);
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbMain.setProgress(10, true);
        } else {
            pbMain.setProgress(10);
        }
        if (myLocation == null) {
            Toast.makeText(this, "Waiting to get location for first time", Toast.LENGTH_SHORT).show();
        } else {
            getDataFromFireBase(myLocation);
        }
    }

    List<Store> shortestStoreList(Location location) {
        List<Store> store3KmList = new ArrayList<>();
        Store store;
        if (location != null) {
            for (int i = 0; i < storeList.size(); i++) {
                float distance = distFrom((float) location.getLatitude(), (float) location.getLongitude(), Float.parseFloat(storeList.get(i).getLat()), Float.parseFloat(storeList.get(i).getLon()));
                if (distance <= 5.1) {
                    store = new Store();
                    store.setId(storeList.get(i).getId());
                    store.setLat(storeList.get(i).getLat());
                    store.setLon(storeList.get(i).getLon());
                    store.setUrl(storeList.get(i).getUrl());
                    store.setName(storeList.get(i).getName());
                    store.setDistance(distance);
                    store.setNotificationTopic(storeList.get(i).getNotificationTopic());
                    store3KmList.add(store);
                }
            }
        }
        return store3KmList;
    }


    float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        return dist / 1000;
    }

    void fetchUrlAndSubscribeTopic(List<Store> storeList) {
        Log.d(TAG, "size of store: " + storeList.size());
        if (!storeList.isEmpty()) {
            if (storeList.size() <= 1) {
                subscribeToTopic(storeList.get(0).getNotificationTopic(), storeList.get(0).getUrl(), storeList.get(0).getName());
            } else {
                float minValue = storeList.get(0).getDistance();
                for (int i = 1; i < storeList.size(); i++) {
                    minValue = Float.min(minValue, storeList.get(i).getDistance());
                }
                for (int i = 0; i < storeList.size(); i++) {
                    if (minValue == storeList.get(i).getDistance()) {
                       // clStoreDetails.setVisibility(View.VISIBLE);
                        subscribeToTopic(storeList.get(i).getNotificationTopic(), storeList.get(i).getUrl(), storeList.get(i).getName());
                        break;
                    }
                }
            }
        } else {
            startWebView(Constant.BRANCH_NAME, Constant.BRANCH_CODE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        getDataFromFireBase(location);
    }
}


