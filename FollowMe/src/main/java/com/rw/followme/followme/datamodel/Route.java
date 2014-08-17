package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafalwesolowski on 31/05/2014.
 */
public class Route {

    @SerializedName("bounds")
    private Bounds bounds;

    @SerializedName("copyrights")
    private String copyrights;

    @SerializedName("legs")
    private List<Leg> legs;

    @SerializedName("overview_polyline")
    private Polyline polyline;

    @SerializedName("summary")
    private String summary;

    @SerializedName("warnings")
    private List<String> warnings;

    @SerializedName("waypoint_order")
    private List<String> waypointOrder;

    public Bounds getBounds() {
        return bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getWaypointOrder() {
        return waypointOrder;
    }
}
