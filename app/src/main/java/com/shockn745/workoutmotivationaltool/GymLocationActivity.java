package com.shockn745.workoutmotivationaltool;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class GymLocationActivity extends ActionBarActivity implements OnMapReadyCallback {

    private Button mSetLocationButton;
    private Button mChangeMaptypeButton;

    private GoogleMap mMap = null;

    private boolean mMaptypeIsHybrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_location);

        // Get the mapFragment and register this Activity as the
        // callback to get the GoogleMap object
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gym_location_map);
        mapFragment.getMapAsync(this);

        // Find elements by id
        mSetLocationButton = (Button) findViewById(R.id.set_location_button);
        mChangeMaptypeButton = (Button) findViewById(R.id.change_maptype_button);

        // Set listeners
        mChangeMaptypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap != null) {
                    if (!mMaptypeIsHybrid) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        mMaptypeIsHybrid = true;
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMaptypeIsHybrid = false;
                    }
                }
            }
        });


    }

    /**
     * Callback called when the map is ready to be used.
     * The map is handled in this function
     * GoogleMap object is non null
     * @param googleMap The map object retrieved
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Store the map as a local variable
        this.mMap = googleMap;

        // Activate the changeMaptype button
        mChangeMaptypeButton.setEnabled(true);

    }
}