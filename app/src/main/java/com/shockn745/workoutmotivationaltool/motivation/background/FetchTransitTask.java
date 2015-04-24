package com.shockn745.workoutmotivationaltool.motivation.background;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.shockn745.workoutmotivationaltool.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Background task to fetch the transit time from the Google Directions API
 * It sends a http request, parse the result JSON string and update the UI<br>
 * It takes a LatLng[2] as parameter, where :<br>
 * LatLng[0] : start point<br>
 * LatLng[1] : destination<br>
 */
public class FetchTransitTask extends AsyncTask<LatLng, Integer, Integer> {

    public interface OnBackAtHomeTimeRetrievedListener {

        /**
         * Callback called when FetchTransitTask is done
         * @param backAtHome Time back at home
         */
        public void onBackAtHomeTimeRetrieved(Date backAtHome);

    }

    private final String LOG_TAG = FetchTransitTask.class.getSimpleName();

    private final static int ARG_ERROR = 0;
    private final static int URL_ERROR = 1;
    private final static int CONNECTION_ERROR = 2;
    private final static int JSON_ERROR = 3;
    private final static int EMPTY_ERROR = 4;
    private final static int NO_ROUTES_ERROR = 5;

    private Activity mActivity;
    private OnBackAtHomeTimeRetrievedListener mListener;

    public FetchTransitTask(Activity mActivity, OnBackAtHomeTimeRetrievedListener mListener) {
        this.mActivity = mActivity;
        this.mListener = mListener;
    }

