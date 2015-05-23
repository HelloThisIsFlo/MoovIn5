package com.shockn745.moovin5.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.AbstractTutorialActivity;
import com.shockn745.moovin5.R;

/**
 * Step 2 of the tutorial
 *
 * @author Kempenich Florian
 *
 */
public class TutorialActivityStep2 extends AbstractTutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_step2);

        // FindViewById
        TextView nextTextView = (TextView) findViewById(R.id.tutorial_step_2_next);
        TextView previousTextView = (TextView) findViewById(R.id.tutorial_step_2_previous);
        TextView contentTextView = (TextView) findViewById(R.id.tutorial_step_2_content_text_view);
        TextView skipTextView = (TextView) findViewById(R.id.tutorial_step_2_skip_text_view);

        // Set text
        String contentHTML = getString(R.string.tutorial_step_2_content);
        contentTextView.setText(Html.fromHtml(contentHTML));
        skipTextView.setText(
                getString(R.string.tutorial_step_2_safe_skip)
        );

        // Set listeners
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startStep3 = new Intent(
                        TutorialActivityStep2.this,
                        TutorialActivityStep3.class
                );
                startActivity(startStep3);
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
