package com.example.pbarn.gps_bead;

import java.io.Serializable;

/**
 * Created by pbarn on 2016. 04. 22..
 */
public class PozAdatok implements Serializable {
    double lat;
    double lon;
    double alt;
    float speed;
    String time;

    public PozAdatok(double lat, double lon, double alt, float speed, String time) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.speed = speed;
        this.time = time;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
