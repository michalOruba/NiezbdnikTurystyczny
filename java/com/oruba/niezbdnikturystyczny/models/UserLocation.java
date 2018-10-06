package com.oruba.niezbdnikturystyczny.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {
    private GeoPoint geo_point;
    private @ServerTimestamp Date time_stamp;
    private User user;

    public UserLocation(GeoPoint geo_point, Date time_stamp, User user) {
        this.geo_point = geo_point;
        this.time_stamp = time_stamp;
        this.user = user;
    }

    public UserLocation() {
     }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geo_point=" + geo_point +
                ", time_stamp=" + time_stamp +
                ", user=" + user +
                '}';
    }
}


