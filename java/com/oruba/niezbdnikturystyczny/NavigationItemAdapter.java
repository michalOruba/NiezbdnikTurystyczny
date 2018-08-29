package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {


    public NavigationItemAdapter(@NonNull Activity context, ArrayList<NavigationItem> items) {
        super(context,0, items);
    }
}
