package com.oruba.niezbdnikturystyczny;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oruba.niezbdnikturystyczny.models.Hill;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Populates listView in the Navigation Layout with all hills from database
 */

public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";

    private FirebaseFirestore mDb;
    private ArrayList<Hill> mHills = new ArrayList<>();
    ArrayList<NavigationItem> hills;
    ListView listView;


    @Override
    protected void onResume() {
        super.onResume();
        if (hills != null && hills.size() == 0)
        getHillsFromDB();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);
        Log.d(TAG, "onCreate: Starting.");

        mDb = FirebaseFirestore.getInstance();
        getHillsFromDB();

    }

    /**
     * Creating Array list for adapter, initializing NavigationItem objects
     */
    public void populateHillList(){
        hills = new ArrayList<>();


        for (Hill hill : mHills){
            hills.add(new NavigationItem(hill.getHill_name(), hill.getHill_height(), getResources().getIdentifier(hill.getHill_avatar(),"drawable",
                    getPackageName()), hill.getHill_geopoint().getLatitude(), hill.getHill_geopoint().getLongitude(), hill.getHill_description()));
        }
        Collections.sort(hills);
        NavigationItemAdapter adapter = new NavigationItemAdapter(this, hills);

        listView = findViewById(R.id.navigation_list);

        listView.setAdapter(adapter);
    }

    /**
     * Method that is getting information about hills from Firestore.
     * If connection is successful, then calls method @populateHillList().
     */
    public void getHillsFromDB(){
        mHills = new ArrayList<>();
        CollectionReference getHillRef = mDb.collection(getString(R.string.collection_hills));
        getHillRef
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
                                    Hill hill = doc.toObject(Hill.class);
                                    mHills.add(hill);
                                    Log.d(TAG, "onEvent: " + hill.getHill_name());
                                } catch (NullPointerException ex) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + ex.getMessage());
                                }
                            }
                            populateHillList();
                        }
                    }
                });
    }

    /**
     * Creates Option menu, with possibility to search by Hills name.
     * @param menu Menu object fo initialization
     * @return When you successfully handle a menu item, return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<NavigationItem> tempList = new ArrayList<>();

                for (NavigationItem temp : hills){
                    if (temp.getmHillName().toLowerCase().contains(newText.toLowerCase())){
                        tempList.add(temp);
                    }
                }
                NavigationItemAdapter adapter = new NavigationItemAdapter(NavigationActivity.this, tempList);

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
