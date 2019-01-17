package com.oruba.niezbdnikturystyczny.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class UserHill implements Parcelable {

    private @ServerTimestamp Date achieve_date;
    private int achieve_summer_status;
    private int achieve_winter_status;
    private Hill hill;

    public UserHill(Date achieve_date, int achieve_summer_status, int achieve_winter_status, Hill hill) {
        this.achieve_date = achieve_date;
        this.achieve_summer_status = achieve_summer_status;
        this.achieve_winter_status = achieve_winter_status;
        this.hill = hill;
    }

    public UserHill(){}

    public Date getAchieve_date() {
        return achieve_date;
    }

    public void setAchieve_date(Date achieve_date) {
        this.achieve_date = achieve_date;
    }

    public int getAchieve_summer_status() {
        return achieve_summer_status;
    }

    public void setAchieve_summer_status(int achieve_summer_status) {
        this.achieve_summer_status = achieve_summer_status;
    }

    public int getAchieve_winter_status() {
        return achieve_winter_status;
    }

    public void setAchieve_winter_status(int achieve_winter_status) {
        this.achieve_winter_status = achieve_winter_status;
    }

    public Hill getHill() {
        return hill;
    }

    public void setHill(Hill hill) {
        this.hill = hill;
    }

    protected UserHill(Parcel in) {
        achieve_summer_status = in.readInt();
        achieve_winter_status = in.readInt();
        hill = in.readParcelable(Hill.class.getClassLoader());
    }

    public static final Creator<UserHill> CREATOR = new Creator<UserHill>() {
        @Override
        public UserHill createFromParcel(Parcel in) {
            return new UserHill(in);
        }

        @Override
        public UserHill[] newArray(int size) {
            return new UserHill[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(achieve_summer_status);
        dest.writeInt(achieve_winter_status);
        dest.writeParcelable(hill, flags);
    }
}
