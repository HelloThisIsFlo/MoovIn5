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
public class FetchTransitTask extends AsyncTask<LatLng, Integer, FetchTransitTask.TransitInfos> {

    public interface OnBackAtHomeTimeRetrievedListener {

        /**
         * Callback called when FetchTransitTask is done
         * @param transitInfos Transit infos
         * @param resultCode RESULT_OK if OK <br>
         *                   ERROR if error <br>
         *                   NO_ROUTES_ERROR if no routes <br>
         *                   CONNECTION_ERROR if connection error
         */
        public void onBackAtHomeTimeRetrieved(TransitInfos transitInfos, int resultCode);

    }

    public static class TransitInfos {
        private int mTransitTime;
        private String mPolylineRoute;
        private Date backAtHomeDate;

        public TransitInfos(int transitTime, String polylineRoute) {
            this.mPolylineRoute = polylineRoute;
            this.mTransitTime = transitTime;
        }

        public int getTransitTime() {
            return mTransitTime;
        }

        public String getPolylineRoute() {
            return mPolylineRoute;
        }

        public Date getBackAtHomeDate() {
            return backAtHomeDate;
        }

        public void setBackAtHomeDate(Date backAtHomeDate) {
            this.backAtHomeDate = backAtHomeDate;
        }
    }

    private final String LOG_TAG = FetchTransitTask.class.getSimpleName();

    public final static int ERROR = -1;
    public final static int RESULT_OK = 0;
    private final static int ARG_ERROR = 1;
    private final static int URL_ERROR = 2;
    public final static int CONNECTION_ERROR = 3;
    private final static int JSON_ERROR = 4;
    private final static int EMPTY_ERROR = 5;
    public final static int NO_ROUTES_ERROR = 6;

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
    protected TransitInfos doInBackground(LatLng... params) {
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
                    TransitInfos transitInfos = parseTransitInfos(jsonString);
                    if (transitInfos == null) {
                        // No routes available
                        publishProgress(NO_ROUTES_ERROR);
                        return null;
                    } else {
                        return transitInfos;
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
                mListener.onBackAtHomeTimeRetrieved(null, ERROR);
                break;

            case URL_ERROR:
                Log.d(LOG_TAG, "Internal error : URL error");
                mListener.onBackAtHomeTimeRetrieved(null, ERROR);
                break;

            case CONNECTION_ERROR:
                Log.d(LOG_TAG, "Connection error !");
                mListener.onBackAtHomeTimeRetrieved(null, CONNECTION_ERROR);
                break;

            case JSON_ERROR:
                Log.d(LOG_TAG, "Error with JSON parsing !");
                mListener.onBackAtHomeTimeRetrieved(null, ERROR);
                break;

            case EMPTY_ERROR:
                Log.d(LOG_TAG, "Empty JSON string !");
                mListener.onBackAtHomeTimeRetrieved(null, ERROR);
                break;

            case NO_ROUTES_ERROR:
                Log.d(LOG_TAG, "No routes => Warn the user !");
                mListener.onBackAtHomeTimeRetrieved(null, NO_ROUTES_ERROR);
                break;
            default:
                break;
        }
    }

    /**
     * Updates the UI of MotivationFragment/
     *
     * @param transitInfos Result of doInBackground method
     */
    @Override
    protected void onPostExecute(TransitInfos transitInfos) {
        if (transitInfos != null) {
            Log.d(LOG_TAG, "Transit time (in seconds) : " + transitInfos.getTransitTime());

            // Get workout, warmup & stretching times
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mActivity);

            int warmup = prefs.getInt(mActivity.getString(R.string.pref_warmup_key),
                    mActivity.getResources().getInteger(R.integer.pref_warmup_default));
            int stretching = prefs.getInt(mActivity.getString(R.string.pref_stretching_key),
                    mActivity.getResources().getInteger(R.integer.pref_stretching_default));
            int workout = prefs.getInt(mActivity.getString(R.string.pref_workout_key),
                    mActivity.getResources().getInteger(R.integer.workout_default));

            // Calculate the time spent and add it to the current time
            // Time spent (in milliseconds)
            // TODO calculate 2 different transit times : to go & to come back (do it in the same FetchTransitTask)
            long timeSpent = (warmup * 60 * 1000)
                    + (stretching * 60 * 1000)
                    + 2 * (transitInfos.getTransitTime() * 1000)
                    + (workout * 60 * 1000);

            // Current time
            Date now = new Date();
            long currentTime = now.getTime();

            // Time back at home
            Date backAtHome = new Date(currentTime + timeSpent);

            transitInfos.setBackAtHomeDate(backAtHome);

            mListener.onBackAtHomeTimeRetrieved(transitInfos, RESULT_OK);
        }
    }

    /**
     * Parses the JSON String and returns the transit time in seconds.
     *
     * @param jsonString JSON String to parse
     * @return Transit time (in seconds)
     * @throws JSONException When there is a problem parsing the JSON String
     */
    private TransitInfos parseTransitInfos(String jsonString) throws JSONException {

        final String JSON_ROUTES = "routes";
        final String JSON_LEGS = "legs";
        final String JSON_DURATION = "duration";
        final String JSON_DURATION_VALUE = "value";
        final String JSON_STATUS = "status";
        final String JSON_POLYLINE = "overview_polyline";
        final String JSON_POINTS = "points";

        JSONObject root = new JSONObject(jsonString);

        // Check that there are routes availables
        if (root.getString(JSON_STATUS).equals("ZERO_RESULTS")) {
            return null;
        }

        // Return the transit time

        JSONObject firstRoute = root.getJSONArray(JSON_ROUTES)
                .getJSONObject(0);

        int transitTime = firstRoute
                .getJSONArray(JSON_LEGS)
                .getJSONObject(0)
                .getJSONObject(JSON_DURATION)
                .getInt(JSON_DURATION_VALUE);

        String polyline = firstRoute
                .getJSONObject(JSON_POLYLINE)
                .getString(JSON_POINTS);

        return new TransitInfos(transitTime, polyline);
    }

}