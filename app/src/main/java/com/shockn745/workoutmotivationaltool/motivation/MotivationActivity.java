package com.shockn745.workoutmotivationaltool.motivation;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.Toolbar;

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
    private boolean mAddCardMenuDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MotivationFragment())
                    .commit();
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // Add toolbar
        setActionBar(mToolbar);

        // Add the navigation arrow
        // Inspection removed, because it won't throw NullPointerException since the actionBar is
        // initialized just above.
        //noinspection ConstantConditions
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Find view by id
        mAddCardButton = (ImageButton) findViewById(R.id.add_card_button);


        mAddCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAddCardMenuDisplayed) {
                    // Show the addCardMenu
                    mAddCardMenuDisplayed = true;
                    View addCardMenu = findViewById(R.id.card_view);

                    // Get the center for the clipping circle
                    /// Get dimensions
                    int width = (int) getResources().getDimension(R.dimen.add_card_menu_width);
                    int height = (int) getResources().getDimension(R.dimen.add_card_menu_height);
                    /// Start circle at top right corner
                    int cx = width;
                    int cy = 0;
                    Log.d(LOG_TAG, "cx = " + cx);
                    Log.d(LOG_TAG, "cy = " + cy);

                    // Get the final radius for the clipping circle
                    int finalRadius = (int) Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));

                    // Create the animator for this view (the start radius is zero)
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(addCardMenu, cx, cy, 0, finalRadius);

                    // Make the view visible and start the animation
                    addCardMenu.setVisibility(View.VISIBLE);
                    anim.setDuration(500);
                    anim.start();
                } else {
                    // Hide the addCardMenu

                    mAddCardMenuDisplayed = false;
                    final View addCardMenu = findViewById(R.id.card_view);

                    // Get the center for the clipping circle
                    /// Get dimensions
                    int width = (int) getResources().getDimension(R.dimen.add_card_menu_width);
                    int height = (int) getResources().getDimension(R.dimen.add_card_menu_height);
                    /// Start circle at top right corner
                    int cx = width;
                    int cy = 0;
                    Log.d(LOG_TAG, "cx = " + cx);
                    Log.d(LOG_TAG, "cy = " + cy);

                    // Get the final radius for the clipping circle
                    int startRadius = (int) Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));

                    // Create the animator for this view (the start radius is zero)
                    Animator anim =ViewAnimationUtils
                            .createCircularReveal(addCardMenu, cx, cy, startRadius, 0);

                    // Add listener to hide the view when animation is done
                    anim.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Hide the addCardMenu
                            addCardMenu.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {}
                        @Override
                        public void onAnimationCancel(Animator animation) {}
                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });

                    // Start the animation
                    anim.setDuration(500);
                    anim.start();
                }
            }
        });
    }
}
