package com.rw.followme.followme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.rw.followme.followme.datamodel.Direction;
import com.rw.followme.followme.datamodel.PlaceNearby;
import com.rw.followme.followme.network.WebClient;

import java.util.Locale;

/**
 * Created by rafalwesolowski on 10/03/2014.
 */
public class MapViewFragment extends Fragment implements TextToSpeech.OnInitListener, SensorEventListener {

    private WebClient client;
    private MapView mapView;
    private TextToSpeech textToSpeech;
    private Context context;
    private GoogleMap googleMap;
    private PlaceNearby result;
    private LatLng parsedLocation;
    private int pagerPosition;
    private int itemPosition;
    private Location pointedLocation;
    private ProgressDialog progressDialog;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;
    private Float azimut;
    float pointDirection = 0;


    public static Fragment newInstance(PlaceNearby placeNearby, int pagerPosition, int itemPosition){
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putString("Place", new Gson().toJson(placeNearby));
        args.putInt("PagerPosition", pagerPosition);
        args.putInt("ItemPosition", itemPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        result = ((ResultActivity)activity).getPlace();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        client = new WebClient(getActivity());
        if(savedInstanceState != null){
            result = new Gson().fromJson(savedInstanceState.getString("Result"), PlaceNearby.class);
        }else{
            result = new Gson().fromJson(getArguments().getString("Place"), PlaceNearby.class);
        }
        textToSpeech = new TextToSpeech(context, this);
        pagerPosition = getArguments().getInt("PagerPosition");
        itemPosition = getArguments().getInt("ItemPosition");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.map_view, container, false);
        mapView = (MapView)view.findViewById(R.id.mapView);
        pointedLocation = new Location("");
        pointedLocation.setLatitude(result.getResults().get(itemPosition).getGeometryResults().getLocation().getLatitude());
        pointedLocation.setLongitude(result.getResults().get(itemPosition).getGeometryResults().getLocation().getLongitude());
        parsedLocation = new LatLng(pointedLocation.getLatitude(), pointedLocation.getLongitude());
        TextView placeNameView = (TextView)view.findViewById(R.id.place_name_view);
        placeNameView.setText(result.getResults().get(itemPosition).getName() + " " +
                String.valueOf(Math.round(pointedLocation.distanceTo(((ResultActivity)context).getCurrentLocation())))
                + " " + getString(R.string.meters_away));
        mapView.onCreate(savedInstanceState);
//        try {
            MapsInitializer.initialize(this.getActivity());
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
        googleMap = mapView.getMap();
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parsedLocation, 15));
        markLocation(parsedLocation, googleMap, result.getResults().get(itemPosition).getName());
        final TextView directionView = (TextView)view.findViewById(R.id.direction_text_view);
        directionView.setVisibility(View.GONE);
        view.findViewById(R.id.get_direction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                //Direction call
                String originLocation = String.valueOf(((ResultActivity) context).getCurrentLocation().getLatitude()) + "," +
                        String.valueOf(((ResultActivity) context).getCurrentLocation().getLongitude());
                String destinationLocation = String.valueOf(pointedLocation.getLatitude()) + "," + String.valueOf(pointedLocation.getLongitude());
                client.getDirection(originLocation, destinationLocation, new Response.Listener<Direction>() {
                    @Override
                    public void onResponse(Direction direction) {
                        hideProgress();
                        String directionText = "12";
                        if (pointDirection > 15.0 && pointDirection < 45.0) directionText = "1";
                        else if (pointDirection >= 45.0 && pointDirection <= 75.0) directionText = "2";
                        else if (pointDirection >= 75.0 && pointDirection <= 105.0) directionText = "3";
                        else if (pointDirection > 105.0 && pointDirection <= 135.0) directionText = "4";
                        else if (pointDirection > 135.0 && pointDirection <= 165.0) directionText = "5";
                        else if (pointDirection > 165.0 && pointDirection <= 195.0) directionText = "6";
                        else if (pointDirection > 195.0 && pointDirection <= 225.0) directionText = "7";
                        else if (pointDirection > 225.0 && pointDirection <= 255.0) directionText = "8";
                        else if (pointDirection > 255.0 && pointDirection <= 285.0) directionText = "9";
                        else if (pointDirection > 285.0 && pointDirection <= 315.0) directionText = "10";
                        else if (pointDirection > 315.0 && pointDirection <= 345.0) directionText = "11";
                        else if (pointDirection > 345.0 && pointDirection <= 15.0) directionText = "12";
                        if(direction != null && direction.getStatus().equals("OK")){
                            if(direction.getRoutes().get(0).getLegs().get(0).getSteps().size() > 0) {
                                String nearestStep = Html.fromHtml(direction.getRoutes().get(0).getLegs().get(0).getSteps().get(0).getHtmlInstructions()).toString();
                                Toast.makeText(context, nearestStep, Toast.LENGTH_SHORT).show();
                                directionView.setVisibility(View.VISIBLE);
                                directionView.setText(String.valueOf(((ResultActivity)context).getCurrentDistance() + " " +
                                                context.getResources().getString(R.string.meters) + " " +
                                        context.getResources().getString(R.string.on_the_hour) + " " + directionText));
                                speakOut(nearestStep + context.getResources().getString(R.string.for_distance) +
                                        direction.getRoutes().get(0).getLegs().get(0).getSteps().get(0).getDistance().getValue() +
                                        context.getResources().getString(R.string.meters) + " " +
                                        context.getResources().getString(R.string.on_the_hour) +
                                        directionText);
                            }
                        }else{
                            directionView.setVisibility(View.GONE);
                            speakOut(context.getResources().getString(R.string.no_places_found));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideProgress();
                        directionView.setVisibility(View.GONE);
                        speakOut(context.getResources().getString(R.string.error_try_again));
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mapView.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mapView.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mapView.onResume();
//        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        LatLng currentLocation = new LatLng(((ResultActivity)context).getCurrentLocation().getLatitude(),
                ((ResultActivity)context).getCurrentLocation().getLongitude());
        Location destination = new Location("");
        destination.setLatitude(result.getResults().get(pagerPosition).getGeometryResults().getLocation().getLatitude());
        destination.setLongitude(result.getResults().get(pagerPosition).getGeometryResults().getLocation().getLongitude());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putString("Result", new Gson().toJson(result));
    }

    private void speakOut(String textToSpeak){
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void markLocation(LatLng location, GoogleMap map, String titleForSnippet){
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(location)
                .title(titleForSnippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mappixel))).showInfoWindow();

        CircleOptions circleOptions = new CircleOptions()
                .center(location)
                .radius(500) // In meters
                .strokeWidth(1)
                .strokeColor(Color.BLUE)
                .fillColor(0x507f82b7);

        // Get back the mutable Circle
        map.addCircle(circleOptions);
    }

    @Override
    public void onInit(int status) {
        if(status ==  TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage((getResources().getConfiguration().locale != null) ? getResources().getConfiguration().locale : Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        }else{
            Log.e("TextToSpeech", "Initilization Failed!");
        }
    }

    private void showProgress(){
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        progressDialog.show();
    }

    private void hideProgress(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // If we don't have a Location, we break out
        if ( ((ResultActivity)context).getCurrentLocation() == null ) return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }
        if (gravity != null && geomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                float baseAzimuth = -azimut*360/(2*3.14159f);
                float bearing = ((ResultActivity)context).getBearing();
//                if(bearing < 0){
//                    bearing = bearing + 360;
//                }
                //This is where we choose to point it
                pointDirection = ((ResultActivity)context).getBearing() - baseAzimuth;

                if(pointDirection < 0){
                    pointDirection = pointDirection + 360;
                }
            }
        }
        //Old algorithm
//        float azimuth = event.values[0];
//        float baseAzimuth = azimuth;
//        float bearing = ((ResultActivity)context).getBearing();
//        if(bearing < 0){
//            bearing = bearing + 360;
//        }
//        //This is where we choose to point it
//        pointDirection = ((ResultActivity)context).getBearing() - baseAzimuth;
//
//        if(pointDirection < 0){
//            pointDirection = pointDirection + 360;
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
