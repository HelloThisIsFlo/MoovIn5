package com.shockn745.moovin5.main;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.shockn745.moovin5.GymLocationActivity;
import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.MotivationActivity;

/**
 * The main fragment of the application
 *
 * @author Florian Kempenich
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private Button mMotivateButton;
    private Button mChangeLocationButton;
    private NumberPicker mDurationPicker;
    private TextView mWarningEditText;
    private ImageView mHomeIcon;
    private ImageView mGymIcon;


    // Components for the timer used by mDurationPicker
    private Handler mHandler;
    private SaveDurationTimer mSavePreferencesTimer;
    private static final int SAVE_PREFERENCES_TIMER_DELAY = 500;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        // Find elements by id
        mMotivateButton = (Button) rootView.findViewById(R.id.motivate_button);
        mChangeLocationButton = (Button) rootView.findViewById(R.id.change_location_button);
        mDurationPicker = (NumberPicker) rootView.findViewById(R.id.duration_picker);
        mWarningEditText = (TextView) rootView.findViewById(R.id.warning_edit_text);
        mHomeIcon = (ImageView) rootView.findViewById(R.id.main_home_image_view);
        mGymIcon = (ImageView) rootView.findViewById(R.id.main_gym_image_view);


        // Set up elements
        initHomeGymCard(mHomeIcon);
        initHomeGymCard(mGymIcon);




        ///////////////
        // TEMP TEMP //
        ///////////////

        final CardView mHomeCard = (CardView) rootView.findViewById(R.id.main_home_card_view);
        final CardView mGymCard = (CardView) rootView.findViewById(R.id.main_gym_card_view);
        final CardView mGymLocationCard = (CardView) rootView.findViewById(R.id.main_change_gym_card_view);
        GymHomeOnTouchListener touchListener = new GymHomeOnTouchListener(
                mHomeCard,
                mGymCard,
                mGymLocationCard,
                getResources().getInteger(R.integer.home_gym_animation_duration)
        );

        mHomeCard.setOnTouchListener(touchListener);
        mGymCard.setOnTouchListener(touchListener);













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

                float elev = getActivity().findViewById(R.id.main_picker_card_view).getElevation();
                Log.d(LOG_TAG, "Elevation = " + elev);
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

    /**
     * Invert the color of the icon & set the imageView width dynamically to get a 1:1 ration
     * @param imageView ImageView to init
     */
    private void initHomeGymCard(final ImageView imageView) {
        // Invert the color of the drawable
        float[] colorMatrix_Negative = {
                -1.0f, 0, 0, 0, 255, //red
                0, -1.0f, 0, 0, 255, //green
                0, 0, -1.0f, 0, 255, //blue
                0, 0, 0, 1.0f, 0 //alpha
        };
        ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);
        imageView.setColorFilter(colorFilter_Negative);

        // Set the width dynamically to get a 1:1 ratio
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // gets called after layout has been done but before display
                        // so we can set the width dynamically.
                        imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.width = imageView.getHeight();
                        imageView.setLayoutParams(layoutParams);
                    }
                });
    }

}