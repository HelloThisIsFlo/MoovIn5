package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.AbstractTutorialActivity;
import com.shockn745.moovin5.R;

/**
 * Step 1 of the tutorial
 *
 * @author Kempenich Florian
 *
 */
public class TutorialActivityStep1 extends AbstractTutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_step1);

        // FindeViewById
        TextView headerTextView = (TextView) findViewById(R.id.tutorial_step_1_header_text_view);
        TextView contentTextView = (TextView) findViewById(R.id.tutorial_step_1_content_text_view);
        TextView nextTextView = (TextView) findViewById(R.id.tutorial_step_1_next);

        // Retrieve text
        String header = getString(R.string.tutorial_step_1_header)
                + " "
                + getString(R.string.app_name);

        CharSequence content = getText(R.string.tutorial_step_1_content);

        // Set text
        headerTextView.setText(header);
        contentTextView.setText(content);

        // Set on click listener
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startStep2 = new Intent(
                        TutorialActivityStep1.this,
                        TutorialActivityStep2.class
                );
                startActivity(startStep2);
                overridePendingTransition(
                        R.anim.tutorial_next_slide_in,
                        R.anim.tutorial_next_slide_out
                );
            }
        });



    }


}
