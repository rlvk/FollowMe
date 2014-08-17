package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rafalwesolowski on 31/05/2014.
 */
public class Step {

    @SerializedName("distance")
    private Distance distance;

    @SerializedName("duration")
    private Duration duration;

    @SerializedName("end_location")
    private Location endLocation;

    @SerializedName("html_instructions")
    private String htmlInstructions;

    @SerializedName("polyline")
    private Polyline polyline;

    @SerializedName("start_location")
    private Location startLocation;

    @SerializedName("travel_mode")
    private String travelMode;

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public String getTravelMode() {
        return travelMode;
    }
}
