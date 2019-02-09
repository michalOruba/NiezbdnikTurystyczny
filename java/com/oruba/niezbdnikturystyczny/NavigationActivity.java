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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oruba.niezbdnikturystyczny.models.Hill;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";

    private FirebaseFirestore mDb;
    private List<Hill> mHills = new ArrayList<>();
    ArrayList<NavigationItem> hills;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);
        Log.d(TAG, "onCreate: Starting.");
        mDb = FirebaseFirestore.getInstance();
        getHillsFromDB();


//        hills = new ArrayList<>();
//
//        for (Hill hill : mHills){
//            hills.add(new NavigationItem(hill.getHill_name(), hill.getHill_height(), getResources().getIdentifier(hill.getHill_avatar(),"drawable", getPackageName()), hill.getHill_geopoint().getLongitude(), hill.getHill_geopoint().getLongitude(), hill.getHill_description()));
//        }
//
//        hills.add(new NavigationItem("Rysy", 2499, getResources().getIdentifier("rysy","drawable", getPackageName()), 49.179554, 20.088063, "Góra położona na granicy polsko-słowackiej, w Tatrach Wysokich. Ma trzy wierzchołki, z których najwyższy jest środkowy, znajdujący się w całości na terytorium Słowacji. Wierzchołek północny, przez który biegnie granica, stanowi najwyżej położony punkt Polski i należy do Korony Europy."));
//        hills.add(new NavigationItem("Kasprowy Wierch", 1987, getResources().getIdentifier("kasprowy","drawable", getPackageName()), 49.236556, 19.932125,"Szczyt w Tatrach Zachodnich o wysokości 1987 m.  Zbudowany jest ze skał krystalicznych (granodioryty i pegmatyty), mimo położenia w młodszej części Tatr zbudowanej ze skał osadowych. Należy bowiem do tzw. wyspy krystalicznej Goryczkowej."));
//        hills.add(new NavigationItem("Kopa Kondracka", 2005, getResources().getIdentifier("kopa","drawable", getPackageName()), 49.1795611, 20.0792878, "Szczyt w Tatrach Zachodnich, najniższy z Czerwonych Wierchów, wysunięty najbardziej na wschód. Zbudowana jest ze skał osadowych (wapieni i dolomitów). Część szczytowa, podobnie jak w przypadku pozostałych szczytów z tej grupy wchodzących w skład wyspy krystalicznej Goryczkowej, pokryta jest czapą skał metamorficznych i alaskitów (na wysokości powyżej 1930 m n.p.m.)."));
//        hills.add(new NavigationItem("Gęsia Szyja", 1489, getResources().getIdentifier("gesia","drawable", getPackageName()), 49.259444, 20.076667, "Szczyt w reglowej części Tatr Wysokich. Jest najwyższy w grupie masywu o tej samej nazwie. Grupę Gęsiej Szyi od masywu Koszystej oddziela Rówień Waksmundzka. Dawniej nazwa Gęsia Szyja dotyczyła tylko wąskiego i wygiętego upłazu na wschodnim grzbiecie, od strony Rusinowej Polany. Jego kształt przypominał góralom gęsią szyję. Później nazwę tę zastosowano do całego masywu."));
//        hills.add(new NavigationItem("Babia Góra", 1723, getResources().getIdentifier("babia","drawable", getPackageName()), 49.573333, 19.529444, "Najwyższy szczyt masywu Babiej Góry w Paśmie Babiogórskim należącym do Beskidu Żywieckiego. W masywie Babiej Góry wyróżniono wiele wierzchołków, aby więc sprecyzować, o który dokładnie z nich chodzi, dla najwyższego używa się odrębnej nazwy Diablak."));
//        hills.add(new NavigationItem("Wielki Giewont", 1894, getResources().getIdentifier("giewont","drawable", getPackageName()), 49.251004, 19.934039, "Wznoszący się na wysokość 1894 m n.p.m. główny szczyt masywu Giewontu w Tatrach Zachodnich. Wierzchołek Wielkiego Giewontu zbudowany jest z wapieni górnej jury, poniżej leżą wapienie triasu (anizyku) (seria wierchowa). Utwory te zapadają pod kątem ok. 80 stopni ku północy i należą do płaszczowiny Giewontu."));
//        hills.add(new NavigationItem("Kościelec", 2155, getResources().getIdentifier("koscielec","drawable", getPackageName()), 49.225278, 20.014444, "Szczyt w Dolinie Gąsienicowej w Tatrach Wysokich. Znajduje się w bocznej Grani Kościelców, która od Zawratowej Turni odbiega w północnym kierunku, dzieląc Dolinę Gąsienicową na Czarną i Zieloną. Nazwa szczytu wywodzi się prawdopodobnie od kształtu przypominającego dach kościoła. Jego sylwetka w kształcie stromej piramidy to bardzo charakterystyczny element panoramy Doliny Gąsienicowej."));
//        hills.add(new NavigationItem("Świnica", 2301, getResources().getIdentifier("swinica","drawable", getPackageName()), 49.219417,20.009306, "Zwornikowy szczyt w grani głównej Tatr Wysokich o dwóch wierzchołkach, różniących się wysokością o 10 m. Mająca kształt szerokiej piramidy skalnej Świnica jest pierwszym od zachodu wybitnym szczytem (o wybitności ponad 100 m) Tatr Wysokich i kapitalnym punktem widokowym."));
//        hills.add(new NavigationItem("Krywań", 2494, getResources().getIdentifier("krywan","drawable", getPackageName()), 49.162778, 19.998889, "Szczyt w południowo-zachodniej części Tatr Wysokich po stronie słowackiej, o charakterystycznym, zakrzywionym wierzchołku, od którego wziął swoją nazwę (nadal spotykana jest wśród starszych górali wymowa \"Krzywań\")."));
//
//        NavigationItemAdapter adapter = new NavigationItemAdapter(this, hills);
//
//        listView = (ListView) findViewById(R.id.navigation_list);
//
//        listView.setAdapter(adapter);

    }
    public void populateHillList(){
        hills = new ArrayList<>();

        for (Hill hill : mHills){
            hills.add(new NavigationItem(hill.getHill_name(), hill.getHill_height(), getResources().getIdentifier(hill.getHill_avatar(),"drawable",
                    getPackageName()), hill.getHill_geopoint().getLatitude(), hill.getHill_geopoint().getLongitude(), hill.getHill_description()));
        }
        NavigationItemAdapter adapter = new NavigationItemAdapter(this, hills);

        listView = (ListView) findViewById(R.id.navigation_list);

        listView.setAdapter(adapter);
    }
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
