package com.oruba.niezbdnikturystyczny.models;


import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Event {

    private GeoPoint geo_point;
    private String name;
    private String avatar;
    private @ServerTimestamp Date time_stamp;

    public Event(GeoPoint geo_point, String name, String avatar, Date time_stamp) {
        this.geo_point = geo_point;
        this.name = name;
        this.avatar = avatar;
        this.time_stamp = time_stamp;
    }

    public Event() {
    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "geo_point=" + geo_point +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", time_stamp=" + time_stamp +
                '}';
    }
}
