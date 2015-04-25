package com.shockn745.workoutmotivationaltool.motivation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toolbar;

import com.shockn745.workoutmotivationaltool.R;

/**
 * This activity is where the location is retrieved, the travel time processed and the information
 * displayed to the user
 *
 * @author Florian Kempenich
 */
public class MotivationActivity extends Activity {

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

        // Inspection removed, because it won't throw NullPointerException since the actionBar is
        // initialized just above.

        //noinspection ConstantConditions
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
