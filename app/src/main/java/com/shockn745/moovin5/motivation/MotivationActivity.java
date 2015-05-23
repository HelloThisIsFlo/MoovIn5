package com.shockn745.moovin5.motivation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.shockn745.moovin5.AnimCompatUtils;
import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.add_card_menu.AddCardMenuCallbacks;
import com.shockn745.moovin5.motivation.add_card_menu.FABCallbacks;

/**
 * This activity is where the location is retrieved, the travel time processed and the information
 * displayed to the user
 *
 * @author Florian Kempenich
 */
public class MotivationActivity extends AppCompatActivity implements FABCallbacks, AddCardMenuCallbacks {

    private static final String LOG_TAG = MotivationActivity.class.getSimpleName();
    private int mRevealDuration;
    private boolean mAddCardMenuDisplayed = false;


    private ImageButton mAddCardButton;
    private CardView mAddCardMenu;

    private MapView mMapView;
    private AdView mAdView;

    private boolean isFABHidden = true;

    /**
     *  Called when the activity is first created, or after Destroy
     *  If savedInstanceState is not null, go back to main activity
     * @param savedInstanceState null if first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRevealDuration = getResources().getInteger(R.integer.card_menu_reveal_duration);
        if (savedInstanceState == null) {
            setContentView(R.layout.motivation_activity);
            MotivationFragment motivationFragment = new MotivationFragment();
            motivationFragment.setShowFABCallback(this);
            getFragmentManager().beginTransaction()
                    .add(R.id.motivation_container, motivationFragment)
                    .commit();


            // Darken the background
            ImageView background = (ImageView)
                    findViewById(R.id.motivation_background_image_view);
            int darkenValue = getResources().getInteger(R.integer.background_darken_value);
            background.setColorFilter(
                    Color.rgb(darkenValue, darkenValue, darkenValue),
                    android.graphics.PorterDuff.Mode.MULTIPLY
            );


            // Set toolbar
            Toolbar mToolbar = (Toolbar) findViewById(R.id.motivation_toolbar);
            setSupportActionBar(mToolbar);

            // Add the navigation arrow
            /// Inspection removed, because it won't throw NullPointerException since the actionBar
            /// is initialized just above.
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            ////////////
            // AdView //
            ////////////

            // Init the AdView
            mAdView = new AdView(this);
            mAdView.setAdUnitId(getString(R.string.banner_ad_unit_id));
            mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            // Disable focus, to prevent recyclerview to scroll to the view when
            // the ad is refreshed
            mAdView.setFocusable(false);
            mAdView.setFocusableInTouchMode(false);



            
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
            mMapView.setClickable(false);
            mMapView.getMapAsync(motivationFragment);


            
            ///////////////
            // Card Menu //
            ///////////////
            
            // Find view by id
            mAddCardButton = (ImageButton) findViewById(R.id.add_card_button);
            mAddCardMenu = (CardView) findViewById(R.id.add_card_menu_card_view);

            mAddCardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Either show or hide the mAddCardMenu
                    if (!mAddCardMenuDisplayed) {
                        revealAddCardMenu();
                    } else {
                        hideAddCardMenu();
                        mAddCardMenuDisplayed = false;
                    }
                }
            });

            // The card menu view is set to invisible (not gone) in the xml so that it's drawn.
            // This allows to get the height before the menu is actually displayed
            // It also allow to set the visibility to GONE when the menu is not displayed, thus
            // preventing extra drawing
            mAddCardMenu.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // gets called after layout has been done but before display
                            // so we can get the height then hide the view
                            mAddCardMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mAddCardMenu.setVisibility(View.GONE);
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

    public AdView getAdView() {
        return mAdView;
    }

    ///////////////////
    // FAB Callbacks //
    ///////////////////

    /**
     * Reveal the FAB. Is to be used once!
     */
    @Override
    public void revealFAB() {
        isFABHidden = false;
        final int REVEAL_DURATION =
                getResources().getInteger(R.integer.card_menu_FAB_reveal_duration);

        // Get the center for the clipping circle
        /// Get dimensions
        int width = mAddCardButton.getWidth();
        int height = mAddCardButton.getHeight();
        /// Start circle at the center
        int cx = width/2;
        int cy = height/2;
        int radius = width/2;

        // Create the animator for the cardMenu (the start radius is zero)
        Animator revealCardMenuFABAnim = AnimCompatUtils.createCircularReveal(
                this,
                mAddCardButton,
                cx,
                cy,
                0,
                radius,
                REVEAL_DURATION
        );

        // Make the view visible
        mAddCardButton.setVisibility(View.VISIBLE);

        revealCardMenuFABAnim.start();

    }


