package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.method.ScrollingMovementMethod;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {

    Dialog myDialog;

    public NavigationItemAdapter(@NonNull Activity context, ArrayList<NavigationItem> items) {
        super(context,0, items);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("NavigationItemAdapter", "getView: creating List View");


        View listItemView = convertView;
        final NavigationItem currentItem = getItem(position);


        //constraintLayout = listItemView.findViewById(R.id.desc_navigation_item_container);
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.navigation_list_item_desc);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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



        ImageView infoImageView = (ImageView) listItemView.findViewById(R.id.navigation_info_button);
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView dialogImageView = (ImageView) myDialog.findViewById(R.id.desc_navigation_image);
                TextView dialogHillName = (TextView) myDialog.findViewById(R.id.desc_navigation_hill_name);
                TextView dialogHillHeight = (TextView) myDialog.findViewById(R.id.desc_navigation_hill_height);
                TextView dialogHillDesc = (TextView) myDialog.findViewById(R.id.desc_navigation_hill_description);
                dialogImageView.setImageResource(currentItem.getmImageResourceId());
                dialogHillName.setText(currentItem.getmHillName());
                dialogHillHeight.setText(String.valueOf(currentItem.getmHillHeight()) + getContext().getString(R.string.MASLevel));
                dialogHillDesc.setText(currentItem.getmHillDescription());
                dialogHillDesc.setMovementMethod(new ScrollingMovementMethod());

                Toast.makeText(getContext(),"Klikam info " + String.valueOf(position),Toast.LENGTH_LONG).show();
                Log.d("Navigation1Fragment", "Dotykam info!!!!!!!!!!!!!!!!!");
                myDialog.show();
            }
        });

        final ImageView navigateImageView = (ImageView) listItemView.findViewById(R.id.navigation_navigate_button);


        navigateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Klikam navi " + String.valueOf(position),Toast.LENGTH_LONG).show();
                Log.d("Navigation1Fragment", "Dotykam navi!!!!!!!!!!!!!!!!!");

                Intent navigateIntent = new Intent(getContext(), MapsActivity.class );
                navigateIntent.putExtra("HILL_LATITUDE", currentItem.getmHillLatitude());
                navigateIntent.putExtra("HILL_LONGITUDE", currentItem.getmHillLongitude());
                navigateIntent.putExtra("HILL_NAME", currentItem.getmHillName());

                Log.d("Navigation1Fragment", "1st Latitude " + currentItem.getmHillLatitude());
                Log.d("Navigation1Fragment", "1st Longitude " + currentItem.getmHillLongitude());

                navigateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(navigateIntent);

            }
        });


        return listItemView;
    }
}
