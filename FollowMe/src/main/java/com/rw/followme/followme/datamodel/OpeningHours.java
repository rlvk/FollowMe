package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class OpeningHours {

    @SerializedName("open_now")
    private boolean openNow;

    public boolean isOpenNow() {
        return openNow;
    }
}
