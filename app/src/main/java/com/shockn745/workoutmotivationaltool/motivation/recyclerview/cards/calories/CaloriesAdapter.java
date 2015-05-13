package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards.calories;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Adapter for the CaloriesItems
 */
public class CaloriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CardCalories.CaloriesItem[] mDataSet;

    public CaloriesAdapter(CardCalories.CaloriesItem[] dataSet) {
        this.mDataSet = dataSet;
    }

    public static class CaloriesItemVH extends RecyclerView.ViewHolder {
        public final ImageView mImageView;
        public final TextView mTextView;

        public CaloriesItemVH(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.calories_list_item_image_view);
            mTextView = (TextView) itemView.findViewById(R.id.calories_list_item_text_view);

            // Invert the color of the drawable
            float[] colorMatrix_Negative = {
                    -1.0f, 0, 0, 0, 255, //red
                    0, -1.0f, 0, 0, 255, //green
                    0, 0, -1.0f, 0, 255, //blue
                    0, 0, 0, 1.0f, 0 //alpha
            };
            ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);
            mImageView.setColorFilter(colorFilter_Negative);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_calories_list_item, parent, false);

        return new CaloriesItemVH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CaloriesItemVH caloriesItemVH = (CaloriesItemVH) holder;

        caloriesItemVH.mTextView.setText(mDataSet[position].getText());
        caloriesItemVH.mImageView.setImageResource(mDataSet[position].getImageId());
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
