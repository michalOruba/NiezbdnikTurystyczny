package com.oruba.niezbdnikturystyczny;


import android.support.annotation.NonNull;


/**
 * Class instances are elements of an array list, displayed on Navigation Layout
 */

public class NavigationItem implements Comparable<NavigationItem> {
    private String mHillName;
    private int mHillHeight;
    private int mImageResourceId;
    private double mHillLatitude;
    private double mHillLongitude;
    private String mHillDescription;


    NavigationItem(String hillName, int hillHeight, int imageResourceID, double hillLatitude, double hillLongitude, String hillDescription){
        mHillName = hillName;
        mHillHeight = hillHeight;
        mImageResourceId = imageResourceID;
        mHillDescription = hillDescription;
        mHillLatitude = hillLatitude;
        mHillLongitude = hillLongitude;

    }

    String getmHillName() {
        return mHillName;
    }

    int getmHillHeight() {
        return mHillHeight;
    }

    int getmImageResourceId() {
        return mImageResourceId;
    }

    double getmHillLongitude() {
        return mHillLongitude;
    }

    double getmHillLatitude() {
        return mHillLatitude;
    }

    String getmHillDescription() {
        return mHillDescription;
    }

    /**
     * Compare objects based on @mHillName
     * @param o Object to compare with
     * @return < 0 if this is first, 0 when equal, > 0 otherwise
     */
    @Override
    public int compareTo(@NonNull NavigationItem o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return -1;
        return mHillName.compareTo(o.mHillName);
    }
}