    /**
     * See class description
     *
     * @param params params[0] : start point<br>params[1] : destination
     * @return Transit time between the two points
     */
    @Override
    protected Integer doInBackground(LatLng... params) {
        // Check if both origin & destination are provided
        if (params.length == 2) {

            // Get the coordinates
            LatLng start = params[0];
            LatLng dest = params[1];

            double startLatitude = start.latitude;
            double startLongitude = start.longitude;
            double destLatitude = dest.latitude;
            double destLongitude = dest.longitude;

            // Create the URL
            final String URL_SCHEME = "http";
            final String URL_AUTHORITY = "maps.googleapis.com";
            final String URL_PATH = "maps/api/directions";
            final String URL_PATH_OUTPUT = "json";
            final String URL_PARAM_ORIGIN = "origin";
            final String URL_PARAM_DESTINATION = "destination";
            final String URL_PARAM_MODE = "mode";

            String originQuery = startLatitude + "," + startLongitude;
            String destQuery = destLatitude + "," + destLongitude;
            final String URL_QUERY_MODE = "transit";

            Uri uri = new Uri.Builder()
                    .scheme(URL_SCHEME)
                    .authority(URL_AUTHORITY)
                    .path(URL_PATH)
                    .appendPath(URL_PATH_OUTPUT)
                    .appendQueryParameter(URL_PARAM_ORIGIN, originQuery)
                    .appendQueryParameter(URL_PARAM_DESTINATION, destQuery)
                    .appendQueryParameter(URL_PARAM_MODE, URL_QUERY_MODE)
                    .build();

            // Fetch JSON from the Google Directions API
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            String jsonString = null;
            try {
                // Init the URL
                URL url = new URL(uri.toString());
                Log.d(LOG_TAG, url.toString());

                // Create the request and connect
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read the input stream into a String
                InputStream stream = connection.getInputStream();
                if (stream == null) {
                    // Note : "finally" block will run even if there's a "return" statement in
                    // the "try" block
                    publishProgress(CONNECTION_ERROR);
                    return null;
                }
                bufferedReader =
                        new BufferedReader(new InputStreamReader(stream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                if (stringBuilder.length() != 0) {
                    jsonString = stringBuilder.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                publishProgress(URL_ERROR);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress(CONNECTION_ERROR);
                return null;
            } finally {
                // If connection has been initialized, disconnect
                if (connection != null) {
                    connection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(LOG_TAG, "Problem closing the buffered reader !");
                    }
                }
            }

            // Parse the JSON String to retrieve transit time
            // Return result if successful
            if (jsonString != null) {
                try {
                    // String too long to log => using debug mode instead
                    // Log.d(LOG_TAG, jsonString);
                    int transitTime = parseTransitTime(jsonString);
                    if (transitTime == -1) {
                        // No routes available
                        publishProgress(NO_ROUTES_ERROR);
                        return null;
                    } else {
                        return transitTime;
                    }
                } catch (JSONException e) {
                    publishProgress(JSON_ERROR);
                    return null;
                }
            } else {
                publishProgress(EMPTY_ERROR);
                return null;
            }
        } else {
            publishProgress(ARG_ERROR);
            return null;
        }
    }

    /**
     * Handle the error that could happen in "doInBackground" method
     *
     * @param errorCode Code of the error
     */
    @Override
    protected void onProgressUpdate(Integer... errorCode) {
        switch (errorCode[0]) {
            case ARG_ERROR:
                Log.d(LOG_TAG, "Please provide an array of 2 arguments : " +
                        "origin and destination !");
                break;
            case URL_ERROR:
                Log.d(LOG_TAG, "Internal error : URL error");
                break;
            case CONNECTION_ERROR:
                Log.d(LOG_TAG, "Connection error !");
                // TODO notify user.
                break;
            case JSON_ERROR:
                Log.d(LOG_TAG, "Error with JSON parsing !");
                break;
            case EMPTY_ERROR:
                Log.d(LOG_TAG, "Empty JSON string !");
                break;
            case NO_ROUTES_ERROR:
                Log.d(LOG_TAG, "No routes => Warn the user !");
                // TODO notify user
                break;
            default:
                break;
        }
    }

    /**
     * Updates the UI of MotivationFragment/
     *
     * @param transitTime Result of doInBackground method
     */
    @Override
    protected void onPostExecute(Integer transitTime) {
        if (transitTime != null) {
            Log.d(LOG_TAG, "Transit time (in seconds) : " + transitTime);

            // Get workout, warmup & stretching times
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mActivity);

            int warmup = prefs.getInt(mActivity.getString(R.string.pref_warmup_key),
                    mActivity.getResources().getInteger(R.integer.pref_warmup_default));
            int stretching = prefs.getInt(mActivity.getString(R.string.pref_stretching_key),
                    mActivity.getResources().getInteger(R.integer.pref_stretching_default));
            int workout = prefs.getInt(mActivity.getString(R.string.pref_workout_key), 0);

            // Calculate the time spent and add it to the current time
            // Time spent (in milliseconds)
            // TODO calculate 2 different transit times : to go & to come back (do it in the same FetchTransitTask)
            long timeSpent = (warmup * 60 * 1000)
                    + (stretching * 60 * 1000)
                    + 2 * (transitTime * 1000)
                    + (workout * 60 * 1000);

            // Current time
            Date now = new Date();
            long currentTime = now.getTime();

            // Time back at home
            Date backAtHome = new Date(currentTime + timeSpent);

            mListener.onBackAtHomeTimeRetrieved(backAtHome);

        }
    }

    /**
     * Parses the JSON String and returns the transit time in seconds.
     *
     * @param jsonString JSON String to parse
     * @return Transit time (in seconds)
     * @throws JSONException When there is a problem parsing the JSON String
     */
    private int parseTransitTime(String jsonString) throws JSONException {

        final String JSON_ROUTES = "routes";
        final String JSON_LEGS = "legs";
        final String JSON_DURATION = "duration";
        final String JSON_DURATION_VALUE = "value";
        final String JSON_STATUS = "status";

        JSONObject root = new JSONObject(jsonString);

        // Check that there are routes availables
        if (root.getString(JSON_STATUS).equals("ZERO_RESULTS")) {
            return -1;
        }

        // Return the transit time
        return root.getJSONArray(JSON_ROUTES)
                .getJSONObject(0)
                .getJSONArray(JSON_LEGS)
                .getJSONObject(0)
                .getJSONObject(JSON_DURATION)
                .getInt(JSON_DURATION_VALUE);
    }

}