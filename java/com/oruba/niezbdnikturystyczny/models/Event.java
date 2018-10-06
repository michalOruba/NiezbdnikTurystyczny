package com.oruba.niezbdnikturystyczny.models;


import com.google.firebase.firestore.GeoPoint;

public class Event {

    private GeoPoint geo_point;
    private String title;
    private String avatar;


    public Event(GeoPoint geo_point, String title, String avatar) {
        this.geo_point = geo_point;
        this.title = title;
        this.avatar = avatar;
    }

    public Event() {

    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "Event{" +
                "geo_point=" + geo_point +
                ", title='" + title + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
