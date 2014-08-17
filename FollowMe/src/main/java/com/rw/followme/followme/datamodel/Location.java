package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class Location {

    @SerializedName("lat")
    private float latitude;

    @SerializedName("lng")
    private float longitude;

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
