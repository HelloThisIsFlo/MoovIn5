package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;

public class TutorialActivityStep3 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_step3);

        // FindViewById
        TextView nextTextView = (TextView) findViewById(R.id.tutorial_step_3_next);
        TextView previousTextView = (TextView) findViewById(R.id.tutorial_step_3_previous);
        TextView contentTextView = (TextView) findViewById(R.id.tutorial_step_3_content_text_view);





//        + "<h3>"
//                + getString(R.string.app_name)
//                + " "
//                + getString(R.string.tutorial_step_2_content_part_2)
//                + "</h3>";









        // Set listeners
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent startStep4 = new Intent(
//                        TutorialActivityStep3.this,
//                        TutorialActivityStep4.class
//                );
//                startActivity(startStep4);
//                overridePendingTransition(
//                        R.anim.tutorial_next_slide_in,
//                        R.anim.tutorial_next_slide_out
//                );
            }
        });
        previousTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startStep2 = new Intent(
                        TutorialActivityStep3.this,
                        TutorialActivityStep2.class
                );
                startActivity(startStep2);
                overridePendingTransition(
                        R.anim.tutorial_previous_slide_in,
                        R.anim.tutorial_previous_slide_out
                );
            }
        });
    }

}
