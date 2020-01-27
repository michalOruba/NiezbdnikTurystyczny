package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;
import com.oruba.niezbdnikturystyczny.models.Event;
import com.oruba.niezbdnikturystyczny.models.User;
import com.oruba.niezbdnikturystyczny.models.UserLocation;

import java.util.Date;
import java.util.Objects;

public class IssueActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "IssueActivity";
    private final int DOUBLE_CLICK_LIMIT_TIME = 20000;

    private ImageButton animalsButton, avalancheButton, landslideButton,
            iceButton, rocksButton, overhangButton,
            trackButton, treeButton, bridgeButton;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    Event event = new Event();
    private long mLastClickTime = 0;
    private String userId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.issue_layout);

        animalsButton = findViewById(R.id.animalsButton);
        animalsButton.setOnClickListener(this);
        avalancheButton = findViewById(R.id.avalancheButton);
        avalancheButton.setOnClickListener(this);
        landslideButton = findViewById(R.id.landslideButton);
        landslideButton.setOnClickListener(this);
        iceButton = findViewById(R.id.iceButton);
        iceButton.setOnClickListener(this);
        rocksButton = findViewById(R.id.rocksButton);
        rocksButton.setOnClickListener(this);
        overhangButton = findViewById(R.id.overhangButton);
        overhangButton.setOnClickListener(this);

        trackButton = findViewById(R.id.trackButton);
        trackButton.setOnClickListener(this);
        treeButton = findViewById(R.id.treeButton);
        treeButton.setOnClickListener(this);
        bridgeButton = findViewById(R.id.bridgeButton);
        bridgeButton.setOnClickListener(this);

        userId = FirebaseAuth.getInstance().getUid();

    }

    /**
     * Method decides about which element was clicked.
     * If user has Internet connection on, proper icon will appear on the map.
     * @param v An element that was clicked by user
     */

    @Override
    public void onClick(View v) {
        if (isNetworkAvailable()) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_LIMIT_TIME){
                Toast.makeText(this, "Już dodałeś wydarzenie!", Toast.LENGTH_SHORT).show();
            }
            else {
                mLastClickTime = SystemClock.elapsedRealtime();

                switch (v.getId()) {
                    case R.id.animalsButton:
                        setEventData(getString(R.string.animals_button), "ic_animals");
                        break;
                    case R.id.avalancheButton:
                        setEventData(getString(R.string.avalanche_button), "ic_avalanche");
                        break;
                    case R.id.landslideButton:
                        setEventData(getString(R.string.landslide_button), "ic_landslide");
                        break;
                    case R.id.iceButton:
                        setEventData(getString(R.string.ice_button), "ic_slippery");
                        break;
                    case R.id.rocksButton:
                        setEventData(getString(R.string.rocks_button), "ic_rocks");
                        break;
                    case R.id.overhangButton:
                        setEventData(getString(R.string.overhang_button), "ic_overhang");
                        break;
                    case R.id.trackButton:
                        setEventData(getString(R.string.track_button), "ic_track");
                        break;
                    case R.id.treeButton:
                        setEventData(getString(R.string.tree_button), "ic_tree");
                        break;
                    case R.id.bridgeButton:
                        setEventData(getString(R.string.bridge_button), "ic_bridge");
                        break;
                }
                getUserDetailInformation();
            }
        }
        else {
            Toast.makeText(this, "Brak połączenia z Internetem.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method that sets up Event object.
     * @param eventName Event name, taken from Strings.xml file.
     * @param avatarName Avatar img file name.
     */
    private void setEventData(String eventName, String avatarName) {
        event.setEvent_name(eventName);
        event.setAvatar(avatarName);
        event.setAdd_date(new Date());
        Toast.makeText(this, "Wydarzenie " + eventName + " zostało dodane", Toast.LENGTH_SHORT).show();
    }


    /**
     *  Method gets current user information from Firestore and adds them to Event object
     */
    private void getUserDetailInformation() {
        try {
            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(Objects.requireNonNull(userId));

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully get the user details.");


                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        event.setEventUser(user);

                        getUsersCurrentPosition();
                    }
                }
            });
        }catch (NullPointerException e){
            Toast.makeText(this, "Niespodziewany błąd.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "getUsersCurrentPosition: NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Method gets Users current location and adds it to Event object and fills object locationForSMS
     */

    private void getUsersCurrentPosition() {

        Log.d(TAG, "getUsersCurrentPosition: Getting Users current location to add issue");
        try {
            DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                    .document(Objects.requireNonNull(userId));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully got users location");
                        UserLocation userLocation = Objects.requireNonNull(task.getResult()).toObject(UserLocation.class);
                        assert userLocation != null;
                        event.setGeo_point(userLocation.getGeo_point());
                        setIssueOnCurrentLocation();
                    }
                }
            });
        }catch (NullPointerException e ){
            Toast.makeText(this, "Niespodziewany błąd.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "getUsersCurrentPosition: NullPointerException: " + e.getMessage());
        }
    }

    /**
     * Creating document in Firestore
     */

    private void setIssueOnCurrentLocation() {
        Log.d(TAG, "setIssueOnCurrentLocation: Setting event in database");
        if (event != null){
            Log.d(TAG, "setIssueOnCurrentLocation: Event name: " + event.getEvent_name() + " event avatar " + event.getAvatar() + "event Geo: " + event.getGeo_point().getLatitude() + ", " + event.getGeo_point().getLongitude()
            + "Event timestamp: " + event.getAdd_date() + " Event user: " + event.getEventUser().getUsername());
            Log.d(TAG, "setIssueOnCurrentLocation: event isn't null");


            final DocumentReference newIssueRef =  mDb.collection(getString(R.string.collection_events))
                    .document();

            event.setEvent_id(newIssueRef.getId());

                    newIssueRef.set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + newIssueRef.getId());
                        }
                    });
        }
    }

    /**
     * Method checks if Internet connection is on
     * @return true if Internet connection is on, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
