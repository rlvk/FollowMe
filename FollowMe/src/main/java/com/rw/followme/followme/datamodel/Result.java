package com.rw.followme.followme.datamodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class Result {

    @SerializedName("geometry")
    private Geometry results;

    @SerializedName("icon")
    private String icon;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("opening_hours")
    private OpeningHours openingHours;

    @SerializedName("photos")
    private List<Photos> photos;

    @SerializedName("price_level")
    private int priceLevel;

    @SerializedName("rating")
    private float rating;

    @SerializedName("reference")
    private String reference;

    @SerializedName("types")
    private List<String> types;

    @SerializedName("vicinity")
    private String vicinity;

    public Geometry getGeometryResults() {
        return results;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

//    public List<Photos> getPhotos() {
//        return photos;
//    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public float getRating() {
        return rating;
    }

    public String getReference() {
        return reference;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getVicinity() {
        return vicinity;
    }
}
