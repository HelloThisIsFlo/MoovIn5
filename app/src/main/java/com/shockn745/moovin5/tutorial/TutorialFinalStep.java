package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.AbstractTutorialActivity;
import com.shockn745.moovin5.R;
import com.shockn745.moovin5.main.MainActivity;

public class TutorialFinalStep extends AbstractTutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_final_step);

        // FindViewById
        TextView doneTextView = (TextView) findViewById(R.id.tutorial_final_step_done);




        // Set listener
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear activities and bring main activity at the top (CLEAR_TOP)
                // Also set pref_tutorial to false
                Activity activity = TutorialFinalStep.this;
                Intent finishTutorial = new Intent(activity, MainActivity.class);
                finishTutorial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

                // Notify that the tutorial is done
                prefs.edit()
                        .putBoolean(activity.getString(R.string.pref_tutorial_key), false)
                        .apply();

                startActivity(finishTutorial);
            }
        });
    }


}
