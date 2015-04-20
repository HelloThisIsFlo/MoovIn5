package com.shockn745.workoutmotivationaltool.motivation;

import android.animation.Animator;
import android.app.Activity;
import android.os.Bundle;
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

    private ImageButton mAddCardButton;

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
                View addCardView = findViewById(R.id.card_view); //TODO remove

                // get the center for the clipping circle
//                int cx = addCardView.getWidth();
//                int cy = addCardView.getHeight();

                int cx = addCardView.getLeft();
                int cy = addCardView.getTop();

                // get the final radius for the clipping circle
                int finalRadius = 1300; // TODO change hardcoded test value

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(addCardView, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                addCardView.setVisibility(View.VISIBLE);
                anim.setDuration(1000);
                anim.start();
            }
        });
    }
}
