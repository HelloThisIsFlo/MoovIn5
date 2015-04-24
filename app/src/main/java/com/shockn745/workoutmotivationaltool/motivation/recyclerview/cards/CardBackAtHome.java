package com.shockn745.workoutmotivationaltool.motivation.recyclerview.cards;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shockn745.workoutmotivationaltool.R;

/**
 * Card displaying the time back at home
 */
public class CardBackAtHome implements CardInterface {

    public static class BackAtHomeVH extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public BackAtHomeVH(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.back_at_home_text_view);
        }
    }

    private String mText;

    public CardBackAtHome(String text) {
        this.mText = text;
    }

    @Override
    public int getViewType() {
        return BACK_AT_HOME_VIEW_TYPE;
    }

    @Override
    public boolean canDismiss() {
        return false;
    }

    public String getText() {
        return mText;
    }
}
