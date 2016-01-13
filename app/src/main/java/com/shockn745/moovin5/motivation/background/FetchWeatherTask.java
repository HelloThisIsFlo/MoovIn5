package com.shockn745.moovin5.motivation.background;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.internal.request.StringParcel;
import com.google.android.gms.maps.model.LatLng;
import com.shockn745.moovin5.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask that fetch the current weather on background
 *
 * @author Florian Kempenich
 */
public class FetchWeatherTask extends AsyncTask<LatLng, Integer, FetchWeatherTask.WeatherInfos> {

    public static class WeatherInfos {
        public final double mTemperature;
        public final String mForecast;
        public final int mWeatherId;

        public WeatherInfos(double mTemperature, String mForecast, int mWeatherId) {
            this.mTemperature = mTemperature;
            this.mForecast = mForecast;
            this.mWeatherId = mWeatherId;
        }
    }

    public interface OnWeatherInfoRetrievedListener {

        /**
         * Callback called when FetchWeatherTask is done
         * @param weatherInfos Current weather at the gym location
         * @param resultCode RESULT_OK if OK <br>
         *                   ERROR if error <br>
         *                   CONNECTION_ERROR if connection error
         */
        void onWeatherInfoRetrieved(WeatherInfos weatherInfos, int resultCode);
    }

    public final static int ERROR = -1;
    public final static int RESULT_OK = 0;
    public final static int CONNECTION_ERROR = 1;

    private final OnWeatherInfoRetrievedListener mListener;
    private final String mOpenWeatherMapApiKey;

    private boolean mIsCelsius;

    public FetchWeatherTask(Context context, OnWeatherInfoRetrievedListener listener) {
        this.mListener = listener;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Retreive the unit type
        mIsCelsius = prefs.getBoolean(
                context.getString(R.string.pref_is_celsius_key),
                context.getResources().getBoolean(R.bool.pref_is_celsius_default)
        );

        // Store the API key
        mOpenWeatherMapApiKey = context.getString(R.string.open_wheather_map_api_key);
    }

    @Override
    protected WeatherInfos doInBackground(LatLng... params) {
        if (params.length != 1) {
            publishProgress(ERROR);
            return null;
        }
        LatLng locationQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        // These are the parameter for the request
        String mode = "json";

        // Use the unit from preferences
        String unit;
        if (mIsCelsius) {
            unit = "metric";
        } else {
            unit = "imperial";
        }

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String QUERY_PARAM_API_KEY = "APPID";
            final String QUERY_PARAM_LAT = "lat";
            final String QUERY_PARAM_LNG = "lon";
            final String MODE_PARAM = "mode";
            final String UNIT_PARAM = "units";

            Uri.Builder builder = new Uri.Builder();

            builder.scheme("http")
                    .authority("api.openweathermap.org")
                    .path("data/2.5/weather")
                    .appendQueryParameter(QUERY_PARAM_API_KEY, mOpenWeatherMapApiKey)
                    .appendQueryParameter(QUERY_PARAM_LAT, Double.toString(locationQuery.latitude))
                    .appendQueryParameter(QUERY_PARAM_LNG, Double.toString(locationQuery.longitude))
                    .appendQueryParameter(MODE_PARAM, mode)
                    .appendQueryParameter(UNIT_PARAM, unit);
            String uriString = builder.build().toString();

            URL url = new URL(uriString);
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                publishProgress(ERROR);
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                forecastJsonStr = null;
            }
            forecastJsonStr = buffer.toString();

        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point
            // in attemping to parse it.
            publishProgress(CONNECTION_ERROR);
            return null;
        }  finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }

        try {
            return parseJsonString(forecastJsonStr);
        } catch (JSONException e) {
            publishProgress(ERROR);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... errorCode) {
        switch (errorCode[0]) {
            case CONNECTION_ERROR :
                mListener.onWeatherInfoRetrieved(null, CONNECTION_ERROR);
                break;
            case ERROR :
                mListener.onWeatherInfoRetrieved(null, ERROR);
                break;
            default:
        }
    }

    @Override
    protected void onPostExecute(WeatherInfos weatherInfos) {
        if (weatherInfos != null) {
            mListener.onWeatherInfoRetrieved(weatherInfos, RESULT_OK);
        }
    }

    /**
     * Parses the JSON String and returns the WeatherInfos
     *
     * @param jsonString JSON String to parse
     * @return WeatherInfos
     * @throws JSONException When there is a problem parsing the JSON String
     */
    private WeatherInfos parseJsonString(String jsonString) throws JSONException {

        final String JSON_WEATHER = "weather";
        final String JSON_FORECAST = "main";
        final String JSON_WEATHER_ID = "id";
        final String JSON_TEMP_ROOT = "main";
        final String JSON_TEMPERATURE = "temp";

        JSONObject root = new JSONObject(jsonString);

        JSONObject weatherJSONObject = root
                .getJSONArray(JSON_WEATHER)
                .getJSONObject(0);

        String forecast = weatherJSONObject.getString(JSON_FORECAST);
        int weatherId = weatherJSONObject.getInt(JSON_WEATHER_ID);
        Double temperature = root
                .getJSONObject(JSON_TEMP_ROOT)
                .getDouble(JSON_TEMPERATURE);

        return new WeatherInfos(temperature, forecast, weatherId);
    }
}
