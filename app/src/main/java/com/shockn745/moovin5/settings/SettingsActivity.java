package com.shockn745.moovin5.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.shockn745.moovin5.R;
import com.shockn745.moovin5.main.MainActivity;

/**
 * Setting activity
 *
 * @author Florian Kempenich
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     *  Called when the activity is first created, or after Destroy
     *  If savedInstanceState is not null, go back to main activity
     * @param savedInstanceState null if first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setContentView(R.layout.settings_activity);
            // Add toolbar
            setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));
            // Add the navigation arrow
            // Inspection removed, because it won't throw NullPointerException since the actionBar is
            // initialized just above.
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_container, new SettingsFragment())
                    .commit();
        } else {
            finish();
        }
    }

    /**
     * Clear saveInstanceState to prevent activity from restoring.
     * @param outState Bundle to be passed to the activity when restoring
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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

            // Init the restart tutorial preference
            initRestartTutorialPref();

            // Init the credits preference
            initCreditsPref();

            // Init the feedback preference
            initFeedbackPref();

            // TODO add "rate the app" link
//            Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            try {
//                startActivity(goToMarket);
//            } catch (ActivityNotFoundException e) {
//                startActivity(new Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse("http://play.google.com/store/apps/details?id="
//                                + getActivity().getPackageName()
//                        ))
//                );
//            }
        }


        /**
         * Set an onClickListener that restarts the tutorial
         */
        private void initRestartTutorialPref() {

            Preference pref = findPreference(getString(R.string.pref_start_tutorial_key));

            // Create the listener
            Preference.OnPreferenceClickListener clickListener =
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            // Set preferences
                            PreferencesUtils.clearGymLocation(getActivity());
                            // Activate tutorial mode
                            PreferenceManager.getDefaultSharedPreferences(getActivity())
                                    .edit()
                                    .putBoolean(
                                            getString(R.string.pref_tutorial_key),
                                            true
                                    ).apply();

                            // Start main activity
                            Intent startTutorial = new Intent(getActivity(), MainActivity.class);
                            startTutorial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(startTutorial);

                            return true;
                        }
                    };

            // Set the onClickListener
            pref.setOnPreferenceClickListener(clickListener);
        }


        /**
         * Set an OnClickListener that open an alert dialog with credits
         */
        private void initCreditsPref() {

            Preference pref = findPreference(getString(R.string.pref_credits_key));

            // Create listener
            Preference.OnPreferenceClickListener listener =
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            // Create dialog
                            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.pref_credits)
                                    .setMessage(getText(R.string.credits_message))
                                    .setPositiveButton(
                                            getString(android.R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    dialog.cancel();
                                                }
                                            }
                                    ).create();

                            // Show dialog
                            dialog.show();

                            return true;
                        }
                    };

            pref.setOnPreferenceClickListener(listener);
        }

        /**
         * Set an OnClickListener that send an email to me
         */
        private void initFeedbackPref() {

            Preference pref = findPreference(getString(R.string.pref_feedback_key));

            // Create listener
            Preference.OnPreferenceClickListener listener =
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            // Send email feedback
                            Intent sendEmail = new Intent(
                                    Intent.ACTION_SENDTO,
                                    Uri.fromParts(
                                            "mailto",
                                            getString(R.string.pref_feedback_email),
                                            null)
                            );
                            sendEmail.putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    getString(R.string.pref_feedback_subject)
                            );
                            sendEmail.putExtra(
                                    Intent.EXTRA_TEXT,
                                    getString(R.string.pref_feedback_body)
                            );

                            startActivity(sendEmail);

                            return true;
                        }
                    };

            pref.setOnPreferenceClickListener(listener);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Test if OnSharedPreferenceChanged is being manually triggered
            //noinspection StatementWithEmptyBody
            if (!mBindingPreferences) {
                //Do something when preference changes
            }

            // Update the preference summary
            Preference preference = findPreference(key);
            if (preference instanceof NumberPickerPreference) {
                preference.setSummary(sharedPreferences.getInt(key, 0)
                                + " "
                                + getString(R.string.pref_summary_minute)
                );
            }
        }
    }
}
