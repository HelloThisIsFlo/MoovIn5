package com.shockn745.workoutmotivationaltool;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shockn745.workoutmotivationaltool.settings.PreferencesUtility;


public class GymLocationActivity extends ActionBarActivity implements OnMapReadyCallback {

    private final static String LOG_TAG = GymLocationActivity.class.getSimpleName();

    public final static String LATITUDE_KEY = "latitude";
    public final static String LONGITUDE_KEY = "longitude";

    private Button mSetLocationButton;
    private Button mChangeMaptypeButton;

    private GoogleMap mMap = null;
    private Marker mMarker = null;
    private LatLng mCoordinates = null;

    private boolean mMaptypeIsHybrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_location);

        // Initialize the GoogleMapOption with the location stored in the preferences
        GoogleMapOptions options = new GoogleMapOptions();
        try {
            //Try to get previous location
            LatLng coord = PreferencesUtility.getCoordinatesFromPreferences(this);

            // Init the map with the saved location
            options.camera(new CameraPosition(
                            coord,
                            getResources().getInteger(R.integer.gym_location_level_zoom),
                            0,
                            0)
            );
        } catch (PreferencesUtility.PreferenceNotInitializedException e) {
            e.printStackTrace();
            // Set default location if retrieval fails
            options.camera(new CameraPosition(
                            new LatLng(0, 0),
                            getResources().getInteger(R.integer.gym_location_level_zoom_not_initialized),
                            0,
                            0)
            );
        }

        // Add the MapFragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mapFragment)
                    .commit();
        }

        // Register this Activity as the callback to get the GoogleMap object
        mapFragment.getMapAsync(this);


        // Find elements by id
        mSetLocationButton = (Button) findViewById(R.id.set_location_button);
        mChangeMaptypeButton = (Button) findViewById(R.id.change_maptype_button);

        // Set listeners
        mChangeMaptypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch map type
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
        mSetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the location to the shared preferences
                if (mCoordinates != null) {
                    PreferencesUtility.saveCoordinatesToPreferences(GymLocationActivity.this, mCoordinates);

                    Log.v(LOG_TAG, "lat : " + mCoordinates.latitude);
                    Log.v(LOG_TAG, "long : " + mCoordinates.longitude);

                    finish();
                }
            }
        });
    }

    /**
     * Callback called when the map is ready to be used.
     * The map is handled in this function
     * GoogleMap object is non null
     *
     * @param googleMap The map object retrieved
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Store the map as a local variable
        this.mMap = googleMap;

        // Activate the changeMaptype button
        mChangeMaptypeButton.setEnabled(true);

        // Add a marker at the previously saved location
        try {
            //Try to get previous location, if fails the marker is simply not added
            LatLng coord = PreferencesUtility.getCoordinatesFromPreferences(this);

            // Add marker to the previously saved location
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(coord));
        } catch (PreferencesUtility.PreferenceNotInitializedException e) {
            Log.v(LOG_TAG, "Location not initialized, not adding the marker");
        }

        // Activate the myLocation layer
        mMap.setMyLocationEnabled(true);


        // Display a marker on the long clicked location
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Display the marker or change its location
                if (mMarker == null) {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    mCoordinates = latLng;
                } else {
                    mMarker.setPosition(latLng);
                    mCoordinates = latLng;
                }

                // Enable "Save Location" button
                mSetLocationButton.setEnabled(true);

            }
        });

    }
}