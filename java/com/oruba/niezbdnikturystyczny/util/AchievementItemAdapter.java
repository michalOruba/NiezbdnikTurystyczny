package com.oruba.niezbdnikturystyczny.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.oruba.niezbdnikturystyczny.R;
import com.oruba.niezbdnikturystyczny.models.UserHill;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AchievementItemAdapter extends ArrayAdapter<UserHill> {

    public AchievementItemAdapter(@NonNull Activity context, ArrayList<UserHill> items) {
        super(context,0, items);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("NavigationItemAdapter", "getView: creating List View");


        View listItemView = convertView;
        final UserHill currentItem = getItem(position);


        //constraintLayout = listItemView.findViewById(R.id.desc_navigation_item_container);

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.achievement_list_item, parent, false);
        }

        TextView achievementHillName = (TextView) listItemView.findViewById(R.id.achievement_hill_name);
        achievementHillName.setText(currentItem.getHill().getHill_name());

        TextView achievementHillHeight = (TextView) listItemView.findViewById(R.id.achievement_hill_height);
        achievementHillHeight.setText(String.valueOf(currentItem.getHill().getHill_height()) + getContext().getString(R.string.MASLevel));


        TextView achievementHillDate = (TextView) listItemView.findViewById(R.id.achievement_hill_date);
        achievementHillDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(currentItem.getAchieve_date()));

        ImageView achievementImageID = (ImageView) listItemView.findViewById(R.id.achievement_image);
        achievementImageID.setImageResource(currentItem.getHill().getHill_avatar());

        TextView achievementHillSummer = (TextView) listItemView.findViewById(R.id.achievement_hill_summer_status);
        if(currentItem.getAchieve_summer_status() == 1) {
            achievementHillSummer.setText(getContext().getString(R.string.hill_achieved));
        }
        else {
            achievementHillSummer.setText(getContext().getString(R.string.hill_not_achieved));
        }

        TextView achievementHillWinter = (TextView) listItemView.findViewById(R.id.achievement_hill_winter_status);
        if(currentItem.getAchieve_winter_status() == 1) {
            achievementHillWinter.setText(getContext().getString(R.string.hill_achieved));
        }
        else {
            achievementHillWinter.setText(getContext().getString(R.string.hill_not_achieved));
        }

        return listItemView;
    }
}
