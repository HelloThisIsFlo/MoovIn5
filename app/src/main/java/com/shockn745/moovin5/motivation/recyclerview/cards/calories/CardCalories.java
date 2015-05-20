package com.shockn745.moovin5.motivation.recyclerview.cards.calories;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.moovin5.R;
import com.shockn745.moovin5.motivation.recyclerview.cards.AbstractCard;

/**
 * Card that display calories information
 *
 * @author Florian Kempenich
 */
public class CardCalories extends AbstractCard {

    public static class CaloriesVH extends RecyclerView.ViewHolder {
        public final TextView headerTextView;
        public final TextView caloriesTextView;
        public final RecyclerView recyclerView;

        public CaloriesVH(View itemView) {
            super(itemView);
            this.headerTextView = (TextView) itemView.findViewById(R.id.calories_header_text_view);
            this.caloriesTextView = (TextView)
                    itemView.findViewById(R.id.calories_calories_text_view);
            this.recyclerView = (RecyclerView) itemView.findViewById(R.id.calories_recycler_view);
        }
    }

    public static class CaloriesItem {
        private final String mText;
        private final int mImageId;

        public CaloriesItem(String name, float quantity, int mImageId) {
            this.mImageId = mImageId;


            mText = String.format("%.1f %s", quantity, name);
        }

        public String getText() {
            return mText;
        }

        public int getImageId() {
            return mImageId;
        }
    }

    private String mHeaderText;
    private String mCaloriesText;
    private CaloriesItem[] mItems;

    public CardCalories(Activity activity) {
        super(activity);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        // Process the estimated calorie burn
        int workoutDuration = prefs.getInt(activity.getString(R.string.pref_workout_key),
                activity.getResources().getInteger(R.integer.workout_default));
        int coeffCaloriesX10 = activity.getResources().getInteger(R.integer.coeff_calories_x10);
        int caloriesBurnt = (int) (workoutDuration * coeffCaloriesX10/10f);

        mHeaderText = activity.getString(R.string.card_header_calories);
        mCaloriesText = caloriesBurnt + " " +activity.getString(R.string.calories_cals);

        // Add the equivalent calories / food
        // Process calories
        float[] calories = new float[4];
        calories[0] = ((float) caloriesBurnt) /
                activity.getResources().getInteger(R.integer.calories_croissant_cals);
        calories[1] = ((float) caloriesBurnt) /
                activity.getResources().getInteger(R.integer.calories_balanced_cals);
        calories[2] = ((float) caloriesBurnt) /
                activity.getResources().getInteger(R.integer.calories_pizza_cals);
        calories[3] = ((float) caloriesBurnt) /
                activity.getResources().getInteger(R.integer.calories_chocolate_cals);
        // Add CaloriesItems
        mItems = new CaloriesItem[4];
        mItems[0] = new CaloriesItem(
                activity.getString(R.string.calories_croissant),
                calories[0],
                R.drawable.calories_croissant
        );
        mItems[1] = new CaloriesItem(
                activity.getString(R.string.calories_balanced),
                calories[1],
                R.drawable.calories_balanced_meal
        );
        mItems[2] = new CaloriesItem(
                activity.getString(R.string.calories_pizza),
                calories[2],
                R.drawable.calories_pizza_slice
        );
        mItems[3] = new CaloriesItem(
                activity.getString(R.string.calories_chocolate),
                calories[3],
                R.drawable.calories_chocolate_bar
        );

    }

    @Override
    public int getViewType() {
        return CALORIES_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return true;
    }

    public String getHeaderText() {
        return mHeaderText;
    }

    public String getCaloriesText() {
        return mCaloriesText;
    }

    public CaloriesItem[] getItems() {
        return mItems;
    }
}