    /**
     * Hide the FAB (when scrolling up)
     */
    @Override
    public void hideFAB() {
        isFABHidden = true;
        int mBottomPositionFAB = mAddCardButton.getBottom();

        // Animate the FAB out of the screen (UP direction)
        mAddCardButton.animate()
                .translationY(-mBottomPositionFAB)
                .setDuration(getResources().getInteger(R.integer.card_menu_FAB_hide_duration))
                .setInterpolator(
                        AnimCompatUtils.createInterpolator(this)
                ).start();
    }


    /**
     * Hide the FAB (when scrolling down and toolbar fully visible)
     * Needs to be called AFTER unHide FAB
     */
    @Override
    public void unHideFAB() {
        isFABHidden = false;
        // Animate the FAB out of the screen (UP direction)
        mAddCardButton.animate()
                .translationY(0)
                .setDuration(getResources().getInteger(R.integer.card_menu_FAB_hide_duration))
                .setInterpolator(AnimCompatUtils.createInterpolator(this))
                .start();
    }

    @Override
    public boolean isFABHidden() {
        return isFABHidden;
    }


    ///////////////////////////
    // AddCardMenu Callbacks //
    ///////////////////////////

    /**
     * Reveal the addCardMenu, if hidden
     */
    @Override
    public void revealAddCardMenu() {
        if (!mAddCardMenuDisplayed) {
            mAddCardMenuDisplayed = true;

            // Get the center for the clipping circle
            /// Get dimensions
            int width = mAddCardMenu.getWidth();
            int height = mAddCardMenu.getHeight();

            /// Start circle at top right corner
            //noinspection UnnecessaryLocalVariable
            int cx = width;
            int cy = 0;

            // Get the final radius for the clipping circle
            @SuppressWarnings("SuspiciousNameCombination")
            int radius = (int) Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));

            // Create the animator for the cardMenu (the start radius is zero)
            Animator revealCardMenuAnim = AnimCompatUtils.createCircularReveal(
                    this,
                    mAddCardMenu,
                    cx,
                    cy,
                    0,
                    radius,
                    mRevealDuration
            );

            // Make the view visible
            mAddCardMenu.setVisibility(View.VISIBLE);

            /////////////////

            // Rotate the addCardButton
            ObjectAnimator rotateFAB = ObjectAnimator.ofFloat(
                    mAddCardButton,
                    "rotation",
                    0,
                    -45f
            ).setDuration(mRevealDuration);
            rotateFAB.setInterpolator(AnimCompatUtils.createInterpolator(this));

            // Start the animations
            revealCardMenuAnim.start();
            rotateFAB.start();
        }
    }

    /**
     * Hide the addCardMenu, if displayed
     */
    @Override
    public void hideAddCardMenu() {
        if (mAddCardMenuDisplayed) {
            mAddCardMenuDisplayed = false;

            // Get the center for the clipping circle
            /// Get dimensions
            int width = mAddCardMenu.getWidth();
            int height = mAddCardMenu.getHeight();
            /// Start circle at top right corner
            int cx = width;
            int cy = 0;

            // Get the final radius for the clipping circle
            @SuppressWarnings("SuspiciousNameCombination")
            int radius = (int) Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));


            // Create the animator for the cardMenu (the start radius is zero)
            Animator hideCardMenuAnim = AnimCompatUtils.createCircularReveal(
                    this,
                    mAddCardMenu,
                    cx,
                    cy,
                    radius,
                    0,
                    mRevealDuration
            );

            // Add listener to hide the view when animation is done
            hideCardMenuAnim.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    // Hide the mAddCardMenu
                    mAddCardMenu.setVisibility(View.GONE);
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
            ).setDuration(mRevealDuration);
            rotateFAB.setInterpolator(AnimCompatUtils.createInterpolator(this));

            // Start the animations
            hideCardMenuAnim.start();
            rotateFAB.start();
        }
    }

}
