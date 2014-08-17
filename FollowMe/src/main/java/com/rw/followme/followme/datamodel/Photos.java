package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class Photos {

    @SerializedName("height")
    private int height;

    @SerializedName("html_attributions")
    private List<String> htmlAttributions;

    @SerializedName("photo_reference")
    private String photoReference;

    @SerializedName("width")
    private int width;

    public int getHeight() {
        return height;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public int getWidth() {
        return width;
    }
}
