package com.shockn745.workoutmotivationaltool.motivation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.shockn745.workoutmotivationaltool.R;

/**
 * This activity is where the location is retrieved, the travel time processed and the information
 * displayed to the user
 *
 * @author Florian Kempenich
 */
public class MotivationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motivation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MotivationFragment())
                    .commit();
        }
    }
}
