package com.oruba.niezbdnikturystyczny;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import java.util.ArrayList;

public class Navigation1Fragment extends Fragment {

    final ArrayList<NavigationItem> hills = new ArrayList<NavigationItem>();
    ListView listView;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSetOld = new ConstraintSet();
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private boolean altLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation1_fragment,container,false);
        final View itemView = inflater.inflate(R.layout.navigation_list_item,container,false);
        setHasOptionsMenu(true);


        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Rysy"));
        hills.add(new NavigationItem("Kasprowy", 2499, R.drawable.rysy, 49.1795611, 20.0792878,"Przykładowy opis wzgórza Kasprowy"));
        hills.add(new NavigationItem("Kopa", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Kopa"));
        hills.add(new NavigationItem("Gęsia", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Gęsia"));
        hills.add(new NavigationItem("Babia", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Babia"));
        hills.add(new NavigationItem("Giewont", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Giewont"));
        hills.add(new NavigationItem("Kościelec", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Kościelec"));
        hills.add(new NavigationItem("Świnica", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Świnica"));
        hills.add(new NavigationItem("Krywań", 2499, R.drawable.rysy, 49.1795611, 20.0792878, "Przykładowy opis wzgórza Krywań"));

        NavigationItemAdapter adapter = new NavigationItemAdapter(getActivity(), hills);

        listView = (ListView) view.findViewById(R.id.navigation_list);

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long viewId = view.getId();

                if (viewId == R.id.navigation_info_button) {
                    Toast.makeText(getActivity(), "navigationInfoButton item clicked", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "ListView clicked: " + id, Toast.LENGTH_SHORT).show();

                }

            }
        });

        return view;
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu,menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Click here to search");
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<NavigationItem> tempList = new ArrayList<>();

                for (NavigationItem temp : hills){
                    if (temp.getmHillName().toLowerCase().contains(newText.toLowerCase())){
                        tempList.add(temp);
                    }
                }
                NavigationItemAdapter adapter = new NavigationItemAdapter(getActivity(), tempList);

                listView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

    }

    public Navigation1Fragment() {

    }



}
