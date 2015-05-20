package com.shockn745.moovin5.motivation.recyclerview.cards;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.background.FetchWeatherTask;

/**
 * Card that display weather information
 *
 * @author Florian Kempenich
 */
public class CardWeather extends AbstractCard {

    public static class WeatherVH extends RecyclerView.ViewHolder {
        public TextView mTempTextView;
        public TextView mForecastTextView;
        public ImageView mImageView;

        public WeatherVH(View itemView, final float ratio) {
            super(itemView);
            this.mTempTextView = (TextView) itemView.findViewById(R.id.temperature_text_view);
            this.mForecastTextView = (TextView) itemView.findViewById(R.id.forecast_text_view);
            this.mImageView = (ImageView) itemView.findViewById(R.id.weather_image_view);

            final View cardView = itemView.findViewById(R.id.weather_image_view);

            // Set the height dynamically after layout (to be able to get the width parameter)
            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = (int) (cardView.getWidth() / ratio);
                    cardView.setLayoutParams(layoutParams);
                }
            });
        }
    }

    private String mTempText;
    private String mForecastText;
    private int mImageResId;

    public CardWeather(Activity activity, FetchWeatherTask.WeatherInfos weatherInfos) {
        super(activity);
        mTempText = Long.toString(Math.round(weatherInfos.mTemperature)) + " °";
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

    public String getTempText() {
        return mTempText;
    }

    public String getForecastText() {
        return mForecastText;
    }

    public int getImageResId() {
        return mImageResId;
    }

    /**
     * Helper method to provide the image resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding image. -1 if no relation is found.
     */
    private int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes

        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.weather_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.weather_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.weather_rain;
        } else if (weatherId == 511) {
            return R.drawable.weather_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.weather_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.weather_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.weather_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.weather_storm;
        } else if (weatherId == 800) {
            return R.drawable.weather_clear;
        } else if (weatherId == 801) {
            return R.drawable.weather_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.weather_clouds;
        }
        return -1;
    }

}
