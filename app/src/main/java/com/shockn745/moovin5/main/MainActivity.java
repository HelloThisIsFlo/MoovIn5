package com.shockn745.moovin5.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.shockn745.moovin5.AbstractTutorialActivity;
import com.shockn745.moovin5.R;
import com.shockn745.moovin5.settings.PreferencesUtils;
import com.shockn745.moovin5.settings.SettingsActivity;
import com.shockn745.moovin5.tutorial.TutorialActivityStep1;

/**
 * Main activity displaying a duration picker, the main "motivate me" button and a secondary
 * button used to change the gym location
 *
 * @author Florian Kempenich
 */
public class MainActivity extends AbstractTutorialActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If in tutorial mode, start tutorial
        if (isInTutorialMode()) {
            // Clear Gym location
            PreferencesUtils.clearGymLocation(this);
            // Start tutorial
            Intent startTutorial = new Intent(this, TutorialActivityStep1.class);
            startActivity(startTutorial);
        }


        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, new MainFragment())
                    .commit();
        }

        // Set the default values for the very first launch of the application.
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Darken the background
        int darkenValue = getResources().getInteger(R.integer.background_darken_value);
        ((ImageView) findViewById(R.id.main_background_image_view))
                .setColorFilter(
                        Color.rgb(darkenValue, darkenValue, darkenValue),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent startSettings = new Intent(this, SettingsActivity.class);
            startActivity(startSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
