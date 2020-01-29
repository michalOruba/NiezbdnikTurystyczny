package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that prepares hills to be display in form of listView on the screen.
 * Sets up Views values and handles popup detail view for clicked item.
 */

public class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {

    private Dialog myDialog;
    private Dialog progressDialog;

    NavigationItemAdapter(@NonNull Activity context, ArrayList<NavigationItem> items) {
        super(context,0, items);
    }

    private void runMapsActivity(final NavigationItem mCurrentItem) {
        progressDialog.setCancelable(false);
        progressDialog.show();

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

    /**
     * Method prepares and displays information about selected hill
     * @param mCurrentItem Clicked element
     */

    private void showInfoDialog(final NavigationItem mCurrentItem) {
        ImageView dialogImageView = myDialog.findViewById(R.id.desc_navigation_image);
        TextView dialogHillName = myDialog.findViewById(R.id.desc_navigation_hill_name);
        TextView dialogHillHeight = myDialog.findViewById(R.id.desc_navigation_hill_height);
        TextView dialogHillDesc = myDialog.findViewById(R.id.desc_navigation_hill_description);
        Button dialogButton = myDialog.findViewById(R.id.navigation_dialog_button);
        dialogImageView.setImageResource(mCurrentItem.getmImageResourceId());
        dialogHillName.setText(mCurrentItem.getmHillName());
        dialogHillHeight.setText(String.format("%1s%2s", mCurrentItem.getmHillHeight(), getContext().getString(R.string.MASLevel)));
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

    /**
     * Method is attaching  properties to each element on the list.
     * @param position Position of clicked element on the list
     * @param convertView is used for recycling elements - only few elements are display at on time.
     * @param parent The parent is provided to possibility of inflate view into that for proper layout parameters
     * @return New list item view to display to the user.
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("NavigationItemAdapter", "getView: creating List View");


        View listItemView = convertView;
        final NavigationItem currentItem = getItem(position);


        createDialogs();

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.navigation_list_item, parent, false);
        }

        TextView navigationHillName = listItemView.findViewById(R.id.navigation_hill_name);
        assert currentItem != null;
        navigationHillName.setText(currentItem.getmHillName());

        TextView navigationHillHeight = listItemView.findViewById(R.id.navigation_hill_height);
        navigationHillHeight.setText(String.format("%1s%2s", currentItem.getmHillHeight(), getContext().getString(R.string.MASLevel)));


        //Handle click on background image
        ImageView navigationImageID = listItemView.findViewById(R.id.navigation_image);
        navigationImageID.setImageResource(currentItem.getmImageResourceId());
        navigationImageID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(currentItem);
            }
        });

        TextView navigationHillDescription = listItemView.findViewById(R.id.navigation_hill_description);
        navigationHillDescription.setText(currentItem.getmHillDescription());


        //Handle click on info icon
        ImageView infoImageView = listItemView.findViewById(R.id.navigation_info_button);
        infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(currentItem);
            }
        });

        final ImageView navigateImageView = listItemView.findViewById(R.id.navigation_navigate_button);
        navigateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runMapsActivity(currentItem);
            }
        });


        return listItemView;
    }


    /**
     * Method that initialize dialogs elements
     */
    private void createDialogs() {
        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.navigation_list_item_desc);
        Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressDialog = new Dialog(getContext());
        progressDialog.setContentView(R.layout.loading_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
