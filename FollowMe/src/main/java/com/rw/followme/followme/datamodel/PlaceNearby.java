package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class PlaceNearby {

    @SerializedName("html_attributions")
    private List<String> htmlAttributions;

    @SerializedName("results")
    private List<Result> results;

    @SerializedName("status")
    private String status;

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }
}
