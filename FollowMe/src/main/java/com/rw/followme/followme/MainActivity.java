package com.rw.followme.followme;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.rw.followme.followme.datamodel.PlaceNearby;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rafalwesolowski on 10/03/2014.
 */
public class MainActivity extends FragmentActivity implements AdapterView.OnItemClickListener,
        SpeechRecognitionFragment.OnShowOnMapClickListener, AllPlacesRequestFragment.OnShowPlacesListener, TextToSpeech.OnInitListener{

    private List<Fragment> fragments;
    private String[] tabTitles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private TextToSpeech textToSpeech;
    private PlaceNearby response;

    public MainActivity(){
        fragments = new ArrayList<Fragment>();
        fragments.add(new SpeechRecognitionFragment());
        fragments.add(new AllPlacesRequestFragment());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textToSpeech = new TextToSpeech(this, this);
        tabTitles = new String[]{getString(R.string.key_based_request), getString(R.string.general_request)};
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.navigation_drawer_item, tabTitles));
        // Set the list's click listener
        drawerList.setOnItemClickListener(this);

        selectItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        selectItem(position);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Insert the fragment by replacing any existing fragment
        if(NetworkStatus.getConnectivityStatus(MainActivity.this) == NetworkStatus.CONNECTED){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragments.get(position),
                    fragments.get(position).getTag()).addToBackStack(null).commit();
        }
        else{
            NetworkStatus.showDialogAlert(MainActivity.this, "No Internet Connection",
                    "You must have Internet Connection enabled");
        }
        // Highlight the selected item, update the title, and close the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    private void speakOut(String textToSpeak){
        if(!textToSpeech.isSpeaking()) {
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void showOnMap(PlaceNearby response, Location currentLocation) {
        this.response = response;
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra(getString(R.string.places_object), new Gson().toJson(response));
        resultIntent.putExtra(getString(R.string.current_location), new Gson().toJson(currentLocation));
        startActivity(resultIntent);
    }

    public PlaceNearby getResponse(){
        return this.response;
    }

    @Override
    public void onInit(int status) {
        if(status ==  TextToSpeech.SUCCESS){
            int result = textToSpeech.setLanguage(getResources().getConfiguration().locale);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }else{
                speakOut(getString(R.string.speak_any_keyword));
            }
        }else{
            Log.e("TextToSpeech", "Initilization Failed!");
        }
    }
}
