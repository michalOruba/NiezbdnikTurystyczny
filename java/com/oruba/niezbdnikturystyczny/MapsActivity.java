package com.oruba.niezbdnikturystyczny;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.oruba.niezbdnikturystyczny.models.ClusterMarker;
import com.oruba.niezbdnikturystyczny.models.PolylineData;
import com.oruba.niezbdnikturystyczny.models.User;
import com.oruba.niezbdnikturystyczny.models.UserLocation;
import com.oruba.niezbdnikturystyczny.util.MyClusterManagerRenderer;
import com.oruba.niezbdnikturystyczny.util.ViewWeightAnimationWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.oruba.niezbdnikturystyczny.Constants.MAPVIEW_BUNDLE_KEY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener {

    private final static String TAG = "MapsActivity";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;


    private View mUserListRecyclerView;
    private RelativeLayout mMapContainer;
    private MapView mMapView;

    private int mMapLayoutState = 1;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double hillLatitude;
    private double hillLongitude;
    private String hillName;
    private double currentLatitude;
    private double currentLongitude;
    LatLngBounds mMapBoundary;
    private ClusterManager mClusterManager;
    UserLocation mUserPosition;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private GeoApiContext mGeoApiContext = null;
    private FirebaseFirestore mDb;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Jestem w onCreate!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        super.onCreate(savedInstanceState);

        mDb = FirebaseFirestore.getInstance();
        getUserPosition();


        setContentView(R.layout.activity_maps);
        mUserListRecyclerView = findViewById(R.id.user_list_recycler_view);
        mMapContainer = findViewById(R.id.map_container);
        findViewById(R.id.btn_full_screen_map).setOnClickListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.user_list_map);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                Log.d("NavigateActivity", "Current latitude: " + currentLatitude + ", current longitude " + currentLongitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        hillName = getIntent().getStringExtra("HILL_NAME");
        hillLatitude = getIntent().getDoubleExtra("HILL_LATITUDE", 0);
        hillLongitude = getIntent().getDoubleExtra("HILL_LONGITUDE", 0);

        Log.d("NavigateActivity", "Hill latitude: " + hillLatitude + ", hill longitude " + hillLongitude);


    }

    private void getUserPosition() {
        DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                .document(FirebaseAuth.getInstance().getUid());
        Log.d(TAG, "getUserPosition: user ID: " + FirebaseAuth.getInstance().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG, "getUserPosition: successfully get the user location.");
                if (task.isSuccessful()) {
                    Log.d(TAG, "getUserPosition: successfully get the user location.");

                    mUserPosition = task.getResult().toObject(UserLocation.class);
                    if (mUserPosition != null) {
                        Log.d(TAG, "D: " + mUserPosition.getGeo_point().getLatitude());
                        Log.d(TAG, "getUserPosition: Long: " + mUserPosition.getGeo_point().getLongitude());
                    }
                } else {
                    Log.d(TAG, "getUserPosition: Cached get failed: ", task.getException());
                }

            }
        });
    }


    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions");

        if(mUserPosition == null){
            Log.d(TAG, "calculateDirections: Brak współrzędnych użytkownika");
            getUserPosition();
        }

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
                //50.25213550,
                //19.02714340
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.mode(TravelMode.WALKING);
        directions.origin(
                new com.google.maps.model.LatLng(

                        //currentLatitude,
                        //currentLongitude
                        mUserPosition.getGeo_point().getLatitude(),
                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }


            @Override
            public void onFailure(Throwable e) {
                Log.d(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "addPolylinesToMap: result routes: " + result.routes.length);

                if (mPolylinesData.size() > 0) {
                    for (PolylineData polylineData : mPolylinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }
                double duration = 99999999999.99;
                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "addPolylinesToMap, run: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();


                    //This loops through all the LatLng coordinates of ONE polyline
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(getApplicationContext().getResources().getColor(R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                }
            }
        });
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for (PolylineData polylineData : mPolylinesData) {
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.getPolyline().getId());
            Log.d(TAG, "onPolylineClick: currentPolyline: " + polyline.getId());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(getApplicationContext().getResources().getColor(R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trip: #" + index + " to " + hillName)
                        .snippet("Duration: " + polylineData.getLeg().duration + "\n" +
                        "Distance: " + polylineData.getLeg().distance));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(getApplication());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getApplication());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getApplication());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
                marker.showInfoWindow();
            } else {
                polylineData.getPolyline().setColor(getApplicationContext().getResources().getColor(R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    private void zoomRoute(List<LatLng> lastLatLngRoute) {
        if (mMap == null || lastLatLngRoute.isEmpty() || lastLatLngRoute == null) return;

        Log.d(TAG, "zoomPolyline: zoomuję polyline dla: " + lastLatLngRoute.get(1));
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lastLatLngRoute) {
            boundsBuilder.include(latLngPoint);
        }
        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
                return;
            }
        }

        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        Log.d("Navigate", "jestem w configbutton");
    }


    private void setCameraView() {
        double bottomBoundary = hillLatitude - 0.005;
        double leftBounday = hillLongitude - 0.005;
        double topBoundary = hillLatitude + 0.005;
        double rightBoundary = hillLongitude + 0.005;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBounday),
                new LatLng(topBoundary, rightBoundary)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        Log.d(TAG, "onMapReady: Jestem w onMapReady!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        mMap = googleMap;
        Marker hillMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(hillLatitude, hillLongitude)));
        setCameraView();
        calculateDirections(hillMarker);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnPolylineClickListener(this);

    }

    private void addMapMarkers(){
        if (mMap != null){
            if (mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getApplicationContext(), mMap);
            }
            if (mClusterManagerRenderer == null){
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            for (UserLocation userLocation : mUserLocations) {
                try {
                    String snippet;
                    if (userLocation.getUser().getUser_id().equals(FirebaseAuth.getInstance().getUid())) {
                        snippet = "This is you";
                    } else {
                        snippet = "Determinate route to " + userLocation.getUser().getUsername() + "?";
                    }
                    int avatar = R.drawable.cwm_logo;
                    try {
                        avatar = Integer.parseInt(userLocation.getUser().getAvatar());
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                    }

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(userLocation.getGeo_point().getLatitude(),userLocation.getGeo_point().getLongitude()),
                            userLocation.getUser().getUsername(),
                            snippet,
                            avatar,
                            userLocation.getUser()
                    );
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }
            mClusterManager.cluster();

            setCameraView();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }


















    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        mMapView.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{
                if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                else if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                break;
            }
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        if (marker.getTitle().contains("Trip: #")) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                    .setMessage("Open Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude + "," + longitude + "&mode=w");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try {
                                if (mapIntent.resolveActivity(getApplication().getPackageManager()) != null){
                                    startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: nullPointerException: Couldn't open Map: " + e.getMessage());
                                Toast.makeText(getApplication(),"Couldn't open map", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = dialogBuilder.create();
            alert.show();
        }
    }
}

