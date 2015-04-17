package com.shockn745.workoutmotivationaltool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.motivation.MotivationActivity;
import com.shockn745.workoutmotivationaltool.settings.SettingsActivity;

/**
 * Main activity displaying a duration picker, the main "motivate me" button and a secondary
 * button used to change the gym location
 * @author Florian Kempenich
 */
public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        // Set the default values for the very first launch of the application.
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
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

    /**
     * The main fragment of the application
     */
    public static class MainFragment extends Fragment {

        private Button mMotivateButton;
        private Button mChangeLocationButton;
        private NumberPicker mDurationPicker;
        private TextView mWarningEditText;

        // Components for the timer used by mDurationPicker
        Handler mHandler;
        SaveDurationTimer mSavePreferencesTimer;
        private static final int SAVE_PREFERENCES_TIMER_DELAY = 500;

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Find elements by id
            mMotivateButton = (Button) rootView.findViewById(R.id.motivate_button);
            mChangeLocationButton = (Button) rootView.findViewById(R.id.change_location_button);
            mDurationPicker = (NumberPicker) rootView.findViewById(R.id.duration_picker);
            mWarningEditText = (TextView) rootView.findViewById(R.id.warning_edit_text);

            // Configure mDurationPicker
            mDurationPicker.setMinValue(getResources().getInteger(R.integer.main_duration_min));
            mDurationPicker.setMaxValue(getResources().getInteger(R.integer.main_duration_max));
            // Disable focus for the elements of the picker (disable keyboard)
            mDurationPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            // Init with the previous value
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (prefs.contains(getString(R.string.pref_workout_key))) {
                // Init
                int workoutPrevious = prefs.getInt(getString(R.string.pref_workout_key),
                        getResources().getInteger(R.integer.workout_default));
                mDurationPicker.setValue(workoutPrevious);
            } else {
                // Save the default value to the preferences
                int workoutDefault = getResources().getInteger(R.integer.workout_default);
                prefs.edit()
                        .putInt(getString(R.string.pref_workout_key),workoutDefault)
                        .apply();
                mDurationPicker.setValue(workoutDefault);
            }


            // Init timer used by mDurationPicker
            mHandler = new Handler();
            mSavePreferencesTimer = new SaveDurationTimer(getActivity());

            // Set listeners
            mMotivateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startMotivation = new Intent(getActivity(), MotivationActivity.class);
                    startActivity(startMotivation);
                }
            });
            mChangeLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent startGymLocation = new Intent(getActivity(), GymLocationActivity.class);
                    startActivity(startGymLocation);
                }
            });
            mDurationPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                /**
                 * Every time the value is changed, a timer is launched, at expiration the value is
                 * saved in preferences.
                 * This is to avoid repetitive writes in the preferences while scrolling through
                 * the numbers
                 * @param newVal Value to be saved
                 */
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    // Cancel previously started timer
                    mHandler.removeCallbacks(mSavePreferencesTimer);
                    mSavePreferencesTimer.setWorkoutDuration(newVal);
                    mHandler.postDelayed(mSavePreferencesTimer, SAVE_PREFERENCES_TIMER_DELAY);
                }
            });

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();

            // Enable "Motivate Me" button only if the gym location has been initialized
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            if (prefs.contains(GymLocationActivity.LATITUDE_KEY) &&
                    prefs.contains(GymLocationActivity.LONGITUDE_KEY)) {
                mMotivateButton.setEnabled(true);
                mWarningEditText.setVisibility(View.GONE);
            } else {
                mMotivateButton.setEnabled(false);
                mWarningEditText.setVisibility(View.VISIBLE);
            }
        }
    }
}
