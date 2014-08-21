package com.rw.followme.followme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.rw.followme.followme.datamodel.PlaceNearby;
import com.rw.followme.followme.network.WebClient;

import java.util.Locale;

/**
 * Created by rafalwesolowski on 10/05/2014.
 */
public class AllPlacesRequestFragment extends Fragment implements LocationListener, TextToSpeech.OnInitListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        View.OnClickListener{

    private TextToSpeech textToSpeech;
    private Button poiRequestButton;
    private Context context;
    private WebClient client;
    private LocationClient locationClient;
    private LocationManager locationManager;
    private Location currentLocation;
    private OnShowPlacesListener onShowOnMapListener;
    private ProgressDialog progressDialog;
    private Button minusDistance;
    private Button plusDistance;
    private Button startSearch;
    private EditText keywordText;
    private TextView distanceView;

    public interface OnShowPlacesListener{
        public void showOnMap(PlaceNearby response, Location currentLocation);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        try {
            onShowOnMapListener = (OnShowPlacesListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnShowPlacesListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new WebClient(getActivity());
        locationClient = new LocationClient(getActivity(), this, this);
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speech_recognition_fragment, container, false);
        textToSpeech = new TextToSpeech(getActivity(), this);
        poiRequestButton = (Button)view.findViewById(R.id.all_points_request_button);
        poiRequestButton.setVisibility(View.VISIBLE);
        view.findViewById(R.id.spokenText).setVisibility(View.GONE);
        view.findViewById(R.id.speakImageButton).setVisibility(View.GONE);
        distanceView = (TextView)view.findViewById(R.id.distanceView);
        plusDistance = (Button)view.findViewById(R.id.plus_distance);
        minusDistance = (Button)view.findViewById(R.id.minus_distance);
        startSearch = (Button)view.findViewById(R.id.start_search);
        keywordText = (EditText)view.findViewById(R.id.keyword_edit_text);
        startSearch.setVisibility(View.GONE);
        keywordText.setVisibility(View.GONE);
        plusDistance.setOnClickListener(this);
        minusDistance.setOnClickListener(this);
        poiRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                client.searchNearby(currentLocation, Integer.parseInt(distanceView.getText().toString()), "", new Response.Listener<PlaceNearby>() {
                    @Override
                    public void onResponse(PlaceNearby response) {
                        hideProgress();
                        if(response != null) {
                            if (response.getStatus().equals("OK")) {
                                onShowOnMapListener.showOnMap(response, currentLocation);
                            } else if (response.getStatus().equals("OVER_QUERY_LIMIT")) {
                                speakOut(getString(R.string.limit_exceeded));
                            } else if (response.getStatus().equals("REQUEST_DENIED")) {
                                speakOut(getString(R.string.request_denied));
                            } else if (response.getStatus().equals("INVALID_REQUEST")) {
                                speakOut(getString(R.string.invalid_request));
                            } else if (response.getStatus().equals("ZERO_RESULTS")) {
                                speakOut(getString(R.string.no_places_found));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Followme", "FollowMe error " + error);
                        hideProgress();
                        speakOut(getString(R.string.error_try_again));
                    }
                });
            }
        });
        return view;
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

    private void speakOut(String textToSpeak){
        if(!textToSpeech.isSpeaking()) {
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.minus_distance:
                if((Integer.parseInt(distanceView.getText().toString()) - 100) >= 0) {
                    distanceView.setText(String.valueOf(Integer.parseInt(distanceView.getText().toString()) - 100));
                }
                break;
            case R.id.plus_distance:
                distanceView.setText(String.valueOf(Integer.parseInt(distanceView.getText().toString()) + 100));
                break;
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
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
