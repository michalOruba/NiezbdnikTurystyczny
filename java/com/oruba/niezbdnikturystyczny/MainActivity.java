package com.oruba.niezbdnikturystyczny;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oruba.niezbdnikturystyczny.models.Hill;
import com.oruba.niezbdnikturystyczny.models.User;
import com.oruba.niezbdnikturystyczny.models.UserHill;
import com.oruba.niezbdnikturystyczny.models.UserLocation;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.oruba.niezbdnikturystyczny.Constants.ERROR_DIALOG_REQUEST;
import static com.oruba.niezbdnikturystyczny.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.oruba.niezbdnikturystyczny.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MainActivity";
    private static final int LOCATION_GET_INTERVAL = 10000;

    private ImageView menu_navigation, menu_help, menu_issue, menu_achievement;
    private FirebaseFirestore mDb;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProvider;
    private UserLocation mUserLocation;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private UserHill mAchievedHill = new UserHill();
    private List<Hill> mHills = new ArrayList<>();
    private UserHill userHill = new UserHill();
    MapsActivity mapsActivity = new MapsActivity();

    private static final String seasons[] = {
            "Winter", "Winter",
            "Winter", "Winter", "Summer",
            "Summer", "Summer", "Summer",
            "Summer", "Summer", "Winter",
            "Winter"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG, "onCreate: " + R.drawable.rysy);
        setContentView(R.layout.activity_main);
        menu_navigation = findViewById(R.id.menu_navigation);
        menu_help = findViewById(R.id.menu_help);
        menu_issue = findViewById(R.id.menu_issue);
        menu_achievement = findViewById(R.id.menu_achievement);

        menu_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(navigationIntent);
            }
        });

        menu_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(navigationIntent);
            }
        });

        menu_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, IssueActivity.class);
                startActivity(navigationIntent);
            }
        });

        menu_achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, AchievementActivity.class);
                startActivity(navigationIntent);
            }
        });

        mDb = FirebaseFirestore.getInstance();
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this);


        getHillsFromDB();
    }




    private void getUserDetails(){
        if (mUserLocation == null){
            mUserLocation = new UserLocation();

            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: successfully get the user details.");


                        User user = task.getResult().toObject(User.class);
                        mUserLocation.setUser(user);

                        ((UserClient) getApplicationContext()).setUser(user);

                        getLastKnownLocation();
                    }
                }
            });
        }
        else {
            getLastKnownLocation();
        }

    }


    private void saveUserLocation(){

        if (mUserLocation != null){
            DocumentReference locationRef = mDb.
                    collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());
            locationRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninserted users location into database" +
                        "\nlatitude: " + mUserLocation.getGeo_point().getLatitude() +
                        "\nlongitude: " + mUserLocation.getGeo_point().getLongitude());


                    }
                }
            });
        }
    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            }
            return;
        }
        mFusedLocationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTime_stamp(null);

                    saveUserLocation();
                }
            }
        });
    }

    private void signOut() {
        stopLocationUpdates();
        if (mapsActivity.isRunning) {
            mapsActivity.stopLocationUpdates();
        }
        mDb.disableNetwork();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                signOut();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ta aplikacja wymaga włączonego modułu GPS, czy chcesz go włączyć?");
        builder.setCancelable(false);
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    public void getLocationPermission(){
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
            getUserDetails();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Nie udało się skorzystać z map.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                    getUserDetails();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode){
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    // (...)
                }
                else{
                    getLocationPermission();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()){
            if (mLocationPermissionGranted){
                // (...)
                getUserDetails();

            }
            else {
                getLocationPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void startUserLocationsRunnable(){
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                getLocation();
                mHandler.postDelayed(mRunnable, LOCATION_GET_INTERVAL);
            }
        }, LOCATION_GET_INTERVAL);
    }
    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            }
            return;
        }
        mFusedLocationProvider.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    Log.d(TAG, "onComplete: latitude: " + geoPoint.getLatitude());
                    Log.d(TAG, "onComplete: longitude: " + geoPoint.getLongitude());

                    mUserLocation.setGeo_point(geoPoint);
                    mUserLocation.setTime_stamp(null);

                    saveUserLocation();
                    checkIfHillAchieved();
                }
            }
        });
    }

    public void getHillsFromDB(){
        mHills = new ArrayList<>();
        CollectionReference getHillRef = mDb.collection(getString(R.string.collection_hills));
        getHillRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                try {
                                    Hill hill = doc.toObject(Hill.class);
                                    mHills.add(hill);
                                    Log.d(TAG, "onEvent: " + hill.getHill_name());
                                } catch (NullPointerException ex) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + ex.getMessage());
                                }
                            }
                            startUserLocationsRunnable();
                        }
                    }
                });
    }

    private void checkIfHillAchieved() {
        LocalDate localDate = new LocalDate();
        int month = localDate.getMonthOfYear();
        for (final Hill hill : mHills) {
            if (Math.abs(mUserLocation.getGeo_point().getLatitude() - hill.getHill_geopoint().getLatitude()) < 0.0005
                    && Math.abs(mUserLocation.getGeo_point().getLongitude() - hill.getHill_geopoint().getLongitude()) < 0.0005) {
                userHill.setHill(hill);
                if (seasons[month].equals("Winter")) {
                        userHill.setAchieve_winter_status(1);
                }
                else if (seasons[month].equals("Summer")){
                    userHill.setAchieve_summer_status(1);
                }

                DocumentReference alreadyAchievedRef = mDb.
                        collection(getString(R.string.collection_user_hills))
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection(getString(R.string.collection_achieved_hills))
                        .document(hill.getHill_id());
                alreadyAchievedRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d(TAG, "onComplete: successfully get the achieved hill details.");

                        try {
                            UserHill userHill = task.getResult().toObject(UserHill.class);
                            mAchievedHill.setHill(userHill.getHill());
                            mAchievedHill.setAchieve_summer_status(userHill.getAchieve_summer_status());
                            mAchievedHill.setAchieve_winter_status(userHill.getAchieve_winter_status());
                        }catch (NullPointerException e){
                            Log.e(TAG, "onComplete: NullPointerException" + e.getMessage());
                        }
                        if (userHill == null || (userHill != null && (mAchievedHill.getAchieve_summer_status() == 0 || mAchievedHill.getAchieve_winter_status() == 0) && (
                                mAchievedHill.getAchieve_summer_status() != userHill.getAchieve_summer_status() || mAchievedHill.getAchieve_winter_status() != userHill.getAchieve_winter_status()))) {
                                if(userHill != null) {
                                    if (userHill.getAchieve_summer_status() == 1) {
                                        userHill.setAchieve_winter_status(mAchievedHill.getAchieve_winter_status());
                                    } else {
                                        userHill.setAchieve_summer_status(mAchievedHill.getAchieve_summer_status());
                                    }
                                }
                                DocumentReference locationRef = mDb.
                                        collection(getString(R.string.collection_user_hills))
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .collection(getString(R.string.collection_achieved_hills))
                                        .document(hill.getHill_id());
                                locationRef.set(userHill).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            createAchievedNotification(hill);
                                        }
                                    }
                                });
                        }
                    }
                });

            }
        }
    }

    public void createAchievedNotification(Hill hill){
        String CHANNEL_ID = "my_channel_01";
        Log.d(TAG, "checkIfHillAchieved: onComplete: Hill achievement saved");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(getResources().getIdentifier(hill.getHill_avatar(),"drawable", getPackageName())) //getResources().getIdentifier("rysy","drawable", getPackageName())
                .setContentTitle(getString(R.string.achievement_notification_title))
                .setContentText(getString(R.string.achievement_notification_body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true);
        NotificationManager notificationManagerCompat = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(1, builder.build());
    }
}
