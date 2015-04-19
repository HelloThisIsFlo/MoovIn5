package com.shockn745.workoutmotivationaltool;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shockn745.workoutmotivationaltool.settings.PreferencesUtils;


public class GymLocationActivity extends Activity implements OnMapReadyCallback {

    private final static String LOG_TAG = GymLocationActivity.class.getSimpleName();

    public final static String LATITUDE_KEY = "latitude";
    public final static String LONGITUDE_KEY = "longitude";

    // UI components
    private Toolbar mToolbar;
    private Button mSetLocationButton;
    private ImageButton mChangeMaptypeButton;

    private GoogleMap mMap = null;
    private Marker mMarker = null;
    private LatLng mCoordinates = null;

    private boolean mMaptypeIsHybrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_location);

        // Find elements by id
        mSetLocationButton = (Button) findViewById(R.id.set_location_button);
        mChangeMaptypeButton = (ImageButton) findViewById(R.id.change_maptype_button);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // Add toolbar
        setActionBar(mToolbar);
        // Add the navigation arrow
        getActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize the GoogleMapOption with the location stored in the preferences
        GoogleMapOptions options = new GoogleMapOptions();
        try {
            //Try to get previous location
            LatLng coord = PreferencesUtils.getCoordinatesFromPreferences(this);

            // Init the map with the saved location
            options.camera(new CameraPosition(
                            coord,
                            getResources().getInteger(R.integer.gym_location_level_zoom),
                            0,
                            0)
            );
        } catch (PreferencesUtils.PreferenceNotInitializedException e) {
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
        MapFragment mapFragment = MapFragment.newInstance(options);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mapFragment)
                    .commit();
        }

        // Register this Activity as the callback to get the GoogleMap object
        mapFragment.getMapAsync(this);


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
                    PreferencesUtils.saveCoordinatesToPreferences(GymLocationActivity.this, mCoordinates);

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
            LatLng coord = PreferencesUtils.getCoordinatesFromPreferences(this);

            // Add marker to the previously saved location
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(coord));
        } catch (PreferencesUtils.PreferenceNotInitializedException e) {
            Log.v(LOG_TAG, "Location not initialized, not adding the marker");
        }

        // Activate the myLocation layer
        mMap.setMyLocationEnabled(true);


        // Display a marker on the long clicked location
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Show "accept" button & display the marker or change its location
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

                // TODO move
                // Show "accept" button
                // Slide satellite button up
                float slideLength = getResources().getDimension(R.dimen.fab_size)
                        + (getResources().getDimension(R.dimen.fab_margin_bottom) / 2);
                ObjectAnimator test = ObjectAnimator.ofFloat(
                        mChangeMaptypeButton,
                        "translationY",
                        0,
                        - slideLength
                );
                Interpolator interpolator = AnimationUtils.loadInterpolator(
                        GymLocationActivity.this,
                        android.R.interpolator.fast_out_slow_in
                );
                test.setDuration(500)
                        .setInterpolator(interpolator);
                test.start();

            }
        });

    }
}