package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.AbstractTutorialActivity;
import com.shockn745.moovin5.GymLocationActivity;
import com.shockn745.moovin5.R;

public class TutorialActivityStep4 extends AbstractTutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_step4);

        // FindViewById
        TextView nextTextView = (TextView) findViewById(R.id.tutorial_step_4_next);
        TextView previousTextView = (TextView) findViewById(R.id.tutorial_step_4_previous);
        TextView contentTextView = (TextView) findViewById(R.id.tutorial_step_4_content_text_view);
        TextView infoTextView = (TextView) findViewById(R.id.tutorial_step_4_info_text_view);

        // Set text
        contentTextView.setText(
                getText(R.string.tutorial_step_4_content)
        );
        infoTextView.setText(
                getString(R.string.tutorial_step_4_infos)
        );

        // Set listeners
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startStep4 = new Intent(
                        TutorialActivityStep4.this,
                        GymLocationActivity.class
                );
                startActivity(startStep4);
                overridePendingTransition(
                        R.anim.tutorial_next_slide_in,
                        R.anim.tutorial_next_slide_out
                );
            }
        });
        previousTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(
                        R.anim.tutorial_previous_slide_in,
                        R.anim.tutorial_previous_slide_out
                );
            }
        });
    }

}