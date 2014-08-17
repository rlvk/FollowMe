package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafalwesolowski on 31/05/2014.
 */
public class Direction {

    @SerializedName("status")
    private String status;

    @SerializedName("routes")
    private List<Route> routes;

    public String getStatus() {
        return status;
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
