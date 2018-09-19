package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {


    public NavigationItemAdapter(@NonNull Activity context, ArrayList<NavigationItem> items) {
        super(context,0, items);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        NavigationItem currentItem = getItem(position);

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.navigation_list_item, parent, false);
        }

        TextView navigationHillName = (TextView) listItemView.findViewById(R.id.navigation_hill_name);
        navigationHillName.setText(currentItem.getmHillName());

        TextView navigationHillHeight = (TextView) listItemView.findViewById(R.id.navigation_hill_height);
        navigationHillHeight.setText(String.valueOf(currentItem.getmHillHeight()) + getContext().getString(R.string.MASLevel));

        ImageView navigationImageID = (ImageView) listItemView.findViewById(R.id.navigation_image);
        navigationImageID.setImageResource(currentItem.getmImageResourceId());
        navigationImageID.setVisibility(View.VISIBLE);

        TextView navigationHillDescription = (TextView) listItemView.findViewById(R.id.navigation_hill_description);
        navigationHillDescription.setText(currentItem.getmHillDescription());



        ImageView navigationImageView = (ImageView) listItemView.findViewById(R.id.navigation_navigate_button);
        navigationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Klikam nawiguj " + String.valueOf(position),Toast.LENGTH_LONG).show();
                Log.d("Navigation1Fragment", "Dotykam nawiguj!!!!!!!!!!!!!!!!!");
            }
        });

        return listItemView;
    }
}
