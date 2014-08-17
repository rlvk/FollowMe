package com.rw.followme.followme.datamodel;


import com.google.gson.annotations.SerializedName;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class Geometry {

    @SerializedName("location")
    private Location location;

    public Location getLocation() {
        return location;
    }
}
