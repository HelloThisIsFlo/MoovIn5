package com.shockn745.workoutmotivationaltool.motivation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.shockn745.workoutmotivationaltool.R;

/**
 * This activity is where the location is retrieved, the travel time processed and the information
 * displayed to the user
 *
 * @author Florian Kempenich
 */
public class MotivationActivity extends Activity implements OnMapReadyCallback {

    private static final String LOG_TAG = MotivationActivity.class.getSimpleName();

    private MapView mMapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.motivation_container, new MotivationFragment())
                    .commit();
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.motivation_toolbar);

        // Add toolbar
        setActionBar(mToolbar);

        // Add the navigation arrow
        /// Inspection removed, because it won't throw NullPointerException since the actionBar is
        /// initialized just above.
        //noinspection ConstantConditions
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Init the MapView
        // Init is done in the activity to tie the MapView lifecycle to the activity lifecycle
        GoogleMapOptions options = new GoogleMapOptions();
        options.liteMode(true)
                .mapType(GoogleMap.MAP_TYPE_NORMAL);
        mMapView = new MapView(this, options);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(LOG_TAG, "OnMapReady called");
    }
}
