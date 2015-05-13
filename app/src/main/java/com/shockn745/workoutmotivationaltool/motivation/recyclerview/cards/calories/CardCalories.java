package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.calories;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;
import com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.CardInterface;

/**
 * Card that display calories information
 */
public class CardCalories implements CardInterface {

    public static class CaloriesVH extends RecyclerView.ViewHolder {
        public final TextView mTextView;
        public final RecyclerView mRecyclerView;

        public CaloriesVH(View itemView) {
            super(itemView);
            this.mTextView = (TextView) itemView.findViewById(R.id.calories_text_view);
            this.mRecyclerView = (RecyclerView) itemView.findViewById(R.id.calories_recycler_view);
        }
    }

    public static class CaloriesItem {
        private final String mText;
        private final int mImageId;

        public CaloriesItem(String name, float quantity, int mImageId) {
            this.mImageId = mImageId;


            mText = String.format("%.2f %s", quantity, name);
        }

        public String getText() {
            return mText;
        }

        public int getImageId() {
            return mImageId;
        }
    }

    private String mText;
    private CaloriesItem[] mItems;

    public CardCalories(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        int workoutDuration = prefs.getInt(activity.getString(R.string.pref_workout_key),
                activity.getResources().getInteger(R.integer.workout_default));
        int coeffCalories = activity.getResources().getInteger(R.integer.coeff_calories);

        int caloriesBurnt = workoutDuration * coeffCalories;

        mText = activity.getString(R.string.calories_text_part_1)
                + caloriesBurnt
                + activity.getString(R.string.calories_text_part_2);

        mItems = new CaloriesItem[4];
        mItems[0] = new CaloriesItem("Croissant", 1.3f, R.drawable.calories_croissant);
        mItems[1] = new CaloriesItem("Balanced meal", 0.7f, R.drawable.calories_balanced_meal);
        mItems[2] = new CaloriesItem("Pizza slice", 3f, R.drawable.calories_pizza_slice);
        mItems[3] = new CaloriesItem("Chocolate bar", 1.1f, R.drawable.calories_chocolate_bar);

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

    public CaloriesItem[] getItems() {
        return mItems;
    }
}
