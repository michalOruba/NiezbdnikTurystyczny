package com.oruba.niezbdnikturystyczny;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.oruba.niezbdnikturystyczny.models.ClusterMarker;
import com.oruba.niezbdnikturystyczny.models.Event;
import com.oruba.niezbdnikturystyczny.models.HelpEvent;
import com.oruba.niezbdnikturystyczny.models.PolylineData;
import com.oruba.niezbdnikturystyczny.models.UserLocation;
import com.oruba.niezbdnikturystyczny.util.MyClusterManagerRenderer;
import com.oruba.niezbdnikturystyczny.util.ViewWeightAnimationWrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static com.oruba.niezbdnikturystyczny.Constants.MAPVIEW_BUNDLE_KEY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener {

    private final static String TAG = "MapsActivity";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final int LOCATION_UPDATE_INTERVAL = 10000;


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
    private double eventLatitude;
    private double eventLongitude;
    private double currentLatitude;
    private double currentLongitude;
    LatLngBounds mMapBoundary;
    private ClusterManager mClusterManager;
    UserLocation mUserPosition;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private GeoApiContext mGeoApiContext = null;
    private FirebaseFirestore mDb;
    private Bundle bundle = new Bundle();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private int updatingStarted = 0;
    private int firstOpening = 0;
    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private List<Event> mEvents = new ArrayList<>();
    private Set<String> mEventsIds = new HashSet<>();
    private List<HelpEvent> mHelpEvents = new ArrayList<>();
    private Set<String> mHelpEventsIds = new HashSet<>();

    private ImageButton mapHelpButton, mapIssueButton;
    private LinearLayout mapIssueLayout, mapHelpLayour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Jestem w onCreate!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        mDb = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_maps);
        mUserListRecyclerView = findViewById(R.id.user_list_recycler_view);
        mMapContainer = findViewById(R.id.map_container);
        findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        mapHelpButton = findViewById(R.id.mapHelpButton);
        mapHelpButton.setOnClickListener(this);
        mapIssueButton = findViewById(R.id.mapIssueButton);
        mapIssueButton.setOnClickListener(this);
        mapIssueLayout = findViewById(R.id.mapIssueLayout);
        mapIssueLayout.setOnClickListener(this);
        mapHelpLayour = findViewById(R.id.mapHelpLayout);
        mapHelpLayour.setOnClickListener(this);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            mUserLocations = savedInstanceState.getParcelableArrayList("USER_LOCATIONS");
        }
        else{
            mUserLocations = new ArrayList<>();
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



    }

    private void getUserPosition(final LatLng marker) {

        String id = FirebaseAuth.getInstance().getUid();
        if (id != null) {
            mDb = FirebaseFirestore.getInstance();
            Log.d(TAG, "getUserPosition: user ID: " + id);
            DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());
            Log.d(TAG, "getUserPosition: " + docRef);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "getUserPosition: successfully get the user location.");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getUserPosition: successfully get the user location.");

                        mUserPosition = task.getResult().toObject(UserLocation.class);
                        if (task.getResult().toObject(UserLocation.class) != null) {

                            Log.d(TAG, "D: " + mUserPosition.getGeo_point().getLatitude());
                            Log.d(TAG, "getUserPosition: Long: " + mUserPosition.getGeo_point().getLongitude());
                            mUserLocations.add(task.getResult().toObject(UserLocation.class));
                            Log.d(TAG, "onSaveInstanceState: im saving things to Parcel");
                            bundle.putParcelableArrayList("USER_LOCATIONS", mUserLocations);
                            calculateDirections(marker);
                        }
                    } else {
                        Log.d(TAG, "getUserPosition: Cached get failed: ", task.getException());
                    }

                }

            });
        }
    }


    private void calculateDirections(LatLng marker) {
        Log.d(TAG, "calculateDirections: calculating directions");

        if(mUserLocations.size() > 0) {

            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    marker.latitude,
                    marker.longitude
            );

            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

            directions.alternatives(true);
            directions.mode(TravelMode.WALKING);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            mUserLocations.get(mUserLocations.size() - 1).getGeo_point().getLatitude(),
                            mUserLocations.get(mUserLocations.size() - 1).getGeo_point().getLongitude()
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
        else{
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setMessage("Błąd obliczania odległości");
            final AlertDialog alert = alertBuilder.create();
            alert.show();

        }
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
                long durationInSeconds = polylineData.getLeg().duration.inSeconds;
                double minutesD = durationInSeconds / 60;
                double hoursD = durationInSeconds / 3600;
                int minutes =  (int) minutesD;
                int hours = (int) hoursD;
                String time = hours + " h " + (minutes - hours *60 ) + " min";

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("Trasa: #" + index)
                        .snippet("Czas: " + time + "\n" +
                        "Odległość: " + polylineData.getLeg().distance + "\n" +
                        "Rozpocząć nawigację?"));
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
        if (lastLatLngRoute.size() > 1){
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
            else {
                Toast.makeText(this, "Brak możliwości wyznaczenia trasy.", Toast.LENGTH_SHORT).show();
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
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
        if (hillLongitude == 0 && hillLatitude == 0){
            hillLatitude = this.getIntent().getDoubleExtra("EVENT_LATITUDE", 0);
            hillLongitude = this.getIntent().getDoubleExtra("EVENT_LONGITUDE", 0);
        }
        double bottomBoundary = hillLatitude - 0.005;
        double leftBoundary = hillLongitude - 0.005;
        double topBoundary = hillLatitude + 0.005;
        double rightBoundary = hillLongitude + 0.005;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        Log.d(TAG, "onMapReady: Jestem w onMapReady!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        mMap = googleMap;
        getHillParams();
        getEventsFromDB();
        getHelpFromDB();
        LatLng hillMarker =  new LatLng(hillLatitude, hillLongitude);
        mMapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setCameraView();
                mMapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                firstOpening = 1;
            }
        });
        getUserPosition(hillMarker);
        //calculateDirections(hillMarker);
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

            for (Event events : mEvents) {
                try {
                    Log.d(TAG, "addMapMarkers: dodaję markery na mapę: " + events.getAdd_date());
                    String snippet;
                    snippet = "Data wystąpienia: " + sfd.format(events.getAdd_date()) + "\nKliknij aby potwierdzić." ;
                    int avatar = events.getAvatar();

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(events.getGeo_point().getLatitude(),events.getGeo_point().getLongitude()),
                            events.getEvent_name(),
                            snippet,
                            avatar,
                            events.getEvent_id()
                    );
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
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }

            for (HelpEvent helpEvents : mHelpEvents) {
                try {
                    Log.d(TAG, "addMapMarkers: dodaję markery na mapę: " + helpEvents.getAdd_date());
                    String snippet;
                    snippet = getString(R.string.new_help_title);
                    int avatar = helpEvents.getAvatar();

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(helpEvents.getGeo_point().getLatitude(),helpEvents.getGeo_point().getLongitude()),
                            helpEvents.getEvent_name(),
                            snippet,
                            avatar,
                            helpEvents.getEvent_id()
                    );
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
                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);
                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }
            mClusterManager.cluster();
        }
    }

    public void getEventsFromDB(){

        CollectionReference getEventsRef = mDb.collection(getString(R.string.collection_events));
        // Converting current date - 1 day to Firebase Timestamp format
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime ( date ); // convert your date to Calendar object
        int daysToDecrement = -1;
        cal.add(Calendar.DATE, daysToDecrement);
        date = cal.getTime();
        Timestamp ts = new Timestamp(date);



        getEventsRef
                .orderBy("add_date", Query.Direction.ASCENDING)
                .whereGreaterThan("add_date", ts)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                try{
                                    Event event = doc.toObject(Event.class);
                                    if(!mEventsIds.contains(event.getEvent_id())){
                                        if(updatingStarted == 1){

                                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                                    .putExtra("EVENT_LATITUDE", event.getGeo_point().getLatitude())
                                                    .putExtra("EVENT_LONGITUDE", event.getGeo_point().getLongitude())
                                                    .putExtra("EVENT_NAME", event.getEvent_name());
                                            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            String CHANNEL_ID = "my_channel_01";
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                    .setSmallIcon(event.getAvatar())
                                                    .setContentTitle(getString(R.string.new_issue_title))
                                                    .setContentText(getString(R.string.new_issue_content))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                    .setContentIntent(pendingIntent)
                                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                                                    .setAutoCancel(true);
                                            NotificationManager notificationManagerCompat = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                                            notificationManagerCompat.notify(1, builder.build());
                                        }
                                        mEventsIds.add(event.getEvent_id());
                                        mEvents.add(event);
                                        Log.d(TAG, "onEvent: " + event.getEvent_name());
                                    }
                                } catch (NullPointerException ex){
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + ex.getMessage());
                                }
                            }
                            addMapMarkers();
                            updatingStarted = 1;
                        }
                    }
                });
    }


    public void getHelpFromDB(){

        CollectionReference getHelpRef = mDb.collection(getString(R.string.collection_help));
        // Converting current date - 1 day to Firebase Timestamp format
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime ( date ); // convert your date to Calendar object
        int daysToDecrement = -1;
        cal.add(Calendar.DATE, daysToDecrement);
        date = cal.getTime();
        Timestamp ts = new Timestamp(date);



        getHelpRef
                .orderBy("add_date", Query.Direction.ASCENDING)
                .whereGreaterThan("add_date", ts)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                try{
                                    HelpEvent helpEvent = doc.toObject(HelpEvent.class);
                                    if(!mHelpEventsIds.contains(helpEvent.getEvent_id())){
                                        if(updatingStarted == 1){

                                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                                    .putExtra("EVENT_LATITUDE", helpEvent.getGeo_point().getLatitude())
                                                    .putExtra("EVENT_LONGITUDE", helpEvent.getGeo_point().getLongitude())
                                                    .putExtra("EVENT_NAME", helpEvent.getEvent_name());
                                            final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                            String CHANNEL_ID = "my_channel_01";
                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                    .setSmallIcon(helpEvent.getAvatar())
                                                    .setContentTitle(getString(R.string.new_help_title))
                                                    .setContentText(getString(R.string.new_help_content))
                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                    .setContentIntent(pendingIntent)
                                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                                                    .setAutoCancel(true);
                                            NotificationManager notificationManagerCompat = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
                                            notificationManagerCompat.notify(1, builder.build());
                                        }
                                        mHelpEventsIds.add(helpEvent.getEvent_id());
                                        mHelpEvents.add(helpEvent);
                                        Log.d(TAG, "onEvent: " + helpEvent.getEvent_name());
                                    }
                                } catch (NullPointerException ex){
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + ex.getMessage());
                                }
                            }
                            addMapMarkers();
                            updatingStarted = 1;
                        }
                    }
                });
    }


    private void startUserLocationsRunnable(){
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                getEventsFromDB();
                getHelpFromDB();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
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

    public void getHillParams(){
        hillName = getIntent().getStringExtra("HILL_NAME");
        hillLatitude = getIntent().getDoubleExtra("HILL_LATITUDE", 0);
        hillLongitude = getIntent().getDoubleExtra("HILL_LONGITUDE", 0);

        Log.d("NavigateActivity", "Hill latitude: " + hillLatitude + ", hill longitude " + hillLongitude);
    }

    protected void onNewIntent(Intent newIntent) {

        Log.d(TAG, "onNewIntent: pobieram nowy event");
        this.setIntent(newIntent);
        eventLatitude = this.getIntent().getDoubleExtra("EVENT_LATITUDE", 0);
        eventLongitude = this.getIntent().getDoubleExtra("EVENT_LONGITUDE", 0);
        final LatLng newEvent = new LatLng(
                eventLatitude,
                eventLongitude
        );
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newEvent, 12));
            }
        });
    }


















    @Override
    protected void onDestroy() {
        locationManager.removeUpdates(locationListener);
        mMapView.onDestroy();
        super.onDestroy();
        stopLocationUpdates();
        mEvents = new ArrayList<>();
        mEventsIds = new HashSet<>();
        mHelpEvents = new ArrayList<>();
        mHelpEventsIds = new HashSet<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable();
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
            case R.id.mapHelpButton:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.mapIssueButton:
                Intent issueIntent = new Intent(this, IssueActivity.class);
                startActivity(issueIntent);
                break;
            case R.id.mapHelpLayout:
                Intent helpLayoutIntent = new Intent(this, HelpActivity.class);
                startActivity(helpLayoutIntent);
                break;
            case R.id.mapIssueLayout:
                Intent issueLayoutIntent = new Intent(this, IssueActivity.class);
                startActivity(issueLayoutIntent);
                break;
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        if (marker.getTitle().contains("Trasa: #")) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                    .setMessage("Czy otworzyć Mapy Google?")
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
        else if (marker.getSnippet().contains("Ktoś")) {
            final AlertDialog.Builder helpDialogBuilder = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.help_confirmation))
                    .setCancelable(true)
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                            LatLng mk = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                            calculateDirections(mk);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = helpDialogBuilder.create();
            alert.show();
        }
        else{
            if(isNetworkAvailable()) {
                final AlertDialog.Builder eventDialogBuilder = new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.event_confirmation))
                        .setCancelable(true)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                                Date date = new Date();
                                Timestamp ts = new Timestamp(date);
                                try {
                                    for (final ClusterMarker clusterMarker : mClusterMarkers) {
                                        if (clusterMarker.getPosition().latitude == marker.getPosition().latitude && clusterMarker.getPosition().longitude == marker.getPosition().longitude) {
                                            DocumentReference documentReference = mDb.collection(getString(R.string.collection_events))
                                                    .document(clusterMarker.getEventId());

                                            documentReference.update("add_date", ts);
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                }
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = eventDialogBuilder.create();
                alert.show();
            }
            else {
                Toast.makeText(this, "Brak połączenia z Internetem.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

