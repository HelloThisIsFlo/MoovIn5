package com.shockn745.moovin5;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shockn745.moovin5.settings.PreferencesUtils;
import com.shockn745.moovin5.tutorial.TutorialActivityFinalStep;

/**
 * Activity where the gym location is set by the user
 *
 * @author Florian Kempenich
 */
public class GymLocationActivity extends AbstractTutorialActivity implements OnMapReadyCallback {


    public final static String LATITUDE_KEY = "latitude";
    public final static String LONGITUDE_KEY = "longitude";

    // UI components
    private ImageButton mSetLocationButton;
    private ImageButton mChangeMaptypeButton;

    private GoogleMap mMap = null;
    private Marker mMarker = null;
    private LatLng mCoordinates = null;

    private boolean mMaptypeIsHybrid = false;
    private boolean mAcceptButtonVisible = false;

    private final Handler mHandler = new Handler();
    private Runnable mHintRunnable;

    /**
     *  Called when the activity is first created, or after Destroy
     *  If savedInstanceState is not null, go back to main activity
     * @param savedInstanceState null if first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.gym_activity);

            // Hint the user 2 sec after start
            scheduleHint();

            // Find elements by id
            mSetLocationButton = (ImageButton) findViewById(R.id.set_location_button);
            mChangeMaptypeButton = (ImageButton) findViewById(R.id.change_maptype_button);
            Toolbar mToolbar = (Toolbar) findViewById(R.id.gym_toolbar);

            // Add toolbar
            setSupportActionBar(mToolbar);

            // Add the navigation arrow

            // Inspection removed, because it won't throw NullPointerException since the actionBar is
            // initialized just above.

            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
            getFragmentManager().beginTransaction()
                    .add(R.id.gym_container, mapFragment)
                    .commit();

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


                        // Cancel hint
                        cancelHint();

                        if (!isInTutorialMode()) {
                            finish();
                        } else {
                            // In tutorial mode
                            // Launch final tutorial step
                            Intent startFinalStep = new Intent(
                                    GymLocationActivity.this,
                                    TutorialActivityFinalStep.class
                            );
                            startActivity(startFinalStep);
                            overridePendingTransition(
                                    R.anim.tutorial_next_slide_in,
                                    R.anim.tutorial_next_slide_out
                            );
                        }
                    }
                }
            });
        } else {
            finish();
        }
    }


    /**
     * Hint the user to long press to select
     * Hint appear after 2 seconds
     */
    private void scheduleHint(){
        mHintRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        GymLocationActivity.this,
                        GymLocationActivity.this.getString(R.string.gym_location_hint),
                        Toast.LENGTH_SHORT
                ).show();

            }
        };
        mHandler.postDelayed(mHintRunnable , 2000);
    }

    /**
     * Cancel the scheduled hint message
     */
    private void cancelHint() {
        mHandler.removeCallbacks(mHintRunnable);
    }


    /**
     * Override the UP button behavior when in tutorial mode
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Cancel hint
                cancelHint();

                if (isInTutorialMode()) {
                    finish();
                    overridePendingTransition(
                            R.anim.tutorial_previous_slide_in,
                            R.anim.tutorial_previous_slide_out
                    );
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Cancel hint
        cancelHint();
    }

    /**
     * Clear saveInstanceState to prevent activity from restoring.
     * @param outState bundle to clear
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.clear();
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
        }

        // Activate the myLocation layer
        mMap.setMyLocationEnabled(true);


        // Display a marker on the long clicked location
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Perform haptic feedback (not handled by GoogleMap)
                findViewById(R.id.gym_container)
                        .performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                // Show "accept" button & display the marker or change its location
                if (mMarker == null) {
                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    mCoordinates = latLng;
                } else {
                    mMarker.setPosition(latLng);
                    mCoordinates = latLng;
                }

                // Check if "accept" button is already visible
                if (!mAcceptButtonVisible) {
                    // Animate FABs
                    /// Init interpolator
                    Interpolator interpolator =
                            AnimCompatUtils.createInterpolator(GymLocationActivity.this);

                    /// Slide "satellite" button up
                    float slideLength = getResources().getDimension(R.dimen.fab_size)
                            + getResources().getDimension(R.dimen.fab_margin_bottom);
                    ObjectAnimator satelliteAnimator = ObjectAnimator.ofFloat(
                            mChangeMaptypeButton,
                            "translationY",
                            0,
                            -slideLength
                    );
                    satelliteAnimator
                            .setDuration(getResources().getInteger(R.integer.fab_anim_duration))
                            .setInterpolator(interpolator);

                    /// Slide in "accept" button
                    float startPosition = getResources().getDimension(R.dimen.fab_size)
                            + getResources().getDimension(R.dimen.fab_margin_bottom)
                            + getResources().getDimension(R.dimen.fab_dynamic_margin);
                    ObjectAnimator acceptAnimator = ObjectAnimator.ofFloat(
                            mSetLocationButton,
                            "translationY",
                            startPosition,
                            0
                    );
                    acceptAnimator
                            .setDuration(getResources().getInteger(R.integer.fab_anim_duration))
                            .setInterpolator(interpolator);

                    /// Start animations
                    mSetLocationButton.setVisibility(View.VISIBLE);
                    acceptAnimator.start();
                    satelliteAnimator.start();

                    mAcceptButtonVisible = true;
                } else {
                    // Draw attention to the "accept" button
                    // Flash with a different color

                    // Highlight FAB with a different color
                    final ImageButton setLocationButtonHighlight =
                            (ImageButton) findViewById(R.id.set_location_button_highlight);

                    // flash == invisible -> visible -> invisible
                    ObjectAnimator flashAnimation = ObjectAnimator.ofFloat(
                            setLocationButtonHighlight,
                            "alpha",
                            0,
                            1f,
                            0).setDuration(1000);

                    Interpolator interpolator =
                            AnimCompatUtils.createInterpolator(GymLocationActivity.this);
                    flashAnimation.setInterpolator(interpolator);

                    // Handle visibility of Highlight FAB
                    setLocationButtonHighlight.setVisibility(View.VISIBLE);
                    flashAnimation.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setLocationButtonHighlight.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });

                    // Start flash animation
                    flashAnimation.start();
                }
            }
        });

    }

}