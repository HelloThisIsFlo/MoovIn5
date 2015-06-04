package com.shockn745.moovin5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * This class factor functions related to the tutorial mode at first launch of the application
 *
 * @author Kempenich Florian
 */
public abstract class AbstractTutorialActivity extends AppCompatActivity {
    private static final String LOG_TAG = AbstractTutorialActivity.class.getSimpleName();

    private boolean inTutorialMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains(getString(R.string.pref_tutorial_key))) {
            // Init
            inTutorialMode = prefs.getBoolean(getString(R.string.pref_tutorial_key), true);
        } else {
            // Save the default value to the preferences
            inTutorialMode = true;
            prefs.edit()
                    .putBoolean(getString(R.string.pref_tutorial_key), inTutorialMode)
                    .apply();
        }

        if (prefs.contains(getString(R.string.pref_is_celsius_key))) {
            Log.d(LOG_TAG, "Pref IsCelsius PRESENT");
            boolean isCelsius = prefs.getBoolean(getString(R.string.pref_is_celsius_key), false);
            Log.d(LOG_TAG, "If FALSE at first launch : NOT GOOD !!!!");
            if (isCelsius) {
                Log.d(LOG_TAG, "TRUE");
            } else {
                Log.d(LOG_TAG, "FALSE");
            }
        } else {
            Log.d(LOG_TAG, "Pref IsCelsius ABSENT");
        }

    }

    /**
     * @return true is in tutorial mode
     */
    protected boolean isInTutorialMode() {
        return inTutorialMode;
    }

    /**
     * Block back key if in tutorial mode
     */
    @Override
    public void onBackPressed() {
        if (!inTutorialMode) {
            super.onBackPressed();
        }
    }
}
