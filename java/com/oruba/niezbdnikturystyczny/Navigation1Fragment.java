package com.oruba.niezbdnikturystyczny;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class Navigation1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation1_fragment,container,false);

        final ArrayList<NavigationItem> hills = new ArrayList<NavigationItem>();

        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));
        hills.add(new NavigationItem("Rysy", 2499, R.drawable.rysy));

        NavigationItemAdapter adapter = new NavigationItemAdapter(getActivity(), hills);

        ListView listView = (ListView) view.findViewById(R.id.navigation_list);

        listView.setAdapter(adapter);











        return view;
    }

    public Navigation1Fragment() {

    }


}
