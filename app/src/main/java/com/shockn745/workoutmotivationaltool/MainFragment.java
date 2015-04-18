package com.shockn745.workoutmotivationaltool;

/**
 * Created by Shock on 17.04.15.
 */

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.motivation.MotivationActivity;

/**
 * The main fragment of the application
 */
public class MainFragment extends Fragment {

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
                    .putInt(getString(R.string.pref_workout_key), workoutDefault)
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