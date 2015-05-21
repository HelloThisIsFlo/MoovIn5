package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.shockn745.moovin5.R;

public class TutorialActivityStep1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_step1);

        TextView headerTextView = (TextView) findViewById(R.id.tutorial_step_1_header_text_view);
        TextView contentTextView = (TextView) findViewById(R.id.tutorial_step_1_content_text_view);



        // Set text
        headerTextView.setText("Welcome to MoovIn5");
        contentTextView.setText(
                Html.fromHtml("<p>This great app has one&nbsp;<br />\n" +
                    "<strong>unique</strong> &amp;&nbsp;<strong>simple</strong><br />\n" +
                    "goal!</p>\n" +
                    "\n" +
                    "<p>Getting you motivated to move in the next<br />\n" +
                    "<u>5 minutes</u></p>\n"
                )
        );












    }


}
