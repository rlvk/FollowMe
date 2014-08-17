package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rafalwesolowski on 31/05/2014.
 */
public class Polyline {

    @SerializedName("points")
    private String points;

    public String getPoints() {
        return points;
    }
}
