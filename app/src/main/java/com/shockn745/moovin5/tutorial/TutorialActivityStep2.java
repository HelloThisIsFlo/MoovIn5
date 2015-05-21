package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

public class TutorialActivityStep2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_step2);

        // FindViewById
        TextView nextTextView = (TextView) findViewById(R.id.tutorial_step_2_next);
        TextView previousTextView = (TextView) findViewById(R.id.tutorial_step_2_previous);





        // Set listeners
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        previousTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startStep1 = new Intent(
                        TutorialActivityStep2.this,
                        TutorialActivityStep1.class
                );
                startActivity(startStep1);
                overridePendingTransition(
                        R.anim.tutorial_previous_slide_in,
                        R.anim.tutorial_previous_slide_out
                );
            }
        });

    }


}
