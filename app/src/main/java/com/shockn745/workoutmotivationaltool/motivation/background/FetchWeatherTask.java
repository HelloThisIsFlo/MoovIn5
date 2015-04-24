package com.shockn745.workoutmotivationaltool.motivation.background;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * AsyncTask that fetch the current weather on background
 */
public class FetchWeatherTask extends AsyncTask<LatLng, Integer, FetchWeatherTask.WeatherInfos> {

    public static class WeatherInfos {
        public final int mTemperature;
        public final String mForecast;
        public final int mWeatherId;

        public WeatherInfos(int mTemperature, String mForecast, int mWeatherId) {
            this.mTemperature = mTemperature;
            this.mForecast = mForecast;
            this.mWeatherId = mWeatherId;
        }
    }

    public interface OnWeatherInfoRetrievedListener {

        /**
         * Callback called when FetchWeatherTask is done
         * @param weatherInfos Current weather at the gym location
         */
        public void OnWeatherInfoRetrieved(WeatherInfos weatherInfos);
    }

    private Context mContext;

    public FetchWeatherTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected WeatherInfos doInBackground(LatLng... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(WeatherInfos weatherInfos) {
        super.onPostExecute(weatherInfos);
    }
}
