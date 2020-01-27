package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.oruba.niezbdnikturystyczny.models.User;
import com.oruba.niezbdnikturystyczny.models.UserLocation;

import java.util.Date;
import java.util.Objects;

public class HelpActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "HelpActivity";
    private final int DOUBLE_CLICK_LIMIT_TIME = 20000;
    private final String EMERGENCY_PHONE_NUMBER = "790398025";

    private ImageButton bloodButton, brokenButton, frostbiteButton,
            stingButton, trapButton, unconsciousButton, callHelpButton, textHelpButton;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private String helpCase = "";
    private GeoPoint locationForSMS;
    private long mLastClickTime = 0;
    private String userId;

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

        userId = FirebaseAuth.getInstance().getUid();


    }

    /**
     * Method decides about which element was clicked. Each element has its own help need.
     * If user has Internet connection on, proper icon will appear on the map.
     * @param v An element that was clicked by user
     */
    @Override
    public void onClick(View v) {
        if(isNetworkAvailable()) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_CLICK_LIMIT_TIME){
                if (v.getId() != R.id.callHelpButton && v.getId() != R.id.textHelpButton)
                    Toast.makeText(this, "Już dodałeś wydarzenie!", Toast.LENGTH_SHORT).show();
            }
            else{
                switch (v.getId()) {

                    case R.id.bloodButton:
                        setHelpEventData(getString(R.string.blood_button), "blood");
                        getUserDetailInformation();
                        break;
                    case R.id.brokenButton:
                        setHelpEventData(getString(R.string.broken_button), "broken");
                        getUserDetailInformation();
                        break;
                    case R.id.frostbiteButton:
                        setHelpEventData(getString(R.string.frostbite_button), "frostbite");
                        getUserDetailInformation();
                        break;
                    case R.id.stingButton:
                        setHelpEventData(getString(R.string.sting_button), "sting");
                        getUserDetailInformation();
                        break;
                    case R.id.trapButton:
                        setHelpEventData(getString(R.string.trap_button), "trap");
                        getUserDetailInformation();
                        break;
                    case R.id.unconsciousButton:
                        setHelpEventData(getString(R.string.unconscious_button), "unconscious");
                        getUserDetailInformation();
                        break;
                }
            }
        }
        else {
            Toast.makeText(this, "Brak połączenia z Internetem.", Toast.LENGTH_SHORT).show();
        }
        switch (v.getId()) {
            case R.id.callHelpButton:
                callForHelp();
                break;
            case R.id.textHelpButton:
                sendSMSForHelp();
                break;
        }
    }

    /**
     * Method that sets up helpEvent object.
     * @param eventName Event name, taken from Strings.xml file.
     * @param avatarName Avatar img file name.
     */
    private void setHelpEventData(String eventName, String avatarName) {
        helpEvent.setEvent_name(eventName);
        helpEvent.setAvatar(avatarName);
        helpEvent.setAdd_date(new Date());
        helpCase = eventName;
        Toast.makeText(this, "Wydarzenie " + eventName + " zostało dodane", Toast.LENGTH_SHORT).show();
    }

    /**
     *  Method gets current user information from Firestore and adds them to HelpEvent object
     */
    private void getUserDetailInformation() {
        mLastClickTime = SystemClock.elapsedRealtime();
        try {
            DocumentReference userRef = mDb.collection(getString(R.string.collection_users))
                    .document(Objects.requireNonNull(userId));

            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully get the user details.");


                        User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
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

    /**
     * Method gets Users current location and adds it to HelpEvent object and fills object locationForSMS
     */

    private void getUsersCurrentPosition() {

        Log.d(TAG, "getUsersCurrentPosition: Getting Users current location to add help");
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
                        helpEvent.setGeo_point(userLocation.getGeo_point());
                        locationForSMS = userLocation.getGeo_point();
                        setHelpOnCurrentLocation();
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

    /**
     * Making Intent to call @EMERGENCY_PHONE_NUMBER via external application
     */

    private void callForHelp() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + EMERGENCY_PHONE_NUMBER));
        startActivity(callIntent);
    }

    /**
     * If locationForSMS is not available, than try to get it from FireStore
     */

    private void sendSMSForHelp(){
        if (locationForSMS == null){
            try {
                DocumentReference docRef = mDb.collection(getString(R.string.collection_user_locations))
                        .document(Objects.requireNonNull(userId));
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "sendSMSForHelp: successfully got users location");
                            UserLocation userLocation = Objects.requireNonNull(task.getResult()).toObject(UserLocation.class);
                            assert userLocation != null;
                            locationForSMS = userLocation.getGeo_point();
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

    /**
     * Making Intent to send @EMERGENCY_PHONE_NUMBER via external application
     */
    public void createSMS(){
        Log.d(TAG, "createSMS: creating SMS");
        Intent textIntent = new Intent(Intent.ACTION_VIEW);
        textIntent.setType("vnd.android-dir/mms-sms");
        textIntent.putExtra("address", EMERGENCY_PHONE_NUMBER);

        if (helpCase.equals(""))
            textIntent.putExtra("sms_body", "Proszę o pomoc. Moja lokalizacja to: Szerokość: " +
                    locationForSMS.getLatitude() + "; Długość: " + locationForSMS.getLongitude() + ".");

        else
            textIntent.putExtra("sms_body", "Proszę o pomoc. Powód wezwania pomocy: " + helpCase +
                    ". Moja lokalizacja to: Szerokość: " + locationForSMS.getLatitude() + "; Długość: " + locationForSMS.getLongitude() + ".");

        startActivity(textIntent);
    }

    /**
     * Creating a popup dialog, which asks user if he wants to call or text emergency
     * Popup shows only, when user is clicking on icon with help cause.
     */

    public void createHelpDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setMessage("Czy chcesz powiadomić służby ratunkowe?")
                .setCancelable(true)
                .setPositiveButton("Zadzwoń", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                        callForHelp();
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
