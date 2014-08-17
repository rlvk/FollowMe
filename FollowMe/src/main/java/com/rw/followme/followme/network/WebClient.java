package com.rw.followme.followme.network;

import android.content.Context;
import android.location.Location;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.rw.followme.followme.R;
import com.rw.followme.followme.datamodel.Direction;
import com.rw.followme.followme.datamodel.PlaceNearby;

/**
 * Created by rafalwesolowski on 30/03/2014.
 */
public class WebClient {

    private final Context context;
    private final RequestQueue requestQueue;
    private final String GOOGLE_SERVER_API_KEY = "AIzaSyD1InFW19JiiXfICYri1wGHhwJnZi15yb0";
    private final Uri BASE_URL;

    public WebClient(Context context){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        BASE_URL = Uri.parse(context.getString(R.string.google_places_base_url));
    }

    public void searchNearby(Location location, int radius, String name, Response.Listener<PlaceNearby> listener, Response.ErrorListener errorListener){
        Uri.Builder builder = BASE_URL.buildUpon().appendEncodedPath("place/nearbysearch/json");
        builder.appendQueryParameter("location", (location != null) ? (location.getLatitude() + "," + location.getLongitude()) : "");
        builder.appendQueryParameter("radius", String.valueOf(radius));
        builder.appendQueryParameter("name", name);
        callService(builder, new TypeToken<PlaceNearby>() {}, listener, errorListener);
    }

    public void getDirection(String currentLocation, String destination, Response.Listener<Direction> listener, Response.ErrorListener errorListener){
        Uri.Builder builder = BASE_URL.buildUpon().appendEncodedPath("directions/json");
        builder.appendQueryParameter("origin", currentLocation);
        builder.appendQueryParameter("destination", destination);
        builder.appendQueryParameter("language", context.getResources().getConfiguration().locale.getLanguage());
        builder.appendQueryParameter("mode", "walking");
        callService(builder, new TypeToken<Direction>() {}, listener, errorListener);
    }

    private void callService(Uri.Builder builder, TypeToken token, Response.Listener listener, Response.ErrorListener errorListener){
        builder.appendQueryParameter("key", GOOGLE_SERVER_API_KEY);
        builder.appendQueryParameter("sensor", String.valueOf(false));
        Request request = new GsonRequest(Method.GET, builder.build().toString(), token, null, listener, errorListener);
        requestQueue.add(request);
    }
}
