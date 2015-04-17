package com.shockn745.workoutmotivationaltool;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            // Build fake data for test purposes
            LatLng[] coord = new LatLng[2];
            coord[0] = new LatLng(52.48969964750192, 13.381244726479053);
            coord[1] = new LatLng(52.461685, 13.419890);


            FetchTransitTask fetchTransitTask = new FetchTransitTask();
            fetchTransitTask.execute(coord);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_test, container, false);
            return rootView;
        }


        /**
         * Background task to fetch the transit time from the Google Directions API
         * It sends a http request, parse the result JSON string and returns the transit time
         * to destination<br>
         * It takes a LatLng[2] as parameter, where :<br>
         *     LatLng[0] : start point<br>
         *     LatLng[1] : destination<br>
         *
         */
        private class FetchTransitTask extends AsyncTask<LatLng, Integer, Integer> {
            private final String LOG_TAG = FetchTransitTask.class.getSimpleName();

            private final static int ARG_ERROR = 0;
            private final static int URL_ERROR = 1;
            private final static int CONNECTION_ERROR = 2;
            private final static int JSON_ERROR = 3;
            private final static int EMPTY_ERROR = 4;


            /**
             * See class description
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
                    // Return result if succesful
                    if (jsonString != null) {
                        try {
                            // String too long to log => using debug mode instead
                            // Log.d(LOG_TAG, jsonString);
                            return parseTransitTime(jsonString);
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
                        break;
                    case JSON_ERROR:
                        Log.d(LOG_TAG, "Error with JSON parsing !");
                        break;
                    case EMPTY_ERROR:
                        Log.d(LOG_TAG, "Empty JSON string !");
                        break;
                    default:
                        break;
                }
            }

            /**
             *
             * @param transitTime
             */
            @Override
            protected void onPostExecute(Integer transitTime) {
                if (transitTime != null) {
                    // Do sthg
                    Log.d(LOG_TAG, "On est dans post execute !");




                }
            }

            /**
             * Parses the JSON String and returns the transit time in seconds.
             * @param jsonString JSON String to parse
             * @return Transit time (in seconds)
             * @throws JSONException When there is a problem parsing the JSON String
             */
            private int parseTransitTime(String jsonString) throws JSONException {


                return 0;
            }

        }
    }
}
