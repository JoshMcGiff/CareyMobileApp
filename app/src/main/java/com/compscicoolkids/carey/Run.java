package com.compscicoolkids.carey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;


public class Run {
    private int id;
    private Date date;
    private LatLng start;
    private LatLng end;
    private double length;
    private int minutes;

    public Run(int id, LatLng start, LatLng end, double length, int minutes) {
        this.id = id;
        this.date = new Date();
        this.start = start;
        this.end = end;
        this.length = length;
        this.minutes = minutes;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public LatLng getStart() {
        return start;
    }

    public LatLng getEnd() {
        return end;
    }

    public double getLength() {
        return length;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public String toString() {
        return "Run{" +
                "id=" + id +
                ", date=" + date +
                ", start=" + start +
                ", end=" + end +
                ", length=" + length +
                ", minutes=" + minutes +
                '}';
    }
}
