package com.oruba.niezbdnikturystyczny;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;


import java.util.ArrayList;

public class Navigation1Fragment extends Fragment {

    final ArrayList<NavigationItem> hills = new ArrayList<NavigationItem>();
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation1_fragment,container,false);
        setHasOptionsMenu(true);


        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Kasprowy", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Kopa", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Gęsia", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Babia", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Giewont", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Kościelec", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Świnica", 2499, R.drawable.rysy, 49.1795611, 20.0792878));
        hills.add(new NavigationItem("Krywań", 2499, R.drawable.rysy, 49.1795611, 20.0792878));

        NavigationItemAdapter adapter = new NavigationItemAdapter(getActivity(), hills);

        listView = (ListView) view.findViewById(R.id.navigation_list);

        listView.setAdapter(adapter);

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
