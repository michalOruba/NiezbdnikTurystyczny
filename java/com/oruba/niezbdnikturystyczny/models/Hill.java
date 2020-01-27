package com.oruba.niezbdnikturystyczny.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Hill implements Parcelable {

    private String hill_name;
    private int hill_height;
    private String hill_avatar;
    private GeoPoint hill_geo_point;
    private String hill_description;
    private String hill_id;

    public Hill(String hill_name, int hill_height, String hill_avatar, GeoPoint hill_geo_point, String hill_description, String hill_id) {
        this.hill_name = hill_name;
        this.hill_height = hill_height;
        this.hill_avatar = hill_avatar;
        this.hill_geo_point = hill_geo_point;
        this.hill_description = hill_description;
        this.hill_id = hill_id;
    }

    public Hill (){
    }

    public String getHill_name() {
        return hill_name;
    }

    public void setHill_name(String hill_name) {
        this.hill_name = hill_name;
    }

    public int getHill_height() {
        return hill_height;
    }

    public void setHill_height(int hill_height) {
        this.hill_height = hill_height;
    }

    public String getHill_avatar() {
        return hill_avatar;
    }

    public void setHill_avatar(String hill_avatar) {
        this.hill_avatar = hill_avatar;
    }

    public GeoPoint getHill_geopoint() {
        return hill_geo_point;
    }

    public void setHill_geopoint(GeoPoint hill_geo_point) {
        this.hill_geo_point = hill_geo_point;
    }

    public String getHill_description() {
        return hill_description;
    }

    public void setHill_description(String hill_description) {
        this.hill_description = hill_description;
    }

    public String getHill_id() {
        return hill_id;
    }

    public void setHill_id(String hill_id) {
        this.hill_id = hill_id;
    }

    @Override
    public String toString() {
        return "Hill{" +
                "hill_name='" + hill_name + '\'' +
                ", hill_height=" + hill_height +
                ", hill_avatar=" + hill_avatar +
                ", hill_geo_point=" + hill_geo_point +
                ", hill_description='" + hill_description + '\'' +
                ", hill_id='" + hill_id + '\'' +
                '}';
    }

    protected Hill(Parcel in) {
        hill_name = in.readString();
        hill_height = in.readInt();
        hill_avatar = in.readString();
        hill_description = in.readString();
    }

    public static final Creator<Hill> CREATOR = new Creator<Hill>() {
        @Override
        public Hill createFromParcel(Parcel in) {
            return new Hill(in);
        }

        @Override
        public Hill[] newArray(int size) {
            return new Hill[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hill_name);
        dest.writeInt(hill_height);
        dest.writeString(hill_avatar);
        dest.writeString(hill_description);
    }
}
