package com.oruba.niezbdnikturystyczny;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


/**
 * Currently unused class
 * Left here for future implementation
 */

public class Navigation2Fragment extends Activity {
//
//    private double hillLatitude;
//    private double hillLongitude;
//    private double currentLatitude;
//    private double currentLongitude;
//    LocationManager locationManager;
//    LocationListener locationListener;
//
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.help_layout); // Zmienić Layout!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//
//                currentLatitude = location.getLatitude();
//                currentLongitude = location.getLongitude();
//                Log.d("NavigateActivity", "Current latitude: " + currentLatitude + ", current longitude " + currentLongitude);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        };
//
//             configureButton();
//
//
//
//
//
//        hillLatitude = getIntent().getDoubleExtra("HILL_LATITUDE",0);
//        hillLongitude = getIntent().getDoubleExtra("HILL_LONGITUDE",0);
//
//        Log.d("NavigateActivity", "Hill latitude: " + hillLatitude + ", hill longitude " + hillLongitude);
//       // Log.d("NavigateActivity", "Current latitude: " + currentLatitude + ", current longitude " + currentLongitude);
//
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 10:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    configureButton();
//                return;
//        }
//    }
//
//    private void configureButton() {
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(new String[]{
//                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
//                    }, 10);
//                    return;
//                }
//            }
//
//        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
//            Log.d("Navigate", "jestem w configbutton");
//    }
//
//    @Override
//    protected void onDestroy() {
//        locationManager.removeUpdates(locationListener);
//        super.onDestroy();
//
//    }
//
//
//
//
//
//
//




}
