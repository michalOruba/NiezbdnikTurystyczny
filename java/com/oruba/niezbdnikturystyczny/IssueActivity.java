package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.oruba.niezbdnikturystyczny.models.Event;
import com.oruba.niezbdnikturystyczny.models.User;
import com.oruba.niezbdnikturystyczny.models.UserLocation;

public class IssueActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "IssueActivity";

    private ImageButton animalsButton, avalancheButton, landslideButton,
            iceButton, rocksButton, overhangButton,
            trackButton, treeButton, bridgeButton;
    private FirebaseFirestore mDb;
    Event event = new Event();



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

        mDb = FirebaseFirestore.getInstance();


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.animalsButton:
                event.setEvent_name(getString(R.string.animals_button));
                event.setAvatar(R.drawable.ic_animals);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.animals_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.avalancheButton:
                event.setEvent_name(getString(R.string.avalanche_button));
                event.setAvatar(R.drawable.ic_avalanche);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.avalanche_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.landslideButton:
                event.setEvent_name(getString(R.string.landslide_button));
                event.setAvatar(R.drawable.ic_landslide);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.landslide_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iceButton:
                event.setEvent_name(getString(R.string.ice_button));
                event.setAvatar(R.drawable.ic_slippery);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.ice_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rocksButton:
                event.setEvent_name(getString(R.string.rocks_button));
                event.setAvatar(R.drawable.ic_rocks);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.rocks_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.overhangButton:
                event.setEvent_name(getString(R.string.overhang_button));
                event.setAvatar(R.drawable.ic_overhang);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.overhang_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.trackButton:
                event.setEvent_name(getString(R.string.track_button));
                event.setAvatar(R.drawable.ic_track);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.track_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.treeButton:
                event.setEvent_name(getString(R.string.tree_button));
                event.setAvatar(R.drawable.ic_tree);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.tree_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bridgeButton:
                event.setEvent_name(getString(R.string.bridge_button));
                event.setAvatar(R.drawable.ic_bridge);
                event.setAdd_date(null);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.bridge_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                break;
        }
        getUserDetailInformation();
    }

    private void getUserDetailInformation() {
        try {
            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(FirebaseAuth.getInstance().getUid());

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully get the user details.");


                        User user = task.getResult().toObject(User.class);
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

    private void getUsersCurrentPosition() {

        Log.d(TAG, "getUsersCurrentPosition: Getting Users current location to add issue");
        try {
            DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully got users location");
                        UserLocation userLocation = task.getResult().toObject(UserLocation.class);
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
}
