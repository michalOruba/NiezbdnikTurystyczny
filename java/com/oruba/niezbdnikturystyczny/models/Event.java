package com.oruba.niezbdnikturystyczny.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Event implements Parcelable {

    private GeoPoint geo_point;
    private String event;
    private String avatar;
    private String event_id;
    private @ServerTimestamp Date time_stamp;

    public Event(GeoPoint geo_point, String event, String avatar, Date time_stamp, String event_id) {
        this.geo_point = geo_point;
        this.event = event;
        this.avatar = avatar;
        this.time_stamp = time_stamp;
        this.event_id = event_id;
    }

    public Event() {
    }

    protected Event(Parcel in) {
        event = in.readString();
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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
                ", name='" + event + '\'' +
                ", avatar='" + avatar + '\'' +
                ", time_stamp=" + time_stamp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(event);
        dest.writeString(event_id);
        dest.writeString(avatar);
    }
}
