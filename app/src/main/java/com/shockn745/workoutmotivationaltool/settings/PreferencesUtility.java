package com.shockn745.workoutmotivationaltool.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.shockn745.workoutmotivationaltool.GymLocationActivity;

/**
 * Utility class with static utility methods
 * @author Florian Kempenich
 */
public class PreferencesUtility {

    /**
     * Get the coordinates stored in the default shared preferences.
     * @param context Context passed in getDefaultSharedPreferences
     * @return Coordinates stored in the preferences
     * @throws PreferenceNotInitializedException if one of the preferences has not been initialized
     */
    public static LatLng getCoordinatesFromPreferences(Context context)
            throws PreferenceNotInitializedException{

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Throw an exception if the preference hasn't been initialized
        if (!prefs.contains(GymLocationActivity.LATITUDE_KEY)) {
            throw new PreferenceNotInitializedException(GymLocationActivity.LATITUDE_KEY);
        } else if (!prefs.contains(GymLocationActivity.LONGITUDE_KEY)) {
            throw new PreferenceNotInitializedException(GymLocationActivity.LONGITUDE_KEY);
        } else {
            double lat = Double.longBitsToDouble(
                    prefs.getLong(GymLocationActivity.LATITUDE_KEY, -1)
            );
            double lng = Double.longBitsToDouble(
                    prefs.getLong(GymLocationActivity.LONGITUDE_KEY, -1)
            );
            return new LatLng(lat, lng);
        }
    }


    /**
     * Save the coordinates to the default shared preferences
     * @param context Context passed in getDefaultSharedPreferences
     * @param coordinates Coordinates to save in the share in the preferences
     */
    public static void saveCoordinatesToPreferences(Context context, LatLng coordinates) {
        long latLong = Double.doubleToLongBits(coordinates.latitude);
        long lngLong = Double.doubleToLongBits(coordinates.longitude);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(GymLocationActivity.LATITUDE_KEY, latLong)
                .putLong(GymLocationActivity.LONGITUDE_KEY, lngLong)
                .apply();
    }

    public static class PreferenceNotInitializedException extends Exception {

        public PreferenceNotInitializedException(String detailMessage) {
            super("The following preference key has not been initialized" +
                    "and has no default value : " + detailMessage);
        }
    }
}
