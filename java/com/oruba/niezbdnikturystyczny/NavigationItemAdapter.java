package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {

    Dialog myDialog;
    Dialog progressDialog;

    public NavigationItemAdapter(@NonNull Activity context, ArrayList<NavigationItem> items) {
        super(context,0, items);
    }

    private void runMapsActivity(final NavigationItem mCurrentItem) {
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.d("Navigation1Fragment", "Dotykam navi!!!!!!!!!!!!!!!!!");

        Intent navigateIntent = new Intent(getContext(), MapsActivity.class );
        navigateIntent.putExtra("HILL_LATITUDE", mCurrentItem.getmHillLatitude());
        navigateIntent.putExtra("HILL_LONGITUDE", mCurrentItem.getmHillLongitude());
        navigateIntent.putExtra("HILL_NAME", mCurrentItem.getmHillName());

        Log.d("Navigation1Fragment", "1st Latitude " + mCurrentItem.getmHillLatitude());
        Log.d("Navigation1Fragment", "1st Longitude " + mCurrentItem.getmHillLongitude());

        navigateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(navigateIntent);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                myDialog.dismiss();
            }
        }, 500);
    }

    private void showInfoDialog(final NavigationItem mCurrentItem) {
        ImageView dialogImageView = (ImageView) myDialog.findViewById(R.id.desc_navigation_image);
        TextView dialogHillName = (TextView) myDialog.findViewById(R.id.desc_navigation_hill_name);
        TextView dialogHillHeight = (TextView) myDialog.findViewById(R.id.desc_navigation_hill_height);
        TextView dialogHillDesc = (TextView) myDialog.findViewById(R.id.desc_navigation_hill_description);
        Button dialogButton = (Button) myDialog.findViewById(R.id.navigation_dialog_button);
        dialogImageView.setImageResource(mCurrentItem.getmImageResourceId());
        dialogHillName.setText(mCurrentItem.getmHillName());
        dialogHillHeight.setText(String.valueOf(mCurrentItem.getmHillHeight()) + getContext().getString(R.string.MASLevel));
        dialogHillDesc.setText(mCurrentItem.getmHillDescription());
        dialogHillDesc.setMovementMethod(new ScrollingMovementMethod());
        myDialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
                runMapsActivity(mCurrentItem);
            }
        });
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
        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
        navigationImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(currentItem);
            }
        });

        TextView navigationHillDescription = (TextView) listItemView.findViewById(R.id.navigation_hill_description);
        navigationHillDescription.setText(currentItem.getmHillDescription());



        ImageView infoImageView = (ImageView) listItemView.findViewById(R.id.navigation_info_button);
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(currentItem);
            }
        });

        final ImageView navigateImageView = (ImageView) listItemView.findViewById(R.id.navigation_navigate_button);





        navigateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runMapsActivity(currentItem);
            }
        });


        return listItemView;
    }
}
