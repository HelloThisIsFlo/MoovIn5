package com.shockn745.workoutmotivationaltool.motivation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.shockn745.workoutmotivationaltool.R;

/**
 * This activity is where the location is retrieved, the travel time processed and the information
 * displayed to the user
 *
 * @author Florian Kempenich
 */
public class MotivationActivity extends Activity {

    private static final String LOG_TAG = MotivationActivity.class.getSimpleName();

    private ImageButton mAddCardButton;
    private CardView addCardMenu;

    private MapView mMapView;
    
    /**
     *  Called when the activity is first created, or after Destroy
     *  If savedInstanceState is not null, go back to main activity
     * @param savedInstanceState null if first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_motivation);
            MotivationFragment motivationFragment = new MotivationFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.motivation_container, motivationFragment)
                    .commit();

            Toolbar mToolbar = (Toolbar) findViewById(R.id.motivation_toolbar);

            // Add toolbar
            setActionBar(mToolbar);

            // Add the navigation arrow
            /// Inspection removed, because it won't throw NullPointerException since the actionBar
            /// is initialized just above.
            //noinspection ConstantConditions
            getActionBar().setDisplayHomeAsUpEnabled(true);

            
            
            /////////////
            // MapView //
            /////////////
            
            // Init the MapView
            // Init is done in the activity to tie the MapView lifecycle to the activity lifecycle
            GoogleMapOptions options = new GoogleMapOptions();
            options.liteMode(true)
                    .mapType(GoogleMap.MAP_TYPE_NORMAL);
            mMapView = new MapView(this, options);
            mMapView.onCreate(null);
            mMapView.getMapAsync(motivationFragment);


            
            ///////////////
            // Card Menu //
            ///////////////
            
            // Find view by id
            mAddCardButton = (ImageButton) findViewById(R.id.add_card_button);
            addCardMenu = (CardView) findViewById(R.id.add_card_menu_card_view);

            mAddCardButton.setOnClickListener(new CardMenuOnClickListener());

            // The card menu view is set to invisible (not gone) in the xml so that it's drawn.
            // This allows to get the height before the menu is actually displayed
            addCardMenu.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // gets called after layout has been done but before display
                            // so we can get the height then hide the view
                            addCardMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            addCardMenu.setVisibility(View.GONE);
                        }

                    });

        } else {
            // If trying to restore : go back to main activity
            finish();
        }
    }

    /**
     * Clear saveInstanceState to prevent activity from restoring.
     * @param outState bundle to clear
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.clear();
    }

    public MapView getMapView() {
        return mMapView;
    }


    /**
     * OnClickListener used to reveal the card menu.
     */
    private class CardMenuOnClickListener implements View.OnClickListener {

        private boolean mAddCardMenuDisplayed = false;

        @Override
        public void onClick(View v) {

            final int REVEAL_DURATION =
                    getResources().getInteger(R.integer.card_menu_reveal_duration);

            // Get the center for the clipping circle
            /// Get dimensions
            int width = addCardMenu.getWidth();
            int height = addCardMenu.getHeight();
            /// Start circle at top right corner
            int cx = width;
            int cy = 0;
            Log.d(LOG_TAG, "cx = " + cx);
            Log.d(LOG_TAG, "cy = " + cy);

            // Get the final radius for the clipping circle
            @SuppressWarnings("SuspiciousNameCombination")
            int radius = (int) Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));

            // Either show or hide the addCardMenu
            if (!mAddCardMenuDisplayed) {
                // Show the addCardMenu
                mAddCardMenuDisplayed = true;

                // Create the animator for the cardMenu (the start radius is zero)
                Animator revealCardMenuAnim = ViewAnimationUtils
                        .createCircularReveal(addCardMenu, cx, cy, 0, radius)
                        .setDuration(REVEAL_DURATION);
                Interpolator interpolator = AnimationUtils.loadInterpolator(
                        MotivationActivity.this,
                        android.R.interpolator.fast_out_slow_in
                );
                revealCardMenuAnim.setInterpolator(interpolator);

                // Make the view visible
                addCardMenu.setVisibility(View.VISIBLE);

                /////////////////

                // Rotate the addCardButton
                ObjectAnimator rotateFAB = ObjectAnimator.ofFloat(
                        mAddCardButton,
                        "rotation",
                        0,
                        -45f
                ).setDuration(REVEAL_DURATION);
                rotateFAB.setInterpolator(interpolator);

                // Start the animations
                revealCardMenuAnim.start();
                rotateFAB.start();

            } else {
                // Hide the addCardMenu
                mAddCardMenuDisplayed = false;

                // Create the animator for the cardMenu (the start radius is zero)
                Animator hideCardMenuAnim = ViewAnimationUtils
                        .createCircularReveal(addCardMenu, cx, cy, radius, 0)
                        .setDuration(REVEAL_DURATION);
                Interpolator interpolator = AnimationUtils.loadInterpolator(
                        MotivationActivity.this,
                        android.R.interpolator.fast_out_slow_in
                );
                hideCardMenuAnim.setInterpolator(interpolator);

                // Add listener to hide the view when animation is done
                hideCardMenuAnim.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Hide the addCardMenu
                        addCardMenu.setVisibility(View.GONE);
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

                /////////////////

                // Rotate the addCardButton
                ObjectAnimator rotateFAB = ObjectAnimator.ofFloat(
                        mAddCardButton,
                        "rotation",
                        -45f,
                        0
                ).setDuration(REVEAL_DURATION);
                rotateFAB.setInterpolator(interpolator);

                // Start the animations
                hideCardMenuAnim.start();
                rotateFAB.start();
            }
        }
    }

}
