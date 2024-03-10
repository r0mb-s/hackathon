package com.example.smartcity;

import com.google.android.gms.maps.model.Marker;

public class Pinpoint {
    public double latitude;
    public double longitude;
    public double percentage;
    public Marker marker;
    public int id;

    public Pinpoint(double latitude, double longitude, double percentage, int id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.percentage = percentage;
        this.id = id;
    }



}
