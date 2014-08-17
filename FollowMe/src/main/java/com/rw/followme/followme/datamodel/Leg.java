package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafalwesolowski on 31/05/2014.
 */
public class Leg {

    @SerializedName("distance")
    private Distance distance;

    @SerializedName("duration")
    private Duration duration;

    @SerializedName("end_address")
    private String endAddress;

    @SerializedName("end_location")
    private Location endLocation;

    @SerializedName("start_address")
    private String startAddress;

    @SerializedName("start_location")
    private Location startLocation;

    @SerializedName("lat")
    private float latitude;

    @SerializedName("lng")
    private float longitude;

    @SerializedName("steps")
    private List<Step> steps;

    @SerializedName("via_waypoint")
    private List<String> viaWaypoints;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<String> getViaWaypoints() {
        return viaWaypoints;
    }
}
