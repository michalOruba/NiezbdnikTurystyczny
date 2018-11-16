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
    String eventName;
    String eventAvatar;



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
                Toast.makeText(this, "Klikam animalsButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.animals_button));
                event.setAvatar(R.drawable.cartman_cop);
                event.setAdd_date(null);
                break;
            case R.id.avalancheButton:
                Toast.makeText(this, "Klikam avalancheButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.avalanche_button));
                event.setAvatar(R.drawable.chef);
                event.setAdd_date(null);
                break;
            case R.id.landslideButton:
                Toast.makeText(this, "Klikam landslideButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.landslide_button));
                event.setAvatar(R.drawable.eric_cartman);
                event.setAdd_date(null);
                break;
            case R.id.iceButton:
                Toast.makeText(this, "Klikam iceButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.ice_button));
                event.setAvatar(R.drawable.ike);
                event.setAdd_date(null);
                break;
            case R.id.rocksButton:
                Toast.makeText(this, "Klikam rocksButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.rocks_button));
                event.setAvatar(R.drawable.kyle);
                event.setAdd_date(null);
                break;
            case R.id.overhangButton:
                Toast.makeText(this, "Klikam overhangButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.overhang_button));
                event.setAvatar(R.drawable.tweek);
                event.setAdd_date(null);
                break;
            case R.id.trackButton:
                Toast.makeText(this, "Klikam trackButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.track_button));
                event.setAvatar(R.drawable.satan);
                event.setAdd_date(null);
                break;
            case R.id.treeButton:
                Toast.makeText(this, "Klikam treeButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.tree_button));
                event.setAvatar(R.drawable.icons8_route_48);
                event.setAdd_date(null);
                break;
            case R.id.bridgeButton:
                Toast.makeText(this, "Klikam bridgeButton", Toast.LENGTH_SHORT).show();
                event.setEvent_name(getString(R.string.bridge_button));
                event.setAvatar(R.drawable.icons8_info_48);
                event.setAdd_date(null);
                break;
        }
        getUserDetailInformation();
    }

    private void getUserDetailInformation() {
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
    }

    private void getUsersCurrentPosition() {

        Log.d(TAG, "getUsersCurrentPosition: Getting Users current location to add issue");

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
