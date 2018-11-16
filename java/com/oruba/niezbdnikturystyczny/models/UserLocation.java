package com.oruba.niezbdnikturystyczny.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation implements Parcelable {
    private GeoPoint geo_point;
    private @ServerTimestamp Date time_stamp;
    private double latitude, longitude;
    private User user;

    private static final String TAG = "UserLocation";

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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



    protected UserLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        user = in.readParcelable(User.class.getClassLoader());
        geo_point = new GeoPoint(latitude, longitude);
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        latitude = getGeo_point().getLatitude();
        longitude = getGeo_point().getLongitude();
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeParcelable(user, flags);
        Log.d(TAG, "writeToParcel: Im writhing things to Parcel: lat: " +latitude + ", long: " + longitude);
    }
}


