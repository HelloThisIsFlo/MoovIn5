package com.shockn745.workoutmotivationaltool;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


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
     * A placeholder fragment containing a simple view.
     */
    public static class SettingsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener{

        private boolean mBindingPreferences;

        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_warmup_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_stretching_key)));

        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            // Update once manually the summary
            mBindingPreferences = true;
            //TODO uncomment and fix
//            onPreferenceChange(preference,
//                    PreferenceManager
//                            .getDefaultSharedPreferences(preference.getContext())
//                            .getString(preference.getKey(), "")
//            );
            mBindingPreferences = false;
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            // Test if onPreferenceChange is manually triggered, when binding summary to value.
            if (!mBindingPreferences) {
                //Do something when preference changes
            }

            // Update summary
            preference.setSummary(newValue.toString()
                            + " "
                            + getString(R.string.pref_summary_minute)
            );

            return true;
        }
    }
}
