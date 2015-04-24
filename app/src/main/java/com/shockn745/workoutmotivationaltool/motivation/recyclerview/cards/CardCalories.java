package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Card that display calories information
 */
public class CardCalories implements CardInterface {

    public static class CaloriesVH extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public CaloriesVH(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.calories_text_view);
        }
    }

    private String mText;

    public CardCalories(String text) {
        this.mText = "Calories" + text;
    }

    @Override
    public int getViewType() {
        return CALORIES_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    public String getText() {
        return mText;
    }
}
