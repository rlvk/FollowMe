package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rafalwesolowski on 31/05/2014.
 */
public class Distance {

    @SerializedName("value")
    private long value;

    @SerializedName("text")
    private String text;

    public long getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
