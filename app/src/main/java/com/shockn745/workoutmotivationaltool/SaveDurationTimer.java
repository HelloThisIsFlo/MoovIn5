package com.shockn745.workoutmotivationaltool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Shock on 17.04.15.
 */
public class SaveDurationTimer implements Runnable {

    private static final String LOG_TAG = SaveDurationTimer.class.getSimpleName();

    private int workoutDuration = -1;
    private Activity mActivity;

    public SaveDurationTimer(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public int getWorkoutDuration() {
        return workoutDuration;
    }

    public void setWorkoutDuration(int workoutDuration) {
        this.workoutDuration = workoutDuration;
    }

    /**
     * Save the workout duration to the shared preferences
     * /!\ Initialize workoutDuration before scheduling /!\
     */
    @SuppressLint("CommitPrefEdits")
    @Override
    public void run() {
        // Save to preferences
        if (workoutDuration != -1) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mActivity);
            prefs.edit()
                    .putInt(mActivity.getString(R.string.pref_workout_key), workoutDuration)
                    .commit();
            Log.d(LOG_TAG, "Workout duration saved in shared prefs");
        }
    }

}
