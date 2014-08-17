package com.rw.followme.followme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.rw.followme.followme.datamodel.PlaceNearby;
import com.rw.followme.followme.network.WebClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by rafalwesolowski on 16/03/2014.
 */
public class SpeechRecognitionFragment extends Fragment implements LocationListener, TextToSpeech.OnInitListener,
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        View.OnClickListener{

    private TextToSpeech textToSpeech;
    private ImageButton btnSpeak;
    private TextView txtText;
    private Context context;
    private static final int REQUEST_SPEECH_CODE = 333;
    private WebClient client;
    private LocationClient locationClient;
    private LocationManager locationManager;
    private Location currentLocation;
    private OnShowOnMapClickListener onShowOnMapClickListener;
    private View view;
    private ProgressDialog progressDialog;
    private Button minusDistance;
    private Button plusDistance;
    private Button startSearch;
    private EditText keywordText;
    private TextView progressView;

    public interface OnShowOnMapClickListener{
        public void showOnMap(PlaceNearby response, Location currentLocation);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        try {
            onShowOnMapClickListener = (OnShowOnMapClickListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement OnShowOnMapClickListener");
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
        view = inflater.inflate(R.layout.speech_recognition_fragment, container, false);
        textToSpeech = new TextToSpeech(getActivity(), this);
        btnSpeak = (ImageButton)view.findViewById(R.id.speakImageButton);
        txtText = (TextView)view.findViewById(R.id.spokenText);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                if(getResources().getConfiguration().locale != null) {
                    if (getResources().getConfiguration().locale.toString().equals("en-UK")) {
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-UK");
                    } else if (getResources().getConfiguration().locale.toString().equals("pl_PL")) {
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pl_PL");
                    } else {
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-UK");
                    }
                }
                try {
                    view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                    startActivityForResult(intent, REQUEST_SPEECH_CODE);
                    txtText.setText("");
                }catch(ActivityNotFoundException e){
                    e.printStackTrace();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("market://details?id=com.google.android.voicesearch"));
                    startActivity(browserIntent);
                }
            }
        });
        progressView = (TextView)view.findViewById(R.id.distanceView);
        plusDistance = (Button)view.findViewById(R.id.plus_distance);
        minusDistance = (Button)view.findViewById(R.id.minus_distance);
        startSearch = (Button)view.findViewById(R.id.start_search);
        keywordText = (EditText)view.findViewById(R.id.keyword_edit_text);
        plusDistance.setOnClickListener(this);
        minusDistance.setOnClickListener(this);
        startSearch.setOnClickListener(this);
        progressView.setText(String.valueOf(1000));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SPEECH_CODE: {
                if (data != null) {
                    view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtText.setText(text.get(0));
                    showProgress();
                    client.searchNearby(currentLocation, Integer.parseInt(progressView.getText().toString()), text.get(0), new Response.Listener<PlaceNearby>() {
                        @Override
                        public void onResponse(PlaceNearby response) {
                            hideProgress();
                            if(response != null) {
                                if (response.getStatus().equals("OK")) {
                                    onShowOnMapClickListener.showOnMap(response, currentLocation);
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
                break;
            }
        }
    }

    @Override
    public void onInit(int status) {
        if(status ==  TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage((getResources().getConfiguration().locale != null) ? getResources().getConfiguration().locale : Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
               // speakOut(getString(R.string.speak_any_keyword));
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
                if((Integer.parseInt(progressView.getText().toString()) - 100) >= 0) {
                    progressView.setText(String.valueOf(Integer.parseInt(progressView.getText().toString()) - 100));
                }
                break;
            case R.id.plus_distance:
                progressView.setText(String.valueOf(Integer.parseInt(progressView.getText().toString()) + 100));
                break;
            case R.id.start_search:
                String keywordString;
                if(keywordText.getText().toString().length() > 0){
                    keywordString = keywordText.getText().toString();
                }else{
                    keywordString = "";
                }
                showProgress();
                client.searchNearby(currentLocation, Integer.parseInt(progressView.getText().toString()), keywordString, new Response.Listener<PlaceNearby>() {
                    @Override
                    public void onResponse(PlaceNearby response) {
                        hideProgress();
                        keywordText.getText().clear();
                        if(response != null) {
                            if (response.getStatus().equals("OK")) {
                                onShowOnMapClickListener.showOnMap(response, currentLocation);
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
                        keywordText.getText().clear();
                        speakOut(getString(R.string.error_try_again));
                    }
                });
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
        currentLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
