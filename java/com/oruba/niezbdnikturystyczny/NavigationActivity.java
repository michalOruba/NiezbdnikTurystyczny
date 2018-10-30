package com.oruba.niezbdnikturystyczny;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";


    ArrayList<NavigationItem> hills;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.navigation_layout);
        Log.d(TAG, "onCreate: Starting.");


        hills = new ArrayList<NavigationItem>();
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy, 49.179554, 20.088063, "Przykładowy opis wzgórza Rysy \nPrzykładowy opis wzgórza Rysy \nPrzykładowy opis wzgórza Rysy \nPrzykładowy opis wzgórza Rysy \nPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza RysyPrzykładowy opis wzgórza Rysy"));
        hills.add(new NavigationItem("Kasprowy Wierch", 2499, R.drawable.rysy, 49.236556, 19.932125,"Przykładowy opis wzgórza Kasprowy"));
        hills.add(new NavigationItem("Kopa Kondracka", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Kopa"));
        hills.add(new NavigationItem("Gęsia Szyja", 2499, R.drawable.rysy, 49.259444, 20.076667, "Przykładowy opis wzgórza Gęsia"));
        hills.add(new NavigationItem("Babia Góra", 2499, R.drawable.rysy, 49.573333, 19.529444, "Przykładowy opis wzgórza Babia"));
        hills.add(new NavigationItem("Giewont", 2499, R.drawable.rysy, 49.251004, 19.934039, "Przykładowy opis wzgórza Giewont"));
        hills.add(new NavigationItem("Kościelec", 2499, R.drawable.rysy, 49.225278, 20.014444, "Przykładowy opis wzgórza Kościelec"));
        hills.add(new NavigationItem("Świnica", 2499, R.drawable.rysy, 49.219417,20.009306, "Przykładowy opis wzgórza Świnica"));
        hills.add(new NavigationItem("Krywań", 2499, R.drawable.rysy, 49.162778, 19.998889, "Przykładowy opis wzgórza Krywań"));

        NavigationItemAdapter adapter = new NavigationItemAdapter(this, hills);

        listView = (ListView) findViewById(R.id.navigation_list);

        listView.setAdapter(adapter);


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
