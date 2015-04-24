package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Card that display weather information
 */
public class CardWeather implements CardInterface {

    public static class WeatherVH extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public WeatherVH(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.weather_text_view);
        }
    }

    private String mText;

    public CardWeather(String text) {
        this.mText = text;
    }

    @Override
    public int getViewType() {
        return WEATHER_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    public String getText() {
        return mText;
    }
}
