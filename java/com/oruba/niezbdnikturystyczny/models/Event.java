package com.oruba.niezbdnikturystyczny.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Event implements Parcelable {

    private GeoPoint geo_point;
    private String event_name;
    private String avatar;
    private String event_id;
    private @ServerTimestamp Date add_date;
    private User user;

    public Event(GeoPoint geo_point, String event_name, String avatar, Date add_date, String event_id, User user) {
        this.geo_point = geo_point;
        this.event_name = event_name;
        this.avatar = avatar;
        this.add_date = add_date;
        this.event_id = event_id;
        this.user = user;
    }

    public Event() {
    }

    protected Event(Parcel in) {
        event_name = in.readString();
        avatar = in.readString();
        event_id = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getAdd_date() {
        return add_date;
    }

    public void setAdd_date(Date time_stamp) {
        this.add_date = time_stamp;
    }

    public User getEventUser() {
        return user;
    }

    public void setEventUser(User user) {
        this.user = user;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    @Override
    public String toString() {
        return "Event{" +
                "geo_point=" + geo_point +
                ", name='" + event_name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", time_stamp=" + add_date +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event_name);
        dest.writeString(event_id);
        dest.writeString(avatar);
    }
}
