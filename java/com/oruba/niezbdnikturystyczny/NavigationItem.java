package com.oruba.niezbdnikturystyczny;


import android.support.annotation.NonNull;


public class NavigationItem implements Comparable<NavigationItem> {
    private String mHillName;
    private int mHillHeight;
    private int mImageResourceId;
    private double mHillLatitude;
    private double mHillLongitude;
    private String mHillDescription;


    public NavigationItem(String hillName, int hillHeight, int imageResourceID, double hillLatitude, double hillLongitude, String hillDescription){
        mHillName = hillName;
        mHillHeight = hillHeight;
        mImageResourceId = imageResourceID;
        mHillDescription = hillDescription;
        mHillLatitude = hillLatitude;
        mHillLongitude = hillLongitude;

    }

    public String getmHillName() {
        return mHillName;
    }

    public int getmHillHeight() {
        return mHillHeight;
    }

    public int getmImageResourceId() {
        return mImageResourceId;
    }

    public double getmHillLongitude() {
        return mHillLongitude;
    }

    public double getmHillLatitude() {
        return mHillLatitude;
    }

    public String getmHillDescription() {
        return mHillDescription;
    }


    @Override
    public int compareTo(@NonNull NavigationItem o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return -1;
        return mHillName.compareTo(o.mHillName);
    }
}
