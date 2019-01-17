package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.oruba.niezbdnikturystyczny.models.HelpEvent;
import com.oruba.niezbdnikturystyczny.models.Hill;
import com.oruba.niezbdnikturystyczny.models.User;
import com.oruba.niezbdnikturystyczny.models.UserLocation;

public class HelpActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "HelpActivity";

    private ImageButton bloodButton, brokenButton, frostbiteButton,
            stingButton, trapButton, unconsciousButton, callHelpButton, textHelpButton;
    private FirebaseFirestore mDb;
    private String helpCase = "";
    private GeoPoint locationToSMS;

    HelpEvent helpEvent = new HelpEvent();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.help_layout);

        bloodButton = findViewById(R.id.bloodButton);
        bloodButton.setOnClickListener(this);
        brokenButton = findViewById(R.id.brokenButton);
        brokenButton.setOnClickListener(this);
        frostbiteButton = findViewById(R.id.frostbiteButton);
        frostbiteButton.setOnClickListener(this);
        stingButton = findViewById(R.id.stingButton);
        stingButton.setOnClickListener(this);
        trapButton = findViewById(R.id.trapButton);
        trapButton.setOnClickListener(this);
        unconsciousButton = findViewById(R.id.unconsciousButton);
        unconsciousButton.setOnClickListener(this);
        callHelpButton = findViewById(R.id.callHelpButton);
        callHelpButton.setOnClickListener(this);
        textHelpButton = findViewById(R.id.textHelpButton);
        textHelpButton.setOnClickListener(this);

        mDb = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bloodButton:
                helpEvent.setEvent_name(getString(R.string.blood_button));
                helpEvent.setAvatar(R.drawable.blood);
                helpEvent.setAdd_date(null);
                helpCase = getString(R.string.blood_button);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.blood_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                getUserDetailInformation();
                break;
            case R.id.brokenButton:
                helpEvent.setEvent_name(getString(R.string.broken_button));
                helpEvent.setAvatar(R.drawable.broken);
                helpEvent.setAdd_date(null);
                helpCase = getString(R.string.broken_button);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.broken_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                getUserDetailInformation();
                break;
            case R.id.frostbiteButton:
                helpEvent.setEvent_name(getString(R.string.frostbite_button));
                helpEvent.setAvatar(R.drawable.frostbite);
                helpEvent.setAdd_date(null);
                helpCase = getString(R.string.frostbite_button);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.frostbite_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                getUserDetailInformation();
                break;
            case R.id.stingButton:
                helpEvent.setEvent_name(getString(R.string.sting_button));
                helpEvent.setAvatar(R.drawable.sting);
                helpEvent.setAdd_date(null);
                helpCase = getString(R.string.sting_button);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.sting_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                getUserDetailInformation();
                break;
            case R.id.trapButton:
                helpEvent.setEvent_name(getString(R.string.trap_button));
                helpEvent.setAvatar(R.drawable.trap);
                helpEvent.setAdd_date(null);
                helpCase = getString(R.string.trap_button);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.trap_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                getUserDetailInformation();
                break;
            case R.id.unconsciousButton:
                helpEvent.setEvent_name(getString(R.string.unconscious_button));
                helpEvent.setAvatar(R.drawable.unconscious);
                helpEvent.setAdd_date(null);
                helpCase = getString(R.string.unconscious_button);
                Toast.makeText(this, "Wydarzenie " + getString(R.string.unconscious_button) + " zostało dodane", Toast.LENGTH_SHORT).show();
                getUserDetailInformation();
                break;
            case R.id.callHelpButton:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:790398025"));
                startActivity(callIntent);
                break;
            case R.id.textHelpButton:
                sendSMSForHelp();
        }
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
                        helpEvent.setEventUser(user);

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

        Log.d(TAG, "getUsersCurrentPosition: Getting Users current location to add help");
        try {
            DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                    .document(FirebaseAuth.getInstance().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully got users location");
                        UserLocation userLocation = task.getResult().toObject(UserLocation.class);
                        helpEvent.setGeo_point(userLocation.getGeo_point());
                        locationToSMS = userLocation.getGeo_point();
                        setHelpOnCurrentLocation();
                    }
                }
            });
        }catch (NullPointerException e ){
            Toast.makeText(this, "Niespodziewany błąd.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "getUsersCurrentPosition: NullPointerException: " + e.getMessage());
        }
    }

    private void setHelpOnCurrentLocation() {
        Log.d(TAG, "setHelpOnCurrentLocation: Setting event in database");
        if (helpEvent != null){
            Log.d(TAG, "setHelpOnCurrentLocation: Event name: " + helpEvent.getEvent_name() + " event avatar " + helpEvent.getAvatar() + "event Geo: " + helpEvent.getGeo_point().getLatitude() + ", " + helpEvent.getGeo_point().getLongitude()
                    + "Event timestamp: " + helpEvent.getAdd_date() + " Event user: " + helpEvent.getEventUser().getUsername());
            Log.d(TAG, "setHelpOnCurrentLocation: event isn't null");


            final DocumentReference newHelpRef =  mDb.collection(getString(R.string.collection_help))
                    .document();

            helpEvent.setEvent_id(newHelpRef.getId());

            newHelpRef.set(helpEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + newHelpRef.getId());
                }
            });
        }
        createHelpDialog();
    }
    private void sendSMSForHelp(){
        if (locationToSMS == null){
            try {
                DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                        .document(FirebaseAuth.getInstance().getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "sendSMSForHelp: successfully got users location");
                            UserLocation userLocation = task.getResult().toObject(UserLocation.class);
                            locationToSMS = userLocation.getGeo_point();
                            createSMS();
                        }
                    }
                });
            }catch (NullPointerException e ){
                Toast.makeText(this, "Niespodziewany błąd.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "sendSMSForHelp: NullPointerException: " + e.getMessage());
            }

        }
        else createSMS();
    }
    public void createSMS(){
        Log.d(TAG, "createSMS: creating SMS");
        Intent textIntent = new Intent(Intent.ACTION_VIEW);
        textIntent.setType("vnd.android-dir/mms-sms");
        textIntent.putExtra("address", "790398025");

        if (helpCase == "") textIntent.putExtra("sms_body", "Proszę o pomoc. Moja lokalizacja to: Szerokość: " + locationToSMS.getLatitude() + "; Długość: " + locationToSMS.getLongitude() + ".");
        else textIntent.putExtra("sms_body", "Proszę o pomoc. Powód wezwania pomocy: " + helpCase + ". Moja lokalizacja to: Szerokość: " + locationToSMS.getLatitude() + "; Długość: " + locationToSMS.getLongitude() + ".");
        startActivity(textIntent);
    }

    public void createHelpDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setMessage("Czy chcesz powiadomić służby ratunkowe?")
                .setCancelable(true)
                .setPositiveButton("Zadzwoń", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:790398025"));
                        startActivity(callIntent);
                    }
                })
                .setNeutralButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("SMS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {
                        sendSMSForHelp();
                    }
                });
        final AlertDialog alert = dialogBuilder.create();
        alert.show();
    }
}
