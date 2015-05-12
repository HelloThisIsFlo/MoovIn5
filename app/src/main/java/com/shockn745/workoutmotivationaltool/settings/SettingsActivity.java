package com.shockn745.workoutmotivationaltool.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Setting activity
 *
 * @author Florian Kempenich
 */
public class SettingsActivity extends Activity {

    /**
     *  Called when the activity is first created, or after Destroy
     *  If savedInstanceState is not null, go back to main activity
     * @param savedInstanceState null if first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.activity_setting);
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_container, new SettingsFragment())
                    .commit();
        } else {
            finish();
        }
    }

    /**
     * Clear saveInstanceState to prevent activity from restoring.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    /**
     * A fragment used to bind the preferences settings to the summary and listen to preference
     * changes
     */
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private boolean mBindingPreferences;

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            // Register this class as the preference listener
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            sp.registerOnSharedPreferenceChangeListener(this);

            //Manually trigger the update of the summary
            mBindingPreferences = true;
            onSharedPreferenceChanged(sp, getString(R.string.pref_warmup_key));
            onSharedPreferenceChanged(sp, getString(R.string.pref_stretching_key));
            mBindingPreferences = false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Test if OnSharedPreferenceChanged is being manually triggered
            if (!mBindingPreferences) {
                //Do something when preference changes
            }

            // Update the preference summary
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getInt(key, 0)
                            + " "
                            + getString(R.string.pref_summary_minute)
            );
        }
    }
}
