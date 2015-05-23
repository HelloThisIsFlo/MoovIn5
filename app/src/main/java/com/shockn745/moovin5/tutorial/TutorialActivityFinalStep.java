package com.shockn745.moovin5.tutorial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.shockn745.moovin5.AbstractTutorialActivity;
import com.shockn745.moovin5.R;
import com.shockn745.moovin5.main.MainActivity;

public class TutorialActivityFinalStep extends AbstractTutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_final_step);

        // Init screenshot
        initTutorialScreenshot();

        // FindViewById
        TextView doneTextView = (TextView) findViewById(R.id.tutorial_final_step_done);




        // Set listener
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear activities and bring main activity at the top (CLEAR_TOP)
                // Also set pref_tutorial to false
                Activity activity = TutorialActivityFinalStep.this;
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

    /**
     * Dynamically set the height of the cardview containing the screenshot
     * (Apparently I could not get the cardview to wrap content in xml)
     */
    private void initTutorialScreenshot() {

        final CardView cardView = (CardView) findViewById(R.id.tutorial_final_step_screenshot_card_view);

        // Get the image ratio
        Bitmap bitmap =
                ((BitmapDrawable) this.getResources().getDrawable(R.drawable.screenshot_tutorial))
                .getBitmap();
        final float ratio;
        if (bitmap != null) {
            float width = bitmap.getWidth();
            float height = bitmap.getHeight();
            ratio = width / height;
        } else {
            ratio = 1;
        }


        // Dynamically set width after layout
        cardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.width = (int) (cardView.getHeight() * ratio);

                cardView.setLayoutParams(layoutParams);
            }
        });
    }


}
