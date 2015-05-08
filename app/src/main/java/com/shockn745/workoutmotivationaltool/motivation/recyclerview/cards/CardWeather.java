package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.background.FetchWeatherTask;

/**
 * Card that display weather information
 */
public class CardWeather implements CardInterface {

    public static class WeatherVH extends RecyclerView.ViewHolder {
        public TextView mTempTextView;
        public TextView mForecastTextView;
        public ImageView mImageView;

        public WeatherVH(View itemView) {
            super(itemView);
            this.mTempTextView = (TextView) itemView.findViewById(R.id.temperature_text_view);
            this.mForecastTextView = (TextView) itemView.findViewById(R.id.forecast_text_view);
            this.mImageView = (ImageView) itemView.findViewById(R.id.weather_image_view);
        }
    }

    private String mTempText;
    private String mForecastText;
    private int mImageResId;

    public CardWeather(FetchWeatherTask.WeatherInfos weatherInfos) {

        mTempText = Integer.toString(weatherInfos.mTemperature) + " °";
        mForecastText = weatherInfos.mForecast;
        mImageResId = getIconResourceForWeatherCondition(weatherInfos.mWeatherId);

    }

    @Override
    public int getViewType() {
        return WEATHER_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    public String getmTempText() {
        return mTempText;
    }

    public String getmForecastText() {
        return mForecastText;
    }

    public int getmImageResId() {
        return mImageResId;
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    private int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes

        //TODO implement with real images
//        if (weatherId >= 200 && weatherId <= 232) {
//            return R.drawable.ic_storm;
//        } else if (weatherId >= 300 && weatherId <= 321) {
//            return R.drawable.ic_light_rain;
//        } else if (weatherId >= 500 && weatherId <= 504) {
//            return R.drawable.ic_rain;
//        } else if (weatherId == 511) {
//            return R.drawable.ic_snow;
//        } else if (weatherId >= 520 && weatherId <= 531) {
//            return R.drawable.ic_rain;
//        } else if (weatherId >= 600 && weatherId <= 622) {
//            return R.drawable.ic_snow;
//        } else if (weatherId >= 701 && weatherId <= 761) {
//            return R.drawable.ic_fog;
//        } else if (weatherId == 761 || weatherId == 781) {
//            return R.drawable.ic_storm;
//        } else if (weatherId == 800) {
//            return R.drawable.ic_clear;
//        } else if (weatherId == 801) {
//            return R.drawable.ic_light_clouds;
//        } else if (weatherId >= 802 && weatherId <= 804) {
//            return R.drawable.ic_cloudy;
//        }

        return R.drawable.weather_rain;
//        return -1;
    }

}
