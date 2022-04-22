package com.compscicoolkids.carey;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//class which represents a run object
public class Run implements Parcelable, Serializable {
    private final String date;
    private final String length;
    private final int minutes;

    public Run(String date, String length, int minutes) {
        this.length = length;
        this.minutes = minutes;
        this.date = date;
    }

    protected Run(Parcel in) {
        date = in.readString();
        length = in.readString();
        minutes = in.readInt();
    }

    public static final Creator<Run> CREATOR = new Creator<Run>() {
        @Override
        public Run createFromParcel(Parcel in) {
            return new Run(in);
        }

        @Override
        public Run[] newArray(int size) {
            return new Run[size];
        }
    };

    public String getDate() {
        return date;
    }

    public String getLength() {
        return length;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public String toString() {
        return "Run{" +
                "date=" + date +
                ", length=" + length +
                ", minutes=" + minutes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(length);
        parcel.writeInt(minutes);
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }

        if(!(o instanceof Run)){
            return false;
        }

        Run r = (Run) o;

        return this.date.equals(r.getDate()) && this.minutes == r.getMinutes() && this.length.equals(r.getLength());
    }
}
