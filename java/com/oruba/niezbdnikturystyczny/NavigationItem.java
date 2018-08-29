package com.oruba.niezbdnikturystyczny;

public class NavigationItem {
    private String mHillName;
    private int mHillHeight;
    private int mImageResourceId;


    public NavigationItem(String hillName, int hillHeight, int imageResourceID){
        mHillName = hillName;
        mHillHeight = hillHeight;
        mImageResourceId = imageResourceID;

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

}
