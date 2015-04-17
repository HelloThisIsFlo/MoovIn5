package com.shockn745.workoutmotivationaltool.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Setting activity
 *
 * @author Florian Kempenich
 */
public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
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
