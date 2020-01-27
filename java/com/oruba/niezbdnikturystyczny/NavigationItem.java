package com.oruba.niezbdnikturystyczny;

public class NavigationItem {
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
}
