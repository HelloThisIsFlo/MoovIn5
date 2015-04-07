package com.shockn745.workoutmotivationaltool;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class GymLocationActivity extends ActionBarActivity implements OnMapReadyCallback {

    private final static String LOG_TAG = GymLocationActivity.class.getSimpleName();

    public final static String LATITUDE_KEY = "latitude";
    public final static String LONGITUDE_KEY = "longitude";

    private Button mSetLocationButton;
    private Button mChangeMaptypeButton;

    private GoogleMap mMap = null;
    private Marker mMarker = null;
    private LatLng mCoordonates = null;

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
                // Convert double to long
                long latLong = Double.doubleToLongBits(mCoordonates.latitude);
                long lngLong = Double.doubleToLongBits(mCoordonates.longitude);

                // Save the location to the shared preferences
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(GymLocationActivity.this);
                prefs.edit()
                        .putLong(LATITUDE_KEY, latLong)
                        .putLong(LONGITUDE_KEY, lngLong)
                        .apply();

                Log.v(LOG_TAG, "lat : " + mCoordonates.latitude);
                Log.v(LOG_TAG, "long : " + mCoordonates.longitude);

                finish();
            }
        });


        // Init mCoordonates - TEMPORARY
        // TODO Remove hardcoded init : retrieve previous location (or default location if 1st time)
        mCoordonates = new LatLng(0, 0);

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


        // Display a marker on the long clicked location
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (mMarker == null) {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    mCoordonates = latLng;
                } else {
                    mMarker.setPosition(latLng);
                    mCoordonates = latLng;
                }

            }
        });

    }
}