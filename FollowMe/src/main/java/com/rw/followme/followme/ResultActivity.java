package com.rw.followme.followme;

import android.app.ActionBar;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.gson.Gson;
import com.rw.followme.followme.datamodel.PlaceNearby;

import java.util.Locale;

/**
 * Created by rafalwesolowski on 21/04/2014.
 */
public class ResultActivity extends FragmentActivity implements TextToSpeech.OnInitListener{

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private PlaceNearby placeNearby;
    private TextToSpeech textToSpeech;
    private Location currentLocation;
    private int currentPosition;
    private float bearing;
    private int currentDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        Bundle extras = getIntent().getExtras();
        String result = null;
        String location = null;
        if(extras != null){
            result = extras.getString(getString(R.string.places_object));
            location = extras.getString(getString(R.string.current_location));
        }

        placeNearby = new Gson().fromJson(result, PlaceNearby.class);
        currentLocation = new Gson().fromJson(location, Location.class);
        currentPosition = 0;
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), placeNearby, this);
        textToSpeech = new TextToSpeech(this, this);
        Location tempLoc = new Location("");
        tempLoc.setLatitude(placeNearby.getResults().get(0).getGeometryResults().getLocation().getLatitude());
        tempLoc.setLongitude(placeNearby.getResults().get(0).getGeometryResults().getLocation().getLongitude());
        bearing = currentLocation.bearingTo(tempLoc);
        currentDistance = Math.round(currentLocation.distanceTo(tempLoc));
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                Location tempLocation = new Location("");
                tempLocation.setLatitude(placeNearby.getResults().get(position).getGeometryResults().getLocation().getLatitude());
                tempLocation.setLongitude(placeNearby.getResults().get(position).getGeometryResults().getLocation().getLongitude());
                bearing = currentLocation.bearingTo(tempLocation);
                currentDistance = Math.round(currentLocation.distanceTo(tempLocation));
                speakOut(String.valueOf(placeNearby.getResults().get(position).getName()) +
                        " " + getString(R.string.place_distance) + " " + String.valueOf(currentDistance)
                        + getString(R.string.meters_away) + " " + ((placeNearby.getResults().get(position).getOpeningHours() != null) ?
                                (placeNearby.getResults().get(position).getOpeningHours().isOpenNow() ? " " + getString(R.string.opened) + " "
                                        : " " + getString(R.string.closed) + " ") + getString(R.string.at_the_moment) : ""));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public PlaceNearby getPlace(){
        return placeNearby;
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    public int getPagerPosition(){
        return currentPosition;
    }

    public float getBearing(){
        return bearing;
    }

    public int getCurrentDistance(){
        return currentDistance;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onInit(int status) {
        if(status ==  TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage((getResources().getConfiguration().locale != null) ? getResources().getConfiguration().locale : Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }else{
                Location tempLocation = new Location("");
                tempLocation.setLatitude(placeNearby.getResults().get(0).getGeometryResults().getLocation().getLatitude());
                tempLocation.setLongitude(placeNearby.getResults().get(0).getGeometryResults().getLocation().getLongitude());
                speakOut(String.valueOf(placeNearby.getResults().get(0).getName()) +
                        " " + getString(R.string.place_distance) + " " + String.valueOf(Math.round(currentLocation.distanceTo(tempLocation)))
                        + getString(R.string.meters_away) + " " + ((placeNearby.getResults().get(0).getOpeningHours() != null) ?
                        (placeNearby.getResults().get(0).getOpeningHours().isOpenNow() ? " " + getString(R.string.opened) + " "
                                : " " + getString(R.string.closed) + " ") + getString(R.string.at_the_moment) : ""));
            }
        }else{
            Log.e("TextToSpeech", "Initilization Failed!");
        }
    }

    private void speakOut(String textToSpeak){
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
}
