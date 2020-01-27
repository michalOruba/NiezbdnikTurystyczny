package com.oruba.niezbdnikturystyczny;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oruba.niezbdnikturystyczny.models.UserHill;
import com.oruba.niezbdnikturystyczny.util.AchievementItemAdapter;

import java.util.ArrayList;
import java.util.Objects;

import static com.oruba.niezbdnikturystyczny.Constants.ALL_HILLS_AVAILABLE;

/**
 * Class handling an achievements.
 *
 */


public class AchievementActivity extends AppCompatActivity {
    private static final String TAG = "NavigationActivity";

    FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private int achievedInSummer = 0;
    private int achievedInWinter = 0;
    private double achievedInSummerPercentage;
    private double achievedInWinterPercentage;
    ArrayList<UserHill> achievedHills = new ArrayList<>();
    ListView listView;
    TextView hillSummerStatusNumbers,
    hillWinterStatusNumbers;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_layout);
        Log.d(TAG, "onCreate: Starting.");


        CollectionReference getAchievedHillRef = getAchievedHillsReference();




        AchievementItemAdapter adapter = new AchievementItemAdapter(this, countAchievedHills(getAchievedHillRef));
        listView = findViewById(R.id.navigation_list);
        listView.setAdapter(adapter);

    }


    /**
     *  Method connects with Firestore to get achieved hills
     * @param getAchievedHillRef parameter referring to current users achieved hills collection
     * @return Method returns an arraylist with data to display
     */
    private ArrayList<UserHill> countAchievedHills(CollectionReference getAchievedHillRef) {
        getAchievedHillRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                try {
                                    UserHill userHill = doc.toObject(UserHill.class);
                                    achievedHills.add(userHill);
                                    achievedInSummer += userHill.getAchieve_summer_status();
                                    achievedInWinter += userHill.getAchieve_winter_status();

                                    Log.d(TAG, "onEvent: " + userHill.getHill().getHill_name());
                                } catch (NullPointerException ex) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + ex.getMessage());
                                }
                            }
                            achievedInSummerPercentage = ((double)achievedInSummer/ALL_HILLS_AVAILABLE)*100;
                            achievedInWinterPercentage = ((double)achievedInWinter/ALL_HILLS_AVAILABLE)*100;
                            hillSummerStatusNumbers = findViewById(R.id.hills_status_summer_numbers);
                            hillWinterStatusNumbers = findViewById(R.id.hills_status_winter_numbers);
                            hillSummerStatusNumbers.setText(getString(R.string.hills_achieved, achievedInSummer, ALL_HILLS_AVAILABLE, achievedInSummerPercentage));
                            hillWinterStatusNumbers.setText(getString(R.string.hills_achieved, achievedInWinter, ALL_HILLS_AVAILABLE, achievedInWinterPercentage));
                        }
                    }
                });
        return achievedHills;
    }

    /**
     *
     * @return Firestore reference to current users Collection
     */
    @NonNull
    private CollectionReference getAchievedHillsReference() {
        return mDb.collection(getString(R.string.collection_user_hills))
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .collection(getString(R.string.collection_achieved_hills));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<UserHill> tempList = new ArrayList<>();

                for (UserHill temp : achievedHills){
                    if (temp.getHill().getHill_name().toLowerCase().contains(newText.toLowerCase())){
                        tempList.add(temp);
                    }
                }
                AchievementItemAdapter adapter = new AchievementItemAdapter(AchievementActivity.this, tempList);

                listView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
